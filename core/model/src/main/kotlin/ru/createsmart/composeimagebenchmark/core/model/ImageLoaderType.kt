package ru.createsmart.composeimagebenchmark.core.model

/**
 * Image rendering strategies for Jetpack Compose performance testing.
 */
public enum class ImageLoaderType(public val displayName: String, public val description: String) {
    /**
     * Option 1: SubcomposeAsyncImage with built-in Compose slots (loading/error).
     * Classic library API. Interrupts list item measurement via SubcomposeLayout.
     */
    SUBCOMPOSE(
        displayName = "SubcomposeAsyncImage (Slots)",
        description = "SubcomposeLayout via loading/error slots",
    ),

    /**
     * Option 2: AsyncImage with direct Painters (no Compose slots, Canvas drawing).
     * Zero subcomposition overhead.
     */
    ASYNC_IMAGE_DIRECT(
        displayName = "AsyncImage (Direct Canvas)",
        description = "Direct DrawScope.draw, zero subcomposition overhead",
    ),

    /**
     * Option 3: Hybrid — fast Canvas for the image + a lightweight Composable
     * overlay that renders a rich error screen only on failure.
     */
    ASYNC_IMAGE_OVERLAY(
        displayName = "AsyncImage + Composable Overlay",
        description = "Canvas for image + local Composable overlay for error UI",
    ),

    /**
     * Option 4: SubcomposeAsyncImage with a monolithic content = { when(state) } lambda.
     * Tests double cost: subcomposition delay plus recomposition inside it on state changes.
     */
    SUBCOMPOSE_CONTENT_SLOT(
        displayName = "SubcomposeAsyncImage (Content Slot)",
        description = "Monolithic content lambda inside SubcomposeLayout",
    ),

    /**
     * Option 5: rememberAsyncImagePainter with manual state management in a Box.
     * Alternative without subcomposition — tests whether plain recomposition is cheaper.
     */
    PAINTER_BOX(
        displayName = "Painter + Box (Manual State)",
        description = "Manual when(painter.state) inside a standard Box",
    ),
    ;

    public companion object {
        public val DEFAULT: ImageLoaderType = SUBCOMPOSE

        public fun fromName(name: String?): ImageLoaderType {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: DEFAULT
        }
    }
}
