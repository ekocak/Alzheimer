package com.ekremkocak.alzheimer.data.sealed

sealed class FlowState<out T> {
    object Loading : FlowState<Nothing>()
    data class Success<out T>(val data: T) : FlowState<T>()
    data class Error(val exception: Exception) : FlowState<Nothing>()
}