/*
 * com.ksfams.sgframework.network
 *
 * Created on 2021-01-25
 * 
 * Copyright (c) 1999-2021 4-LAB Co., Ltd. All Rights Reserved.
 */
package com.ksfams.sgframework.network

import com.ksfams.sgframework.models.enums.StatusCode
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Response

/**
 *
 * Retrofit standard response interface
 * `sealed class` : class 조합 enum 처리 가능
 *
 * Create Date 2021-01-25
 * @version 1.00        2021-01-25
 * @since   1.00
 * @see
 * @author  강성훈(shkang@4-lab.com)
 * Revision History
 * who              when            what
 * shkang         2021/01/25     변경내용을 기술합니다.
 */

sealed class ApiResponse<out T> {

    /**
     * API Success response class from OkHttp request call.
     * The [data] is a nullable generic type. (A response without data)
     *
     * @param response A response from OkHttp request call.
     *
     * @property statusCode [StatusCode] is Hypertext Transfer Protocol (HTTP) response status codes.
     * @property headers The header fields of a single HTTP message.
     * @property raw The raw response from the HTTP client.
     * @property data The de-serialized response body of a successful data.
     */
    data class Success<T>(val response: Response<T>) : ApiResponse<T>() {
        val statusCode: StatusCode = getStatusCodeFromResponse(response)
        val headers: Headers = response.headers()
        val raw: okhttp3.Response = response.raw()
        val data: T? = response.body()
    }


    sealed class Failure<T> {

        data class Error<T>(val response: Response<T>) : ApiResponse<T>() {
            val statusCode: StatusCode = getStatusCodeFromResponse(response)
            val headers: Headers = response.headers()
            val raw: okhttp3.Response = response.raw()
            val errorBody: ResponseBody? = response.errorBody()

            override fun toString(): String = "[ApiResponse.Failure.Error-$statusCode] errorResponse: $response"
        }
    }

    companion object {

        fun <T> getStatusCodeFromResponse(response: Response<T>): StatusCode {
            return StatusCode.values().find { it.code == response.code() } ?: StatusCode.Unknown
        }
    }
}