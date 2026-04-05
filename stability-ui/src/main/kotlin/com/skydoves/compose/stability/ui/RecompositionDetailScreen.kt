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
package com.skydoves.compose.stability.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.ParameterChange
import com.skydoves.compose.stability.ui.theme.MonitorColors
import com.skydoves.compose.stability.ui.theme.MonitorTypography

/**
 * Inline detail view shown when a composable list item is expanded.
 * Displays parameter stability info and recent change history.
 */
@Composable
internal fun RecompositionDetailInline(
  state: ComposableRecompositionState,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 22.dp, top = 8.dp, bottom = 4.dp),
  ) {
    // Unstable parameters section
    if (state.unstableParameters.isNotEmpty()) {
      Text(
        text = "Unstable Parameters",
        style = MonitorTypography.caption,
        color = MonitorColors.unstable,
      )
      Spacer(modifier = Modifier.height(4.dp))
      state.unstableParameters.forEach { param ->
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "  - $param",
            style = MonitorTypography.body,
            color = MonitorColors.unstable.copy(alpha = 0.8f),
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    // Recent parameter changes
    if (state.recentChanges.isNotEmpty()) {
      Text(
        text = "Recent Parameter Changes",
        style = MonitorTypography.caption,
        color = MonitorColors.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(4.dp))

      // Group by parameter name and show the latest state
      val latestByParam = state.recentChanges
        .groupBy { it.name }
        .mapValues { (_, changes) -> changes.last() }

      latestByParam.values.forEach { change ->
        ParameterChangeRow(change = change)
      }
    }
  }
}

@Composable
private fun ParameterChangeRow(change: ParameterChange) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f),
    ) {
      Text(
        text = change.name,
        style = MonitorTypography.body,
        color = MonitorColors.onBackground,
      )

      Spacer(modifier = Modifier.width(6.dp))

      Text(
        text = change.type,
        style = MonitorTypography.caption,
        color = MonitorColors.onSurfaceVariant,
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    // Stability badge
    StabilityBadge(stable = change.stable)

    // Changed indicator
    if (change.changed) {
      Spacer(modifier = Modifier.width(4.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(MonitorColors.runtime.copy(alpha = 0.2f))
          .padding(horizontal = 6.dp, vertical = 2.dp),
      ) {
        Text(
          text = "changed",
          style = MonitorTypography.caption,
          color = MonitorColors.runtime,
        )
      }
    }
  }
}

@Composable
private fun StabilityBadge(stable: Boolean) {
  val color = if (stable) MonitorColors.stable else MonitorColors.unstable
  val label = if (stable) "stable" else "unstable"

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(color.copy(alpha = 0.15f))
      .padding(horizontal = 6.dp, vertical = 2.dp),
  ) {
    Text(
      text = label,
      style = MonitorTypography.caption,
      color = color,
    )
  }
}
