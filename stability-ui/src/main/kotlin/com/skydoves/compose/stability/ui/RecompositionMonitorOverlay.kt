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

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Floating draggable overlay bubble that shows real-time recomposition stats.
 *
 * Place this composable at the root of your app's composition:
 * ```kotlin
 * setContent {
 *   MyAppTheme {
 *     Box {
 *       MyAppContent()
 *       RecompositionMonitorOverlay()
 *     }
 *   }
 * }
 * ```
 *
 * The bubble shows the count of "hot" composables (those with RED severity).
 * Tap it to open the full [RecompositionDetailActivity].
 * Drag it to reposition anywhere on screen.
 */
@Composable
fun RecompositionMonitorOverlay(
  modifier: Modifier = Modifier,
) {
  if (!StabilityMonitor.isInstalled()) return
  if (!StabilityMonitor.config.showOverlay) return

  val composables by RecompositionEventStore.composables.collectAsState()
  val config = StabilityMonitor.config
  val context = LocalContext.current

  val redCount = composables.count { severityOf(it.recompositionCount, config) == Severity.RED }
  val yellowCount =
    composables.count { severityOf(it.recompositionCount, config) == Severity.YELLOW }
  val totalCount = composables.size

  val bubbleColor = when {
    redCount > 0 -> Severity.RED.color
    yellowCount > 0 -> Severity.YELLOW.color
    totalCount > 0 -> Severity.GREEN.color
    else -> Color(0xFF7C4DFF)
  }

  var offsetX by remember { mutableFloatStateOf(0f) }
  var offsetY by remember { mutableFloatStateOf(0f) }

  Box(
    modifier = modifier,
    contentAlignment = Alignment.TopEnd,
  ) {
    Column(
      modifier = Modifier
        .statusBarsPadding()
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Main bubble
      Box(
        modifier = Modifier
          .size(56.dp)
          .shadow(8.dp, CircleShape)
          .clip(CircleShape)
          .background(bubbleColor)
          .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
          .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              offsetX += dragAmount.x
              offsetY += dragAmount.y
            }
          }
          .pointerInput(Unit) {
            detectTapGestures(
              onTap = {
                val intent = RecompositionDetailActivity.createIntent(context)
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
              },
            )
          },
        contentAlignment = Alignment.Center,
      ) {
        if (redCount > 0) {
          PulsingContent(redCount)
        } else {
          Text(
            text = totalCount.toString(),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
          )
        }
      }

      // Mini label — always visible
      Box(
          modifier = Modifier
            .padding(top = 4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
          Text(
            text = "RC",
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
          )
        }
    }
  }
}

@Composable
private fun PulsingContent(count: Int) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val alpha by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 0.5f,
    animationSpec = infiniteRepeatable(
      animation = tween(800),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "pulseAlpha",
  )

  Text(
    text = count.toString(),
    color = Color.White,
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = FontFamily.Monospace,
    textAlign = TextAlign.Center,
    modifier = Modifier.alpha(alpha),
  )
}
