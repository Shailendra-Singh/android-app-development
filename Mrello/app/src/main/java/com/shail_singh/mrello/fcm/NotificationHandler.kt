package com.shail_singh.mrello.fcm

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.shail_singh.mrello.Constants
import com.shail_singh.mrello.R
import com.shail_singh.mrello.models.NotificationModels
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class NotificationHandler(private val context: Context) : Callback<ResponseBody> {
    private val serverPublicKey: String =
        context.applicationInfo.metaData.getString(Constants.FCM_SERVER_KEY)!!

    fun sendNotification(token: String, title: String, body: String) {
        val notification = NotificationModels.Notification(title, body)
        val message = NotificationModels.Message(token, notification)
        sendRequest(message)
    }

    interface BoardNotificationService {
        @POST(Constants.FCM_SERVER_END_POINT)
        fun postNotification(
            @Header(Constants.FCM_AUTHORIZATION) authorizationToken: String,
            @Header(Constants.CONTENT_TYPE) contentType: String,
            @Body message: NotificationModels.Message
        ): Call<ResponseBody>
    }

    private val apiClient: Retrofit = Retrofit.Builder().baseUrl(Constants.FCM_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    private fun sendRequest(notificationMessage: NotificationModels.Message) {
        val authHeader = "key=$serverPublicKey"
        val service: BoardNotificationService =
            apiClient.create(BoardNotificationService::class.java)

        val serviceCall: Call<ResponseBody> = service.postNotification(
            authHeader, Constants.APPLICATION_JSON, notificationMessage
        )
        serviceCall.enqueue(this)
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        if (response.isSuccessful) {
            Log.i("FCM", response.isSuccessful.toString())
        } else {
            Log.e("FCM", response.code().toString())
            Toast.makeText(
                context, context.getString(R.string.error_member_notification), Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Log.e("FCM", "API ERROR")
    }
}