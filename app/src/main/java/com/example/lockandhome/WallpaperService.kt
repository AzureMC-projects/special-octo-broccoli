package com.example.lockandhome

import android.app.*
import android.content.*
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder

class WallpaperService : Service() {

    // SCREEN_ON / USER_PRESENT can't be declared in the manifest on modern Android,
    // so this foreground service registers the receiver at runtime.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = applyWallpaper(context)
    }

    override fun onCreate() {
        super.onCreate()
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel("wp", "Wallpaper keeper", NotificationManager.IMPORTANCE_MIN)
        )
        val notif = Notification.Builder(this, "wp")
            .setContentTitle("Lock And Home is active")
            .setSmallIcon(android.R.drawable.ic_menu_gallery)
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notif)
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(receiver, filter)
        }
        applyWallpaper(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    companion object {
        fun applyWallpaper(context: Context) {
            val app = context.applicationContext
            Thread {
                try {
                    app.assets.open("lockandhome.png").use { stream ->
                        val bmp = BitmapFactory.decodeStream(stream) ?: return@Thread
                        WallpaperManager.getInstance(app).setBitmap(
                            bmp, null, true,
                            WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }
}
