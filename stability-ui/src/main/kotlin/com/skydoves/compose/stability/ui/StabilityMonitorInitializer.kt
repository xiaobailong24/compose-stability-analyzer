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
import android.content.ContentProvider
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.util.Log

/**
 * Auto-initializes [StabilityMonitor] when the app starts, similar to LeakCanary.
 *
 * Disable auto-initialization by adding this to your AndroidManifest.xml inside
 * the `<application>` tag:
 * ```xml
 * <meta-data
 *     android:name="stability_monitor_auto_init"
 *     android:value="false" />
 * ```
 */
internal class StabilityMonitorInitializer : ContentProvider() {

  override fun onCreate(): Boolean {
    val context = context ?: return false
    val appContext = context.applicationContext

    if (isAutoInitDisabled(appContext)) {
      Log.d(TAG, "Auto-initialization disabled via manifest metadata")
      return true
    }

    if (appContext is Application) {
      StabilityMonitor.install(appContext)
    }
    return true
  }

  private fun isAutoInitDisabled(context: android.content.Context): Boolean {
    return try {
      val appInfo = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA,
      )
      val metadata = appInfo.metaData ?: return false
      !metadata.getBoolean("stability_monitor_auto_init", true)
    } catch (_: PackageManager.NameNotFoundException) {
      false
    }
  }

  override fun query(
    uri: Uri,
    projection: Array<out String>?,
    selection: String?,
    selectionArgs: Array<out String>?,
    sortOrder: String?,
  ): Cursor? = null

  override fun getType(uri: Uri): String? = null
  override fun insert(uri: Uri, values: ContentValues?): Uri? = null
  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<out String>?,
  ): Int = 0

  companion object {
    private const val TAG = "StabilityMonitor"
  }
}
