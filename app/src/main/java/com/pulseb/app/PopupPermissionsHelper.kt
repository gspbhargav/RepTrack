package com.pulseb.app

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 * Opens system settings so the user can enable everything needed for the popup
 * (alarms, display over other apps, notifications, battery). PulseB will appear
 * in "Alarms & reminders" and "Display over other apps" once the app is installed
 * and has declared the permissions; these helpers take the user to the right screens.
 */
object PopupPermissionsHelper {

    /**
     * Opens the "Display over other apps" screen for this app.
     * PulseB will appear in that list because we declare SYSTEM_ALERT_WINDOW.
     */
    fun openOverlaySettings(context: Context): Boolean {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Opens the "Alarms & reminders" (schedule exact alarm) settings for this app.
     * Requires Android 12+ (API 31). The app must declare SCHEDULE_EXACT_ALARM in the manifest
     * to appear in that list. If the system intent is not available, falls back to app detail settings.
     */
    fun openAlarmsAndRemindersSettings(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            openAppDetailSettings(context)
            return true
        }
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            openAppDetailSettings(context)
        }
    }

    /**
     * Opens this app's detail settings. From there the user can:
     * - Set Battery to Unrestricted
     * - Configure Notifications (e.g. allow full-screen notifications)
     */
    fun openAppDetailSettings(context: Context): Boolean {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Opens the app's notification settings (channel list or app notifications).
     * User can enable "Full screen notifications" / full-screen intent for the popup.
     */
    @RequiresApi(26)
    fun openNotificationSettings(context: Context): Boolean {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    fun canDrawOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
}
