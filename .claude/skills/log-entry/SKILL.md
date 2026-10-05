---
name: log-entry
description: Add an entry to docs/process-log.md summarising a work session, plus a LinkedIn post draft. Use at the end of a session, a sprint, or when Sara says "log", "diary", "LinkedIn post".
---

# Process log entry

## Steps
1. Gather what happened: `git log --oneline` since the last log entry, merged PRs, specs/ADRs created, problems solved.
2. Ask Sara (in Spanish) for 1–2 things she learned or found hard, in her own words. Use her words; don't invent feelings or opinions.
3. Append to `docs/process-log.md` (English):
   ```markdown
   ## YYYY-MM-DD · <short title>

   **Done:** <bullets, with links to PRs/specs/ADRs>
   **Decisions:** <bullets, link ADRs>
   **Problems & fixes:** <what broke, root cause, fix>
   **Learned:** <her words>
   **Next:** <next step>

   📸 *Screenshots:* `screenshots/process/<files>`

   > 💬 **LinkedIn draft:** <3–6 lines, first person, concrete, one technical insight, no hype, no invented metrics>
   ```
4. Suggest which screenshots to add and the git commands.
