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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Gemini's implementation of a responsive order action row,
 * now using [AdaptiveActionLayout] (custom Layout) instead of FlowRow + weight.
 *
 * Key differences from [OrderActionRow]:
 * - No outlined chip border around the rating section
 * - Uses filled [Button] instead of OutlinedButton
 * - Uses [AdaptiveActionLayout] as the base layout component
 */
@Composable
fun OrderActionFlowRow(
  ratingText: String,
  buttonText: String,
  modifier: Modifier = Modifier,
) {
  AdaptiveActionLayout(
    modifier = modifier,
    horizontalSpacing = 16.dp,
    verticalSpacing = 12.dp,
    mainContent = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = ratingText,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          repeat(5) {
            Icon(
              imageVector = Icons.Filled.Star,
              contentDescription = null,
              tint = Color(0xFFE0E0E0),
              modifier = Modifier.size(16.dp),
            )
          }
        }
      }
    },
    actionContent = {
      Button(
        onClick = { /* TODO */ },
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFFFFD54F),
          contentColor = Color.Black,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      ) {
        Text(
          text = buttonText,
          style = MaterialTheme.typography.labelLarge,
        )
      }
    },
  )
}

// === Previews ===

@Preview(showBackground = true, widthDp = 360, name = "Gemini 1. Short EN")
@Composable
private fun GeminiPreviewShortEn() {
  OrderActionFlowRow(
    ratingText = "Rate order",
    buttonText = "Order again",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "Gemini 2. Portuguese")
@Composable
private fun GeminiPreviewPortuguese() {
  OrderActionFlowRow(
    ratingText = "Minha avaliação",
    buttonText = "Pedir de novo",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "Gemini 3. German")
@Composable
private fun GeminiPreviewGerman() {
  OrderActionFlowRow(
    ratingText = "Meine Bewertung",
    buttonText = "Erneut bestellen",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "Gemini 4. Russian")
@Composable
private fun GeminiPreviewRussian() {
  OrderActionFlowRow(
    ratingText = "Моя оценка заказа",
    buttonText = "Заказать снова",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "Gemini 5. Extra long")
@Composable
private fun GeminiPreviewExtraLong() {
  OrderActionFlowRow(
    ratingText = "Bewerten Sie Ihre Bestellung bitte",
    buttonText = "Nochmal bestellen",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "Gemini 6. Short CN")
@Composable
private fun GeminiPreviewShortCn() {
  OrderActionFlowRow(
    ratingText = "评价",
    buttonText = "再来一单",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}
