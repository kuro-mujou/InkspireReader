package com.inkspire.ebookreader.ui.bookcontent.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DrawerViewModel(): ViewModel(){
    private val _state = MutableStateFlow(DrawerState())
    val state: StateFlow<DrawerState> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _state.value
    )

    fun onAction(action: DrawerAction) {
        when(action) {
            is DrawerAction.CloseDrawer -> {
                _state.update {
                    it.copy(
                        visibility = false,
                        fromUser = true
                    )
                }
            }
            is DrawerAction.OpenDrawer -> {
                _state.update {
                    it.copy(
                        visibility = true,
                        fromUser = true
                    )
                }
            }
            is DrawerAction.ChangeTabIndex -> {
                _state.update { it.copy(selectedTabIndex = action.index) }
            }
            is DrawerAction.UpdateDrawerAnimateState -> {
                _state.update {
                    it.copy(
                        isAnimating = action.isAnimating,
                        fromUser = action.isAnimating
                    )
                }
            }
        }
    }
}