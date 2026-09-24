# SubcomposeLayout Impact on Compose List Rendering — Summary

## 🎯 Hypothesis

Compose lists that show async images are everywhere: feeds, photo galleries, product
catalogs. Coil, a popular image-loading library, gives developers several ways to load
an image inside such a list. One popular way, `SubcomposeAsyncImage`, uses a Compose
feature called `SubcomposeLayout` under the hood. This feature lets a component wait
until the Measure phase to decide what to compose. That is useful for slot-based APIs
like `loading` / `success` / `error`, but it is known to be more expensive than normal
composition.

The problem: most advice on this topic is theoretical. Developers are told to "avoid
`SubcomposeLayout` in hot paths like lists," with no real numbers showing how much it
actually costs, or when it matters.

This study asks a simple question: **which way of loading an image in a Compose list is
actually the fastest, and by how much?** I compare five real implementation choices
under the exact same conditions, and measure real Jank, frame time, and frame duration
on real phones.

## 🧪 Setup

**Five strategies compared.** All five use the same card layout, the same dataset, and
the same `ImageLoader` (same memory cache size, disk cache turned off, same fake
decode-delay interceptor). Only the image-loading approach changes:

1. `AsyncImage` (Direct Canvas) — zero subcomposition
2. `AsyncImage` + Composable Overlay — Canvas image, Composable error screen only on failure
3. `SubcomposeAsyncImage` (named slots: `loading` / `success` / `error`)
4. `SubcomposeAsyncImage` (monolithic `content` lambda, manual `when(state)`)
5. `rememberAsyncImagePainter` + manual `when(state)` in a plain `Box`

**Dataset:** 500 cards, all images and avatars are local — no network involved. Cards
have different aspect ratios (16:9, 9:16, 4:3). Each card has a
fixed, per-item decode delay and a fixed error flag — no randomness between runs.

**Devices:** Samsung SM-T595 (Android 10, API 29) and Samsung SM-G9750 (Android 12,
API 31). Only physical devices — no emulator.

**Fixed for every run:** `CompilationMode.Full()`, `StartupMode.WARM`, one pinned light
theme, system animations turned off, airplane mode on, cooldown pauses between runs to
avoid CPU throttling.

**Metrics:** `FrameTimingMetric` is the main metric (`frameDurationCpuMs`, and on
API 31+, `frameOverrunMs`). A manual `TraceSectionMetric` around each strategy's render
code is a secondary metric — see the caveat in Key Findings.

## 📊 Results at a Glance

These numbers are the median of medians, across 5 runs × 10 iterations per strategy.
Full distributions and per-iteration traces are in the paper and the raw data archive,
not shown here.

<div align="center">
  <img alt="frame_duration_by_percentile" src="https://github.com/user-attachments/assets/715d2a83-0871-4771-975d-3deadddd764c" width="100%" />
</div>

*The strategy that ranks worst on P90/P95 flips between devices: on the older SM-T595
(left), the two `SubcomposeAsyncImage` strategies fall behind; on the flagship SM-G9750
(right), `Painter + Box` becomes the worst performer instead. See Key Finding 1.*

**SM-T595 (Android 10, API 29) — `frameDurationCpuMs` (ms)**

| Strategy | P50 | P90 | P95 | P99 |
|---|---|---|---|---|
| 1. AsyncImage (Direct Canvas) | 12.18 | 18.26 | 25.06 | 29.68 |
| 2. AsyncImage + Overlay | 12.24 | 18.68 | 25.28 | 29.80 |
| 3. SubcomposeAsyncImage (Slots) | 12.16 | 20.36 | 27.22 | 30.30 |
| 4. SubcomposeAsyncImage (Content Slot) | 12.30 | 22.02 | 27.70 | 30.42 |
| 5. Painter + Box | 12.76 | 19.62 | 25.02 | 30.26 |

**SM-G9750 (Android 12, API 31) — `frameDurationCpuMs` (ms)**

| Strategy | P50 | P90 | P95 | P99 |
|---|---|---|---|---|
| 1. AsyncImage (Direct Canvas) | 7.22 | 10.44 | 11.88 | 15.18 |
| 2. AsyncImage + Overlay | 7.30 | 10.60 | 12.02 | 15.62 |
| 3. SubcomposeAsyncImage (Slots) | 7.36 | 10.72 | 12.16 | 15.56 |
| 4. SubcomposeAsyncImage (Content Slot) | 7.30 | 10.66 | 12.16 | 15.56 |
| 5. Painter + Box | 7.56 | 11.04 | 12.54 | 16.00 |

