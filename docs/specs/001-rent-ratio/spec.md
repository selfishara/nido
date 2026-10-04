# Spec 001 · Rent-to-income ratio

- **Story:** US-2.3 · **Epic:** EPIC-2 My money · **Status:** Draft
- **Author:** Sara · **Date:** 2026-10-03

## 1. Why
Young people looking for a flat don't know whether a rent is "too much" for their salary. A single, clear number with a traffic light (and the maximum rent that would be healthy) helps them decide **before** committing to a flat.

## 2. User stories
- As a user, I want to see what percentage of my net monthly income my share of the rent would take, with a traffic light, so that I decide with data.
- As a user, I want to know the maximum rent that keeps me in the green zone, so that I know what to look for.

## 3. Acceptance criteria
- **AC1 · Ratio**
  Given a net monthly income of €1,500 and a rent share of €450,
  when I calculate the ratio,
  then I get **30.0%**.
- **AC2 · Traffic light**
  Given a calculated ratio, the level is:
  **GREEN** if < 30% · **AMBER** if ≥ 30% and ≤ 40% · **RED** if > 40%.
  (Examples: 29.9 → GREEN · 30.0 → AMBER · 40.0 → AMBER · 40.1 → RED.)
- **AC3 · Rent above income**
  Given a rent share greater than the income,
  when I calculate the ratio,
  then I get the real ratio (e.g. 120.0%), level RED, and a flag saying the rent exceeds the income.
- **AC4 · Healthy maximum**
  Given a net monthly income of €1,500,
  when I calculate the ratio,
  then I also get the maximum rent for the green zone: **€449** (see rule R4).
- **AC5 · Invalid input**
  Given an income or rent share that is missing, zero, negative or has more than 2 decimals,
  when I calculate the ratio,
  then I get a clear validation error and no result.

## 4. Business rules
- **R1** `ratio = rentShare / netMonthlyIncome × 100`
- **R2** The ratio is rounded **half-up to 1 decimal**. The traffic light is decided on the **rounded** value (what the user sees must match the colour).
- **R3** Thresholds are system configuration (defaults: amber from 30, red above 40), **not** user input.
- **R4** Healthy maximum = the largest **whole euro** amount whose ratio, **rounded per R2**, is still GREEN. Examples: €1,500 → €449 · €1,000 → €299 · €2,000 → **€598** (not €599: 599 / 2000 = 29.95% → rounds to 30.0 → AMBER). ⚠️ A naive `income × 0.30` formula gets this wrong: the tests must catch it.
- **R5** Only rent is considered (no bills: those belong to US-2.5). Income is **net monthly**. Currency: EUR only.
- **R6** In a shared flat, the user enters **their own share** of the rent.

## 5. Edge cases
- Very small ratios (€1 rent, €3,000 income) → 0.0%, GREEN.
- Very large amounts: income and rent up to €1,000,000 are accepted; above that → validation error.
- Values with 3+ decimals (e.g. 450.555) → validation error (no silent rounding of money).

## 6. Security & privacy
- **Exception to constitution principle 3:** the endpoint is **public** until authentication exists (Sprint 2). Justification: it is a stateless calculation that stores nothing. To revisit in EPIC-1 (and rate-limited in US-1.4).
- Amounts are financial data: **never in URLs** (they end up in logs and browser history) and **never written to logs**.
- Nothing is persisted.

## 7. Out of scope
- Saving incomes or rents (US-2.1, US-2.2).
- Bills and full cash flow (US-2.5).
- Comparison with the area's reference rent (US-2.6).
- User-defined thresholds.

## 8. Open questions
- *(none: clarified on 2026-10-03, see decisions above)*
