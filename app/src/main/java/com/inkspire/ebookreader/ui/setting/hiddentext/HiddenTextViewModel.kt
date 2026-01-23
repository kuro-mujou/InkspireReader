package com.inkspire.ebookreader.ui.setting.hiddentext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkspire.ebookreader.domain.repository.HiddenTextRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HiddenTextViewModel(
    private val hiddenTextRepository: HiddenTextRepository
): ViewModel() {
    private val _state = MutableStateFlow(HiddenTextState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _state.value
    )

    init {
        viewModelScope.launch {
            hiddenTextRepository.getHiddenTextsFlow().collectLatest { hiddenTexts ->
                _state.update { it.copy(hiddenTexts = hiddenTexts) }
            }
        }
    }

    fun onAction(action: HiddenTextAction) {
        when (action) {
            is HiddenTextAction.OnDeleteHiddenTexts -> {
                viewModelScope.launch {
                    hiddenTextRepository.deleteHiddenTexts(_state.value.hiddenTexts.filter { it.isSelected }.map { it.id })
                }
            }
            is HiddenTextAction.ToggleHiddenTextSelectedState -> {
                _state.update { currentState ->
                    val newHiddenTexts = currentState.hiddenTexts.map {
                        if (it.id == action.item.id) {
                            it.copy(isSelected = !it.isSelected)
                        } else {
                            it
                        }
                    }
                    currentState.copy(
                        hiddenTexts = newHiddenTexts
                    )
                }
            }
        }
    }
}