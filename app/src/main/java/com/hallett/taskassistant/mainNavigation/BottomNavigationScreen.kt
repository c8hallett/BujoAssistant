package com.hallett.taskassistant.mainNavigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.hallett.taskassistant.corndux.performers.actions.BottomNavigationClicked

sealed interface BottomNavigationScreen {
    val route: String
    val labelResId: Int
    val icon: ImageVector
    val action: BottomNavigationClicked
}