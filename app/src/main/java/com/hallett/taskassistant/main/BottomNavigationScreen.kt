package com.hallett.taskassistant.main

import androidx.compose.ui.graphics.vector.ImageVector
import com.hallett.taskassistant.main.corndux.ClickBottomNavigation

sealed interface BottomNavigationScreen {
    val route: String
    val labelResId: Int
    val icon: ImageVector
    val action: ClickBottomNavigation
}