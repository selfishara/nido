# CLAUDE.md

Context for AI assistants (Claude Code and others) working on **Nido**. Read this first, then follow the links. Keep it short and current: if something here is wrong, fix it in the same PR.

## What Nido is
A mobile + web app that helps young people in Spain **rent a flat without getting scammed** and **understand their money** (rent-to-income ratio, savings, payslips). Solo portfolio project by Sara (backend dev), built like a professional team project: Scrum, Spec-Driven Development, TDD, ADRs, reviewed PRs.

Nido is also Sara's **final project for the AI Dev Tools Zoomcamp 2026** (target deadline 2026-11-16). Requirements, scoring and status: [`docs/course-ai-dev-tools.md`](docs/course-ai-dev-tools.md). When planning work, prefer tasks that close a gap in that table.

## Working with Sara (read this)
- **This is a learning project.** Explain the *why* behind every non-trivial change, and name the concept (pattern, principle, security issue) so she can look it up.
- **Sara runs git herself.** Don't commit, push, merge or create branches unless she explicitly asks. Suggest the commands instead.
- **Chat in Spanish; everything in the repo in English** (code, comments, docs, commits, PRs).
- **Prefer guiding over writing.** For domain code, propose the test and give hints; write the implementation only when she asks.
- She works on **Windows + Git Bash + IntelliJ IDEA**. Give commands for Git Bash (`./gradlew`, forward slashes).

## Stack
| Layer | Tech | Version / status |
|---|---|---|
| Language | Java | **25** (LTS), via Gradle toolchain |
| Framework | Spring Boot | **4.1.x** (web MVC, data JPA, validation, actuator) |
| Build | Gradle (Kotlin DSL) + wrapper | **9.7.x** — always use `./gradlew`, never a global `gradle` |
| Database | PostgreSQL | **18** (pin it; no `:latest`) · pgvector planned for RAG |
| Migrations | Flyway | No migrations yet |
| Tests | JUnit 5 · AssertJ · Testcontainers · Spring Boot test slices | ✅ |
| Local infra | Docker Compose (`compose.yaml`) via Spring Boot Docker Compose support | ✅ |
| Web client | Angular | Planned (ADR-0003 pending) |
| Mobile | Kotlin Multiplatform (Android) | Planned |
| AI | Claude API behind an `LlmClient` port | Planned (EPIC-3) |
| Auth | OAuth2/OIDC + PKCE | Planned (spike S0-4 → ADR-0004) |
| CI | GitHub Actions | Planned (US-0.3) |

## Repo map & key paths
```
nido/
├── CLAUDE.md                       ← you are here
├── compose.yaml                    Local Postgres (Spring starts it automatically on bootRun)
├── .claude/
│   ├── skills/<name>/SKILL.md      Project skills (see "AI tooling")
│   └── agents/<name>.md            Project subagents
├── backend/
│   ├── build.gradle.kts            Dependencies & toolchain
│   └── src/
│       ├── main/java/com/nido/backend/
│       │   ├── BackendApplication.java
│       │   └── <module>/{domain,application,infrastructure}/
│       ├── main/resources/
│       │   ├── application.properties
│       │   ├── db/migration/       Flyway scripts: V1__description.sql …
│       │   └── prompts/            (planned) versioned LLM prompts
│       └── test/java/com/nido/backend/<module>/…   Mirrors main/ packages
├── app/                            (planned) KMP Android app — MVVM
├── web/                            (planned) Angular client
└── docs/
    ├── specs/constitution.md       Non-negotiable principles
    ├── specs/NNN-name/             spec.md · plan.md · tasks.md per feature
    ├── specs/_template/            Copy for new specs
    ├── adr/NNNN-title.md           Architecture Decision Records
    ├── research/                   Spike outputs (apis.md, scams.md)
    ├── learning/                   Sara's theory notes (she writes "My own words")
    ├── backlog.md                  Epics + user stories
    └── process-log.md              Project diary (+ LinkedIn drafts)
```

