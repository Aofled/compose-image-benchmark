package ru.createsmart.composeimagebenchmark.feature.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import ru.createsmart.composeimagebenchmark.core.model.ImageLoaderType
import ru.createsmart.composeimagebenchmark.feature.feed.component.FeedCard
import ru.createsmart.composeimagebenchmark.feature.feed.model.BenchmarkItemUio

public const val BENCHMARK_FEED_LIST_TAG: String = "benchmark_feed_list"
public const val BENCHMARK_FEED_TITLE_TAG: String = "benchmark_feed_title"

/**
 * Public entry point of the feature module.
 *
 * Keeps [FeedViewModel] and [FeedUiState] internal to this module while still
 * allowing :app to host the screen.
 *
 * @param initialLoaderType Strategy selected on launch. Driven by the Intent extra
 * in :app so Macrobenchmark can pick the strategy under test without UI interaction.
 */
@Composable
public fun FeedRoute(
    modifier: Modifier = Modifier,
    initialLoaderType: ImageLoaderType = ImageLoaderType.DEFAULT,
) {
    FeedScreen(
        modifier = modifier,
        initialLoaderType = initialLoaderType,
    )
}

@Composable
internal fun FeedScreen(
    modifier: Modifier = Modifier,
    initialLoaderType: ImageLoaderType = ImageLoaderType.DEFAULT,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialLoaderType) {
        viewModel.selectLoaderType(initialLoaderType)
    }

    FeedContent(
        uiState = uiState,
        imageLoader = viewModel.imageLoader,
        onSelectLoaderType = viewModel::selectLoaderType,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeedContent(
    uiState: FeedUiState,
    imageLoader: ImageLoader,
    onSelectLoaderType: (ImageLoaderType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                testTagsAsResourceId = true
            },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val currentTitle = (uiState as? FeedUiState.Success)
                        ?.selectedLoaderType
                        ?.displayName
                        ?: stringResource(R.string.feature_feed_title)
                    Text(
                        text = currentTitle,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.testTag(BENCHMARK_FEED_TITLE_TAG),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                actions = {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(
                            painter = painterResource(R.drawable.feature_feed_ic_change_circle),
                            contentDescription = stringResource(R.string.feature_feed_switch_strategy),
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                    ) {
                        ImageLoaderType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type.displayName) },
                                onClick = {
                                    onSelectLoaderType(type)
                                    isMenuExpanded = false
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (uiState) {
                is FeedUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is FeedUiState.Error -> {
                    Text(
                        text = uiState.message ?: stringResource(
                            uiState.messageRes ?: R.string.feature_feed_unknown_error,
                        ),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                    )
                }

                is FeedUiState.Success -> {
                    FeedList(
                        items = uiState.items,
                        loaderType = uiState.selectedLoaderType,
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedList(
    items: List<BenchmarkItemUio>,
    loaderType: ImageLoaderType,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.testTag(BENCHMARK_FEED_LIST_TAG),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(
            items = items,
            key = { item -> item.id },
        ) { item ->
            // Single card for all strategies.
            FeedCard(
                item = item,
                imageLoaderType = loaderType,
                imageLoader = imageLoader,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
