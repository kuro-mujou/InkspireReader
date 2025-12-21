package com.inkspire.ebookreader.ui.bookcontent.drawer

sealed interface DrawerAction {
    data object CloseDrawer : DrawerAction
    data object OpenDrawer : DrawerAction
    data class ChangeTabIndex(val index: Int) : DrawerAction
    data class UpdateDrawerAnimateState(val isAnimating: Boolean) : DrawerAction
}