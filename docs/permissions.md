# Agent permissions and security notes

What AI agents may do in the Nido repo, what they may not do, and how each rule is enforced.
A rule written only in a prompt can be ignored by the model; where possible it is also enforced by a hook.

## Allowed

| Capability | Who | Notes |
|---|---|---|
| Read any file in the repo except secrets | All agents | Reviewers are limited to `Read, Grep, Glob, Bash` in their frontmatter |
| Edit code, tests and docs | Main session, when Sara asks | Teaching mode: hints first, full code only on request |
| Run Gradle tasks (`./gradlew test`, `build`, `bootRun`) | All agents | Needed to verify work |
| Run read-only git commands (`status`, `diff`, `log`, `show`, `branch`) | All agents | Needed for reviews |
| Web search and fetch | `researcher` subagent | Prefer primary sources |

## Not allowed

| Action | Why | Enforced by |
|---|---|---|
| `git commit`, `push`, `merge`, `rebase`, `reset --hard`, `checkout -b`, `switch -c`, `branch -d/-D` | Sara runs git herself (learning goal, and she reviews every change) | **Hook** `.claude/hooks/block-git-writes.sh` + `CLAUDE.md` |
| Reading or writing `.env` or other secret files | Secrets must never reach a model or the repo | **Hook** (same script) + `.gitignore` + `CLAUDE.md` |
| Adding dependencies, frameworks or modules a story doesn't need | Scope control | `CLAUDE.md` + `code-reviewer` |
| Sending personal or financial data to an LLM without anonymising it | GDPR and privacy | `CLAUDE.md` + `security-reviewer` |
| Scraping listing portals | Legal risk (`docs/research/apis.md`) | `CLAUDE.md` |

## Data sent to AI tools
- **Code and docs** of this public repo: may be sent to the coding agent.
- **Real user data** (salaries, IDs, payslips, contracts): never pasted into a coding session. Use synthetic test data.
- **In the app**, Claude API calls go through the `LlmClient` port, with PII anonymised first and output validated against a schema.

## MCP servers
List every MCP server the agents can use here, with its permissions.

| Server | Access | Status |
|---|---|---|
| _(to add)_ | | Planned for the Module 5 deliverable |
