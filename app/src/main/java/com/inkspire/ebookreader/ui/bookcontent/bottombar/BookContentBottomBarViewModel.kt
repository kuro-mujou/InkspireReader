package com.inkspire.ebookreader.ui.bookcontent.bottombar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.ui.bookcontent.bottombar.common.BottomBarMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BookContentBottomBarViewModel: ViewModel(){
    private val _state = MutableStateFlow(BookContentBottomBarState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    fun onAction(action: BookContentBottomBarAction) {
        when (action) {
            is BookContentBottomBarAction.ChangeBottomBarVisibility -> {
                _state.update { currentState ->
                    val nextMode = if (!currentState.bottomBarVisibility && !currentState.bottomBarMode.isPersistent) {
                        BottomBarMode.Main
                    } else {
                        currentState.bottomBarMode
                    }
                    currentState.copy(
                        bottomBarVisibility = !currentState.bottomBarVisibility,
                        bottomBarMode = nextMode
                    )
                }
            }
            is BookContentBottomBarAction.ResetBottomBarMode -> {
                _state.update { it.copy(bottomBarMode = BottomBarMode.Main) }
            }
            is BookContentBottomBarAction.AutoScrollIconClicked -> {
                _state.update { it.copy(bottomBarMode = BottomBarMode.AutoScroll) }
            }
            is BookContentBottomBarAction.SettingIconClicked -> {
                _state.update { it.copy(bottomBarMode = BottomBarMode.Settings) }
            }
            is BookContentBottomBarAction.ThemeIconClicked -> {
                _state.update { it.copy(bottomBarMode = BottomBarMode.Theme) }
            }
            is BookContentBottomBarAction.TtsIconClicked -> {
                _state.update { it.copy(bottomBarMode = BottomBarMode.Tts) }
            }
        }
    }
}