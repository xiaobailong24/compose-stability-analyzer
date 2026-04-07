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

import com.skydoves.compose.stability.runtime.ComposeStabilityAnalyzer
import com.skydoves.compose.stability.runtime.ParameterChange
import com.skydoves.compose.stability.runtime.RecompositionEvent
import com.skydoves.compose.stability.runtime.RecompositionLogger
import com.skydoves.compose.stability.runtime.createRecompositionTracker
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests the complete recomposition monitoring data flow:
 * RecompositionTracker -> ComposeStabilityAnalyzer -> RecompositionEventStore -> UI state
 *
 * This validates that when composables recompose, the overlay bubble
 * will receive and display the correct data.
 */
class RecompositionMonitorTest {

  @Before
  fun setup() {
    RecompositionEventStore.clear()
    RecompositionEventStore.config = StabilityMonitorConfig()
  }

  @After
  fun tearDown() {
    RecompositionEventStore.clear()
  }

  // -------------------------------------------------------------------------
  // RecompositionEventStore tests
  // -------------------------------------------------------------------------

  @Test
  fun `EventStore receives events and updates composables list`() {
    val event = createEvent("MyComposable", "tag1", recompositionCount = 1)
    RecompositionEventStore.log(event)

    val composables = RecompositionEventStore.composables.value
    assertEquals(1, composables.size)
    assertEquals("MyComposable", composables[0].composableName)
    assertEquals("tag1", composables[0].tag)
    assertEquals(1, composables[0].recompositionCount)
  }

  @Test
  fun `EventStore aggregates multiple events for same composable`() {
    RecompositionEventStore.log(createEvent("Comp", "t", recompositionCount = 1))
    RecompositionEventStore.log(createEvent("Comp", "t", recompositionCount = 2))
    RecompositionEventStore.log(createEvent("Comp", "t", recompositionCount = 3))

    val composables = RecompositionEventStore.composables.value
    assertEquals(1, composables.size)
    assertEquals(3, composables[0].recompositionCount)
  }

  @Test
  fun `EventStore tracks multiple different composables`() {
    RecompositionEventStore.log(createEvent("CompA", "a", recompositionCount = 5))
    RecompositionEventStore.log(createEvent("CompB", "b", recompositionCount = 2))
    RecompositionEventStore.log(createEvent("CompC", "c", recompositionCount = 10))

    val composables = RecompositionEventStore.composables.value
    assertEquals(3, composables.size)
    // Sorted by recomposition count descending
    assertEquals("CompC", composables[0].composableName)
    assertEquals("CompA", composables[1].composableName)
    assertEquals("CompB", composables[2].composableName)
  }

  @Test
  fun `EventStore tracks unstable parameters`() {
    val event = RecompositionEvent(
      composableName = "Comp",
      tag = "",
      recompositionCount = 1,
      parameterChanges = listOf(
        ParameterChange("user", "User", null, "User@1", changed = false, stable = false),
        ParameterChange("count", "Int", null, 42, changed = false, stable = true),
      ),
      unstableParameters = listOf("user"),
    )
    RecompositionEventStore.log(event)

    val state = RecompositionEventStore.composables.value[0]
    assertEquals(listOf("user"), state.unstableParameters)
    assertEquals(2, state.recentChanges.size)
  }

  @Test
  fun `EventStore clear resets all data`() {
    RecompositionEventStore.log(createEvent("A", "", 1))
    RecompositionEventStore.log(createEvent("B", "", 1))
    assertEquals(2, RecompositionEventStore.composables.value.size)

    RecompositionEventStore.clear()
    assertEquals(0, RecompositionEventStore.composables.value.size)
  }

  @Test
  fun `EventStore chains delegate logger`() {
    val logged = mutableListOf<RecompositionEvent>()
    RecompositionEventStore.delegate = object : RecompositionLogger {
      override fun log(event: RecompositionEvent) {
        logged.add(event)
      }
    }

    val event = createEvent("Comp", "", 1)
    RecompositionEventStore.log(event)

    assertEquals(1, logged.size)
    assertEquals("Comp", logged[0].composableName)
    // Also stored in EventStore
    assertEquals(1, RecompositionEventStore.composables.value.size)

    RecompositionEventStore.delegate = null
  }

  // -------------------------------------------------------------------------
  // Severity tests
  // -------------------------------------------------------------------------

  @Test
  fun `severityOf returns GREEN for low counts`() {
    val config = StabilityMonitorConfig(yellowThreshold = 5, redThreshold = 20)
    assertEquals(Severity.GREEN, severityOf(0, config))
    assertEquals(Severity.GREEN, severityOf(4, config))
  }

  @Test
  fun `severityOf returns YELLOW for medium counts`() {
    val config = StabilityMonitorConfig(yellowThreshold = 5, redThreshold = 20)
    assertEquals(Severity.YELLOW, severityOf(5, config))
    assertEquals(Severity.YELLOW, severityOf(19, config))
  }

