package com.hallett.bujoass.presentation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissValue
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.DismissValue.DismissedToEnd
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallett.bujoass.databinding.FragmentDashboardBinding
import com.hallett.bujoass.presentation.PresentationMessage
import com.hallett.bujoass.presentation.model.Task
import com.hallett.bujoass.presentation.ui.BujoAssFragment
import com.hallett.bujoass.presentation.ui.task_list.TaskSwipeHelper
import com.hallett.bujoass.presentation.ui.task_list.TaskSwipeHelper.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.Flow

@ExperimentalMaterialApi
class DashboardFragment(): BujoAssFragment() {
  private companion object {
    const val ANIMATION_DURATION = 300
  }

  private val viewModel: DashboardFragmentViewModel by lazy {
    ViewModelProvider(this, vmpfactory).get(DashboardFragmentViewModel::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = ComposeView(requireContext()).apply {
    setContent {
      TaskList(viewModel.observeDashboardItems())
    }
  }

  @Composable
  fun TaskList(dashboardItemsFlow: Flow<List<DashboardItem>>) {
    val dashboardItems = dashboardItemsFlow.collectAsState(listOf())

    LazyColumn {
      items(dashboardItems.value) { dashboardItem ->
        when (dashboardItem) {
          is DashboardItem.TaskItem   -> TaskItem(task = dashboardItem)
          is DashboardItem.HeaderItem -> HeaderItem(header = dashboardItem)
        }
      }
    }
  }

  @ExperimentalMaterialApi
  @Composable
  fun TaskItem(task: DashboardItem.TaskItem) {
    val dismissState = rememberDismissState(confirmStateChange = {
      when (it) {
        DismissedToEnd   -> false
        DismissedToStart -> false
        Default          -> false
      }
    })
    SwipeToDismiss(state = dismissState, directions = setOf(StartToEnd, EndToStart), background = {
      val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
      val color by animateColorAsState(
        when (dismissState.targetValue) {
          Default          -> Color.LightGray
          DismissedToEnd   -> Color.Green
          DismissedToStart -> Color.Red
        }
      )
      val alignment = when (direction) {
        StartToEnd -> Alignment.CenterStart
        EndToStart -> Alignment.CenterEnd
      }
      val icon = when (direction) {
        StartToEnd -> Icons.Default.Done
        EndToStart -> Icons.Default.Delete
      }
      val scale by animateFloatAsState(
        if (dismissState.targetValue == Default) 0.75f else 1f
      )

      Box(
        Modifier
          .fillMaxSize()
          .background(color)
          .padding(horizontal = 20.dp), contentAlignment = alignment
      ) {
        Icon(
          icon, contentDescription = "Localized description", modifier = Modifier.scale(scale)
        )
      }
    }, dismissContent = {
      Card(
        modifier = Modifier
          .padding(horizontal = 8.dp, vertical = 8.dp)
          .fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(corner = CornerSize(4.dp))
      ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
          Text(task.task.taskName, style = MaterialTheme.typography.h6)
          Text(task.task.status.toString(), style = MaterialTheme.typography.caption)
        }
      }
    })
  }

  @Composable
  fun DraggableTaskCard(
    task: DashboardItem.TaskItem,
    isRevealed: Boolean,
    cardOffset: Float,
    onExpand: () -> Unit,
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
      targetValueByState = { if (isRevealed) cardOffset - offsetX.value else -offsetX.value },
    )

    Card(
      elevation = 2.dp,
      backgroundColor = Color.White,
      shape = RoundedCornerShape(corner = CornerSize(4.dp)),
      modifier = Modifier
      .offset{ IntOffset((offsetX.value + offsetTransition).roundToInt(), 0) }
      .padding(horizontal = 8.dp, vertical = 8.dp)
      .fillMaxWidth()
      .pointerInput(Unit) {
        detectHorizontalDragGestures { change, dragAmount ->
          val original = Offset(offsetX.value, 0f)
          val summed = original + Offset(x = dragAmount, y = 0f)
          val newValue = Offset(x = summed.x.coerceIn(0f, cardOffset), y = 0f)
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