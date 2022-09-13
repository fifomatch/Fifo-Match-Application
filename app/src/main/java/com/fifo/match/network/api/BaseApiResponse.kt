package com.fifo.match.network.api

import com.fifo.match.network.NetworkResult
import com.fifo.match.network.entities.Result
import com.fifo.match.network.utils.Utility
import org.json.JSONObject
import retrofit2.Response

abstract class BaseApiResponse {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<Result<T>>): NetworkResult<*> {
        try {
            val response = apiCall.invoke()

            /*if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    if(body.status==200 || body.status == 201 || body.status == 402|| body.status == 401) return NetworkResult.Success( body) else error(body.message)
                }
            }*/
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null)
                    return if(body.status !=200 && body.status !=201)
                        NetworkResult.Error(Throwable(body.message))
                    else
                        NetworkResult.Success(body,body.message)
            }
            return if (response.errorBody() != null) {
                    val e = JSONObject(response.errorBody()!!.string()).getString("message")
                    error(e)
            } else
                error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return Utility.resolveError(e)
        }
    }
    private fun error(errorMessage: String): NetworkResult.Error =
        NetworkResult.Error(Throwable(errorMessage))
}