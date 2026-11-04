# Dependency CVE Review — November 2026

**Date:** 2026-11-04
**Analyst:** researcher agent
**Scope:** Runtime dependencies of Nido backend (Spring Boot 4.1.2)

---

## Summary table

| CVE | Dependency | CVSS | Severity | Fixed in | Patch in minor line | Affects Nido |
|-----|-----------|------|----------|----------|---------------------|--------------|
| CVE-2026-19880 | ch.qos.logback:logback-classic 1.5.38 | 6.3 (v4) / 7.3 (v3) | Medium/High | 1.6.3 | No — no 1.5.x backport exists | NO (SiftingAppender not used) |
| CVE-2026-110879 | tools.jackson.core:jackson-databind 3.1.7 | 9.8 | Critical | 3.1.8 | Yes — 3.1.8 is in the 3.1.x line | NO (polymorphic typing not enabled) |
| CVE-2026-110879 | com.fasterxml.jackson.core:jackson-databind 2.21.7 | 9.8 | Critical | 2.21.8 | Yes — 2.21.8 is in the 2.21.x line | NO (polymorphic typing not enabled) |

---

## CVE-2026-19880 — logback-classic

**Affected version:** 1.5.38 (and all versions 0.9.14 – 1.6.2)
**GHSA:** GHSA-9mh8-hq67-v26g
**CVSS v4:** 6.3 (Medium) · `CVSS:4.0/AV:N/AC:L/AT:P/PR:N/UI:N/VC:L/VI:L/VA:L/SC:N/SI:N/SA:N`
**CVSS v3:** 7.3 (High) [reported by some scanners — the official OSV record uses CVSS v4 6.3]
**CWE:** CWE-22 (Improper Limitation of a Pathname to a Restricted Directory — Path Traversal)
**EPSS:** 0.33 % (26th percentile as of publication)

