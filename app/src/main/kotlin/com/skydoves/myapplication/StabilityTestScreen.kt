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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card as M3Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import com.skydoves.myapplication.models.ImmutableData
import com.skydoves.myapplication.models.MixedStabilityClass
import com.skydoves.myapplication.models.StableUser
import com.skydoves.myapplication.models.UnstableUser
import com.skydoves.myapplication.models.UserState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Comprehensive stability test screen exercising many recomposition scenarios.
 * Each section is annotated with @TraceRecomposition so the monitor overlay
 * can visualize the differences in real time.
 */
@Composable
fun StabilityTestScreen() {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Primitives", "Collections", "State", "Nested", "Stress")

  Column(modifier = Modifier.fillMaxSize()) {
    TabRow(selectedTabIndex = selectedTab) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = { Text(title, fontSize = 12.sp) },
        )
      }
    }

    when (selectedTab) {
      0 -> PrimitivesTab()
      1 -> CollectionsTab()
      2 -> StateManagementTab()
      3 -> NestedComposablesTab()
      4 -> StressTestTab()
    }
  }
}

// ---------------------------------------------------------------------------
// Tab 1: Primitive & basic type stability
// ---------------------------------------------------------------------------

@Composable
private fun PrimitivesTab() {
  var intVal by remember { mutableIntStateOf(0) }
  var floatVal by remember { mutableStateOf(1.0f) }
  var boolVal by remember { mutableStateOf(false) }
  var stringVal by remember { mutableStateOf("hello") }
  var nullableInt by remember { mutableStateOf<Int?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text("Primitive Stability Tests", fontWeight = FontWeight.Bold)

    TrackedIntDisplay(value = intVal)
    TrackedFloatDisplay(value = floatVal)
    TrackedBoolDisplay(value = boolVal)
    TrackedStringDisplay(value = stringVal)
    TrackedNullableDisplay(value = nullableInt)

    Spacer(modifier = Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(onClick = { intVal++ }) { Text("+Int") }
      Button(onClick = { floatVal += 0.5f }) { Text("+Float") }
      Button(onClick = { boolVal = !boolVal }) { Text("!Bool") }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(onClick = { stringVal = "hello ${intVal}" }) { Text("Str") }
      Button(onClick = { nullableInt = if (nullableInt == null) 1 else null }) {
        Text("Null?")
      }
    }
  }
}

@TraceRecomposition(tag = "primitive-int")
@Composable
fun TrackedIntDisplay(value: Int) {
  StabilityCard(label = "Int (stable)", content = "$value", color = Color(0xFF4CAF50))
}

@TraceRecomposition(tag = "primitive-float")
@Composable
fun TrackedFloatDisplay(value: Float) {
  StabilityCard(label = "Float (stable)", content = "$value", color = Color(0xFF4CAF50))
}

@TraceRecomposition(tag = "primitive-bool")
@Composable
fun TrackedBoolDisplay(value: Boolean) {
  StabilityCard(label = "Boolean (stable)", content = "$value", color = Color(0xFF4CAF50))
}

@TraceRecomposition(tag = "primitive-string")
@Composable
fun TrackedStringDisplay(value: String) {
  StabilityCard(label = "String (stable)", content = value, color = Color(0xFF4CAF50))
}

@TraceRecomposition(tag = "primitive-nullable")
@Composable
fun TrackedNullableDisplay(value: Int?) {
  StabilityCard(
    label = "Int? (stable)",
    content = value?.toString() ?: "null",
    color = Color(0xFF4CAF50),
  )
}

// ---------------------------------------------------------------------------
// Tab 2: Collection stability
// ---------------------------------------------------------------------------

@Composable
private fun CollectionsTab() {
  var counter by remember { mutableIntStateOf(0) }
  val mutableItems = remember { mutableStateListOf("Item A", "Item B", "Item C") }
  var immutableItems by remember {
    mutableStateOf<ImmutableList<String>>(persistentListOf("Immutable 1", "Immutable 2"))
  }
  var listItems by remember { mutableStateOf(listOf("List 1", "List 2")) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text("Collection Stability Tests", fontWeight = FontWeight.Bold)

    TrackedImmutableListDisplay(items = immutableItems)
    TrackedListDisplay(items = listItems)
    TrackedMutableListDisplay(items = mutableItems.toList())
    TrackedMixedClassDisplay(
      data = MixedStabilityClass(
        id = counter,
        name = "Item $counter",
        tags = mutableListOf("tag1"),
      ),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(onClick = {
        counter++
        mutableItems.add("Item ${mutableItems.size + 1}")
      }) { Text("+Mutable") }

      Button(onClick = {
        counter++
        immutableItems = immutableItems + "Immutable ${immutableItems.size + 1}"
      }) { Text("+Immutable") }

      Button(onClick = {
        counter++
        listItems = listItems + "List ${listItems.size + 1}"
      }) { Text("+List") }
    }
  }
}

