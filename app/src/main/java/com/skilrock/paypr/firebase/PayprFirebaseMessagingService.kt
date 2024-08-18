package com.skilrock.paypr.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.skilrock.paypr.R
import com.skilrock.paypr.paypr_app.data_class.response_data.LoginResponseData
import com.skilrock.paypr.paypr_app.ui.activity.HomePayprActivity
import com.skilrock.paypr.paypr_app.ui.activity.SplashActivity
import com.skilrock.paypr.utility.*

class PayprFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        Log.d("log", "New Token: $newToken")
        SharedPrefUtils.setFcmToken(this, newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("log", "Remote From: " + remoteMessage.from)
        Log.d("log", "Remote Notification Message Body: " + remoteMessage.notification?.body)

        val data = remoteMessage.data
        Log.i("log", "Title: " + data["title"])
        Log.i("log", "Notification Message Body: " + data["body"])

        val displayNotification = data["displayNotification"] ?: "YES"
        if (displayNotification.equals("YES", true))
            showNotification(this, data["title"] ?: "NA", data["body"] ?: "NA", data["channel"] ?: "NA")
    }

    private fun showNotification(context: Context, title: String, body: String, channel: String) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1

        val channelId: String
        val channelName: String

        when (channel) {
            NOTIFICATION_CHANNEL_TRANSACTIONAL -> {
                channelId   = NOTIFICATION_CHANNEL_TRANSACTIONAL
                channelName = "These notifications are for transactional purpose."
            }
            NOTIFICATION_CHANNEL_ALERT -> {
                channelId   = NOTIFICATION_CHANNEL_ALERT
                channelName = "These notifications are for alert purpose."
            }
            else -> {
                channelId   = NOTIFICATION_CHANNEL_PROMOTIONAL
                channelName = "These notifications are for promotion purpose."
            }
        }

        val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notification_tone)
        val attributes: AudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mChannel.setSound(sound, attributes)
            mChannel.lightColor = Color.RED
            notificationManager.createNotificationChannel(mChannel)
        }

        val notifyPendingIntent: PendingIntent

        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            PlayerInfo.destroy()
            val pendingIntent = Intent(this, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            notifyPendingIntent = PendingIntent.getActivity(this, 0, pendingIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PlayerInfo.setLoginData(Gson().fromJson(SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_DATA), LoginResponseData::class.java))
            notifyPendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(HomePayprActivity::class.java)
                .setGraph(R.navigation.paypr_nav_graph)
                .setDestination(R.id.profileFragment)
                //.setArguments(bundle)
                .createPendingIntent()
        }

        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_svg_paypr_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(notifyPendingIntent)
            .setLights(Color.RED, 3000, 3000)
            .setVibrate(longArrayOf(100000, 10000))
            .setSound(sound)
            .setColor(ContextCompat.getColor(context, R.color.destructive))
            .setAutoCancel(true)

        notificationManager.notify(notificationId, mBuilder.build())
    }

}