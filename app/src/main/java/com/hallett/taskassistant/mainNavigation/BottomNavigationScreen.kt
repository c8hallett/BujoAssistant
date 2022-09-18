package com.hallett.taskassistant.mainNavigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.hallett.taskassistant.mainNavigation.corndux.BottomNavigationClicked

sealed interface BottomNavigationScreen {
    val route: String
    val labelResId: Int
    val icon: ImageVector
    val action: BottomNavigationClicked
}