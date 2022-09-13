package com.fifo.match.network

import com.fifo.match.network.entities.Result


sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: Result<T>, message: String) : NetworkResult<Result<T>>(data)
    object Loading : NetworkResult<Nothing>()
    data class Error(var exception: Throwable) : NetworkResult<Nothing>()
}

/*
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: Result<T>) : NetworkResult<Result<T>>(data)
    object Loading : NetworkResult<Nothing>()
    data class Error(var exception: Throwable) : NetworkResult<Nothing>()
}*/
