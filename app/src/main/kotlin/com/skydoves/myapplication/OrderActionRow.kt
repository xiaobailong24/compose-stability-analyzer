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
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A responsive order action row that uses [FlowRow] to handle two layout scenarios:
 *
 * **Case 1 — Single row (enough space):**
 * ```
 * [Rate order  ☆☆☆☆☆]          [Order again]
 * ```
 * Rating chip fills remaining space; the button is right-aligned with intrinsic width.
 *
 * **Case 2 — Two rows (not enough space, e.g. long translations):**
 * ```
 * [Minha avaliação           ★★★☆☆]
 *                          [Pedir de novo]
 * ```
 * Rating chip takes full first row; button wraps to second row, right-aligned.
 *
 * Key trick: [maxLines] = 1 on Text makes it report full content width as its
 * minimum intrinsic width, so FlowRow can correctly decide when to wrap.
 * Without this, weight(1f) causes the intrinsic width to be ~0 and
 * FlowRow never wraps.
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
  FlowRow(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    // Rating chip: outlined pill container with label + stars
    // weight(1f) fills remaining space on the same row;
    // when the button can't fit, FlowRow wraps it to the next line,
    // and this chip stretches to full width on the first row.
    Surface(
      modifier = Modifier
        .weight(1f)
        .height(40.dp)
        .align(Alignment.CenterVertically),
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
          text = ratingLabel,
          fontSize = 13.sp,
          color = Color(0xFF333333),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          repeat(5) { index ->
            val filled = index < rating
            Icon(
              imageVector = Icons.Filled.Star,
              contentDescription = "Star ${index + 1}",
              modifier = Modifier.size(18.dp),
              tint = if (filled) Color(0xFFFFC107) else Color(0xFFBDBDBD),
            )
          }
        }
      }
    }

    // Action button: yellow pill with black border
    OutlinedButton(
      onClick = onButtonClick,
      modifier = Modifier.height(40.dp),
      shape = RoundedCornerShape(50),
      border = BorderStroke(1.dp, Color(0xFF333333)),
      colors = ButtonDefaults.outlinedButtonColors(
        containerColor = Color(0xFFFFEB3B),
        contentColor = Color(0xFF333333),
      ),
    ) {
      Text(
        text = buttonLabel,
        fontSize = 13.sp,
      )
    }
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

@Preview(showBackground = true, widthDp = 360, name = "8. Short both — single row")
@Composable
private fun PreviewShortBoth() {
  OrderActionRow(
    ratingLabel = "评价",
    buttonLabel = "再来一单",
    rating = 0,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}
