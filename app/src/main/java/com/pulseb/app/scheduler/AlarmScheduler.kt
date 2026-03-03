package com.pulseb.app.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.pulseb.app.service.LoggingForegroundService

/**
 * Schedules alarms for triggering the logging service. On Android 12+ (API 31+), exact alarms
 * require SCHEDULE_EXACT_ALARM or USE_EXACT_ALARM. If the app doesn't have permission,
 * falls back to inexact alarm to avoid SecurityException.
 */
class AlarmScheduler(private val context: Context) {

    fun scheduleNextTrigger() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = SystemClock.elapsedRealtime() + INTERVAL_MS
        val pendingIntent = createTriggerPendingIntent()
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canExact) {
                Log.w(TAG, "Alarm scheduled (inexact): exact alarm permission off – may be delayed in Doze. Enable Alarms & reminders for this app.")
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                Log.d(TAG, "Alarm scheduled (exact), next in ${INTERVAL_MS / 1000}s")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val clockInfo = AlarmManager.AlarmClockInfo(
                        System.currentTimeMillis() + INTERVAL_MS,
                        null
                    )
                    alarmManager.setAlarmClock(clockInfo, pendingIntent)
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Alarm schedule failed, using inexact", e)
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun createTriggerPendingIntent(): PendingIntent {
        val intent = Intent(context, LoggingForegroundService::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getForegroundService(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            @Suppress("DEPRECATION")
            PendingIntent.getService(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    companion object {
        private const val TAG = "PulseB.Alarm"
        private const val REQUEST_CODE = 2001
        private const val INTERVAL_MS = 15 * 1000L // 15 seconds
    }
}
