package ru.createsmart.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.createsmart.composeimagebenchmark.core.model.BenchmarkIntentExtras
import ru.createsmart.composeimagebenchmark.core.model.ImageLoaderType
import ru.createsmart.composeimagebenchmark.feature.feed.BENCHMARK_FEED_LIST_TAG

private const val TARGET_PACKAGE = "ru.createsmart.composeimagebenchmark"
private const val FIND_TIMEOUT_MS = 10_000L

private const val BENCHMARK_ITERATIONS = 10

// Timings for CPU thermal throttling protection
private const val INTERATION_COOLDOWN_MS = 1_500L
private const val AFTER_TEST_COOLDOWN_MS = 15_000L // Pause after each @Test method

private const val COOLDOWN_SLEEP_MS = 2_000L

private const val FAST_SWIPE_STEPS = 20
private const val MEDIUM_SWIPE_STEPS = 25
private const val MARGIN_DIVIDER = 10
private const val REPEAT = 10
private const val START_Y_FRACTION = 0.85
private const val END_Y_FRACTION = 0.15
private const val JITTER_OFFSET_FRACTION = 0.15
private const val CENTER_X_DIVIDER = 2

@OptIn(ExperimentalMetricApi::class)
@RunWith(AndroidJUnit4::class)
public class FeedScrollBenchmark {

    @get:Rule
    public val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * Cooldown after each test.
     * Minimizes the application and allows the processor to cool down to its base temperature before the next test.
     * Launch command: ./gradlew :benchmark:connectedBenchmarkAndroidTest
     * Launch a single test command:
     * ./gradlew :benchmark:connectedBenchmarkAndroidTest --tests "*.scrollAsyncImageDirect"
     * Rerun command: ./gradlew :benchmark:connectedBenchmarkAndroidTest --rerun-tasks
     * Stop command: ./gradlew --stop
     */
    @After
    public fun tearDownCoolDown() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
        device.waitForIdle()
        Thread.sleep(AFTER_TEST_COOLDOWN_MS)
    }

    @Test
    public fun scrollAsyncImageDirect(): Unit = benchmarkStressScroll(ImageLoaderType.ASYNC_IMAGE_DIRECT)

    @Test
    public fun scrollAsyncImageOverlay(): Unit = benchmarkStressScroll(ImageLoaderType.ASYNC_IMAGE_OVERLAY)

    @Test
    public fun scrollSubcomposeAsyncImage(): Unit = benchmarkStressScroll(ImageLoaderType.SUBCOMPOSE)

    @Test
    public fun scrollSubcomposeContentSlot(): Unit = benchmarkStressScroll(ImageLoaderType.SUBCOMPOSE_CONTENT_SLOT)

    @Test
    public fun scrollPainterBox(): Unit = benchmarkStressScroll(ImageLoaderType.PAINTER_BOX)

    private fun benchmarkStressScroll(loaderType: ImageLoaderType) {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(
                FrameTimingMetric(),
            ),
            iterations = BENCHMARK_ITERATIONS,
            compilationMode = CompilationMode.Full(),
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                device.waitForIdle()
                Thread.sleep(INTERATION_COOLDOWN_MS)

                startActivityAndWait { intent ->
                    intent.putExtra(BenchmarkIntentExtras.EXTRA_LOADER_TYPE, loaderType.name)
                }

                device.wait(
                    Until.hasObject(By.res(BENCHMARK_FEED_LIST_TAG)),
                    FIND_TIMEOUT_MS,
                )

                device.waitForIdle()
                Thread.sleep(COOLDOWN_SLEEP_MS)
            },
        ) {
            val feedList = device.findObject(By.res(BENCHMARK_FEED_LIST_TAG))
                ?: error("Feed list not found by resource ID!")

            feedList.setGestureMargin(device.displayWidth / MARGIN_DIVIDER)

            val centerX = device.displayWidth / CENTER_X_DIVIDER
            val startY = (device.displayHeight * START_Y_FRACTION).toInt()
            val endY = (device.displayHeight * END_Y_FRACTION).toInt()
            val jitterOffset = (device.displayHeight * JITTER_OFFSET_FRACTION).toInt()

            feedList.fling(Direction.DOWN)
            device.waitForIdle()

            // ==================== 6 STRESS TEST STEPS ====================

            // STEP 1: fast swipes down (stressing parallel loading)
            repeat(REPEAT) {
                device.swipe(centerX, startY, centerX, endY, FAST_SWIPE_STEPS)
                device.waitForIdle()
            }

            // STEP 2: swipes up (checks memory recycling)
            repeat(REPEAT) {
                device.swipe(centerX, endY, centerX, startY, FAST_SWIPE_STEPS)
                device.waitForIdle()
            }

            // STEP 3: Jitter test (quick small swipes back and forth)
            repeat(REPEAT) {
                device.swipe(centerX, startY, centerX, startY - jitterOffset, FAST_SWIPE_STEPS)
                device.swipe(centerX, startY - jitterOffset, centerX, startY, FAST_SWIPE_STEPS)
            }

            // STEP 4: Chaos (interrupted swipes without waiting for idle)
            // Stress tests coroutine cancellation and inertia change
            repeat(REPEAT) {
                device.swipe(centerX, startY, centerX, endY, FAST_SWIPE_STEPS)
                device.swipe(centerX, endY, centerX, startY, FAST_SWIPE_STEPS)
            }
            device.waitForIdle()

            // STEP 5: Slow swipes for FPS consistency check
            repeat(REPEAT) {
                device.swipe(centerX, startY, centerX, endY, MEDIUM_SWIPE_STEPS)
                device.waitForIdle()
            }

            // STEP 6: Final fling and stabilization
            feedList.fling(Direction.DOWN)
            device.waitForIdle()
        }
    }
}
