package com.inkspire.ebookreader.common

sealed interface MyResult<out D, out E: MyError> {
    data class Success<out D>(val data: D): MyResult<D, Nothing>
    data class Error<out E: MyError>(val error: E):
        MyResult<Nothing, E>
}

inline fun <T, E: MyError, R> MyResult<T, E>.map(map: (T) -> R): MyResult<R, E> {
    return when(this) {
        is MyResult.Error -> MyResult.Error(error)
        is MyResult.Success -> MyResult.Success(map(data))
    }
}

fun <T, E: MyError> MyResult<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}

inline fun <T, E: MyError> MyResult<T, E>.onSuccess(action: (T) -> Unit): MyResult<T, E> {
    return when(this) {
        is MyResult.Error -> this
        is MyResult.Success -> {
            action(data)
            this
        }
    }
}
inline fun <T, E: MyError> MyResult<T, E>.onError(action: (E) -> Unit): MyResult<T, E> {
    return when(this) {
        is MyResult.Error -> {
            action(error)
            this
        }
        is MyResult.Success -> this
    }
}

interface MyError
sealed interface DataError: MyError {
    enum class Remote : DataError {
        REQUEST_TIMEOUT,
        NO_INTERNET,
        UNKNOWN,
        SERIALIZATION,
        TOO_MANY_REQUESTS,
        SERVER,
        UNAUTHORIZED,
        NOT_FOUND,
        UNEXPECTED_CONTENT_TYPE_HTML,
        HTML_PARSING_FAILED,
        DOWNLOAD_CONFIRMATION_FAILED
    }

    enum class Local: DataError {
        DISK_FULL,
        UNKNOWN
    }
}

typealias EmptyResult<E> = MyResult<Unit, E>