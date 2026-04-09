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

/**
 * Configuration for the recomposition monitor.
 *
 * @property yellowThreshold Recomposition count to trigger YELLOW severity.
 * @property redThreshold Recomposition count to trigger RED severity.
 * @property maxTrackedComposables Maximum number of composables to track in memory.
 * @property maxHistoryPerComposable Maximum parameter change history entries per composable.
 * @property showOverlay Whether to show the floating overlay bubble.
 */
data class StabilityMonitorConfig(
  val yellowThreshold: Int = 5,
  val redThreshold: Int = 20,
  val maxTrackedComposables: Int = 100,
  val maxHistoryPerComposable: Int = 20,
  val showOverlay: Boolean = true,
)
