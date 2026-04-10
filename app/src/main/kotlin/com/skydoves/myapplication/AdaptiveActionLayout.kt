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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * 自适应两端对齐/折行布局组件。
 *
 * 规则：
 * 1. 默认情况下，[mainContent] 和 [actionContent] 在同一行两端对齐。
 *    其中 [mainContent] 会自动撑满除了 [actionContent] 之外的剩余空间。
 * 2. 如果 [mainContent] 的内容过长，导致一行放不下，则触发换行。
 *    换行后，[mainContent] 撑满第一行，[actionContent] 移动到第二行并靠右对齐。
 *
 * 为什么不用 FlowRow + weight(1f)？
 * FlowRow 先放置非 weight 子项（按钮），再把剩余空间分给 weight 子项。
 * weight 子项永远"刚好"填满剩余空间，所以换行永远不会触发。
 * 自定义 Layout 通过 maxIntrinsicWidth 获取主内容的真实期望宽度，
 * 从而正确判断是否需要换行。
 */
@Composable
fun AdaptiveActionLayout(
  modifier: Modifier = Modifier,
  horizontalSpacing: Dp = 16.dp,
  verticalSpacing: Dp = 12.dp,
  mainContent: @Composable () -> Unit,
  actionContent: @Composable () -> Unit,
) {
  Layout(
    modifier = modifier.fillMaxWidth(),
    content = {
      // 用 Box 包裹每个插槽内容，确保 Layout 严格看到 2 个 Measurable，
      // 无论外部传入的插槽里有几个组件。
      Box { mainContent() }
      Box { actionContent() }
    },
  ) { measurables, constraints ->
    val horizontalSpacingPx = horizontalSpacing.roundToPx()
    val verticalSpacingPx = verticalSpacing.roundToPx()

    val mainMeasurable = measurables[0]
    val actionMeasurable = measurables[1]

    // 1. 优先测量操作区（按钮），获取其自然尺寸。
    //    必须将 minWidth 设为 0，否则 fillMaxWidth 会导致
    //    constraints.minWidth = maxWidth，强制按钮撑满父容器宽度。
    val actionPlaceable = actionMeasurable.measure(
      constraints.copy(minWidth = 0),
    )

    // 2. 获取主内容区在不限制宽度时的真实"期望宽度"
    val mainPreferredWidth =
      mainMeasurable.maxIntrinsicWidth(constraints.maxHeight)

    // 3. 核心判断：期望宽度 + 间距 + 按钮宽度 > 父容器宽度 → 需要换行
    val layoutWidth = constraints.maxWidth
    val isWrap =
      (mainPreferredWidth + horizontalSpacingPx + actionPlaceable.width) > layoutWidth

    // 4. 根据是否换行，测量主内容区
    val mainPlaceable = if (isWrap) {
      // 换行：主内容区撑满整行宽度
      mainMeasurable.measure(
        constraints.copy(minWidth = layoutWidth, maxWidth = layoutWidth),
      )
    } else {
      // 不换行：主内容区占据剩余空间（总宽度 - 按钮宽度 - 间距）
      val remainingWidth =
        layoutWidth - actionPlaceable.width - horizontalSpacingPx
      mainMeasurable.measure(
        constraints.copy(
          minWidth = remainingWidth,
          maxWidth = remainingWidth,
        ),
      )
    }

    // 5. 计算父容器最终总高度
    val layoutHeight = if (isWrap) {
      mainPlaceable.height + verticalSpacingPx + actionPlaceable.height
    } else {
      max(mainPlaceable.height, actionPlaceable.height)
    }

    // 6. 将组件摆放到对应位置
    layout(layoutWidth, layoutHeight) {
      if (isWrap) {
        // 换行态：主内容第一行，按钮第二行靠右
        mainPlaceable.placeRelative(x = 0, y = 0)
        actionPlaceable.placeRelative(
          x = layoutWidth - actionPlaceable.width,
          y = mainPlaceable.height + verticalSpacingPx,
        )
      } else {
        // 单行态：两端对齐，垂直居中
        val mainY = (layoutHeight - mainPlaceable.height) / 2
        val actionY = (layoutHeight - actionPlaceable.height) / 2
        mainPlaceable.placeRelative(x = 0, y = mainY)
        actionPlaceable.placeRelative(
          x = layoutWidth - actionPlaceable.width,
          y = actionY,
        )
      }
    }
  }
}
