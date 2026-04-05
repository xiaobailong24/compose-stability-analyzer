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

import android.app.Application
import android.util.Log
import com.skydoves.compose.stability.runtime.ComposeStabilityAnalyzer

/**
 * Public entry point for the in-app recomposition monitor.
 *
 * Similar to LeakCanary, call [install] in your [Application.onCreate]:
 * ```kotlin
 * class MyApp : Application() {
 *   override fun onCreate() {
 *     super.onCreate()
 *     StabilityMonitor.install(this)
 *   }
 * }
 * ```
 *
 * Or rely on automatic initialization via [StabilityMonitorInitializer].
 * Disable auto-init by adding to your AndroidManifest.xml:
 * ```xml
 * <meta-data
 *     android:name="stability_monitor_auto_init"
 *     android:value="false" />
 * ```
 */
object StabilityMonitor {

  private const val TAG = "StabilityMonitor"

  @Volatile
  private var installed = false

  /** Monitor configuration. Update before calling [install] for full effect. */
  var config: StabilityMonitorConfig = StabilityMonitorConfig()
    set(value) {
      field = value
      RecompositionEventStore.config = value
    }

  /**
   * Installs the recomposition monitor.
   *
   * This hooks into [ComposeStabilityAnalyzer] by setting a custom logger that
   * aggregates events in [RecompositionEventStore]. If a logger is already set,
   * it will be chained so existing logging (e.g., Logcat) continues to work.
   *
   * @param application The application instance.
   */
  fun install(application: Application) {
    if (installed) return
    installed = true

    RecompositionEventStore.config = config

    // Chain with any existing logger so Logcat output is preserved
    val existingLogger = ComposeStabilityAnalyzer.getLogger()
    RecompositionEventStore.delegate = existingLogger

    ComposeStabilityAnalyzer.setLogger(RecompositionEventStore)
    ComposeStabilityAnalyzer.setEnabled(true)

    Log.d(TAG, "Recomposition monitor installed")
  }

  /** Returns true if [install] has been called. */
  fun isInstalled(): Boolean = installed

  /** Clears all collected recomposition data. */
  fun clearData() {
    RecompositionEventStore.clear()
  }
}
