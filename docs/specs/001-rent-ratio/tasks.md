# Tasks 001 · Rent-to-income ratio

TDD loop for each task: 🔴 write a failing test → 🟢 minimum code to pass → 🔵 refactor.

- [ ] **T1** · Create branch `feat/US-2.3-rent-ratio`; commit this spec, plan and tasks first (`docs(specs): add spec 001 rent ratio`)
- [ ] **T2** · 🔴 `RentRatioCalculatorTest`: AC1 (1500 / 450 → 30.0) → 🟢 implement R1 + R2
- [ ] **T3** · 🔴 Parameterised test for AC2 boundaries (29.9, 30.0, 40.0, 40.1) and R2 rounding (e.g. 1500 / 449.4 → 30.0 → AMBER) → 🟢 `EffortThresholds.classify`
- [ ] **T4** · 🔴 AC3 (rent > income → RED + flag) → 🟢
- [ ] **T5** · 🔴 AC4 + R4 (1500 → 449 · 1000 → 299 · **2000 → 598**, the rounding trap) → 🟢
- [ ] **T6** · 🔴 AC5 in the domain (null, 0, negative, >2 decimals, >1,000,000) → 🟢 `InvalidAmountException`
- [ ] **T7** · Use case interface + service (thin: delegates to the calculator)
- [ ] **T8** · 🔴 `RentRatioControllerTest` (`@WebMvcTest`): happy path → 🟢 controller + DTOs
- [ ] **T9** · 🔴 Invalid body → 400 Problem Details → 🟢 Bean Validation + error handling
- [ ] **T10** · Thresholds in `application.properties` via `@ConfigurationProperties`
- [ ] **T11** · Write **ADR-0002 · Package structure**
- [ ] **T12** · Manual check with `curl`, 📸 screenshot, update `docs/specs/README.md` (status → Implemented) and the process log
