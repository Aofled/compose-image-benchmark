package ru.createsmart.composeimagebenchmark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ru.createsmart.composeimagebenchmark.core.designsystem.theme.BenchmarkTheme
import ru.createsmart.composeimagebenchmark.core.model.BenchmarkIntentExtras
import ru.createsmart.composeimagebenchmark.core.model.ImageLoaderType
import ru.createsmart.composeimagebenchmark.feature.feed.FeedRoute

@AndroidEntryPoint
public class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Strategy is chosen at launch time via Intent extra, so Macrobenchmark
        // never has to touch the UI to select it.
        val loaderType = ImageLoaderType.fromName(
            intent?.getStringExtra(BenchmarkIntentExtras.EXTRA_LOADER_TYPE),
        )

        setContent {
            // Theme is pinned, NOT isSystemInDarkTheme(): the device's system theme
            // must not become an uncontrolled variable between benchmark runs.
            BenchmarkTheme(darkTheme = false) {
                FeedRoute(initialLoaderType = loaderType)
            }
        }
    }
}
