# 🧱 `record` vs `class` (and `final`)

## Theory

### `record` (Java 16+)
A class **designed to carry immutable data**. From the header alone, Java generates:
- `private final` fields
- the canonical constructor (all fields)
- accessors: `amberFrom()`, `redAbove()` (no `get` prefix)
- `equals()`, `hashCode()` and `toString()` based on the data

```java
public record EffortThresholds(BigDecimal amberFrom, BigDecimal redAbove) { }
```
The same as a `class` would need ~30 lines.

**A record can have:** methods (behaviour that uses its data), `static` constants and factories, a **compact constructor** to validate invariants:
```java
public EffortThresholds {
    if (amberFrom.compareTo(redAbove) > 0) {
        throw new IllegalArgumentException("amberFrom must be <= redAbove");
    }
}
```
**A record cannot:** change its fields after creation, extend another class (it's implicitly `final`), declare extra instance fields, be a JPA entity (Hibernate needs a no-args constructor and mutable fields).

### `final`: a modifier, not a kind of class
`final` means *"cannot be changed/overridden from here on"*. What it blocks depends on where you put it:

| Where | Meaning | Example |
|---|---|---|
| Variable / field | Can't be **reassigned** | `final BigDecimal x = …; x = y; // ❌` |
| Method | Can't be **overridden** in a subclass | `public final void pay()` |
| Class | Can't be **extended** | `public final class String` |

⚠️ **`final` ≠ immutable.** A `final` field can't point to another object, but the object it points to can still change:
```java
final List<String> names = new ArrayList<>();
names.add("Sara");          // ✅ allowed: the list changes
names = new ArrayList<>();  // ❌ not allowed: reassignment
```
That's why records use immutable types inside (`BigDecimal`, `String`, `List.copyOf(...)`).

### Record vs final class vs class
| | `class` | `final class` | `record` |
|---|---|---|---|
| Can be extended | ✅ | ❌ | ❌ (implicitly final) |
| Fields reassignable | if not `final` | if not `final` | ❌ always `final` |
| Constructor, getters, equals/hashCode/toString | you write them | you write them | ✅ generated |
| Equality | by identity (unless you override `equals`) | idem | **by value** |
| Typical use | services, entities, stateful objects | utility/value classes before Java 16 | DTOs, value objects, results, config |

## Practice in Nido
- `RentRatio`, `EffortThresholds` → **records**: they *are* their data. Two thresholds 30/40 are the same thing.
- `RentRatioCalculator` → **class**: behaviour with no data of its own.
- Future JPA entities → **classes** (JPA requirement), mapped to/from domain records in `infrastructure/persistence`.
- Dependencies injected into services → `private final` fields (constructor injection).

**Key question:** *"Are two objects with the same values the same thing?"* Yes → `record`. No (identity matters, e.g. a user) → `class`.

**Pitfall seen in practice:** writing `public class X(…)` with a record header → compiler error *"Record header declared for non-record"* / *"'{' expected"*. The header `(…)` only exists in records.

## 🧠 My own words
> *When would you use a record, a final class and a normal class? What does `final` NOT guarantee?*
- record → when the object is just data, and two objects with the same values are the same thing (e.g. thresholds, DTOs, results).
- final class → when the object has behaviour, but you don't want it to be extended (utility classes, value objects).
- class → when the object has behaviour and identity matters (e.g. users, entities).
- final does NOT guarantee immutability; it only prevents reassignment of the variable or overriding of methods. The object itself can still be mutable if it contains mutable fields. 
- Final classes can not be extended/inherited from.
- Don't make Spring service classes `final`: Spring creates proxies (runtime subclasses) for features like `@Transactional`.
  