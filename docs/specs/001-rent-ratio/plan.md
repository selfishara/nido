# Plan 001 · Rent-to-income ratio

- **Spec:** [spec.md](spec.md) · **Constitution check:** ✅ hexagonal · ✅ tests first · ⚠️ public endpoint (justified exception, spec §6) · ✅ privacy

## 1. Architecture
First business module → it also establishes the package structure (**ADR-0002**).

```
com.nido.backend.money
├── domain/
│   ├── model/
│   │   ├── EffortLevel.java            enum GREEN, AMBER, RED
│   │   ├── EffortThresholds.java       record(amberFrom, redAbove) + classify(ratio)
│   │   └── RentRatio.java              record(percentage, level, healthyMaxRent, rentExceedsIncome)
│   ├── service/
│   │   └── RentRatioCalculator.java    pure Java: calculate(income, rentShare) → RentRatio
│   └── exception/
│       └── InvalidAmountException.java
├── application/
│   ├── port/in/CalculateRentRatioUseCase.java      (input port, interface)
│   └── CalculateRentRatioService.java              (implements the use case)
└── infrastructure/
    ├── web/
    │   ├── RentRatioController.java
    │   ├── RentRatioRequest.java       record + Bean Validation
    │   └── RentRatioResponse.java      record
    └── config/
        └── MoneyConfig.java            @ConfigurationProperties → thresholds, wires the beans
```

**Dependency rule:** `domain` has no Spring imports. `RentRatioCalculator` is plain Java and is instantiated as a bean in `MoneyConfig`.

## 2. Data model
None: nothing is persisted. Money is handled as `BigDecimal` (**never `double`**: `0.1 + 0.2 ≠ 0.3`).

## 3. API contract
| Method | Path | Request | Response | Auth |
|---|---|---|---|---|
| POST | `/api/v1/money/rent-ratio` | `{ "netMonthlyIncome": 1500.00, "rentShare": 450.00 }` | `200 { "percentage": 30.0, "level": "AMBER", "healthyMaxRent": 449, "rentExceedsIncome": false }` | Public (spec §6) |

- **Why POST, if it doesn't create anything?** So that amounts travel in the **body**, not in the URL (spec §6).
- Errors → `400` with **Problem Details (RFC 9457)**, which Spring supports out of the box:
  `{ "title": "Invalid request", "status": 400, "errors": { "rentShare": "must be greater than 0" } }`

## 4. Security
| STRIDE | Threat | Control |
|---|---|---|
| Information disclosure | Amounts leaking via URLs or logs | POST body · no amounts in logs |
| Denial of service | Abuse of a public endpoint | Bounded input (≤ 1,000,000) · rate limiting in US-1.4 |
| Tampering | Malformed input | Bean Validation (`@NotNull @Positive @DecimalMax @Digits`) + domain validation |

## 5. Test strategy
| Level | Tool | Covers |
|---|---|---|
| Unit (domain) | JUnit 5 + AssertJ, `@ParameterizedTest` | AC1–AC4, R2 rounding, boundaries 29.9/30.0/40.0/40.1 |
| Unit (domain) | JUnit 5 | AC5 domain validation |
| Web slice | `@WebMvcTest` + MockMvc | Happy path JSON, AC5 → 400 Problem Details |

No database, no Testcontainers needed: domain tests run in milliseconds.

## 6. Decisions
- **ADR-0002 · Package structure:** feature modules → hexagonal layers inside each (`domain` / `application` / `infrastructure`), plus a future `ai` module (see the chat discussion about the AI agent structure).