## Commands
Backend commands run from `backend/`. Docker Desktop must be running.
| Task | Command |
|---|---|
| Run the API (starts Postgres) | `./gradlew bootRun` |
| Health check | `curl http://localhost:8080/actuator/health` → `{"status":"UP"}` |
| All tests | `./gradlew test` |
| One test class | `./gradlew test --tests "*RentRatioCalculatorTest"` |
| Test report | open `backend/build/reports/tests/test/index.html` |
| Clean build | `./gradlew clean build` |
| Stop Gradle daemons (after JDK changes) | `./gradlew --stop` |
| List JDKs Gradle sees | `./gradlew -q javaToolchains` |
| Postgres up/down manually | `docker compose up -d` / `docker compose down` (repo root) |

Git routine (Sara types these):
```bash
git switch main && git pull                 # always before a new branch
git switch -c feat/US-x.y-short-name
git status && git diff                      # before every commit
git add <paths> && git commit -m "type(scope): message"
git push -u origin <branch>                 # then open a PR; don't merge before review
```

## Architecture (must follow)
- **Hexagonal, feature-first.** Root package `com.nido.backend`. One package per business module (`money`, `scam`, `account`, `ai`…), each with:
  - `domain/` → `model/` (records, value objects), `service/` (domain services), `port/` (interfaces), `exception/`. **Plain Java: no Spring, JPA or Jackson imports.**
  - `application/` → use cases: `port/in/` (input ports) + services implementing them.
  - `infrastructure/` → adapters: `web/` (controllers, request/response DTOs), `persistence/` (JPA entities, repositories), `config/` (Spring wiring, `@ConfigurationProperties`), external clients.
- **Dependencies point inwards:** `infrastructure → application → domain`. Never the other way.
- **Create packages only when a story needs them** (YAGNI). No empty module skeletons.
- `shared/` only for real cross-cutting code. No `utils` dumping ground.
- AI code lives in the `ai` module behind a port (`LlmClient`). Business modules never call an LLM SDK directly. Prompts are versioned files in `src/main/resources/prompts/`.

### Clients (planned)
- **Android app (KMP): MVVM + clean layers.** `presentation/` (Compose screens + ViewModels exposing an immutable UI state via `StateFlow`), `domain/` (use cases, models), `data/` (repositories, Ktor API client). No business rules in ViewModels or screens: the backend is the source of truth for rules like the rent-ratio traffic light.
- **Web (Angular):** standalone components + services; components render state (signals), services call the API. Same rule: no business logic duplicated in the client.
- Background: `docs/learning/mvvm-vs-hexagonal.md`.

## Workflow (must follow)
0. Follow [`docs/process.md`](docs/process.md) (PM → Engineer → QA loop, PASS/FAIL) and the limits in [`docs/permissions.md`](docs/permissions.md).
1. **Spec before code.** Every P0/P1 story has `docs/specs/NNN-name/{spec,plan,tasks}.md`. Read it and the constitution before coding. Unclear requirement → `[NEEDS CLARIFICATION]` and ask; never guess business rules.
2. **TDD in the domain:** red → green → refactor. Each acceptance criterion (AC) maps to ≥1 test, named after it: `ac1_…`.
3. **One topic per branch and PR.** Branches: `feat/US-x.y-name`, `fix/…`, `docs/…`, `spike/S0-x-…`, `chore/…`. Branch from an up-to-date `main`.
4. **Conventional Commits**, imperative: `feat(money): …`, `fix(docs): …`, `test(…)`, `refactor(…)`, `chore(…)`, `ci(…)`.
5. Important or hard-to-reverse decisions → **new ADR** (never rewrite an accepted ADR; supersede it).
6. Renaming or moving anything (files, story IDs) → **grep for references** and update them in the same PR.

