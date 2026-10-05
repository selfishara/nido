---
name: tdd-cycle
description: Guide Sara through one TDD cycle (red, green, refactor) for a task in a spec's tasks.md, in teaching mode. Use when she says "next task", "T3", "let's do the next test", or wants to implement part of a spec.
---

# TDD cycle (teaching mode)

Sara is learning. **Guide, don't do it for her**, unless she explicitly asks for the solution.

## Steps
1. Find the active spec (`docs/specs/NNN-*/tasks.md`) and the next unchecked task. Re-read the AC and rules it covers in `spec.md`.
2. **🔴 Red:** propose the test(s) for that task:
   - Name the method after the AC (`ac2_…`). Use `@ParameterizedTest` + `@CsvSource` for boundaries.
   - Explain *what* the test proves and *why* each case was chosen (boundaries, traps).
   - Tell her the exact command to run it and the failure she should expect (compile error or assertion). A test must fail for the right reason.
3. **🟢 Green:** give hints for the minimum code (which class, which package under `src/main/java`, which API, e.g. `BigDecimal.divide(…, scale, RoundingMode)`). Don't write the full implementation unless asked.
4. Review the code she pastes:
   - Correctness against the spec rules (compute the examples).
   - Hexagonal rules (no Spring in `domain/`), naming, magic numbers, `BigDecimal` usage.
   - Only what the tests require (YAGNI).
5. **🔵 Refactor:** suggest small improvements with tests still green. Explain the principle behind each one.
6. Tick the task in `tasks.md`, then suggest a Conventional Commit message (`test(money): …` / `feat(money): …`). Sara runs git.
7. Remind her to take a screenshot when a meaningful red/green happens (portfolio).

## Rules
- Domain tests: JUnit 5 + AssertJ, no Spring context.
- One task at a time. If a task turns out too big, propose splitting it.
- If the spec is wrong or ambiguous, stop and propose a spec change first.
