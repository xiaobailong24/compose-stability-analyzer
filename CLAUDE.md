# CLAUDE.md — Compose Stability Analyzer

## Project Overview

Compose Stability Analyzer is a Kotlin compiler plugin ecosystem that analyzes Jetpack Compose composable functions for stability. It tells developers whether composables are skippable/restartable and why parameters are stable or unstable. The project has two main distribution channels:

- **IDE Plugin** (IntelliJ/Android Studio): Gutter icons, hover tooltips, inline hints, code inspections, stability explorer, recomposition cascade visualizer, and live recomposition heatmap.
- **Gradle Plugin**: `@TraceRecomposition` annotation for runtime tracking, plus `stabilityDump`/`stabilityCheck` tasks for CI/CD validation.

**Group**: `com.github.skydoves` | **Version**: `0.7.2` | **License**: Apache 2.0

## Repository Structure

```
stability-compiler/          # Kotlin compiler plugin (FIR + IR transformations)
stability-runtime/           # Multiplatform runtime library (annotations, tracing)
stability-gradle/            # Gradle plugin (tasks, reporting, validation)
stability-lint/              # Android Lint checks for @TraceRecomposition usage
compiler-tests/              # Compiler plugin integration tests
app/                         # Demo Android app
app-model/                   # Shared models for demo app
compose-stability-analyzer-idea/  # IntelliJ IDE plugin (separate build, Kotlin 2.3.0)
docs/                        # MkDocs documentation site
spotless/                    # License header templates
gradle/                      # Version catalog (libs.versions.toml)
```

### Module Dependencies

- `stability-compiler` → `stability-runtime` (runtime annotations)
- `stability-gradle` → standalone (uses Gradle API only)
- `stability-lint` → standalone (uses Android Lint API)
- `compiler-tests` → `stability-compiler`, `stability-runtime`
- `app` → `app-model`
- `compose-stability-analyzer-idea` → `stability-runtime-jvm` (separate Kotlin 2.3.0 build)
- `stability-runtime` → no internal dependencies (pure Kotlin multiplatform)

## Build & Development Commands

### Prerequisites
- **Java**: JDK 21 (CI), JDK 11+ (local development)
- **Kotlin**: 2.3.20 (main project), 2.3.0 (IDE plugin only)
- **Gradle**: 8.13 (via wrapper)

### Common Commands

```bash
# Build everything (excludes IDE plugin)
./gradlew assemble

# Run all tests for a specific module
./gradlew :stability-compiler:test
./gradlew :stability-runtime:jvmTest
./gradlew :stability-gradle:test
./gradlew :stability-lint:test
./gradlew :compiler-tests:test

# Generate compiler test classes from test data (required before running compiler-tests)
./gradlew :compiler-tests:generateTests

# Code formatting
./gradlew spotlessApply          # Auto-fix formatting
./gradlew spotlessCheck          # Check formatting (CI)

# Binary API compatibility
./gradlew apiDump                # Update API baseline files
./gradlew apiCheck               # Validate against baseline (CI)

# Build IDE plugin separately (excluded from main build)
./gradlew -p compose-stability-analyzer-idea buildPlugin
./gradlew -p compose-stability-analyzer-idea verifyPlugin

# Publish to Maven Local (for local testing)
./gradlew publishToMavenLocal
```

### Pre-PR Checklist

1. `./gradlew spotlessApply` — format code
2. `./gradlew apiDump` — update API baseline if public API changed
3. Run relevant module tests

## Key Source Locations

### stability-compiler
`stability-compiler/src/main/kotlin/com/skydoves/compose/stability/compiler/`

| File | Purpose |
|------|---------|
| `StabilityAnalyzerPluginRegistrar.kt` | Plugin entry point |
| `StabilityAnalyzerIrGenerationExtension.kt` | IR transformation entry |
| `StabilityAnalyzerTransformer.kt` | Core stability analysis and IR transformation |
| `ComposableStabilityChecker.kt` | FIR checker infrastructure |
| `StabilityInfoCollector.kt` | JSON stability report export |
| `RecompositionIrBuilder.kt` | IR code generation for recomposition tracking |
| `StabilityAnalyzerCommandLineProcessor.kt` | Compiler CLI options |

### stability-runtime
`stability-runtime/src/commonMain/kotlin/com/skydoves/compose/stability/runtime/`

| File | Purpose |
|------|---------|
| `StabilityModels.kt` | Core data types (`ParameterStability`, `ComposableStabilityInfo`) |
| `TraceRecomposition.kt` | `@TraceRecomposition` annotation |
| `RecompositionTracker.kt` | Runtime tracking infrastructure |
| `RecompositionLogger.kt` | Logging abstraction with platform implementations |

