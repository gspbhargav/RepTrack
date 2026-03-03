# Popup every 15 seconds when app is closed – Permissions & settings

For the popup to appear every 15 seconds **even when the app is closed**, the following are required.

---

## 1. Permissions (manifest – already declared)

The app declares these in `AndroidManifest.xml`:

| Permission | Purpose |
|------------|--------|
| `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` | So the 15-second alarm can fire on time; makes PulseB appear in **Alarms & reminders**. |
| `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_DATA_SYNC` | So the service that shows the popup can run in the foreground. |
| `RECEIVE_BOOT_COMPLETED` | So the alarm is rescheduled after device reboot. |
| `USE_FULL_SCREEN_INTENT` | So the popup **activity** can be shown when the app is in the background (required for “open popup when app is closed”). |

The app also declares `SYSTEM_ALERT_WINDOW` (so PulseB appears in **Display over other apps**) and requests `POST_NOTIFICATIONS` at runtime on Android 13+. The main screen has a **Setup** section with buttons to open Display over other apps, App settings (Alarms & reminders, Battery), and Notification settings. The user may still need to adjust **settings** (see below).

---

## 2. Settings the user should enable

### A. Full-screen intent (needed so the popup opens when app is closed)

- **Android 10–13:** Often works by default; no special setting.
- **Android 14+:** This is a **special** permission. The user may need to turn it on:
  - **Settings → Apps → PulseB → Notifications**  
    Enable **“Allow full screen notifications”** or similar (wording depends on device).
  - Or: **Settings → Apps → Special app access → Display over other apps** or **Full screen intents** → enable for PulseB.

Without this, the 15-second trigger may only show a notification in the status bar instead of opening the popup.

### B. Exact alarms (so the 15-second alarm fires on time)

- **Settings → Apps → PulseB**  
  Look for **“Alarms & reminders”** or **“Schedule exact alarms”** and turn it **on** for PulseB.

If this is off, the system may delay or batch alarms and the popup may not come every 15 seconds.

### C. Battery / background (so the app isn’t killed when closed)

- **Settings → Apps → PulseB → Battery**  
  Set to **“Unrestricted”** (or “Don’t optimize” / “No restrictions”), so the app can run in the background and alarms keep firing when the app is closed.

On some OEMs (Samsung, Xiaomi, Huawei, etc.) there may be extra “battery saver” or “autostart” options; enabling them for PulseB helps reliability.

---

## 3. How it works

1. When the user opens the app at least once, the first alarm is scheduled (every 15 seconds).
2. Every 15 seconds the alarm fires → the system starts `LoggingForegroundService`.
3. The service shows a **high-priority notification** with a **full-screen intent** that launches `PopupActivity` (the search popup).
4. If **full-screen intent** is allowed, the popup activity opens on top (even when the app was closed).
5. The popup auto-closes after 4 seconds; the service schedules the next 15-second alarm and stops.
6. After reboot, `BootReceiver` reschedules the alarm so it keeps working.

---

## 4. Summary: what to give / enable for this app

- **Full-screen notifications** (or full-screen intent) for PulseB – so the popup opens when the app is closed.
- **Alarms & reminders** (exact alarms) for PulseB – so the 15-second timer is accurate.
- **Battery: Unrestricted** for PulseB – so the app can run in the background when closed.

If any of these are missing, the popup may only work when the app is open or may be delayed.
