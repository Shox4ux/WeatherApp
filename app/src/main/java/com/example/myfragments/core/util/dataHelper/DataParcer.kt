package com.example.myfragments.core.util.dataHelper

import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend  inline fun <T, reified E> parseResponse(call: suspend () -> Response<T?>): ResultWrapper<T?, E> {

    return try {
        val resp = call.invoke()

        if (resp.isSuccessful) {
            val body = resp.body()
            ResultWrapper.Success(value = body)
        } else {
            try {
                if (resp.errorBody() != null) {
                    val errorResponse = Gson().fromJson(resp.errorBody()!!.string(), E::class.java)
                    ResultWrapper.ErrorResponse(
                        code = resp.code(),
                        error = errorResponse
                    )

                } else {
                    ResultWrapper.NetworkError(resp.code())
                }
            } catch (e: IOException) {
                ResultWrapper.NetworkError(resp.code(), e.message)
            } catch (e: HttpException) {
                val code = e.code()
                val errorResponse =
                    Gson().fromJson(e.response()?.errorBody()?.string(), E::class.java)
                ResultWrapper.ErrorResponse(code, errorResponse)
            } catch (e: Exception) {
                ResultWrapper.NetworkError(resp.code())
            }
        }


    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> {
                ResultWrapper.NetworkError(null, throwable.message)
            }
            is HttpException -> {
                val code = throwable.code()
                val errorResponse =
                    Gson().fromJson(throwable.response()?.errorBody()?.string(), E::class.java)
                ResultWrapper.ErrorResponse(code, errorResponse)
            }
            else -> {
                ResultWrapper.ErrorResponse(null, null)
            }
        }
    }

}