## Code style
- **Formatting:** follow the existing style (Spring Initializr defaults: tabs in Java). **TBD:** adopt an automatic formatter (Spotless) in US-0.3; update this section then.
- Java 25: `record` for immutable data (DTOs, value objects, results, config); `class` for services, controllers and JPA entities. `private final` fields + constructor injection (no field `@Autowired`). See `docs/learning/record-vs-class.md`.
- **Money = `BigDecimal`, never `double`/`float`.** Explicit `RoundingMode`. Avoid double rounding (multiply before dividing).
- No magic numbers: named constants or `@ConfigurationProperties`.
- Names: classes `PascalCase` nouns (`RentRatioCalculator`), use cases `VerbNounUseCase`, tests `<Class>Test`, test methods `acN_whatItChecks`.
- Comments explain **why**, not what. Javadoc on public domain classes referencing the spec rule (e.g. "spec 001, R2").
- Tests: JUnit 5 + AssertJ. `@ParameterizedTest` for boundaries. Domain tests have **no Spring context**. Web: `@WebMvcTest`. Testcontainers only for persistence/integration.
- REST: `/api/v1/<module>/…`. Errors as Problem Details (RFC 9457). Validate at the edge (Bean Validation) **and** invariants in the domain.
- Database: schema only through Flyway migrations (`V<n>__<description>.sql`); never `ddl-auto=update`. `spring.jpa.open-in-view=false`.

## Security & privacy (non-negotiable)
- **Never commit secrets.** Env vars only; document them in `.env.example`. `.env` is gitignored.
- Endpoints are **authenticated by default**. A public endpoint needs an explicit, justified exception in its spec.
- **Financial or personal data never in URLs or logs** (use request bodies; don't log amounts, IDs, payslips).
- Minimise data; don't store documents unless the user opts in. **Anonymise PII before anything reaches an LLM.**
- User text sent to an LLM (listings, contracts) is **untrusted**: defend against prompt injection; validate LLM output against a schema.
- **AI is advisory:** never a legal or financial verdict. Scam detection = risk indicators with reasons.
- **Fairness:** language quality, names or nationality are never scam signals.
- Pin versions of images and dependencies (no `:latest`).

## Don'ts
- ❌ Don't add frameworks, modules or dependencies a story doesn't need.
- ❌ Don't put Spring/JPA annotations in `domain/`.
- ❌ Don't skip, disable or weaken tests to get green.
- ❌ Don't scrape listing portals (Idealista, Fotocasa): legal risk, see `docs/research/apis.md`.
- ❌ Don't invent figures, legal rules or grant amounts: cite official sources (BOE, INE, ministries) or mark them unverified.
- ❌ Don't force-push to `main` or rewrite shared history.

## AI tooling in this repo
**Skills** (`.claude/skills/`, invoke with `/name`):
| Skill | Use it to |
|---|---|
| `/new-spec` | Create `docs/specs/NNN-name/` from the template, with clarification questions first |
| `/tdd-cycle` | Walk through one task of a spec's `tasks.md` in red → green → refactor, teaching mode |
| `/new-adr` | Create the next numbered ADR |
| `/pr-description` | Draft the PR description from the current branch diff |
| `/log-entry` | Add a process-log entry + a LinkedIn post draft |

**Subagents** (`.claude/agents/`, delegated automatically or by asking "use the X agent"):
| Agent | Role |
|---|---|
| `code-reviewer` | Reviews a diff against this file and the constitution (architecture, conventions, tests) |
| `security-reviewer` | OWASP Top 10 + OWASP LLM Top 10, secrets, PII, privacy |
| `spec-checker` | Checks that every acceptance criterion of a spec has tests and code |
| `researcher` | Spike research with primary sources → `docs/research/` format |

**Hooks** (`.claude/settings.json` + `.claude/hooks/`):
| Hook | What it enforces |
|---|---|
| `block-git-writes.sh` (PreToolUse) | Blocks git commands that change history or branches, and any access to `.env` files |

## Current state (update every sprint)
- **Sprint 1 · Walking skeleton.** Backend boots with Postgres ✅.
- **In progress:** spec 001 / US-2.3 rent-to-income ratio (TDD in `money.domain`): T2 ✅ ratio · T3 ✅ traffic light · next T4.
- **Next:** ADR-0002 package structure · CI (US-0.3) · ADR-0003 Angular web client · KMP Android app.
- **Sprint 2:** spike S0-4 identity provider → ADR-0004 → OAuth2/OIDC login.
- **ADR numbering:** 0001 tech stack · 0002 package structure · 0003 web client · 0004 identity provider.
