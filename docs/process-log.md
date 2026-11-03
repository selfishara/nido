# 📓 Process log

Project diary: decisions, learnings and screenshots. Each entry can become a LinkedIn post.

---

## 2026-10-01 · Day 0: why I'm starting Nido

**Context:** I want to get back to what I love (building apps, APIs, AI and cybersecurity) with a personal project run like a professional team would run it.

**Decisions:**
- Merge two ideas: **housing** (scam detector, contract reviewer) and **personal finance for young people** (rent-to-income ratio, savings, payslip).
- **Mobile + web** app with KMP and a Spring Boot backend ([ADR-0001](adr/0001-tech-stack.md)).
- Backlog in **GitHub Projects**, which I'm using for the first time ([guide](github-projects-guide.md)).
- **Spec-Driven Development**: every feature starts as a spec ([notes](learning/spec-driven-development.md)).
- Git by hand in the terminal to regain fluency ([cheatsheet](learning/git-cheatsheet.md)).
- Everything documented in English.

## 2026-11-03 · First live API: from domain rules to `POST /api/v1/money/rent-ratio`

**Context:** Spec [001 · Rent-to-income ratio](specs/001-rent-ratio/spec.md), tasks T6–T9, on branch `feat/US-2.3-rent-ratio-api`. Built test-first (TDD), one hexagonal layer at a time.

**Done:**
- **T6 · Domain validation:** `InvalidAmountException` + guard clauses in `RentRatioCalculator` (missing, ≤ 0, more than 2 decimals, above 1,000,000). 22 domain tests green.
- **T7 · Use case:** input port `CalculateRentRatioUseCase` + thin `CalculateRentRatioService`, both plain Java.
- **T8 · Endpoint:** `RentRatioController`, `RentRatioRequest` / `RentRatioResponse` DTOs and `MoneyConfig` (bean wiring). `@WebMvcTest` + MockMvc test, then called the **real running app** from PowerShell: `30.0 · AMBER · 449 · false` ✅
- **T9 (part 1) · Errors:** `MoneyExceptionHandler` (`@RestControllerAdvice`) maps `InvalidAmountException` to `400` Problem Details (RFC 9457). Before: `500`.
- Temporary architecture map: [money-module.md](architecture/money-module.md) (Mermaid + legend of every package and class).

**Decisions:**
- `domain` and `application` stay **framework-free**: no `@Service`; Spring wiring lives in `infrastructure/config/MoneyConfig` (to be recorded in ADR-0002).
- Controllers depend on the **port** (interface), not on the service: Dependency Inversion.
- Separate **DTOs** from the domain model, so the public API contract doesn't change when the domain does. `level` is a `String` in the DTO.
- Only **our own** exception is mapped to a 4xx; its messages name the field, never the amount. Generic exceptions are never echoed to the client.

**Problems & fixes:**
- **A test found a DoS bug.** With validation called *after* the calculation, a negative income sent `healthyMaxRent` into an infinite loop (the test ran for 55 minutes). Fix: guard clauses at the very top of `calculate`. Lesson: validate at the door, not in the living room.
- **404 with a "working" controller.** `@WebMvcTest(RentRatioControllerTest.class)` pointed at the test itself, so Spring started with no controllers. The clue was in the MockMvc output: `Handler: ResourceHttpRequestHandler` + `No static resource`. Also explained why the test had compiled before the controller existed.
- **Git staging trap.** IntelliJ added `MoneyExceptionHandler` when it was still empty; later edits weren't in the staged snapshot. Caught it by reading `git status` + `git diff` before committing. `git add` is a snapshot, not a live link.
- **Stray file `h`** in the repo root: the `less` pager's help screen, saved by pressing `s` + `h`. Rule: in the pager, only `q`.
- Several IntelliJ quick fixes suggested the wrong fix (e.g. "create field in `EffortThresholds`"). Read the cause before accepting.

**Next:** finish T9 with Bean Validation on `RentRatioRequest` (defense in depth) → Swagger UI → T10 configurable thresholds → T11 ADR-0002 → T12 final check, PR and spec status `Implemented`.

> 💬 **LinkedIn draft:** "My personal project Nido has its first live API endpoint: you send your net income and your rent, and it tells you what share of your salary goes to housing, with a traffic-light level and the highest healthy rent. I built it test-first, in hexagonal layers. The best moment: a unit test caught a bug where a negative salary sent the calculation into an infinite loop. On a public endpoint, that's a denial-of-service waiting to happen. Validating input at the very start of the domain fixed it. Next step: proper 400 errors, Swagger docs and the Angular screen."
