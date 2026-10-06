# 🏛️ MVVM vs Hexagonal architecture

## Theory
They are **not alternatives**: they solve different problems at different levels of the system.

| | **MVVM** (Model-View-ViewModel) | **Hexagonal** (Ports & Adapters) |
|---|---|---|
| Organises | The **user interface** | The **whole application** around its business logic |
| Where | **Frontend**: Android/KMP, Angular, iOS… | Mainly **backend** (the idea also applies to frontends) |
| Core piece | **ViewModel**: holds the screen state; the View observes it | **Domain**: pure business rules, surrounded by ports and adapters |
| Question it answers | *"How does the screen update when data changes?"* | *"How do I change database, framework or AI provider without touching the business rules?"* |
| Dependency rule | View → ViewModel → Model (the ViewModel doesn't know the View) | Adapters → application → domain (the domain knows nothing outside it) |

### MVVM in one picture
```
View (Compose screen) ──events──▶ ViewModel ──calls──▶ Repository / use case
        ▲                              │
        └──── observes UI state ◀──────┘   (StateFlow / signals)
```
- **View**: draws the state and sends user events. No logic.
- **ViewModel**: turns events into actions, exposes an immutable **UI state**, survives screen rotation (Android).
- **Model**: data and business access (repositories, use cases, API clients).

### Hexagonal in one picture
```
[web adapter] ─▶ [input port / use case] ─▶ [DOMAIN] ◀─ [output port] ◀─ [persistence / LLM / API adapters]
```
The domain defines interfaces (ports); infrastructure implements them (adapters).

**There is no MVVM in a backend**: there's no screen observing state. Requests come in, responses go out.

## Practice in Nido
```
Android app (KMP)            Web (Angular)                    Backend (Spring Boot)
─────────────────            ─────────────                    ─────────────────────
Compose screens              Components                       infrastructure/web
   ↕ UI state                   ↕ signals                          ↓
ViewModel  ← MVVM            Services                         application (use cases)
   ↓                            ↓                                  ↓
Repository → Ktor client ──HTTPS──▶ ◀── HttpClient ────────▶  domain   ← hexagonal
                                                                  ↑
                                                         infrastructure/persistence
```
- **Backend → hexagonal** (`domain` / `application` / `infrastructure`).
- **KMP app → MVVM + clean layers** (`presentation` / `domain` / `data`), like SongSwipe.
- **Angular → components + services** (MVVM-like: the component renders state, services talk to the API).

💼 Interview one-liner: *"MVVM is a presentation pattern; hexagonal is an application architecture. Nido uses hexagonal in the backend and MVVM in the mobile client, connected through a REST API."*

## 🧠 My own words
> *Why can't a Spring backend "be MVVM"? Where would a business rule like the rent-ratio traffic light live, and why not in the ViewModel?*
