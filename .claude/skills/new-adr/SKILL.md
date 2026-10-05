---
name: new-adr
description: Create a new Architecture Decision Record in docs/adr/. Use when an important or hard-to-reverse technical decision is made or needs to be made, or when Sara says "ADR", "document this decision".
---

# New ADR

## Steps
1. List `docs/adr/` and take the next number (4 digits). Check the ADR numbering note in CLAUDE.md ("Current state").
2. Create `docs/adr/NNNN-short-title.md` with this structure:
   ```markdown
   # ADR-NNNN · <Title>

   - **Status:** Proposed | Accepted | Superseded by ADR-XXXX
   - **Date:** YYYY-MM-DD

   ## Context
   <The problem or situation that forces a decision. Facts, constraints.>

   ## Decision
   <What we chose. A table if there are several parts.>

   ## Alternatives considered
   - **<Option>**: why it was rejected.

   ## Consequences
   - ➕ <benefit>
   - ➖ <cost, risk, or limitation> (always include at least one)
   ```
3. If it replaces an earlier decision, **don't edit the old ADR's content**: only change its status line to `Superseded by ADR-NNNN` (or `Partially superseded`).
4. Update references: README, CLAUDE.md (stack table, ADR numbering), other ADRs that mention the topic.
5. Explain the decision to Sara in Spanish, including the trade-offs, and suggest the git commands.

## Rules
- One decision per ADR. Short (one screen if possible).
- Write it so that someone reading it a year later understands **why**.
