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
import androidx.compose.foundation.layout.FlowRow
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
 * 基于 FlowRow + weight(1f) 的订单操作行（有缺陷，无法换行）。
 *
 * 此实现用于对比演示 FlowRow 的局限性：
 * - FlowRow 先放置非 weight 子项（按钮），再把剩余空间分给 weight 子项
 * - weight 子项永远"刚好"填满剩余空间，所以换行永远不会触发
 * - 即使文案很长，评分区域也会被压缩，按钮始终在同一行
 *
 * 对比 [OrderActionRow]（基于自定义 Layout）可以正确换行。
 */
@Composable
fun OrderActionFlowRow(
  ratingText: String,
  buttonText: String,
  modifier: Modifier = Modifier,
) {
  FlowRow(
    modifier = modifier.fillMaxWidth(),
    // spacedBy 控制同行元素间距，Alignment.End 控制换行后靠右对齐
    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
    // 控制第一行和第二行的上下间距
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    // 左侧组件：评价文案 + 星星
    Row(
      modifier = Modifier
        // 关键点：weight(1f) 让此元素吃掉同行所有剩余空间。
        // 但这也是导致无法换行的根本原因：FlowRow 先放置按钮，
        // 再把剩余空间给 weight 子项，weight 子项永远"刚好够"。
        .weight(1f)
        .padding(vertical = 8.dp),
      // 撑开后，内部内容首尾两端对齐
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      // 评价文案
      Text(
        text = ratingText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyMedium,
        // fill = false 让文字按真实宽度提供期望尺寸，
        // 但外层 Row 的 weight(1f) 已经决定了整体行为，无法改变换行结果
        modifier = Modifier.weight(1f, fill = false),
      )

      // 防止文字和星星贴得太近
      Spacer(modifier = Modifier.width(8.dp))

      // 五颗星星
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

    // 右侧组件：操作按钮（无 weight，自适应宽度）
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
  }
}

// === Preview ===

@Preview(showBackground = true, widthDp = 360, name = "FlowRow 短文案-不换行")
@Composable
private fun FlowRowPreviewShortEn() {
  OrderActionFlowRow(
    ratingText = "Rate order",
    buttonText = "Order again",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "FlowRow 葡语-期望换行但实际不换行")
@Composable
private fun FlowRowPreviewPortuguese() {
  OrderActionFlowRow(
    ratingText = "Minha avaliação",
    buttonText = "Pedir de novo",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "FlowRow 德语-期望换行但实际不换行")
@Composable
private fun FlowRowPreviewGerman() {
  OrderActionFlowRow(
    ratingText = "Meine Bewertung",
    buttonText = "Erneut bestellen",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "FlowRow 超长文案-期望换行但实际不换行")
@Composable
private fun FlowRowPreviewExtraLong() {
  OrderActionFlowRow(
    ratingText = "Bewerten Sie Ihre Bestellung bitte",
    buttonText = "Nochmal bestellen",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Preview(showBackground = true, widthDp = 360, name = "FlowRow 中文短文案-不换行")
@Composable
private fun FlowRowPreviewShortCn() {
  OrderActionFlowRow(
    ratingText = "评价",
    buttonText = "再来一单",
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}
