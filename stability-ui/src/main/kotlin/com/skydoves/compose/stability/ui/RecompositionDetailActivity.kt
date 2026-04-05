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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.ui.theme.MonitorColors
import com.skydoves.compose.stability.ui.theme.MonitorTypography

/**
 * Full-screen activity displaying detailed recomposition data.
 * Opened by tapping the [RecompositionMonitorOverlay] bubble.
 */
class RecompositionDetailActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      RecompositionMonitorScreen(onClose = { finish() })
    }
  }

  companion object {
    fun createIntent(context: Context): Intent =
      Intent(context, RecompositionDetailActivity::class.java)
  }
}

@Composable
private fun RecompositionMonitorScreen(onClose: () -> Unit) {
  val composables by RecompositionEventStore.composables.collectAsState()
  val config = StabilityMonitor.config

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MonitorColors.background)
      .statusBarsPadding(),
  ) {
    // Top bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(MonitorColors.surface)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = "Recomposition Monitor",
        style = MonitorTypography.title,
        color = MonitorColors.onBackground,
      )

      Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Clear button
        Text(
          text = "Clear",
          style = MonitorTypography.subtitle,
          color = MonitorColors.unstable,
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { StabilityMonitor.clearData() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        )

        // Close button
        Text(
          text = "Close",
          style = MonitorTypography.subtitle,
          color = MonitorColors.onSurfaceVariant,
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClose() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        )
      }
    }

    // List content
    RecompositionListScreen(
      composables = composables,
      config = config,
    )
  }
}
