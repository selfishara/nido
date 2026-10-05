---
name: pr-description
description: Draft a pull request title and description from the current branch's changes. Use when Sara is about to open a PR or asks "write the PR description".
---

# PR description

## Steps
1. Run `git log --oneline main..HEAD` and `git diff main...HEAD --stat`, then read the relevant parts of the diff.
2. Check the branch contains **one topic**. If it mixes topics, say so before writing anything.
3. Find the related story/spec (branch name, commits, `docs/specs/`).
4. Draft (in English):
   ```markdown
   <type>(<scope>): <summary>          ← PR title, Conventional Commits style

   ## What
   <1–3 sentences: what changes and why. Link the spec/story.>

   ## How to test
   1. <exact commands, e.g. `cd backend && ./gradlew test`>
   2. <manual check, e.g. curl with expected output>

   ## Notes
   - <decisions, trade-offs, follow-ups, anything reviewers should know>

   ## Checklist
   - [ ] Tests added/updated and green
   - [ ] Spec/ADR/docs updated (incl. CLAUDE.md "Current state" if relevant)
   - [ ] No secrets, no PII in logs or URLs
   ```
5. Show it to Sara; she creates the PR herself.
