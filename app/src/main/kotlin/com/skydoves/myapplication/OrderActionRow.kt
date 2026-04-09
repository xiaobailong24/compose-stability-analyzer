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
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    horizontalArrangement = Arrangement.End,
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
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          repeat(5) { index ->
            val filled = index < rating
            Icon(
              imageVector = if (filled) {
                Icons.Filled.Star
              } else {
                Icons.Outlined.StarOutline
              },
              contentDescription = "Star ${index + 1}",
              modifier = Modifier.size(18.dp),
              tint = if (filled) Color(0xFFFFC107) else Color(0xFFBDBDBD),
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

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

@Preview(showBackground = true, widthDp = 340, name = "Single row — Rate order")
@Composable
private fun OrderActionRowPreviewSingleRow() {
  OrderActionRow(
    ratingLabel = "Rate order",
    buttonLabel = "Order again",
    rating = 0,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 340, name = "Single row — My rating")
@Composable
private fun OrderActionRowPreviewRated() {
  OrderActionRow(
    ratingLabel = "My rating",
    buttonLabel = "Order again",
    rating = 3,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 340, name = "Two rows — Portuguese")
@Composable
private fun OrderActionRowPreviewTwoRows() {
  OrderActionRow(
    ratingLabel = "Minha avaliação",
    buttonLabel = "Pedir de novo",
    rating = 3,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}
