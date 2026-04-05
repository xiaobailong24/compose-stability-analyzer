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
package com.skydoves.compose.stability.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal object MonitorColors {
  val background = Color(0xFF1E1E2E)
  val surface = Color(0xFF2D2D3F)
  val surfaceVariant = Color(0xFF3D3D52)
  val onBackground = Color(0xFFE0E0E0)
  val onSurface = Color(0xFFCCCCCC)
  val onSurfaceVariant = Color(0xFF999999)
  val stable = Color(0xFF4CAF50)
  val unstable = Color(0xFFF44336)
  val runtime = Color(0xFFFFC107)
  val accent = Color(0xFF7C4DFF)
  val divider = Color(0xFF3D3D52)
}

internal object MonitorTypography {
  val title = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
  )
  val subtitle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
  )
  val body = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
  )
  val caption = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
  )
}
