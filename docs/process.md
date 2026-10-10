# Development process

How a story goes from the backlog to `main` in Nido, and which human or agent does each step.
Linked from `CLAUDE.md` and `AGENTS.md`. Agents must follow it.

## Roles

| Role | Who | Tool | Output |
|---|---|---|---|
| **PM** | Agent + Sara | `/new-spec` skill | `docs/specs/NNN-name/{spec,plan,tasks}.md`, status `Draft` → `Approved` by Sara |
| **Engineer** | **Sara** (learning project) | `/tdd-cycle` skill, one task at a time | Tests + code on a `feat/US-x.y-…` branch |
| **QA** | Subagents, each in a **fresh context** | `spec-checker`, `code-reviewer`, `security-reviewer` | A report ending in a verdict |
| **Release** | Agent + Sara | `/pr-description`, then `/log-entry` | PR text, process-log entry |

Why QA runs in subagents: a reviewer that did not see the implementation is not biased by it.
The engineer's session history never reaches the QA session.

## The loop

```text
backlog story
   │
   ▼
PM ── /new-spec ──► spec Draft ──► Sara approves ──► spec Approved
                                                         │
                                                         ▼
                                     Engineer (Sara) ── /tdd-cycle per task
                                                         │
                                                         ▼
                        QA: spec-checker ─► code-reviewer ─► security-reviewer*
                                                         │
                                 FAIL ◄──────────────────┤
                         (back to Engineer               │ PASS
                          with the findings)             ▼
                                              /pr-description ─► PR ─► merge (Sara)
                                                         │
                                                         ▼
                                                    /log-entry
```

\* `security-reviewer` is required when the change touches endpoints, authentication, personal or
financial data, file uploads, external APIs or LLM calls. Otherwise it is optional.

## Verdicts

Each QA agent ends its report with one verdict. The orchestrating session maps them like this:

| Agent says | Meaning |
|---|---|
| *Aprobado* / all ACs ✅ / no 🔴 or 🟠 findings | **PASS** |
| *Aprobado con cambios menores* | **PASS** after Sara fixes the 🟡 items or records why not |
| *Cambios necesarios* / any ❌ AC / any 🔴 or 🟠 finding | **FAIL** → back to the Engineer step |

A story is done only when every required QA agent returns PASS.

## Sessions and context
- **One fresh session per story** (and per QA run). Long sessions forget the beginning ("context rot").
- Start a story with: `Work on US-x.y following docs/process.md.`
- Run QA with: `Run QA for US-x.y following docs/process.md.` The main session acts as the orchestrator:
  it launches the QA subagents in order and reports PASS or FAIL. It does not fix code itself.

## Creating or improving skills
1. Do the task once with the agent and correct it where it goes wrong.
2. At the end, ask: *"Save this as a project skill in `.claude/skills/`. Include the corrections I made. Do not create a global skill."*
3. When a skill needs a correction later, fix it the same way at the end of that session.

## Not yet
- **Parallel agents in git worktrees.** Useful when several independent stories can run at once.
  Not used while Sara implements and runs git herself.
