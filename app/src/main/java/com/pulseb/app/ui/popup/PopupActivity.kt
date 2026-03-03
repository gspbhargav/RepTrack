package com.pulseb.app.ui.popup

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pulseb.app.SearchPopup
import com.pulseb.app.ui.theme.PulseBTheme
import kotlinx.coroutines.delay

/**
 * Activity that shows the quick-search popup. Launched from the background every 15 seconds
 * via full-screen intent so it appears even when the app is closed. Auto-closes after 4 seconds.
 */
class PopupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Popup: opened")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        enableEdgeToEdge()
        var query by mutableStateOf("")
        setContent {
            PulseBTheme {
                SearchPopup(
                    query = query,
                    onQueryChange = { query = it },
                    onDismiss = { finish() },
                    modifier = Modifier
                )
            }
            LaunchedEffect(Unit) {
                delay(4000)
                finish()
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Popup: closed")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "PulseB.Popup"
    }
}
