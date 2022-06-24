package com.example.myfragments.core.util.dataHelper


sealed class ResultWrapper<out T, out L>() {

    data class Success<out T>(val code: Int? = null, val value: T?) : ResultWrapper<T, Nothing>()

    data class ErrorResponse<L>(val code: Int? = null, val error: L?) : ResultWrapper<Nothing, L>()

    data class NetworkError(val code: Int? = null, val message: String? = null) :
        ResultWrapper<Nothing, Nothing>()

}