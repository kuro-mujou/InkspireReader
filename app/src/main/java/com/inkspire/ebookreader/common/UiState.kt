package com.inkspire.ebookreader.common

sealed interface UiState<out T> {
    data object None : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data object Empty : UiState<Nothing>

    data class Error(val throwable: Throwable) : UiState<Nothing>
}