# Java 25 Semantic Obfuscator / Deobfuscator (Production Spec)

---

# 🎯 GOAL

Build a **production-grade semantic Java obfuscator + deobfuscator** using **Java 25**.

The tool must:

* Obfuscate Java source code **semantically (human-readable but anonymized)**
* Fully support **reversible deobfuscation**
* Work on **real-world Spring Boot + JPA + Lombok projects**
* Preserve **runtime behavior 100%**
* Be **safe, deterministic, and non-destructive**

---

# ❗ CRITICAL DESIGN RULE

## 🚫 DO NOT use ProGuard-style names

❌ BAD:

```
com.foo.DeliveryLocation → a.b.c.X1
```

## ✅ REQUIRED STYLE (semantic naming)

✔ Class:

```
com.foo.DeliveryLocation → com.foo.SadGiraffe
```

✔ Method:

```
getCity → fetchGoldenStripe
```

✔ Field:

```
city → velvetLeaf
```

---

# 🧠 NAMING STRATEGY (MANDATORY)

## Class Names

```
Adjective + Animal
→ FlyingTiger, SadGiraffe, GoldenFalcon
```

## Method Names

```
Verb + Adjective + Object
→ fetchGoldenStripe, calculateSilentRiver
```

## Field Names

```
Adjective + Object
→ velvetLeaf, silverStone
```

---

## 🔁 Deterministic Generator

* Same input → same output
* Collision-safe
* Infinite combinations (no small word pool limit)
* Prefer combinatoric or Faker-based generation

---

# 🧠 CORE REQUIREMENTS

---

## 1. AST-Based Semantic Transformation

Use:

* JavaParser
* SymbolSolver

❌ NEVER use string replacement

---

## 2. Mapping System (REVERSIBLE)

Format:

```
<FQCN>#<memberName> → obfuscatedName
```

Example:

```
com.foo.DeliveryLocation            → SadGiraffe
com.foo.DeliveryLocation#city       → velvetLeaf
com.foo.DeliveryLocation#getCity    → fetchVelvetLeaf
```

Requirements:

* Deterministic
* Serializable (JSON/YAML)
* Bidirectional (supports reverse lookup)

---

## 3. Deobfuscation

Must:

* Read mapping file
* Restore ALL original names
* Work reliably after full obfuscation

---

# 🔥 CRITICAL FEATURES (REAL-WORLD BUG FIXES)

---

## ✅ 4. Deep Method Chains

Handle:

```
parent.getA().getB().getC().getValue()
```

Must:

* Resolve each step type
* Rename correctly at every level

---

## ✅ 5. Lambda Support (CRITICAL)

Handle:

```
list.stream()
    .map(x -> x.getCity())
```

Must:

* Infer lambda variable type from generics
* Rename calls inside lambda

---

## ✅ 6. Method References

Handle:

```
DeliveryLocation::getCity
```

Must resolve and rename correctly

---

## ✅ 7. Java Records (FULL SUPPORT)

Handle:

```
record User(String name, int age)
```

Must:

* Rename record components
* Ensure accessor consistency:

  ```
  name() → obfuscated
  ```
* Update constructor parameters

---

## ✅ 8. JPA / Hibernate (CRITICAL)

### mappedBy Fix

```
@OneToMany(mappedBy = "parent")
```

If field renamed → update string literal

---

### Also handle:

* `@JoinColumn(name = "...")`
* `@JoinTable(...)`

---

## ✅ 9. Lombok Support

Handle:

* `@Builder`
* `@SuperBuilder`
* `@Getter`
* `@Setter`

Must:

* Not break generated methods
* Keep builder compatibility
* Keep getter/setter mapping valid

---

## ✅ 10. Constructors

Handle ALL:

* public / private / protected
* overloaded
* record constructors

---

## ✅ 11. DTO / Entity Consistency

If a field is renamed:

```
city → velvetLeaf
```

Then update:

* getter → `getVelvetLeaf`
* setter
* constructor param
* all usages

---

# 🚫 DO NOT OBFUSCATE THESE

---

## Packages:

```
java.*
javax.*
jakarta.*
org.springframework.*
org.hibernate.*
```

---

# 🏗️ ARCHITECTURE

---

## Pipeline

```
Parse → Analyze → Generate Mapping → Transform → Write
```

---

## Pass-Based Design

1. Symbol resolution
2. Mapping generation
3. Rename transformation
4. Annotation fix
5. Deobfuscation

---

## Visitors

Use visitors for:

* MethodCallExpr
* MethodReferenceExpr
* FieldDeclaration
* RecordDeclaration
* AnnotationExpr

---

# 🔒 SAFETY RULES

---

## MUST:

* Wrap all symbol resolution in try/catch
* If resolution fails → SKIP

---

## Fallback Logic:

If method mapping not found:

→ fallback to getter → field mapping

---

# 📁 FILESYSTEM & EXECUTION RULES (CRITICAL)

---

## ❗ NEVER MODIFY ORIGINAL PROJECT

* ❌ No in-place changes
* ❌ No overwriting
* ❌ No partial updates

---

## ✅ OBFUSCATION MODE

Input:

```
/project
```

Output:

```
/project-obf
```

Rules:

* Copy project → `-obf`
* Perform ALL transformations inside `-obf`
* Keep original untouched

---

## ✅ DEOBFUSCATION MODE

Input:

```
/project-obf
```

Output:

```
/project-orj
```

Rules:

* Copy project → `-orj`
* Restore original names using mapping

---

## 🚫 IGNORE DIRECTORIES

```
.idea/
target/
build/
out/
node_modules/
.git/
```

---

## 📄 FILE FILTERING

Process ONLY:

```
*.java
```

Ignore:

```
*.class
*.jar
*.log
```

---

## 📦 MAPPING FILE

### During obfuscation:

```
project-obf/mapping.json
```

### During deobfuscation:

* Read mapping from obfuscated project

---

## ⚠️ FAIL-SAFE

If mapping file missing:

* ❌ DO NOT deobfuscate
* ✅ Fail with clear error

---

# ⚡ PERFORMANCE

* Cache resolved types
* Avoid repeated parsing
* Support large projects

---

# 🧪 TESTING

---

## Unit Tests

* method rename
* field rename
* record handling

---

## Integration Tests

* Spring Boot app
* JPA relations
* Lombok entities

---

## E2E

* obfuscate → compile → run
* deobfuscate → equals original

---

# 🚀 OPTIONAL (ADVANCED)

* Spring `@Query` rewriting
* MapStruct
* Jackson `@JsonProperty`

---

# 📌 FINAL INSTRUCTION

This is NOT a demo.

Generate:

* Clean architecture
* Modular visitors
* Fully working system

Handle ALL edge cases safely.
Do NOT simplify.
