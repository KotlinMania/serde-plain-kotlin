# serde-plain-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fserde-plain-kotlin-blue.svg)](https://github.com/KotlinMania/serde-plain-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/serde-plain-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/serde-plain-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/serde-plain-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/serde-plain-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`mitsuhiko/serde-plain`](https://github.com/mitsuhiko/serde-plain) (crate `serde_plain`).

**Original Project:** This port is based on [`mitsuhiko/serde-plain`](https://github.com/mitsuhiko/serde-plain). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `mitsuhiko/serde-plain`

> The text below is reproduced from [`https://github.com/mitsuhiko/serde-plain`](https://github.com/mitsuhiko/serde-plain). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs where appropriate so they continue to resolve from this repository.

# Serde Plain

This crate implements a plain text serializer and deserializer. It can only
serialize and deserialize primitives and derivatives thereof (like basic enums
or newtypes). It internally uses the `FromStr` and `Display` trait to convert
objects around.

## From String

To parse a value from a string the from_str helper can be used:

```rust
assert_eq!(serde_plain::from_str::<i32>("42").unwrap(), 42);
```

This is particularly useful if enums are in use:

```rust
use serde::Deserialize;

#[derive(Deserialize, Debug, PartialEq, Eq)]
pub enum MyEnum {
    VariantA,
    VariantB,
}

assert_eq!(serde_plain::from_str::<MyEnum>("VariantA").unwrap(), MyEnum::VariantA);
```

## To String

The inverse is also possible with to_string:

```rust
assert_eq!(serde_plain::to_string(&true).unwrap(), "true");
```

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:serde-plain-kotlin:0.1.0")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same license terms as the upstream [`mitsuhiko/serde-plain`](https://github.com/mitsuhiko/serde-plain). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the serde_plain authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`mitsuhiko/serde-plain`](https://github.com/mitsuhiko/serde-plain) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
