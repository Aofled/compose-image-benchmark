package ru.createsmart.composeimagebenchmark.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.createsmart.composeimagebenchmark.core.designsystem.theme.StarGold
import java.util.Locale

/**
 * Rating badge component. Adds vector and text load to the layout.
 */
@Composable
public fun RatingBadge(
    rating: Float,
    modifier: Modifier = Modifier,
) {
    val formattedRating = remember(rating) {
        String.format(Locale.US, "%.1f", rating)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "★",
                color = StarGold,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formattedRating,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
