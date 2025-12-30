package com.inkspire.ebookreader.common

sealed interface UiState<out T> {
    data object None : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data object Empty : UiState<Nothing>

    data class Error(val throwable: Throwable) : UiState<Nothing>
}

val UiState<*>.isNone: Boolean
    get() = this is UiState.None

val UiState<*>.isLoading: Boolean
    get() = this is UiState.Loading

val UiState<*>.isSuccess: Boolean
    get() = this is UiState.Success<*>

val UiState<*>.isEmpty: Boolean
    get() = this is UiState.Empty

val UiState<*>.isError: Boolean
    get() = this is UiState.Error