**Description:**
`MDCBasedDiscriminator` in `SiftingAppender` passes MDC values directly into `FileNamePattern` without sanitising path separators. An attacker who can control an MDC value (e.g., through an HTTP request header that the application stores in the MDC) can inject `../` sequences to write log files to arbitrary directories outside the configured log root. The initial fix in 1.6.3 stripped `/` and `\` from MDC values; 1.6.5 strengthened this further by rejecting values entirely when they contain `..`, variable references (`${}`), or other special characters.

**Exploitation conditions:**
All three of the following must be true simultaneously:

1. The application uses `SiftingAppender` in its Logback configuration.
2. That `SiftingAppender` is configured with an `MDCBasedDiscriminator`.
3. An attacker can influence the MDC value that the discriminator reads (e.g., a request-scoped MDC key populated from user-supplied input).

**Fixed in:** 1.6.3 (initial strip), 1.6.5 (complete rejection)
**Patch in 1.5.x line:** No. Version 1.5.38 is the last release in the 1.5.x branch. The logback team did not backport the fix; the Spring Boot team confirmed the same. The only remediation path is upgrading to 1.6.3 or later.

**Spring Boot context:**
Spring Boot 4.1.2 BOM manages `logback.version = 1.5.38`. Spring Boot will not backport this within the 4.1.x line. Spring Boot 4.2.0 will ship with Logback 1.6.5. The workaround for 4.1.x is to override the `logback.version` property in `build.gradle.kts`:

```kotlin
// build.gradle.kts — override inside the `extra` block or as a dependency constraint
extra["logback.version"] = "1.6.6"   // latest stable as of 2026-11-04
```

**Impact on Nido:** **NO.**
Nido does not use `SiftingAppender` or `MDCBasedDiscriminator`. The default Spring Boot logging configuration uses `ConsoleAppender` and `RollingFileAppender` with fixed file names. No MDC value is propagated from user-controlled input. The vulnerability is architecturally inactive in this codebase.

**Sources:**
- [GHSA-9mh8-hq67-v26g (GitHub Advisory)](https://github.com/advisories/GHSA-9mh8-hq67-v26g)
- [OSV — CVE-2026-19880](https://osv.dev/vulnerability/CVE-2026-19880)
- [Logback news / release notes](https://logback.qos.ch/news.html)
- [Spring Boot issue #52400 — Bump logback ≥ 1.6.3](https://github.com/spring-projects/spring-boot/issues/52400)
- [Vigilance — Logback file write via MDCBasedDiscriminator](https://vigilance.fr/vulnerability/Logback-file-write-via-MDCBasedDiscriminator-52344)

---

## CVE-2026-110879 — jackson-databind

**GHSA:** GHSA-m8xf-2cr7-77ph
**CVSS v3.1:** 9.8 (Critical) · `CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H`
**Published:** 2026-10-28 · **Modified:** 2026-10-30
**CWE:** CWE-502 (Deserialization of Untrusted Data)

**Description:**
This is an incomplete fix for CVE-2026-54512. `jackson-databind` validates the outer class of a class-name type ID against the configured `PolymorphicTypeValidator` (PTV), but it then accepts attacker-controlled classes embedded in three positions that bypass that check:

1. **Generic type arguments** — a type ID such as `Env<Gadget>` is accepted even when `Gadget` alone would be rejected by the PTV.
2. **Array element types** — an array type ID like `[Lpkg.Gadget;` bypasses the PTV when the array base type is on the allow-list.
3. **Validator substitution** — when an outer type is approved, the configured validator is effectively replaced with a permissive alternative for the type's internals.

With Spring on the classpath, this enables remote code execution via `ProcessBuilder.start()` triggered through `FileSystemXmlApplicationContext`. Stock JDK gadgets allow file creation/truncation and URL requests.

**Affected versions:**

| Range | Fixed in |
|-------|----------|
| 2.10.0 – 2.18.11 | 2.18.12 |
| 2.19.0 – 2.21.7 | 2.21.8 |
| 2.22.0 – 2.22.3 | 2.22.4 |
| 3.0.0 – 3.1.7 | 3.1.8 |
| 3.2.0 – 3.2.3 | 3.2.4 |

**Exploitation conditions:**
Exploitation requires **at least one** of the following to be active in the application:

- `@JsonTypeInfo(use = Id.CLASS)` on a type that is deserialized from untrusted input.
- `ObjectMapper.enableDefaultTyping(...)` or the equivalent `MapperBuilder.enableDefaultTyping(...)`.
- Spring Security's legacy Jackson module with default typing.
- Spring Data Redis convenience APIs that enable polymorphic typing on `RedisSerializer`.

Without polymorphic type handling enabled, deserialization of untrusted JSON does not trigger this code path at all.

---

### tools.jackson.core:jackson-databind 3.1.7

**Affected version:** 3.1.7
**Fixed in:** 3.1.8 (released 2026-10-17 or 2026-10-18)
**Patch in 3.1.x line:** **Yes** — 3.1.8 is a patch release within the same minor line.

**Spring Boot 4.1.2 context:**
Spring Boot 4.1.2 BOM manages `tools.jackson` at **3.1.7**. The fix is available one patch version above the managed version. The upgrade can be forced by adding a dependency constraint in `build.gradle.kts`:

```kotlin
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "tools.jackson.core" && requested.name == "jackson-databind") {
            useVersion("3.1.8")
            because("CVE-2026-110879")
        }
    }
}
```

**Impact on Nido:** **NO.**
Nido has no `@JsonTypeInfo` annotation on any type. It has no call to `enableDefaultTyping`. The API receives a flat JSON object with two `BigDecimal` fields — there is no polymorphic deserialization anywhere in the codebase. The vulnerability mechanism is architecturally inactive.

---

### com.fasterxml.jackson.core:jackson-databind 2.21.7

**Affected version:** 2.21.7
**Fixed in:** 2.21.8 (released 2026-10-17 or 2026-10-18)
**Patch in 2.21.x line:** **Yes** — 2.21.8 is a patch release within the same minor line.

**Why this version is present:**
Spring Boot 4.1.2 also manages `com.fasterxml.jackson` at 2.21.7. `springdoc-openapi-starter-webmvc-ui 3.2.0` pulls in Jackson 2 (swagger-core requires it), and after the fix for the springdoc/spring-boot BOM conflict (springdoc issue #3373), Spring Boot's BOM wins and the resolved version is 2.21.7. This jar is therefore present at runtime.

The upgrade can be forced:

```kotlin
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.fasterxml.jackson.core" && requested.name == "jackson-databind") {
            useVersion("2.21.8")
            because("CVE-2026-110879")
        }
    }
}
```

Alternatively, overriding the Spring Boot-managed property:

```kotlin
extra["jackson-bom.version"] = "2.21.8"
```

**Impact on Nido:** **NO.**
Same reasoning as for Jackson 3. No polymorphic typing is enabled. The Jackson 2 jar is used exclusively by springdoc for OpenAPI schema generation, not for deserializing untrusted user input.

---

## Recommendation

Both CVEs are **architecturally inactive** in Nido's current codebase. Neither vulnerability can be triggered under the described runtime conditions. That said, **upgrading is still recommended** as a defence-in-depth measure and to silence scanner alerts in future CI/CD pipelines.

### Priority actions

| Priority | Action | Effort |
|----------|--------|--------|
| Medium | Upgrade Jackson 3 to 3.1.8: add a resolution constraint for `tools.jackson.core:jackson-databind` | ~5 min |
| Medium | Upgrade Jackson 2 to 2.21.8: override `jackson-bom.version` or add a resolution constraint for `com.fasterxml.jackson.core:jackson-databind` | ~5 min |
| Low | Upgrade Logback to 1.6.6: override `logback.version` in `build.gradle.kts`. **Note:** 1.6.x has breaking changes vs 1.5.x (Janino conditional expressions removed in 1.5.37; API surface changes in 1.6.0). Validate that existing `logback-spring.xml` / `logback.xml` configurations still work after the jump. | ~30 min |
| Low | Add a CI dependency-audit step (e.g., `./gradlew dependencyCheckAnalyze` with OWASP Dependency-Check, or GitHub Dependabot) so CVEs are caught automatically going forward. | 1–2 h |

### What NOT to do

- Do not add `@JsonTypeInfo` or `enableDefaultTyping` to fix anything — there is nothing to fix in terms of exploitability, and doing so would actually *activate* the vulnerability.
- Do not replace Logback with Log4j2 or another framework; the exposure is too low to justify the migration cost.

### Notes on conflicting CVSS scores

CVSS v3 scores reported by scanners for CVE-2026-19880 range from 6.3 to 7.3. The advisory in OSV (which is sourced from the GitHub Advisory Database) uses CVSS v4 and reports 6.3 (Medium). Some commercial scanners report 7.3 (High) using their own CVSS v3 calculation. The primary source (GHSA-9mh8-hq67-v26g) should be treated as authoritative. The `AT:P` (Attack Requirements: Present) modifier in the CVSS v4 vector reflects that exploitation requires a specific SiftingAppender + MDCBasedDiscriminator configuration.

---

## Open questions

1. **[NEEDS CLARIFICATION]** Does any existing or planned Logback configuration in Nido use `SiftingAppender`? If yes, the logback upgrade to 1.6.6 becomes High priority.
2. **[NEEDS CLARIFICATION]** Will Spring Data Redis be added in a future sprint? If so, verify that the Redis serialiser configuration does not enable default typing.
3. Spring Boot 4.2.0 ships Logback 1.6.5 and presumably bumps Jackson. What is the planned timeline for adopting Spring Boot 4.2.x? This would resolve all three vulnerabilities without manual overrides.

---

## Sources

- [GHSA-9mh8-hq67-v26g — logback path traversal (GitHub Advisory)](https://github.com/advisories/GHSA-9mh8-hq67-v26g)
- [GHSA-m8xf-2cr7-77ph — jackson-databind PolymorphicTypeValidator bypass (GitHub Advisory)](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-m8xf-2cr7-77ph)
- [OSV CVE-2026-19880](https://osv.dev/vulnerability/CVE-2026-19880)
- [Logback news / changelog](https://logback.qos.ch/news.html)
- [Spring Boot issue #52400 — Bump logback ≥ 1.6.3](https://github.com/spring-projects/spring-boot/issues/52400)
- [Red Hat CVE-2026-110879](https://access.redhat.com/security/cve/cve-2026-110879)
- [Vert.x PR #325 — Jackson 2.21.8 and 3.1.8 fixing CVE-2026-110879](https://github.com/vert-x3/vertx-dependencies/pull/325)
- [springdoc-openapi issue #3373 — springdoc 3.1.1 lifts Jackson to 2.22.x in Gradle](https://github.com/springdoc/springdoc-openapi/issues/3373)
- [HeroDevs — CVE-2026-19880 directory](https://www.herodevs.com/vulnerability-directory/cve-2026-19880)
- [HeroDevs — CVE-2026-110879 directory](https://www.herodevs.com/vulnerability-directory/cve-2026-110879)
- [Vigilance — Logback file write via MDCBasedDiscriminator](https://vigilance.fr/vulnerability/Logback-file-write-via-MDCBasedDiscriminator-52344)
## Decision (2026-11-04)
- **Jackson (CVE-2026-110879, 9.8):** fixed by overriding Spring Boot's BOM to `3.1.8` / `2.21.8` in `build.gradle.kts`.
  Verified in the official release notes (jackson-databind #6287). Mend suggests 3.2.4 / 2.22.4 (latest), but the same-line patch is the lowest-risk fix.
  Not exploitable today anyway: we don't use polymorphic typing.
- **Logback (CVE-2026-19880, 6.3–7.3):** risk accepted. Requires an MDC-based `SiftingAppender` writing to files; Nido logs to the console only.
  Fixed in 1.6.3+; Spring Boot 4.2 ships 1.6.x. Revisit on that upgrade.
  ⚠️ Guardrail: never add a file appender whose path uses request data (headers, MDC) before upgrading.
- **Test-only libraries** (json-smart, android-json, asm): accepted. Not on the runtime classpath.