# ADR-0005 · MCP server for the agent pack

- **Status:** Accepted
- **Date:** 2026-10-10

## Context

The AI Dev Tools Zoomcamp final project (criterion 12) requires at least one MCP tool/server
as part of the agent extension pack. The server must let an AI assistant query project
artefacts (specs, stories, the constitution) without file-system access, so the interaction
is auditable and the assistant never edits docs by accident.

The server must:
- Expose three read-only tools: `list_specs`, `get_story(id)`, `get_constitution`.
- Communicate over **stdio** so Claude Desktop / Claude Code can wire it via `.mcp.json`
  without running a separate HTTP service.
- Live in its own Gradle module (`mcp-server/`) to keep it independently runnable and
  testable, with no circular dependency on `backend/`.

Stack constraints: Java 25, Gradle 9.7.x, Spring Boot 4.1.x already in use.

## Decision

Build the MCP server as a **separate Gradle subproject** (`mcp-server/`) using:

| Piece | Choice |
|---|---|
| Framework | Spring AI 2.0.1 (GA, released 2026-08-21) |
| Starter | `org.springframework.ai:spring-ai-starter-mcp-server` (BOM `spring-ai-bom:2.0.1`) |
| Transport | stdio (`spring.ai.mcp.server.stdio=true`) |
| Tools | Plain `@Tool`-annotated methods reading `docs/` via `java.nio.file` |
| Wiring | `.mcp.json` at repo root pointing to the assembled JAR |

The three tools are read-only: they only traverse `docs/specs/` and read `.md` files.
No write access, no network calls, no Spring Security needed (process boundary is the
security boundary for stdio).

## Alternatives considered

- **Python + FastMCP (as in the course):** No new language in the repo; the course uses Python
  as a teaching example, not as a requirement. Keeping everything in Java avoids a second
  toolchain and runtime in CI.
- **Java MCP SDK without Spring (`io.modelcontextprotocol.sdk:mcp`):** Lower-level; more
  boilerplate for tool registration and JSON serialisation. Spring AI's `@Tool` annotation
  and auto-configuration give the same result with significantly less code, and the BOM
  already manages versions. Rejected on complexity grounds.
- **Embed the MCP server inside `backend/`:** Mixes concerns and couples the server's
  classpath to the full web stack. A separate module keeps the fat JAR small and runnable
  without Docker Compose.

## Consequences

- ➕ One language and one build system across the whole repo.
- ➕ Spring AI auto-configuration handles stdio wiring; no manual transport code.
- ➕ Independent Gradle module: can be built and tested without starting Postgres.
- ➖ Adds Spring AI BOM to the dependency graph; introduces an extra fat JAR artefact.
- ➖ `mcp-server/` must be kept in sync with `docs/` path conventions (if specs move, the
  tools break). Mitigate: paths configured via `@ConfigurationProperties`, not hardcoded.