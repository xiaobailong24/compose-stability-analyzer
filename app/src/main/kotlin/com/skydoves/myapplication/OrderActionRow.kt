/*
 * Designed and developed by 2025 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skydoves.myapplication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A responsive order action row using custom [Layout] to handle two scenarios:
 *
 * **Case 1 — Single row (enough space):**
 * ```
 * [Rate order  ☆☆☆☆☆]          [Order again]
 * ```
 * Rating chip stretches to fill remaining space; button right-aligned.
 *
 * **Case 2 — Two rows (not enough space):**
 * ```
 * [Minha avaliação           ★★★☆☆]
 *                          [Pedir de novo]
 * ```
 * Rating chip takes full width; button wraps to second row, right-aligned.
 *
 * Why not FlowRow + weight(1f)?
 * FlowRow places non-weighted children first, then gives remaining space to
 * weighted children. This means weighted children ALWAYS fit the leftover,
 * so wrapping never triggers. A custom Layout measures both children's natural
 * widths to decide single-line vs. two-line placement.
 */
@Composable
fun OrderActionRow(
  ratingLabel: String,
  buttonLabel: String,
  rating: Int = 0,
  onRatingChanged: (Int) -> Unit = {},
  onButtonClick: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  val gap = 8.dp

  Layout(
    content = {
      // [0] Rating chip
      RatingChip(label = ratingLabel, rating = rating)
      // [1] Action button
      ActionButton(label = buttonLabel, onClick = onButtonClick)
    },
    modifier = modifier.fillMaxWidth(),
  ) { measurables, constraints ->
    val gapPx = gap.roundToPx()

    // 1. Measure button at its natural (wrap-content) width
    val buttonPlaceable = measurables[1].measure(
      constraints.copy(minWidth = 0),
    )

    // 2. Check if rating's natural width + gap + button fit on one row
    val ratingNaturalWidth = measurables[0].maxIntrinsicWidth(constraints.maxHeight)
    val singleLine =
      ratingNaturalWidth + gapPx + buttonPlaceable.width <= constraints.maxWidth

    if (singleLine) {
      // Single row: rating fills remaining space, button on the right
      val ratingWidth = constraints.maxWidth - gapPx - buttonPlaceable.width
      val ratingPlaceable = measurables[0].measure(
        Constraints.fixed(ratingWidth, buttonPlaceable.height),
      )
      val rowHeight = maxOf(ratingPlaceable.height, buttonPlaceable.height)
      layout(constraints.maxWidth, rowHeight) {
        ratingPlaceable.placeRelative(
          0,
          (rowHeight - ratingPlaceable.height) / 2,
        )
        buttonPlaceable.placeRelative(
          constraints.maxWidth - buttonPlaceable.width,
          (rowHeight - buttonPlaceable.height) / 2,
        )
      }
    } else {
      // Two rows: rating full width on row 1, button right-aligned on row 2
      val ratingPlaceable = measurables[0].measure(
        Constraints.fixed(constraints.maxWidth, buttonPlaceable.height),
      )
      val totalHeight = ratingPlaceable.height + gapPx + buttonPlaceable.height
      layout(constraints.maxWidth, totalHeight) {
        ratingPlaceable.placeRelative(0, 0)
        buttonPlaceable.placeRelative(
          constraints.maxWidth - buttonPlaceable.width,
          ratingPlaceable.height + gapPx,
        )
      }
    }
  }
}

@Composable
private fun RatingChip(
  label: String,
  rating: Int,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.height(40.dp),
    shape = RoundedCornerShape(50),
    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
    color = Color.Transparent,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = label,
        fontSize = 13.sp,
        color = Color(0xFF333333),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f, fill = false),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
          Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Star ${index + 1}",
            modifier = Modifier.size(18.dp),
            tint = if (index < rating) Color(0xFFFFC107) else Color(0xFFBDBDBD),
          )
        }
      }
    }
  }
}

@Composable
private fun ActionButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier.height(40.dp),
    shape = RoundedCornerShape(50),
    border = BorderStroke(1.dp, Color(0xFF333333)),
    colors = ButtonDefaults.outlinedButtonColors(
      containerColor = Color(0xFFFFEB3B),
      contentColor = Color(0xFF333333),
    ),
  ) {
    Text(text = label, fontSize = 13.sp)
  }
}

// === Previews with various text lengths ===

@Preview(showBackground = true, widthDp = 360, name = "1. Short EN — single row")
@Composable
private fun PreviewShortEn() {
  OrderActionRow(
    ratingLabel = "Rate order",
    buttonLabel = "Order again",
    rating = 0,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "2. Short EN rated — single row")
@Composable
private fun PreviewShortEnRated() {
  OrderActionRow(
    ratingLabel = "My rating",
    buttonLabel = "Order again",
    rating = 3,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "3. Portuguese — may wrap")
@Composable
private fun PreviewPortuguese() {
  OrderActionRow(
    ratingLabel = "Minha avaliação",
    buttonLabel = "Pedir de novo",
    rating = 3,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "4. German — wraps")
@Composable
private fun PreviewGerman() {
  OrderActionRow(
    ratingLabel = "Meine Bewertung",
    buttonLabel = "Erneut bestellen",
    rating = 4,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "5. Japanese — single row")
@Composable
private fun PreviewJapanese() {
  OrderActionRow(
    ratingLabel = "評価する",
    buttonLabel = "もう一度注文",
    rating = 0,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "6. Russian — wraps")
@Composable
private fun PreviewRussian() {
  OrderActionRow(
    ratingLabel = "Моя оценка заказа",
    buttonLabel = "Заказать снова",
    rating = 2,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "7. Extra long — wraps + ellipsis")
@Composable
private fun PreviewExtraLong() {
  OrderActionRow(
    ratingLabel = "Bewerten Sie Ihre Bestellung bitte",
    buttonLabel = "Nochmal bestellen",
    rating = 5,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "8. Short CN — single row")
@Composable
private fun PreviewShortCn() {
  OrderActionRow(
    ratingLabel = "评价",
    buttonLabel = "再来一单",
    rating = 0,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}
