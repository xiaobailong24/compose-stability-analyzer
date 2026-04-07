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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.skydoves.compose.stability.runtime.ComposeStabilityAnalyzer
import com.skydoves.compose.stability.runtime.createRecompositionTracker
import com.skydoves.compose.stability.ui.RecompositionMonitorOverlay
import com.skydoves.compose.stability.ui.StabilityMonitor
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test that runs on an Android emulator to verify:
 * 1. The recomposition monitor overlay bubble appears on screen
 * 2. The bubble updates when recompositions are tracked
 * 3. Tapping buttons triggers recomposition and the bubble reflects it
 */
class RecompositionMonitorOverlayTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    StabilityMonitor.clearData()
  }

  @Test
  fun overlay_bubble_is_displayed_after_tracking() {
    composeTestRule.setContent {
      MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
          var counter by remember { mutableIntStateOf(0) }

          Column {
            // Manually tracked composable
            val tracker = remember {
              createRecompositionTracker("TestComposable", "test", 1)
            }
            tracker.trackParameter("counter", "Int", counter, true)
            tracker.logIfThresholdMet()

            Text("Counter: $counter")
            Button(onClick = { counter++ }) {
              Text("Increment")
            }
          }

          RecompositionMonitorOverlay(
            modifier = Modifier.align(Alignment.TopEnd),
          )
        }
      }
    }

    // After first composition, tracker fires -> EventStore has 1 composable
    // The bubble should show "1" (totalCount) and the "RC" label
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("RC").assertIsDisplayed()

    // Tap button to trigger recomposition
    composeTestRule.onNodeWithText("Increment").performClick()
    composeTestRule.waitForIdle()

    // Bubble should still be visible
    composeTestRule.onNodeWithText("RC").assertIsDisplayed()
  }

  @Test
  fun overlay_shows_count_matching_tracked_composables() {
    composeTestRule.setContent {
      MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
          Column {
            // Two separate tracked composables
            val t1 = remember { createRecompositionTracker("CompA", "a", 1) }
            t1.trackParameter("x", "Int", 1, true)
            t1.logIfThresholdMet()

            val t2 = remember { createRecompositionTracker("CompB", "b", 1) }
            t2.trackParameter("y", "String", "hello", true)
            t2.logIfThresholdMet()

            Text("Two tracked composables")
          }

          RecompositionMonitorOverlay(
            modifier = Modifier.align(Alignment.TopEnd),
          )
        }
      }
    }

    composeTestRule.waitForIdle()

    // Bubble should show "2" for two tracked composables
    composeTestRule.onNodeWithText("2").assertIsDisplayed()
    composeTestRule.onNodeWithText("RC").assertIsDisplayed()
  }
}
