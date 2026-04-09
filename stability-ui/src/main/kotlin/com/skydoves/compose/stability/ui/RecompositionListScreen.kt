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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.ui.theme.MonitorColors
import com.skydoves.compose.stability.ui.theme.MonitorTypography

/**
 * Main list screen showing all tracked composables sorted by recomposition count.
 */
@Composable
internal fun RecompositionListScreen(
  composables: List<ComposableRecompositionState>,
  config: StabilityMonitorConfig,
  modifier: Modifier = Modifier,
) {
  var searchQuery by remember { mutableStateOf("") }

  val filtered = if (searchQuery.isBlank()) {
    composables
  } else {
    composables.filter {
      it.composableName.contains(searchQuery, ignoreCase = true) ||
        it.tag.contains(searchQuery, ignoreCase = true)
    }
  }

  Column(modifier = modifier.fillMaxSize()) {
    // Search bar
    TextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = {
        Text(
          text = "Search composables...",
          style = MonitorTypography.body,
          color = MonitorColors.onSurfaceVariant,
        )
      },
      singleLine = true,
      textStyle = MonitorTypography.body.copy(color = MonitorColors.onBackground),
      colors = TextFieldDefaults.colors(
        focusedContainerColor = MonitorColors.surfaceVariant,
        unfocusedContainerColor = MonitorColors.surfaceVariant,
        focusedIndicatorColor = MonitorColors.accent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = MonitorColors.accent,
      ),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .clip(RoundedCornerShape(8.dp)),
    )

    // Summary row
    SummaryRow(composables = composables, config = config)

    if (filtered.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = if (composables.isEmpty()) {
            "No recomposition events yet.\nAnnotate composables with @TraceRecomposition."
          } else {
            "No results for \"$searchQuery\""
          },
          style = MonitorTypography.body,
          color = MonitorColors.onSurfaceVariant,
        )
      }
    } else {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
          items = filtered,
          key = { "${it.composableName}|${it.tag}" },
        ) { state ->
          ComposableListItem(state = state, config = config)
        }
      }
    }
  }
}

@Composable
private fun SummaryRow(
  composables: List<ComposableRecompositionState>,
  config: StabilityMonitorConfig,
) {
  val redCount = composables.count { severityOf(it.recompositionCount, config) == Severity.RED }
  val yellowCount =
    composables.count { severityOf(it.recompositionCount, config) == Severity.YELLOW }
  val greenCount =
    composables.count { severityOf(it.recompositionCount, config) == Severity.GREEN }
  val totalRecompositions = composables.sumOf { it.recompositionCount }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    SummaryChip("Total: ${composables.size}", MonitorColors.onSurface)
    SummaryChip("RC: $totalRecompositions", MonitorColors.accent)
    SummaryChip("$redCount", Severity.RED.color)
    SummaryChip("$yellowCount", Severity.YELLOW.color)
    SummaryChip("$greenCount", Severity.GREEN.color)
  }
}

@Composable
private fun SummaryChip(text: String, color: Color) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(color.copy(alpha = 0.15f))
      .padding(horizontal = 10.dp, vertical = 4.dp),
  ) {
    Text(
      text = text,
      style = MonitorTypography.caption,
      color = color,
    )
  }
}

@Composable
private fun ComposableListItem(
  state: ComposableRecompositionState,
  config: StabilityMonitorConfig,
) {
  var expanded by remember { mutableStateOf(false) }
  val severity = severityOf(state.recompositionCount, config)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { expanded = !expanded }
      .padding(horizontal = 16.dp, vertical = 10.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      // Severity dot
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(severity.color),
      )

      Spacer(modifier = Modifier.width(12.dp))

      // Composable name + tag
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = state.composableName,
          style = MonitorTypography.subtitle,
          color = MonitorColors.onBackground,
        )
        if (state.tag.isNotEmpty()) {
          Text(
            text = state.tag,
            style = MonitorTypography.caption,
            color = MonitorColors.onSurfaceVariant,
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Recomposition count badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(severity.color.copy(alpha = 0.2f))
          .padding(horizontal = 10.dp, vertical = 4.dp),
      ) {
        Text(
          text = "${state.recompositionCount}",
          style = MonitorTypography.subtitle,
          color = severity.color,
        )
      }

      // Unstable param count
      if (state.unstableParameters.isNotEmpty()) {
        Spacer(modifier = Modifier.width(6.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MonitorColors.unstable.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
          Text(
            text = "${state.unstableParameters.size} unstable",
            style = MonitorTypography.caption,
            color = MonitorColors.unstable,
          )
        }
      }
    }

    // Expanded detail
    AnimatedVisibility(visible = expanded) {
      RecompositionDetailInline(state = state)
    }
  }

  // Divider
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(0.5.dp)
      .padding(start = 38.dp)
      .background(MonitorColors.divider),
  )
}
