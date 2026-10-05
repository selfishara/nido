---
name: new-spec
description: Create a new Spec-Driven Development spec (spec.md, plan.md, tasks.md) for a user story in docs/specs/. Use when Sara wants to start a new feature or story, or says "new spec", "write the spec for US-x.y".
---

# New spec (SDD)

Goal: turn a backlog story into an approved spec **before** any code is written.

## Steps
1. Identify the story ID (e.g. US-2.4) and read it in `docs/backlog.md`.
2. Read `docs/specs/constitution.md` and `docs/specs/README.md` (to get the next free number `NNN`).
3. Read related research in `docs/research/` if the story touches scams, APIs or data sources.
4. **Clarify first.** List the business decisions the story leaves open (inputs, rules, edge cases, privacy, auth). Ask Sara with 2–4 concrete options each and a recommendation. **Do not guess business rules.**
5. Create `docs/specs/NNN-short-name/` by copying `docs/specs/_template/`, then fill in:
   - `spec.md`: what & why only (no tech). Acceptance criteria as **Given / When / Then**, with concrete numbers. Business rules numbered R1…Rn. Edge cases. Security & privacy. Out of scope.
   - `plan.md`: packages following the hexagonal layout in CLAUDE.md, API contract, STRIDE table, test strategy, ADR needed?
   - `tasks.md`: small ordered tasks, one per AC where possible, each marked 🔴/🟢 for TDD. First task: commit the spec.
6. **Self-check the rules with numbers:** compute every example in the ACs and look for traps (rounding, boundaries, empty or extreme values). Fix the spec if an example is wrong.
7. Add a row to the table in `docs/specs/README.md` with status `Draft`.
8. Explain to Sara (in Spanish) the key decisions and anything tricky. Suggest the git commands; don't run them.

## Rules
- Repo content in English; chat in Spanish.
- Every public endpoint is an explicit, justified exception (constitution, principle 3).
- Financial/personal data: never in URLs or logs.