**SM-G9750 (Android 12, API 31) — `frameOverrunMs` (ms), the direct Jank metric**

| Strategy | P50 | P90 | P95 | P99 |
|---|---|---|---|---|
| 1. AsyncImage (Direct Canvas) | -7.86 | -4.60 | -3.16 | 0.28 |
| 2. AsyncImage + Overlay | -7.76 | -4.44 | -2.98 | 0.70 |
| 3. SubcomposeAsyncImage (Slots) | -7.72 | -4.32 | -2.86 | 0.72 |
| 4. SubcomposeAsyncImage (Content Slot) | -7.74 | -4.36 | -2.82 | 0.64 |
| 5. Painter + Box | -7.08 | -3.74 | -2.28 | 1.32 |

A negative value means the frame finished before its deadline — no visible jank. A
positive value means the frame missed its deadline — visible jank. `frameOverrunMs`
needs API 31+, so it is only available for the G9750 run.

## 💡 Key Findings

1. **The bottleneck is device-class dependent, not universal.** On the older/weaker
   device (API 29), the two `SubcomposeAsyncImage`-based strategies are the worst
   performers on P90/P95. On the flagship device (API 31), the ranking flips: strategy 5
   (`rememberAsyncImagePainter`) becomes the worst across *every* percentile, because it
   reliably re-enters `AsyncImagePainter.State.Empty` on every recomposition of a
   recycled list item — a documented quirk of that API, not an architectural cost.
   "Avoid `SubcomposeLayout`" is not a universal recommendation; the right choice
   depends on the target hardware tier.

2. **The effect lives in P90/P95, not in P99.** Across both devices, P99 is dominated
   by a factor common to all five strategies (most likely decode latency), which
   compresses the visible gap between strategies to a few percent. The architectural
   difference is clearly visible in P90/P95, where it is not masked by that shared tail
   cost.

3. **Caution: naive manual `trace()` wrapping underestimates `SubcomposeLayout`-based
   strategies.** Subcomposition itself runs inside the Compose framework during the
   Measure phase, not inside the application code that calls
   `SubcomposeAsyncImage(...)`. A `trace()` section wrapped only around application code
   closes before that deferred cost occurs, making the slot/content-lambda strategies
   look cheaper than they are by this metric alone — the opposite of what the
   framework-level `FrameTimingMetric` shows. Treat manually-scoped trace sections as
   supplementary, not authoritative, for anything involving `SubcomposeLayout`.

4. **The fastest strategy is also the least useful one.** Strategy 1 (`AsyncImage`,
   Direct Canvas) wins on raw numbers, but part of that win is not free: on a failed
   load it falls back to a plain `ColorPainter` — a solid color rectangle, not a real
   error screen. It is not a fair pick once a proper error UI is required.

   Among the four strategies that do render a real Composable error screen
   (`ErrorPlaceholder`), **Strategy 2 (`AsyncImage` + Composable Overlay)** is the most
   consistent choice: best or second-best on almost every percentile, on both devices,
   and never the worst. Its overhead over the "no error UI" Direct Canvas baseline stays
   under half a millisecond on both devices — because the expensive error screen only
   composes on the rare failed load, not on every frame. This is exactly how the
   strategy is meant to work: fast Canvas drawing for the common case, a real Composable
   UI only when something actually goes wrong.

## 🚀 Practical Takeaway

   If you truly don't need an error screen, `AsyncImage` (Direct Canvas) is the fastest
   choice on both device classes we tested. If you do need one — which most real apps do —
   **`AsyncImage` + Composable Overlay is the practical default**: it gives you a real
   error UI at almost no measured cost over the fastest strategy.
   
   Beyond that, there is no single "safest" strategy for every device. On lower-end
   hardware, minimizing `SubcomposeLayout` usage still pays off. On modern hardware, the
   bigger risk is a high-frequency API quirk (`rememberAsyncImagePainter`'s guaranteed
   `Empty` pass) rather than subcomposition itself — profile on your actual target device
   class before choosing.

## 🛠 Reproducibility

- Kotlin 2.4.20 · Compose BOM 2026.09.00 · Coil 3.6.2 · AGP 9.4.0 · Hilt 2.60.1
- Dataset size: 500 items, seeded/fixed decode delays and error flags
