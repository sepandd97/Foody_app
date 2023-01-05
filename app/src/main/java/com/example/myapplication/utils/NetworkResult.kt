package com.example.myapplication.utils

sealed class NetworkResult<T>(
    val data: T? =null,
    val messages: String?=null
) {
    class Success<T>(data: T):NetworkResult<T>(data)
    class  Error<T>(messages: String?,data: T? = null):NetworkResult<T>(data,messages)
    class  Loading<T>:NetworkResult<T>()

}