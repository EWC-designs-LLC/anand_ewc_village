package com.golfcart.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.golfcart.R
import com.golfcart.ui.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // declare a variable to check is app in background
    var appinbackground = false

    private var notifManager: NotificationManager? = null
    val CHANNEL_ONE_ID = "0"

    private var mChannel: NotificationChannel? = null
    var mpref: SharedPreferences? = null
    var context: Context? = null

    var classname: Class<*>? = null

    var bundle = Bundle()

    override fun onNewToken(token: String) {

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        context = this
        //ShortcutBadger
        if (remoteMessage.data.isEmpty()) {
            CustomNotification("VillagesGPS", "Message", SplashActivity::class.java, bundle)
        } else {
            val jsonObject = JSONObject(remoteMessage.data as Map<*, *>?)
            try {
                CustomNotification(
                    "VillagesGPS", jsonObject.getString("message"),
                    SplashActivity::class.java, bundle
                )
                   setBadgeSamsung(context!!, jsonObject.getInt("badge"));
            } catch (e: Exception) {
            }
        }
    }


    private fun setBadgeSamsung(context: Context, count: Int) {
        val launcherClassName = getLauncherClassName(context) ?: return
        val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
        intent.putExtra("badge_count", count)
        intent.putExtra("badge_count_package_name", context.packageName)
        intent.putExtra("badge_count_class_name", launcherClassName)
        context.sendBroadcast(intent)
    }


    private fun getLauncherClassName(context: Context): String? {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfos) {
            val pkgName = resolveInfo.activityInfo.applicationInfo.packageName
            if (pkgName.equals(context.packageName, ignoreCase = true)) {
                return resolveInfo.activityInfo.name
            }
        }
        return null
    }

    private fun receivedNotification(message: String, label: String) {
        //appinbackground=mpref.getBoolean(.appinbackground,false);
        if (appinbackground) {
        } else {
            CustomNotification("VillageGPS", message, SplashActivity::class.java, bundle)
        }
    }

    private fun CustomNotification(
        title: String,
        description: String,
        destination: Class<*>,
        bundle: Bundle
    ) {
        if (notifManager == null) {
            notifManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder: NotificationCompat.Builder
            val intent = Intent(this, destination) // new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtras(bundle)
            val pendingIntent: PendingIntent
            if (mChannel == null) {
                mChannel =
                    NotificationChannel(CHANNEL_ONE_ID, title, NotificationManager.IMPORTANCE_HIGH)
                mChannel!!.description = description
                mChannel!!.enableVibration(true)
                mChannel!!.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notifManager!!.createNotificationChannel(mChannel!!)
            }
            builder = NotificationCompat.Builder(this, CHANNEL_ONE_ID)
            //  .setBadgeIconType(R.drawable.applogo)
            intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_MUTABLE)
            builder.setContentTitle(title)
                .setSmallIcon(getNotificationIcon()) // required
                .setContentText(description) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.applogo))
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            val notification = builder.build()
            notifManager!!.notify(0, notification)
        } else {
            val intent = Intent(this, destination)
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            var pendingIntent: PendingIntent? = null
            pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_MUTABLE)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                .setSound(defaultSoundUri)
                .setSmallIcon(getNotificationIcon())
                .setContentIntent(pendingIntent)
                .setStyle(
                    NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(description)
                )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(100, notificationBuilder.build())
        }
    }

    @SuppressLint("MutableImplicitPendingIntent")
    private fun CustomNotification1(title: String, description: String) {
        if (notifManager == null) {
            notifManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder: NotificationCompat.Builder
            val intent = Intent() // new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent: PendingIntent
            if (mChannel == null) {
                mChannel =
                    NotificationChannel(CHANNEL_ONE_ID, title, NotificationManager.IMPORTANCE_HIGH)
                mChannel!!.description = description
                mChannel!!.enableVibration(true)
                mChannel!!.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notifManager!!.createNotificationChannel(mChannel!!)
            }
            builder = NotificationCompat.Builder(this, CHANNEL_ONE_ID)
            intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            pendingIntent =
                PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_IMMUTABLE)
            builder.setContentTitle(title)
                .setSmallIcon(getNotificationIcon()) // required
                .setContentText(description) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.applogo))
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            val notification = builder.build()
            notifManager!!.notify(0, notification)
        } else {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            var pendingIntent: PendingIntent? = null
            pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_MUTABLE)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                .setSound(defaultSoundUri)
                .setSmallIcon(getNotificationIcon())
                .setContentIntent(pendingIntent)
                .setStyle(
                    NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(description)
                )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(100, notificationBuilder.build())
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.applogo else R.drawable.applogo
    }

}