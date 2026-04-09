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

import androidx.compose.ui.graphics.Color

/**
 * Severity level for recomposition frequency of a composable.
 *
 * @property color The display color for this severity level.
 */
internal enum class Severity(val color: Color) {
  /** Recomposition count is within healthy range. */
  GREEN(Color(0xFF4CAF50)),

  /** Recomposition count is moderately high. */
  YELLOW(Color(0xFFFFC107)),

  /** Recomposition count is excessively high. */
  RED(Color(0xFFF44336)),
}

/**
 * Determines the severity of a given recomposition count based on thresholds.
 */
internal fun severityOf(
  recompositionCount: Int,
  config: StabilityMonitorConfig,
): Severity = when {
  recompositionCount >= config.redThreshold -> Severity.RED
  recompositionCount >= config.yellowThreshold -> Severity.YELLOW
  else -> Severity.GREEN
}
