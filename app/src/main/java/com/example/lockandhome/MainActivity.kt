package com.example.lockandhome

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 0)
        }
        val svc = Intent(this, WallpaperService::class.java)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }
        layout.addView(TextView(this).apply {
            text = "Sets your home and lock screen to lockandhome.png every time the device is unlocked."
            textSize = 16f
        })
        layout.addView(Button(this).apply {
            text = "Start"
            setOnClickListener { startForegroundService(svc) }
        })
        layout.addView(Button(this).apply {
            text = "Stop"
            setOnClickListener { stopService(svc) }
        })
        layout.addView(Button(this).apply {
            text = "Apply now"
            setOnClickListener { WallpaperService.applyWallpaper(this@MainActivity) }
        })
        setContentView(layout)

        startForegroundService(svc)
    }
}