@TraceRecomposition(tag = "collection-immutable")
@Composable
fun TrackedImmutableListDisplay(items: ImmutableList<String>) {
  CollectionCard(
    label = "ImmutableList (stable)",
    items = items,
    color = Color(0xFF4CAF50),
  )
}

@TraceRecomposition(tag = "collection-list")
@Composable
fun TrackedListDisplay(items: List<String>) {
  CollectionCard(
    label = "List<String> (runtime)",
    items = items,
    color = Color(0xFFFFC107),
  )
}

@TraceRecomposition(tag = "collection-mutable")
@Composable
fun TrackedMutableListDisplay(items: List<String>) {
  CollectionCard(
    label = "MutableList snapshot (unstable origin)",
    items = items,
    color = Color(0xFFF44336),
  )
}

@TraceRecomposition(tag = "collection-mixed")
@Composable
fun TrackedMixedClassDisplay(data: MixedStabilityClass) {
  StabilityCard(
    label = "MixedStabilityClass (unstable)",
    content = "id=${data.id}, name=${data.name}, tags=${data.tags.size}",
    color = Color(0xFFF44336),
  )
}

// ---------------------------------------------------------------------------
// Tab 3: State management patterns
// ---------------------------------------------------------------------------

@Composable
private fun StateManagementTab() {
  var userState by remember { mutableStateOf<UserState>(UserState.Loading) }
  var stableUser by remember { mutableStateOf(StableUser("Alice", 28)) }
  var unstableUser by remember { mutableStateOf(UnstableUser("Bob", 32)) }
  var immutableData by remember { mutableStateOf(ImmutableData("Sample", 100)) }
  var toggleEnabled by remember { mutableStateOf(false) }
  var textInput by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text("State Management Tests", fontWeight = FontWeight.Bold)

    TrackedSealedStateDisplay(state = userState)
    TrackedStableUserDetail(user = stableUser)
    TrackedUnstableUserDetail(user = unstableUser)
    TrackedImmutableDataDetail(data = immutableData)
    TrackedToggleDisplay(enabled = toggleEnabled, onToggle = { toggleEnabled = it })
    TrackedTextInputDisplay(text = textInput, onTextChange = { textInput = it })

    Spacer(modifier = Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(onClick = {
        userState = when (userState) {
          is UserState.Loading -> UserState.Success(stableUser)
          is UserState.Success -> UserState.Error("Something went wrong")
          is UserState.Error -> UserState.Loading
        }
      }) { Text("Cycle State") }

      Button(onClick = {
        stableUser = StableUser("Alice ${(1..99).random()}", (20..40).random())
      }) { Text("New User") }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(onClick = { unstableUser.age++ }) { Text("Mutate Age") }
      Button(onClick = {
        immutableData = ImmutableData("Sample ${(1..99).random()}", (1..999).random())
      }) { Text("New @Immutable") }
    }
  }
}

@TraceRecomposition(tag = "state-sealed")
@Composable
fun TrackedSealedStateDisplay(state: UserState) {
  val (label, content, color) = when (state) {
    is UserState.Loading -> Triple("Loading", "...", Color(0xFFFFC107))
    is UserState.Success -> Triple("Success", state.user.name, Color(0xFF4CAF50))
    is UserState.Error -> Triple("Error", state.message, Color(0xFFF44336))
  }
  StabilityCard(label = "UserState: $label (sealed)", content = content, color = color)
}

@TraceRecomposition(tag = "state-stable-user")
@Composable
fun TrackedStableUserDetail(user: StableUser) {
  StabilityCard(
    label = "StableUser (stable)",
    content = "${user.name}, age ${user.age}",
    color = Color(0xFF4CAF50),
  )
}

@TraceRecomposition(tag = "state-unstable-user")
@Composable
fun TrackedUnstableUserDetail(user: UnstableUser) {
  StabilityCard(
    label = "UnstableUser (unstable - var props)",
    content = "${user.name}, age ${user.age}",
    color = Color(0xFFF44336),
  )
}

@TraceRecomposition(tag = "state-immutable")
@Composable
fun TrackedImmutableDataDetail(data: ImmutableData) {
  StabilityCard(
    label = "@Immutable data (stable)",
    content = "${data.text}: ${data.number}",
    color = Color(0xFF4CAF50),
  )
}

@TraceRecomposition(tag = "state-toggle")
@Composable
fun TrackedToggleDisplay(enabled: Boolean, onToggle: (Boolean) -> Unit) {
  M3Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column {
        Text("Boolean toggle (stable)", fontWeight = FontWeight.Medium, fontSize = 13.sp)
        Text("enabled = $enabled", fontSize = 11.sp, color = Color.Gray)
      }
      Switch(checked = enabled, onCheckedChange = onToggle)
    }
  }
}

