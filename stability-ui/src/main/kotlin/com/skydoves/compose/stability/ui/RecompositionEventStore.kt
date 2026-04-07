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

import com.skydoves.compose.stability.runtime.ParameterChange
import com.skydoves.compose.stability.runtime.RecompositionEvent
import com.skydoves.compose.stability.runtime.RecompositionLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory store that collects [RecompositionEvent]s by implementing [RecompositionLogger].
 *
 * Events are aggregated per composable (keyed by "composableName|tag") and exposed
 * as a [StateFlow] for reactive UI consumption.
 */
internal object RecompositionEventStore : RecompositionLogger {

  private val states = ConcurrentHashMap<String, ComposableRecompositionState>()
  private val _composables = MutableStateFlow<List<ComposableRecompositionState>>(emptyList())

  /** Observable list of tracked composables, sorted by recomposition count descending. */
  val composables: StateFlow<List<ComposableRecompositionState>> = _composables.asStateFlow()

  var config: StabilityMonitorConfig = StabilityMonitorConfig()

  /** Delegate logger to chain with (e.g., the default Logcat logger). */
  var delegate: RecompositionLogger? = null

  override fun log(event: RecompositionEvent) {
    delegate?.log(event)

    val key = "${event.composableName}|${event.tag}"
    val existing = states[key]

    val updated = if (existing != null) {
      val newHistory = (existing.recentChanges + event.parameterChanges)
        .takeLast(config.maxHistoryPerComposable)
      existing.copy(
        recompositionCount = event.recompositionCount,
        unstableParameters = event.unstableParameters,
        recentChanges = newHistory,
        lastRecompositionTimestamp = System.currentTimeMillis(),
      )
    } else {
      if (states.size >= config.maxTrackedComposables) {
        // Evict the composable with the lowest recomposition count
        val minKey = states.minByOrNull { it.value.recompositionCount }?.key
        if (minKey != null) states.remove(minKey)
      }
      ComposableRecompositionState(
        composableName = event.composableName,
        tag = event.tag,
        recompositionCount = event.recompositionCount,
        unstableParameters = event.unstableParameters,
        recentChanges = event.parameterChanges,
        lastRecompositionTimestamp = System.currentTimeMillis(),
      )
    }

    states[key] = updated
    emitSnapshot()
  }

  /** Clears all tracked data. */
  fun clear() {
    states.clear()
    emitSnapshot()
  }

  private fun emitSnapshot() {
    _composables.value = states.values
      .sortedByDescending { it.recompositionCount }
      .toList()
  }
}

/**
 * Snapshot of a single composable's recomposition state.
 *
 * @property composableName Simple name of the composable function.
 * @property tag Custom tag from @TraceRecomposition (empty if unset).
 * @property recompositionCount Total number of recompositions observed.
 * @property unstableParameters Names of parameters deemed unstable.
 * @property recentChanges Recent parameter change records (bounded by config).
 * @property lastRecompositionTimestamp Epoch millis of the most recent recomposition.
 */
internal data class ComposableRecompositionState(
  val composableName: String,
  val tag: String,
  val recompositionCount: Int,
  val unstableParameters: List<String>,
  val recentChanges: List<ParameterChange>,
  val lastRecompositionTimestamp: Long,
)
