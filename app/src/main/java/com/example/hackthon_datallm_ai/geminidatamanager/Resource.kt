package com.example.hackthon_datallm_ai.geminidatamanager

sealed class   Resource<T>(
    val data: T? = null,
    val message: String? = null
) {


    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T> : Resource<T>()
    class Stop<T>:Resource<T>()
}