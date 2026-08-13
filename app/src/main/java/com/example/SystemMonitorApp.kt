package com.example

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.util.NotificationHelper

class SystemMonitorApp : Application() {

    private var hasNotifiedThisEpisode = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                val tempCelsius = tempTenths / 10f
                val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)

                val isOverheat = tempCelsius >= 40.0f || health == BatteryManager.BATTERY_HEALTH_OVERHEAT

                if (isOverheat && !hasNotifiedThisEpisode) {
                    hasNotifiedThisEpisode = true
                    NotificationHelper.showOverheatNotification(context, tempCelsius)
                } else if (tempCelsius < 38.0f && health != BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                    // Reset single notification trigger when temperature drops back down
                    hasNotifiedThisEpisode = false
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }
}
