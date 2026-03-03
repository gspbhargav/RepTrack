package com.pulseb.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pulseb.app.scheduler.AlarmScheduler
import com.pulseb.app.ui.theme.PulseBTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Schedule next alarm so logging runs even when app is closed
        AlarmScheduler(this).scheduleNextTrigger()
        setContent {
            PulseBTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val requestPermission = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { }
                    LaunchedEffect(Unit) {
                        requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Greeting(name = "Android", modifier = Modifier.padding(horizontal = 16.dp))
                        PopupSetupSection(modifier = Modifier.padding(16.dp))
                        SearchPopupHost(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        window.decorView.clearFocus()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity onStop – app in background; next popup alarm in ~15s if Alarms & reminders enabled")
    }

    companion object {
        private const val TAG = "PulseB.Main"
    }
}

@Composable
fun PopupSetupSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.setup_section_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.setup_section_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                OutlinedButton(
                    onClick = { PopupPermissionsHelper.openAlarmsAndRemindersSettings(context) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.setup_alarms_reminders))
                }
                Text(
                    text = stringResource(R.string.setup_alarms_reminders_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(
                onClick = { PopupPermissionsHelper.openOverlaySettings(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.setup_display_over_other_apps))
            }
            Text(
                text = stringResource(R.string.setup_display_over_other_apps_hint),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = { PopupPermissionsHelper.openAppDetailSettings(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.setup_app_settings))
            }
            Text(
                text = stringResource(R.string.setup_app_settings_hint),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                OutlinedButton(
                    onClick = { PopupPermissionsHelper.openNotificationSettings(context) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.setup_notification_settings))
                }
                Text(
                    text = stringResource(R.string.setup_notification_settings_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchPopupHost(modifier: Modifier = Modifier) {
    var showPopup by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // Show popup every 15 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000)
            showPopup = true
            query = ""
        }
    }

    // Auto-close popup after 4 seconds
    LaunchedEffect(showPopup) {
        if (!showPopup) return@LaunchedEffect
        delay(4000)
        showPopup = false
    }

    if (showPopup) {
        SearchPopup(
            query = query,
            onQueryChange = { query = it },
            onDismiss = { showPopup = false },
            modifier = modifier
        )
    }
}

@Composable
fun SearchPopup(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchResults = remember(query) { SearchData.search(query, maxResults = 4) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick search") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Type to search...") },
                    singleLine = true
                )
                Text(
                    "Suggestions (top 4):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                searchResults.forEach { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { },
                        tonalElevation = 1.dp,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = item,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                if (searchResults.isEmpty() && query.isNotBlank()) {
                    Text(
                        "No matches",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PulseBTheme {
        Greeting("Android")
    }
}
