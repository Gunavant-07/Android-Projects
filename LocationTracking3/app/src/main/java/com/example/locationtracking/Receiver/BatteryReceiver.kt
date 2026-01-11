package com.example.locationtracking.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.example.locationtracking.Activity.MainActivity

class BatteryReceiver(private val listener: MainActivity) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPercentage = (level * 100 / scale.toFloat()).toInt()

            // Notify the listener about the battery percentage change
            listener.onBatteryPercentageChanged(batteryPercentage)
        }
    }

    interface BatteryChangeListener {
        fun onBatteryPercentageChanged(percentage: Int)
    }
}