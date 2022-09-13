package com.fifo.match.network.utils

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import com.fifo.match.R
import com.fifo.match.network.AuthenticationException
import com.fifo.match.network.NetworkErrorException
import com.fifo.match.network.NetworkResult
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*


object Utility {

    fun convertToRequestBody(params: Map<String, Any>): RequestBody? {
        var requestBody: RequestBody? = null
        try {
            requestBody = Gson().toJson(params).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return requestBody
    }
    fun <T> parseJson(response: String, className: Class<T>) = Gson().fromJson<T>(response, className)

    fun <T> toJson(response: T) = Gson().toJson(response)

    fun resolveError(e: Exception): NetworkResult.Error {
        var error = e

        when (e) {
            is SocketTimeoutException -> {
                error = NetworkErrorException(errorMessage = "connection error!")
            }
            is ConnectException -> {
                error = NetworkErrorException(errorMessage = "no internet access!")
            }
            is UnknownHostException -> {
                error = NetworkErrorException(errorMessage = "no internet access!")
            }
            is JSONException -> {
                error = NetworkErrorException(errorMessage = "${e.localizedMessage}")
            }
        }

        if(e is HttpException){
            when(e.code()){
                502 -> {
                    error = NetworkErrorException(e.code(),  "internal error!")
                }
                401 -> {
                    throw AuthenticationException("authentication error!")
                }
                400 -> {
                    error = NetworkErrorException.parseException(e)
                }
            }
        }
        return NetworkResult.Error(error)
    }

    fun getFormattedDateNotification(context: Context, smsTimeInMilis: String): String? {
        var smsTimeInMilis = smsTimeInMilis.toLong()
        try {
            smsTimeInMilis = smsTimeInMilis + 1000
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val weekdateTimeFormatString = "EEEE"
            val dateTimeFormatString = "d MMM"
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
                var time =
                    DateUtils.getRelativeTimeSpanString(smsTimeInMilis)
                        .toString()
                if (time.contains(" minutes ago")) {
                    time = time.replace(" minutes ago", "m")
                } else if (time.contains(" hours ago")) {
                    time = time.replace(" hours ago", "h")
                } else if (time.contains(" hour ago")) {
                    time = time.replace(" hour ago", "h")
                } else if (time.contains(" minute ago")) {
                    time = time.replace(" minute ago", "m")
                }
                if (time.equals("0m", ignoreCase = true)) {
                    time = "Now"
                } else if (time.equals("0 min", ignoreCase = true)) {
                    time = "Now"
                } else if (time.equals("+0 min", ignoreCase = true)) {
                    time = "Now"
                } else if (time.equals("In 0 minutes", ignoreCase = true)) {
                    time = "Now"
                }
                if (time.contains("-")) {
                    time = time.replace("-", "")
                }
                time
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                context.resources.getString(R.string.yesterday)
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
            ) {
                val dateNow = now[Calendar.DATE]
                val dateSMS = smsTime[Calendar.DATE]
                val result = dateNow - dateSMS
                if (result < 7) {
                    var timeNew =
                        DateFormat.format(weekdateTimeFormatString, smsTime)
                            .toString()
                    if (timeNew.equals("0m", ignoreCase = true)) {
                        timeNew = "Now"
                    } else if (timeNew.equals("0 min", ignoreCase = true)) {
                        timeNew = "Now"
                    } else if (timeNew.equals("+0 min", ignoreCase = true)) {
                        timeNew = "Now"
                    } else if (timeNew.equals("In 0 minutes", ignoreCase = true)) {
                        timeNew = "Now"
                    }
                    timeNew
                } else {
                    var timeNew =
                        DateFormat.format(dateTimeFormatString, smsTime)
                            .toString()
                    if (timeNew.equals("0m", ignoreCase = true)) {
                        timeNew = "Now"
                    } else if (timeNew.equals("0 min", ignoreCase = true)) {
                        timeNew = "Now"
                    } else if (timeNew.equals("+0 min", ignoreCase = true)) {
                        timeNew = "Now"
                    } else if (timeNew.equals("In 0 minutes", ignoreCase = true)) {
                        timeNew = "Now"
                    }
                    timeNew
                }
            } else {
                var timeNew =
                    DateFormat.format("d MMM yyyy", smsTime).toString()
                if (timeNew.equals("0m", ignoreCase = true)) {
                    timeNew = "Now"
                } else if (timeNew.equals("0 min", ignoreCase = true)) {
                    timeNew = "Now"
                } else if (timeNew.equals("+0 min", ignoreCase = true)) {
                    timeNew = "Now"
                } else if (timeNew.equals("In 0 minutes", ignoreCase = true)) {
                    timeNew = "Now"
                }
                timeNew
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }


    fun getDate(time: String): String {
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis = java.lang.Long.parseLong(time)
        return DateFormat.format("HH:mm", cal).toString()
    }



}