### stability-gradle
`stability-gradle/src/main/kotlin/com/skydoves/compose/stability/gradle/`

| File | Purpose |
|------|---------|
| `StabilityAnalyzerGradlePlugin.kt` | Plugin registration, task setup |
| `StabilityDumpTask.kt` | Export stability baseline |
| `StabilityCheckTask.kt` | Validate against baseline |
| `StabilityComparison.kt` | Diff/regression detection |
| `StabilityAnalyzerExtension.kt` | DSL configuration |

## Code Conventions

### Style
- **Indentation**: 2 spaces (enforced by ktlint via Spotless)
- **Max line length**: 100 characters (`.editorconfig`)
- **Explicit API mode**: enabled — all public declarations must have explicit visibility
- **License headers**: Apache 2.0, auto-enforced by Spotless (templates in `spotless/`)
- **Context parameters**: used in compiler module (`-Xcontext-parameters` compiler flag)

### Naming
- **Packages**: `com.skydoves.compose.stability.*`
- **Classes**: PascalCase
- **Functions**: camelCase
- **Constants**: UPPER_SNAKE_CASE

### Formatting Exclusions
Spotless skips:
- `**/build/**` — build outputs
- `**/src/test/data/**` — compiler test data files (special formatting)
- `ComposableStabilityChecker.kt` — uses context parameters unsupported by ktlint

### Kotlin Version Constraint
The IDE plugin (`compose-stability-analyzer-idea`) is pinned to **Kotlin 2.3.0** because the K2 Analysis API from IntelliJ 2025.2 uses context receivers, which were removed in Kotlin 2.3.20. It must be built separately from the main project.

## Testing

### Test Frameworks by Module
| Module | Framework | Notes |
|--------|-----------|-------|
| `compiler-tests` | Kotlin Compiler Test Framework + JUnit 5 | Generated test classes |
| `stability-compiler` | JUnit 5 | Unit tests |
| `stability-runtime` | Kotlin Test (multiplatform) | `commonTest`, `jvmTest`, etc. |
| `stability-gradle` | JUnit 4 | Unit tests |
| `stability-lint` | Android Lint Test Framework | Detector tests |
| `compose-stability-analyzer-idea` | IntelliJ Platform Test Framework | Plugin tests |

### Compiler Test Types (`compiler-tests/src/test/data/`)

1. **Diagnostic tests** (`diagnostic/`): Frontend-only, test FIR analysis and error reporting. Diagnostics are injected as comments.
2. **Box tests** (`box/`): Full compiler pipeline (FIR + IR + codegen). Must contain a `box()` function returning `"OK"`.
3. **FIR dump tests** (`dump/fir/`): Validate FIR tree output against expected `.fir.txt` files.
4. **IR dump tests** (`dump/ir/`): Validate IR output against expected `.fir.kt.txt` files.

After adding new test data files, run `./gradlew :compiler-tests:generateTests` to regenerate test classes.

## CI/CD

### GitHub Workflows (`.github/workflows/`)

| Workflow | Trigger | What it checks |
|----------|---------|----------------|
| `android.yml` | PR + push to main | `spotlessCheck`, `apiCheck`, build + compiler tests (macOS) |
| `module-tests.yml` | PR + push to main | Per-module test suites in parallel (ubuntu), IDE plugin verify |
| `publish.yml` | GitHub release | Publish to Maven Central |
| `publish-docs.yml` | Push to main | Deploy MkDocs to GitHub Pages |

All CI jobs use **Java 21** (Zulu) except publishing which uses Java 17.

## Publishing

### Maven Central Artifacts
- `compose-stability-compiler` — compiler plugin (fat JAR via Shadow)
- `compose-stability-runtime` — multiplatform runtime (JVM, Android, iOS, macOS, JS, WASM)
- `compose-stability-gradle` — Gradle plugin
- `stability-lint` — Android Lint checks

### Not Published
- `app`, `app-model` — sample app
- `compiler-tests` — test-only module
- `compose-stability-analyzer-idea` — distributed via JetBrains Marketplace separately

### Publishing Pipeline
Uses `com.vanniktech.maven.publish` (Nexus) with GPG signing, triggered by GitHub release events. Publishing uses `--no-configuration-cache` due to plugin limitations.

## Dependency Management
- Version catalog: `gradle/libs.versions.toml`
- Renovate bot auto-merges minor/patch updates (excludes Kotlin, KSP, Spotless, Maven Plugin)
- Binary API compatibility tracked via `kotlin-binary-compatibility-validator`