  @Test
  fun `severityOf returns RED for high counts`() {
    val config = StabilityMonitorConfig(yellowThreshold = 5, redThreshold = 20)
    assertEquals(Severity.RED, severityOf(20, config))
    assertEquals(Severity.RED, severityOf(100, config))
  }

  // -------------------------------------------------------------------------
  // End-to-end: RecompositionTracker -> EventStore flow
  // -------------------------------------------------------------------------

  @Test
  fun `full tracking flow produces overlay data`() {
    // Wire up: RecompositionEventStore as the global logger
    val previousLogger = ComposeStabilityAnalyzer.getLogger()
    ComposeStabilityAnalyzer.setLogger(RecompositionEventStore)
    ComposeStabilityAnalyzer.setEnabled(true)

    try {
      // Simulate what the demo composables do:
      // val t = remember { createRecompositionTracker("TrackedIntDisplay", "primitive-int") }
      // t.trackParameter("value", "Int", 42, true)
      // t.logIfThresholdMet()
      val tracker = createRecompositionTracker("TrackedIntDisplay", "primitive-int", 1)

      // First recomposition
      tracker.trackParameter("value", "Int", 0, true)
      tracker.logIfThresholdMet()

      var composables = RecompositionEventStore.composables.value
      assertEquals(1, composables.size)
      assertEquals("TrackedIntDisplay", composables[0].composableName)
      assertEquals(1, composables[0].recompositionCount)

      // Second recomposition with changed value
      tracker.trackParameter("value", "Int", 1, true)
      tracker.logIfThresholdMet()

      composables = RecompositionEventStore.composables.value
      assertEquals(1, composables.size)
      assertEquals(2, composables[0].recompositionCount)

      // Verify the overlay would show GREEN (2 < yellowThreshold=5)
      assertEquals(Severity.GREEN, severityOf(composables[0].recompositionCount, StabilityMonitorConfig()))
    } finally {
      ComposeStabilityAnalyzer.setLogger(previousLogger)
    }
  }

  @Test
  fun `multiple trackers produce correct overlay counts`() {
    ComposeStabilityAnalyzer.setLogger(RecompositionEventStore)
    ComposeStabilityAnalyzer.setEnabled(true)

    try {
      val intTracker = createRecompositionTracker("IntDisplay", "int", 1)
      val strTracker = createRecompositionTracker("StrDisplay", "str", 1)

      // Simulate 25 recompositions on IntDisplay (should be RED)
      repeat(25) { i ->
        intTracker.trackParameter("value", "Int", i, true)
        intTracker.logIfThresholdMet()
      }

      // Simulate 3 recompositions on StrDisplay (should be GREEN)
      repeat(3) { i ->
        strTracker.trackParameter("value", "String", "v$i", true)
        strTracker.logIfThresholdMet()
      }

      val composables = RecompositionEventStore.composables.value
      assertEquals(2, composables.size)

      // Sorted by count desc
      val config = StabilityMonitorConfig()
      assertEquals("IntDisplay", composables[0].composableName)
      assertEquals(25, composables[0].recompositionCount)
      assertEquals(Severity.RED, severityOf(composables[0].recompositionCount, config))

      assertEquals("StrDisplay", composables[1].composableName)
      assertEquals(3, composables[1].recompositionCount)
      assertEquals(Severity.GREEN, severityOf(composables[1].recompositionCount, config))

      // Overlay bubble should show: redCount=1, totalCount=2
      val redCount = composables.count { severityOf(it.recompositionCount, config) == Severity.RED }
      assertEquals(1, redCount)
    } finally {
      ComposeStabilityAnalyzer.setLogger(ComposeStabilityAnalyzer.getLogger())
    }
  }

  @Test
  fun `parameter changes are tracked correctly`() {
    ComposeStabilityAnalyzer.setLogger(RecompositionEventStore)
    ComposeStabilityAnalyzer.setEnabled(true)

    try {
      val tracker = createRecompositionTracker("UserCard", "user", 1)

      tracker.trackParameter("user", "UnstableUser", "User@1", false)
      tracker.trackParameter("count", "Int", 0, true)
      tracker.logIfThresholdMet()

      val state = RecompositionEventStore.composables.value[0]
      assertEquals(listOf("user"), state.unstableParameters)
      assertTrue(state.recentChanges.any { it.name == "user" && !it.stable })
      assertTrue(state.recentChanges.any { it.name == "count" && it.stable })
    } finally {
      ComposeStabilityAnalyzer.setLogger(ComposeStabilityAnalyzer.getLogger())
    }
  }

  private fun createEvent(
    name: String,
    tag: String,
    recompositionCount: Int,
  ) = RecompositionEvent(
    composableName = name,
    tag = tag,
    recompositionCount = recompositionCount,
    parameterChanges = emptyList(),
    unstableParameters = emptyList(),
  )
}
