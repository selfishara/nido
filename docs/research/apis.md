# S0-3 · APIs & data sources map

- **Spike:** S0-3 · **Status:** Draft · **Date:** 2026-10-01
- **Question:** Which data can Nido use **legally and for free**, and how does that shape the MVP?

## TL;DR
- ✅ **Rent reference prices are available from official open data** (SERPAVI, INE, Incasòl/Generalitat). They are enough for the rent-to-income ratio and for the "abnormal price" scam signal.
- ✅ **Catastro** has a free JSON API with no key: it can check that the address in a listing actually exists and what kind of property it is.
- ⚠️ **Listing portals (Idealista, Fotocasa) are not a viable data source.** Idealista's API is restricted to approved partners, and scraping breaks their terms. → **The user pastes the listing**: no scraping, no dependency on a portal.
- ⚠️ **Grants data has no API.** It has to be modelled as rules from the official decree (RD 326/2026), and the rules must be versioned.

## Sources

### Rent prices

| Source | What it gives | Access | Format | Use in Nido |
|---|---|---|---|---|
| **SERPAVI** (Ministry of Housing) | Reference rent €/m² by area, built from tax data | Free download (web viewer + PDF/tables) | XLSX / PDF | Reference price per area → "abnormal price" rule (US-3.2) |
| **INE API JSON** (Tempus3) | Official series, e.g. rental reference index (IRAV), rental price index | Free, no key. `https://servicios.ine.es/wstempus/js/{lang}/{function}/{input}` | JSON / CSV | Price trends, contract-update caps |
| **Incasòl / Generalitat de Catalunya** | Average rent and number of contracts by municipality and **by Barcelona neighbourhood**, from the deposit register (near-census data) | Free, quarterly | XLSX / open data | Most accurate MVP data for Catalonia → start here |
| **Barcelona Open Data** | Average rent by district and neighbourhood | Free | CSV / API | Complements Incasòl for Barcelona |

### Property & location

| Source | What it gives | Access | Use in Nido |
|---|---|---|---|
| **Catastro, free web services (OVC)** | Non-protected cadastral data (use, built area, year built) by address or cadastral reference | Free, **no key**, JSON (`/json/`) and XML (`/rest/`) | Check that the address exists and that "80 m²" in the listing matches reality → scam signal |
| **CartoCiudad (IGN)** *(to verify)* | Official geocoding of Spanish addresses | Free | Address → coordinates → area |

### Listings

| Source | Status | Decision |
|---|---|---|
| **Idealista API** | Restricted to approved business partners; requires an application (developers.idealista.com, blocked from my research environment → **verify manually**) | Request access only as a "nice to have". **Not on the MVP's critical path** |
| Scraping portals | Breaks terms of service, legal risk, fragile | ❌ Discarded |
| **The user pastes the listing text or URL** | Under our control | ✅ MVP approach |

### Grants

| Source | Status |
|---|---|
| **RD 326/2026, State Housing Plan 2026-2030** (in force since April 2026) | Includes the youth rental grant. ⚠️ **Secondary sources give conflicting amounts and limits** (e.g. €250 vs €300 per month; rent caps €900 vs €1,000). → **Read the BOE text directly** before modelling it (part of US-6.1) |
| Regional calls (each autonomous community manages the application) | Different limits per region → rules engine with a configuration per region |

### AI & security services (for later epics)

| Need | Option | Note |
|---|---|---|
| Listing and contract analysis | Claude API | Paid per token → caching and per-user limits (ADR-0001) |
| Reused listing photos | Reverse image search APIs (e.g. Google Vision Web Detection, TinEye) | Paid → P2, research separately |
| Spanish tenancy law (LAU) text for RAG | BOE consolidated text | Public, free → EPIC-4 |

## Impact on the backlog
1. **US-3.1** changes to: *"paste listing text or URL"* (no automatic import from portals).
2. **New story · US-3.5:** check the listing address against Catastro (exists? use? surface area?).
3. **New story · US-2.6:** show the reference rent for the user's area next to their rent (start with Barcelona and Catalonia, using Incasòl data).
4. **New chore:** a data import job for reference prices (quarterly), with the source and date stored on each record.
5. **US-6.1:** model grants as **versioned rules**, quoting the BOE article.

## Lessons from this spike
- **Primary sources beat secondary ones.** Blogs and news sites contradicted each other on the grant amounts: always go to the BOE or the official dataset.
- "Is there an API?" is only half the question. The other half is **licence and terms of use**.
- Designing *around* a missing API (the user pastes the listing) is also an architecture decision → candidate for **ADR-0003**.

## Open questions
- [NEEDS CLARIFICATION] Licence and attribution terms of SERPAVI and Incasòl data for reuse in an app.
- [NEEDS CLARIFICATION] Is it worth applying for Idealista API access as a personal project?
- [NEEDS CLARIFICATION] Does CartoCiudad offer a REST geocoder with no key? (verify)

## Sources
- [SERPAVI, reference rent index viewer](https://serpavi.mivau.gob.es/)
- [SERPAVI 2026 publication](https://publicaciones.transportes.gob.es/serpavi-2026-sistema-estatal-de-referencia-del-precio-del-alquiler-de-vivienda)
- [INE API JSON, reference](https://www.ine.es/dyngs/DAB/index.htm?cid=1100)
- [INE, rental reference index (IRAV)](https://www.ine.es/uc/oC7D0Ncd)
- [Generalitat, rental market statistics](https://habitatge.gencat.cat/ca/dades/indicadors_estadistiques/estadistiques_de_construccio_i_mercat_immobiliari/mercat_de_lloguer/)
- [Generalitat, rents by Barcelona district and neighbourhood](https://habitatge.gencat.cat/ca/dades/indicadors_estadistiques/estadistiques_de_construccio_i_mercat_immobiliari/mercat_de_lloguer/lloguers-barcelona-per-districtes-i-barris/)
- [Idescat, housing rental statistics](https://www.idescat.cat/pub/?id=lh)
- [Barcelona data portal, average rent](https://portaldades.ajuntament.barcelona.cat/estad%C3%ADstiques/b37xv8wcjh)
- [Catastro, free web services (PDF)](https://www.catastro.hacienda.gob.es/ws/Webservices_Libres.pdf)
- [Catastro, free access services](https://www.catastro.hacienda.gob.es/ayuda/ayuda_cl.htm)
- [yagueto/idealista-api (describes access requirements)](https://github.com/yagueto/idealista-api)
- [RD 326/2026, State Housing Plan 2026-2030](https://noticias.juridicas.com/base_datos/Admin/996827-rd-326-2026-de-22-abr-regula-el-plan-estatal-de-vivienda-2026-2030.html)
- [Civio, key points of the State Housing Plan 2026-2030](https://civio.es/el-boe-nuestro-de-cada-dia/2026/04/23/claves-del-plan-estatal-de-vivienda-2026-2030/)
- [idealista/news, rental grants by region 2026](https://www.idealista.com/news/inmobiliario/vivienda/2026/08/25/816126-ayudas-al-alquiler-como-solicitarlas-y-listado-por-ccaa)
