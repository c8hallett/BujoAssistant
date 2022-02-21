package com.hallett.taskassistant.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface BottomNavigationScreen {
    val route: String
    val labelResId: Int
    val icon: ImageVector
}