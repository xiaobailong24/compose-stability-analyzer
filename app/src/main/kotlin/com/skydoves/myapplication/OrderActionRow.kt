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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 基于自定义 [AdaptiveActionLayout] 的订单操作行（正确实现，支持换行）。
 *
 * 一行放得下时：
 * ```
 * [Rate order  ☆☆☆☆☆]          [Order again]
 * ```
 *
 * 一行放不下时（自动换行）：
 * ```
 * [Minha avaliação           ★★★☆☆]
 *                          [Pedir de novo]
 * ```
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
  AdaptiveActionLayout(
    modifier = modifier,
    horizontalSpacing = 8.dp,
    verticalSpacing = 8.dp,
    mainContent = {
      // 评分区域：圆角边框胶囊 + 文字 + 星星
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(40.dp),
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
            modifier = Modifier.weight(1f),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
              Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star ${index + 1}",
                modifier = Modifier.size(18.dp),
                tint = if (index < rating) {
                  Color(0xFFFFC107)
                } else {
                  Color(0xFFBDBDBD)
                },
              )
            }
          }
        }
      }
    },
    actionContent = {
      // 操作按钮：黄色圆角胶囊
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
        Text(text = buttonLabel, fontSize = 13.sp)
      }
    },
  )
}

// === Preview ===

@Preview(showBackground = true, widthDp = 360, name = "自定义Layout 短文案-单行")
@Composable
private fun PreviewShortEn() {
  OrderActionRow(
    ratingLabel = "Rate order",
    buttonLabel = "Order again",
    rating = 0,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "自定义Layout 中等文案-单行")
@Composable
private fun PreviewShortEnRated() {
  OrderActionRow(
    ratingLabel = "My rating",
    buttonLabel = "Order again",
    rating = 3,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "自定义Layout 葡语-换行")
@Composable
private fun PreviewPortuguese() {
  OrderActionRow(
    ratingLabel = "Minha avaliação",
    buttonLabel = "Pedir de novo",
    rating = 3,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "自定义Layout 德语-换行")
@Composable
private fun PreviewGerman() {
  OrderActionRow(
    ratingLabel = "Meine Bewertung",
    buttonLabel = "Erneut bestellen",
    rating = 4,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "自定义Layout 超长文案-换行+省略")
@Composable
private fun PreviewExtraLong() {
  OrderActionRow(
    ratingLabel = "Bewerten Sie Ihre Bestellung bitte",
    buttonLabel = "Nochmal bestellen",
    rating = 5,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "自定义Layout 中文短文案-单行")
@Composable
private fun PreviewShortCn() {
  OrderActionRow(
    ratingLabel = "评价",
    buttonLabel = "再来一单",
    rating = 0,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}
