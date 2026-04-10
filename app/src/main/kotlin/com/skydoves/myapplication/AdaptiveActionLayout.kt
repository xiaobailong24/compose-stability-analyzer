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
 * A self-adaptive layout component with justify / wrap behavior.
 *
 * Rules:
 * 1. By default, [mainContent] and [actionContent] sit on the same row,
 *    justified to opposite ends. [mainContent] fills remaining space
 *    after [actionContent].
 * 2. If [mainContent] is too wide to fit on one line, wrapping triggers:
 *    [mainContent] fills the entire first row, and [actionContent] moves
 *    to a second row, right-aligned.
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
      // Wrap each slot in a Box so Layout always sees exactly 2 measurables,
      // regardless of how many composables are inside each slot.
      Box { mainContent() }
      Box { actionContent() }
    },
  ) { measurables, constraints ->
    val horizontalSpacingPx = horizontalSpacing.roundToPx()
    val verticalSpacingPx = verticalSpacing.roundToPx()

    val mainMeasurable = measurables[0]
    val actionMeasurable = measurables[1]

    // 1. Measure the action area (button) at its natural (wrap-content) size.
    //    Relax minWidth to 0 so the button isn't forced to fill parent width
    //    (fillMaxWidth on the Layout sets minWidth = maxWidth in constraints).
    val actionPlaceable = actionMeasurable.measure(
      constraints.copy(minWidth = 0),
    )

    // 2. Get the main content's preferred width (unconstrained)
    val mainPreferredWidth =
      mainMeasurable.maxIntrinsicWidth(constraints.maxHeight)

    // 3. Core decision: if preferred + spacing + button > available → wrap
    val layoutWidth = constraints.maxWidth
    val isWrap =
      (mainPreferredWidth + horizontalSpacingPx + actionPlaceable.width) > layoutWidth

    // 4. Measure main content based on wrap decision
    val mainPlaceable = if (isWrap) {
      // Wrap: main content fills the entire row
      mainMeasurable.measure(
        constraints.copy(minWidth = layoutWidth, maxWidth = layoutWidth),
      )
    } else {
      // Single line: main content takes remaining space
      val remainingWidth =
        layoutWidth - actionPlaceable.width - horizontalSpacingPx
      mainMeasurable.measure(
        constraints.copy(
          minWidth = remainingWidth,
          maxWidth = remainingWidth,
        ),
      )
    }

    // 5. Calculate total height
    val layoutHeight = if (isWrap) {
      mainPlaceable.height + verticalSpacingPx + actionPlaceable.height
    } else {
      max(mainPlaceable.height, actionPlaceable.height)
    }

    // 6. Place children
    layout(layoutWidth, layoutHeight) {
      if (isWrap) {
        mainPlaceable.placeRelative(x = 0, y = 0)
        actionPlaceable.placeRelative(
          x = layoutWidth - actionPlaceable.width,
          y = mainPlaceable.height + verticalSpacingPx,
        )
      } else {
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
