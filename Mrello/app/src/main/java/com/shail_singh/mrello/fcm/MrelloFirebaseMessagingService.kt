package com.shail_singh.mrello.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shail_singh.mrello.R
import com.shail_singh.mrello.activities.MainActivity
import com.shail_singh.mrello.activities.auth.SignInActivity
import com.shail_singh.mrello.firebase.MrelloFirestore

class MrelloFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Check if message contains a data payload.
        Log.d(TAG, "${resources.getString(R.string.from)}${remoteMessage.from}")
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "${resources.getString(R.string.message_notification_body)}${it.title}")
            Log.d(TAG, "${resources.getString(R.string.message_notification_body)}${it.body}")
            sendNotification(it.title!!, it.body!!)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "${resources.getString(R.string.refreshed_token)}${token}")
    }

    private fun sendNotification(title: String, message: String) {
        val intent = if (MrelloFirestore().getCurrentId().isNotEmpty()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, SignInActivity::class.java)
        }
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        )
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = this.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this, channelId
        ).setContentTitle(title).setContentText(message).setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_round).setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Channel Mrello Title", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}