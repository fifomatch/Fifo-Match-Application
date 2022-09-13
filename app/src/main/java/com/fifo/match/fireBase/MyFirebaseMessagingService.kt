package com.fifo.match.fireBase

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fifo.match.App
import com.fifo.match.R
import com.fifo.match.ui.activity.home.HomeActivity
import com.fifo.match.ui.activity.match.MatchActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val NOTIFICATION_CHANNEL = "FIFO match"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.notification != null) {
            Log.d("TAGnotification", "onMessageReceived: ")


            if (!App.isChatOpen) {
                sendNotification(
                    remoteMessage.notification?.body ?: "", "Other", 1,
                    "", "", "", "", "", ""
                )
            }else if (remoteMessage.notification?.title?.contains("sent a message") == false){
                sendNotification(
                    remoteMessage.notification?.body ?: "", "Other", 1,
                    "", "", "", "", "", ""
                )
            }
        }
    }

    private fun sendNotification(tittle: String, intentTitle: String, notificationId: Int, senderid: String, senderImage: String, senderName: String, receverImage: String, receiverId: String, room_id: String) {
        var intent: Intent? = null
        intent = Intent(this, HomeActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager1 = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager1.cancelAll()
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentTitle(getString(R.string.app_name))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_logo_old)
            .setColor(resources.getColor(R.color.orange))
            .setStyle(NotificationCompat.BigTextStyle().bigText(tittle))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            val attributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
            channel.enableLights(true)
            channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
        try {
           /* val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            notificationBuilder.setSound(uri)*/


            val notification: Notification = notificationBuilder.build()
            notification.sound = Uri.parse(("android.resource://$packageName") + "/" + R.raw.android_notification
            )
        }catch (e:Exception){}

    }

    private fun getIntent(tittle: String, id: String, image: String, senderName: String, receverImage: String, receiverId: String): Intent? {
        var intent: Intent? = null
        val bundle = Bundle()
        try {
            intent = Intent(this, MatchActivity::class.java)
            intent.action = "" + System.currentTimeMillis()
            val bundle = Bundle()
            if (tittle.equals("Date Invitation")) {
                bundle.putString("from", "Date Invitation");
                bundle.putString("sender_image", image);
                bundle.putString("sender_id", id);
                bundle.putString("sender_name", senderName);
            } else {
                bundle.putString("from", "User Matched");
                bundle.putString("sender_image", image);
                bundle.putString("sender_name", senderName)
                bundle.putString("receiver_image", receverImage)
                bundle.putString("sender_id", id)
                bundle.putString("receiver_id", receiverId)
            }
            intent.putExtra("bundle", bundle)
        } catch (e: java.lang.Exception) {
            e.fillInStackTrace()
        }
        return intent
    }

}