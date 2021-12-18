package com.hallett.bujoass.presentation.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.hallett.bujoass.presentation.model.Task
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import com.hallett.bujoass.presentation.ui.task_list.TaskSwipeHelper.*
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.Flow

@ExperimentalMaterialApi
class DashboardFragment(): BujoAssFragment() {
  private companion object {
    const val ANIMATION_DURATION: Int = 300
    const val REVEALED_OFFSET: Float = 100f
  }

  private val viewModel: DashboardFragmentViewModel by lazy {
    ViewModelProvider(this, vmpfactory).get(DashboardFragmentViewModel::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = ComposeView(requireContext()).apply {
    setContent {
      TaskList(viewModel.observeDashboardItems(), viewModel.observeExpandedTask())
    }
  }

  @Composable
  fun TaskList(dashboardItemsFlow: Flow<List<DashboardItem>>, expandedTaskFlow: Flow<Task?>) {
    val dashboardItems = dashboardItemsFlow.collectAsState(listOf())
    val expandedTask = expandedTaskFlow.collectAsState(initial = null)

    LazyColumn {
      items(dashboardItems.value) { dashboardItem ->
        when (dashboardItem) {
          is DashboardItem.TaskItem   -> TaskItem(taskItem = dashboardItem, expandedTask)
          is DashboardItem.HeaderItem -> HeaderItem(header = dashboardItem)
        }
      }
    }
  }

  @ExperimentalMaterialApi
  @Composable
  fun TaskItem(taskItem: DashboardItem.TaskItem, revealedTask: State<Task?>) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Icon(
        Icons.Default.Delete,
        contentDescription = "Localized description",
        modifier = Modifier.fillMaxHeight().align(Alignment.CenterStart))
      DraggableTaskCard(
        task = taskItem,
        isRevealed = taskItem.task == revealedTask.value,
        cardOffset = REVEALED_OFFSET,
        onExpandRight = { viewModel.expandTaskRight(taskItem.task) },
        onCollapse = { viewModel.expandTaskRight(null) }
      )
    }
  }

  @Composable
  @SuppressLint("UnusedTransitionTargetStateParameter")
  fun DraggableTaskCard(
    task: DashboardItem.TaskItem,
    isRevealed: Boolean,
    cardOffset: Float,
    onExpandRight: () -> Unit,
    onExpandLeft: () -> Unit,
    onCollapse: () -> Unit,
  ) {
    val offsetX = remember { mutableStateOf(0f) }
    val transitionState = remember {
      MutableTransitionState(isRevealed).apply {
        targetState = !isRevealed
      }
    }
    val transition = updateTransition(transitionState, label = "")
    val offsetTransition by transition.animateFloat(
      label = "cardOffsetTransition",
      transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
      targetValueByState = {
        when {
          isRevealed   -> cardOffset - offsetX.value
          else -> -offsetX.value
        }
      }
    )

    Card(
      elevation = 2.dp,
      backgroundColor = Color.White,
      shape = RoundedCornerShape(corner = CornerSize(4.dp)),
      modifier = Modifier
        .offset { IntOffset((offsetX.value + offsetTransition).roundToInt(), 0) }
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .fillMaxWidth()
        .pointerInput(Unit) {
          detectHorizontalDragGestures { change, dragAmount ->
            val newValue = Offset(x = (offsetX.value + dragAmount).coerceIn(-cardOffset, cardOffset), y = 0f)
            if (newValue.x >= 10) {
              onExpand()
              return@detectHorizontalDragGestures
            } else if (newValue.x <= 0) {
              onCollapse()
              return@detectHorizontalDragGestures
            }
            change.consumePositionChange()
            offsetX.value = newValue.x
          }
        }
    ) {
      Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        Text(task.task.taskName, style = MaterialTheme.typography.h6)
        Text(task.task.status.toString(), style = MaterialTheme.typography.caption)
      }
    }
  }

  @Composable
  fun HeaderItem(header: DashboardItem.HeaderItem) {
    Text(header.headerText, style = MaterialTheme.typography.h4, color = Color.White)
  }
}