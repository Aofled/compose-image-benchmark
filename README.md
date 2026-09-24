# ComposeImageBenchmark

A reusable Android project for running **controlled, reproducible Macrobenchmark
performance studies** of Jetpack Compose UI patterns — built to host more than one
study over time, not just a single one-off comparison.

The first study hosted here measures the impact of `SubcomposeLayout` on list
rendering performance (Jank, P99 frame time, RenderThread-inclusive frame duration)
across several Coil 3 image-loading strategies. See [`docs/benchmark/`](docs/benchmark/)
for the results of every study conducted in this repository.

## 🧭 What this is

- A **Now In Android**-style modular Android project (Convention Plugins in
  `build-logic/`, strict module boundaries, DIP between layers), purpose-built for
  running controlled A/B performance comparisons rather than shipping a product.
- A harness where the measurement infrastructure — dataset isolation, deterministic
  delay/error simulation, DI-injected `ImageLoader`, Macrobenchmark setup — is
  designed to be **reused across studies**, not tied to the specific comparison it
  currently ships with.
- Every completed study is tagged and released so its exact code and data remain reproducible even as the harness itself
  keeps evolving on `main`.

## 📁 Project structure

Modular monolith, following the Now In Android convention:

```
ComposeImageBenchmark/
├── app/                     # :app — DI graph, ImageLoader/theme wiring, reads the strategy from an Intent extra
├── benchmark/               # :benchmark — MacrobenchmarkRule-based test suite (Android Test module)
├── build-logic/             # Composite build — Convention Plugins shared by every module below
│   └── convention/
├── core/
│   ├── data/                # :core:data — asset-backed data source, Hilt modules, delay/error interceptor, ImageLoader config
│   ├── designsystem/        # :core:designsystem — shared theme, card scaffold, UI used identically by every strategy
│   ├── domain/               # :core:domain — use cases, repository interfaces (DIP), javax.inject only
│   └── model/                # :core:model — pure Kotlin/JVM domain models, zero Android SDK dependency
├── feature/
│   └── feed/                 # :feature:feed — UDF screen (StateFlow), strategy dispatcher, compared composables
├── docs/
│   └── benchmark/             # Results and SUMMARY.md of every study run on this harness
└── gradle/
    └── libs.versions.toml     # Single source of truth for every dependency version
```

## 🏗️ Architecture highlights

A few decisions worth calling out, beyond "it's modular":

- **Convention Plugins, not copy-pasted Gradle.** `build-logic` holds every reusable
  plugin (`android.application`, `.compose`, `hilt`, `detekt`, `android.benchmark`).
  Every module applies one or two plugin IDs — no version numbers duplicated anywhere.
- **One `ImageLoader`, explicitly injected end-to-end.** No reliance on Coil's
  `SingletonImageLoader.Factory`. Every strategy under comparison receives the exact
  same Hilt-provided instance — same interceptor, same fixed-size cache — as an
  explicit parameter, so a missing wire-up fails loudly at compile time instead of
  silently drifting to an uncontrolled default.
- **Deterministic-by-construction dataset.** Every card's decode delay and failure
  state is baked into the JSON dataset itself (computed from the real asset file
  size, not randomized at runtime) — reproducible between devices and sessions, with
  zero network involved anywhere in the pipeline.
- **One shared card shell, one shared `Modifier`.** Every strategy renders inside the
  same card scaffold and receives an identical `Modifier` for the compared slot. No
  strategy can quietly apply its own sizing or clipping — which would otherwise
  silently contaminate the comparison.
- **Release-like measurement, not debug.** The dedicated `benchmark` build type is
  non-debuggable with R8 minification/shrinking enabled, `profileable` for system
  tracing, and `CompilationMode.Full()` pinned explicitly for every run — removing
  JIT/interpreter warm-up as a hidden variable.
- **Zero-UI strategy switching.** Macrobenchmark selects what to measure through a
  single Intent extra, read once in `onCreate`. No tap or menu interaction ever
  pollutes a measured frame.

## ✅ Requirements

- JDK 21
- A **physical Android device**. Macrobenchmark results from an emulator are not
  reliable and are not used for any study in this repository.
- See `gradle/libs.versions.toml` for the exact toolchain and library versions used.

## 🚀 Running the benchmarks

All commands run from the repo root, with exactly one device connected.

```bash
# Run the full suite (all 5 strategies)
./gradlew :benchmark:connectedBenchmarkAndroidTest

# Run a single strategy
./gradlew :benchmark:connectedBenchmarkAndroidTest --tests "*.scrollAsyncImageDirect"
./gradlew :benchmark:connectedBenchmarkAndroidTest --tests "*.scrollAsyncImageOverlay"
./gradlew :benchmark:connectedBenchmarkAndroidTest --tests "*.scrollSubcomposeAsyncImage"
./gradlew :benchmark:connectedBenchmarkAndroidTest --tests "*.scrollSubcomposeContentSlot"
./gradlew :benchmark:connectedBenchmarkAndroidTest --tests "*.scrollPainterBox"

# Force a clean re-run, ignoring Gradle's up-to-date cache
./gradlew :benchmark:connectedBenchmarkAndroidTest --rerun-tasks

# Stop a running/stuck Gradle daemon
./gradlew --stop
```

Results (including raw `.perfetto-trace` files) are written under
`benchmark/build/outputs/connected_android_test_additional_output/`.

**Before a real measurement session:** charge the device to 100%, enable airplane
mode, and disable system animations (Developer Options → Window/Transition/Animator
duration scale → Off). If a run fails with a `trace_processor_shell` MD5 mismatch,
clear the stale binary the benchmark library left on the device:

```bash
adb shell rm -f /data/local/tmp/trace_processor_shell
# if that fails with Permission denied:
adb root && adb shell rm -f /data/local/tmp/trace_processor_shell && adb unroot
```

## 🔍 Static analysis (Detekt)

```bash
# Lint everything — every module in the main build, plus the build-logic composite build
./gradlew detektAll

# Lint a single module
./gradlew :feature:feed:detekt
```

Autocorrect is controlled by `detekt.autocorrect` in `gradle.properties`, set in both
the root project and `build-logic` (they're separate Gradle builds, so each needs it).

## 📊 Studies conducted

See [`docs/benchmark/`](docs/benchmark/) for the full list, each with its own
`SUMMARY.md` and a link to the tagged code/data snapshot used to produce it.
