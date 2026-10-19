# Nido as the AI Dev Tools Zoomcamp final project

Nido is Sara's final project for the [AI Dev Tools Zoomcamp 2026](https://github.com/DataTalksClub/ai-dev-tools-zoomcamp)
(DataTalks.Club, live cohort). The certificate depends only on the final project and the peer reviews.

## Deadlines
| Submission | Deadline (Europe/Madrid) | Plan |
|---|---|---|
| Project 1 | 2026-10-26, 23:00 | Optional, only if Nido is ready |
| Project 2 | 2026-11-16, 23:00 | **Target** |

After submitting: review the peer projects assigned on the [course platform](https://courses.datatalks.club/ai-dev-tools-2026/).
The 2026 project rules are still a draft: check the [project page](https://github.com/DataTalksClub/ai-dev-tools-zoomcamp/tree/main/project) before submitting.

## Scoring criteria and Nido status
Update the Status column as work is merged.

| # | Criterion (max points) | What it needs | Nido status |
|---|---|---|---|
| 1 | Problem description (2) | README explains the problem, functionality and expected behaviour | ✅ README |
| 2 | AI-assisted workflow (2) | Documented AI workflow: prompts or delegation, context files, manual review, verification | 🟡 CLAUDE.md, AGENTS.md, `docs/process.md`; add a short "How AI was used" section to the README |
| 3 | Technologies & architecture (2) | Frontend, backend, DB, containers and CI/CD described and how they fit | 🟡 ADR-0001; update when the web client exists |
| 4 | Frontend (3) | Functional, well structured, centralised API calls, tests | ❌ Angular client planned (ADR-0003) |
| 5 | API contract (2) | `openapi.yaml` that reflects frontend needs and drives the backend | ❌ |
| 6 | Backend (3) | Well structured, follows OpenAPI, tests | 🟡 Spring Boot hexagonal + TDD in progress |
| 7 | Database (2) | Integrated, supports environments, documented | 🟡 Postgres + Flyway planned |
| 8 | Containerisation (2) | Whole system runs with Docker Compose | 🟡 Compose runs Postgres only |
| 9 | Integration tests (2) | Separate from unit tests, cover key workflows, documented | 🟡 Testcontainers available |
| 10 | Deployment (2) | Public URL or clear proof | ❌ |
| 11 | CI/CD (2) | Runs tests and deploys when they pass | ❌ US-0.3 |
| 12 | Agent extension pack (2) | Instructions, skill, subagent, MCP tool/server, hook, permissions note | 🟡 Missing MCP server and plugin |
| 13 | Security, audit, DevOps hardening (2) | PR audit output, security scan findings, agent security notes, ops diagnosis, AI tool/data policy | 🟡 `security-reviewer`, `docs/permissions.md` |
| 14 | Reproducibility (2) | Clear setup, run, test and deploy instructions | 🟡 |

## Module 5: Agent Extension Pack
Minimum: 1 instructions file · 1 skill · 1 subagent · 1 MCP tool/server · 1 hook or guardrail · 1 plugin or custom agent · 1 permissions note.

| Piece | Nido |
|---|---|
| Instructions | ✅ `CLAUDE.md` + `AGENTS.md` |
| Skill | ✅ `.claude/skills/` (5) |
| Subagent | ✅ `.claude/agents/` (4) |
| Hook / guardrail | ✅ `.claude/hooks/block-git-writes.sh` |
| Permissions note | ✅ `docs/permissions.md` |
| MCP tool/server | ❌ Idea: read-only server exposing `list_specs`, `get_story`, `get_constitution` |
| Plugin / custom agent | ❌ Package skills + agents + hook as `nido-agent-pack` |

Demo for the module: the agent reads the instructions → a skill is invoked → a subagent reviews an API change →
the agent calls an MCP tool → a hook blocks or checks an action → Sara reviews the final diff.
Document it in `docs/agent-extension-pack.md`.

## Key ideas from the course (apply them)
- **Skills** for repeated procedures; create them by doing the task once with the agent and then saving it.
- **Subagents** for work that needs a fresh context or a specialised role (reviews never run in the implementer's context).
- **The main session orchestrates** the process in `docs/process.md`; it doesn't implement.
- **Rules that matter are enforced by code (hooks, CI), not only by prompts.**
- One fresh session per story to avoid context rot.
