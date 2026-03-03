package com.pulseb.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pulseb.app.scheduler.AlarmScheduler

/**
 * Reschedules the logging alarm after device reboot. Alarms are cleared on reboot,
 * so this keeps background logging working when the app is closed.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            Log.d(TAG, "Boot completed – rescheduling popup alarm")
            AlarmScheduler(context).scheduleNextTrigger()
        }
    }

    companion object {
        private const val TAG = "PulseB.Alarm"
    }
}