@TraceRecomposition(tag = "state-text-input")
@Composable
fun TrackedTextInputDisplay(text: String, onTextChange: (String) -> Unit) {
  M3Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text("String input (stable)", fontWeight = FontWeight.Medium, fontSize = 13.sp)
      TextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = { Text("Type to trigger recomposition...") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
      )
    }
  }
}

// ---------------------------------------------------------------------------
// Tab 4: Nested composables (cascade recomposition)
// ---------------------------------------------------------------------------

@Composable
private fun NestedComposablesTab() {
  var outerCount by remember { mutableIntStateOf(0) }
  var innerCount by remember { mutableIntStateOf(0) }
  var showChild by remember { mutableStateOf(true) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text("Nested Composable Tests", fontWeight = FontWeight.Bold)
    Text(
      "Watch how parent recomposition cascades to children",
      fontSize = 12.sp,
      color = Color.Gray,
    )

    TrackedParent(
      outerValue = outerCount,
      innerValue = innerCount,
      showChild = showChild,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(onClick = { outerCount++ }) { Text("Outer++") }
      Button(onClick = { innerCount++ }) { Text("Inner++") }
      OutlinedButton(onClick = { showChild = !showChild }) {
        Text(if (showChild) "Hide" else "Show")
      }
    }
  }
}

@TraceRecomposition(tag = "nested-parent")
@Composable
fun TrackedParent(outerValue: Int, innerValue: Int, showChild: Boolean) {
  M3Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF7C4DFF).copy(alpha = 0.08f)),
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        "Parent (outer=$outerValue)",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
      )

      Spacer(modifier = Modifier.height(8.dp))

      TrackedChild(value = innerValue)

      AnimatedVisibility(visible = showChild) {
        TrackedConditionalChild(value = outerValue + innerValue)
      }

      TrackedLambdaChild(
        label = "Lambda child",
        onClick = { /* captured: outerValue */ },
      )
    }
  }
}

@TraceRecomposition(tag = "nested-child")
@Composable
fun TrackedChild(value: Int) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF2196F3).copy(alpha = 0.1f))
      .padding(10.dp),
  ) {
    Text("Child: value=$value", fontSize = 12.sp)
  }
}

@TraceRecomposition(tag = "nested-conditional")
@Composable
fun TrackedConditionalChild(value: Int) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFFFF9800).copy(alpha = 0.1f))
      .padding(10.dp),
  ) {
    Text("Conditional child: sum=$value", fontSize = 12.sp)
  }
}

@TraceRecomposition(tag = "nested-lambda")
@Composable
fun TrackedLambdaChild(label: String, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF009688).copy(alpha = 0.1f))
      .clickable { onClick() }
      .padding(10.dp),
  ) {
    Text("$label (lambda is stable)", fontSize = 12.sp)
  }
}

// ---------------------------------------------------------------------------
// Tab 5: Stress test (many items recomposing rapidly)
// ---------------------------------------------------------------------------

@Composable
private fun StressTestTab() {
  var globalTick by remember { mutableIntStateOf(0) }
  val itemCount = 20

  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text("Stress Test: $itemCount items", fontWeight = FontWeight.Bold)
      Button(onClick = { globalTick++ }) {
        Text("Tick #$globalTick")
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      items(itemCount) { index ->
        TrackedListItem(
          index = index,
          tick = globalTick,
          isEven = index % 2 == 0,
        )
      }
    }
  }
}

@TraceRecomposition(tag = "stress-item")
@Composable
fun TrackedListItem(index: Int, tick: Int, isEven: Boolean) {
  val bgColor = if (isEven) {
    Color(0xFFE3F2FD)
  } else {
    Color(0xFFFCE4EC)
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(bgColor)
      .padding(horizontal = 16.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text("Item #$index", fontSize = 13.sp)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("tick=$tick", fontSize = 11.sp, color = Color.Gray)
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(if (isEven) Color(0xFF4CAF50) else Color(0xFFF44336)),
      )
    }
  }
}

// ---------------------------------------------------------------------------
// Shared UI helpers
// ---------------------------------------------------------------------------

@Composable
private fun StabilityCard(label: String, content: String, color: Color) {
  M3Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(label, fontWeight = FontWeight.Medium, fontSize = 13.sp)
      Text(content, fontSize = 12.sp, color = Color.DarkGray)
    }
  }
}

@Composable
private fun CollectionCard(label: String, items: List<String>, color: Color) {
  M3Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(label, fontWeight = FontWeight.Medium, fontSize = 13.sp)
      Text(
        "${items.size} items: ${items.joinToString(limit = 3)}",
        fontSize = 12.sp,
        color = Color.DarkGray,
      )
    }
  }
}
