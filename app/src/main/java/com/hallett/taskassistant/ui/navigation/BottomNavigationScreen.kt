package com.hallett.taskassistant.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.hallett.taskassistant.corndux.BottomNavigationClicked

sealed interface BottomNavigationScreen {
    val route: String
    val labelResId: Int
    val icon: ImageVector
    val action: BottomNavigationClicked
}