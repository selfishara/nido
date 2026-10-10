# AGENTS.md

Instructions for any AI coding agent working on **Nido** (Claude Code, Codex, Cursor, Copilot…).

The full project instructions live in [`CLAUDE.md`](CLAUDE.md). Read it first: it is the single source of truth.
This file exists so that agents which look for `AGENTS.md` find the same rules.

## The five rules you must not break
1. **Spec before code.** Every P0/P1 story has `docs/specs/NNN-name/`. Never guess business rules.
2. **Sara runs git.** Do not commit, push, merge or create branches. Suggest the commands instead.
3. **Hexagonal architecture.** No Spring, JPA or Jackson imports in `domain/`.
4. **Security & privacy.** No secrets in the repo. No financial or personal data in URLs or logs. Anonymise PII before it reaches an LLM.
5. **Teach, don't just do.** This is a learning project: explain the *why*, prefer hints over full implementations.

## Where things are
| What | Where |
|---|---|
| Full instructions | `CLAUDE.md` |
| Development process (roles and review loop) | `docs/process.md` |
| What agents may and may not do | `docs/permissions.md` |
| Reusable skills | `.claude/skills/<name>/SKILL.md` |
| Specialised subagents | `.claude/agents/<name>.md` |
| Hooks (guardrails enforced outside the model) | `.claude/settings.json` + `.claude/hooks/` |
