package com.reptrack.app.ui.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.reptrack.app.data.db.SessionSetEntity
import com.reptrack.app.ui.components.LoadingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import com.reptrack.app.Screen

@Composable
private fun rememberElapsedTime(startedAt: Long): String {
    var elapsed by remember { mutableLongStateOf(System.currentTimeMillis() - startedAt) }
    LaunchedEffect(startedAt) {
        while (isActive) {
            elapsed = System.currentTimeMillis() - startedAt
            delay(1_000L)
        }
    }
    val totalSeconds = elapsed / 1_000
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSessionScreen(
    navController: NavController,
    viewModel: ActiveSessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showFinishDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            navController.popBackStack()
        }
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish Workout?") },
            text = { Text("This will save your session and return you to the home screen.") },
            confirmButton = {
                Button(onClick = {
                    showFinishDialog = false
                    viewModel.finishSession()
                }) { Text("Finish") }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) { Text("Keep Going") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.templateName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (uiState.startedAt > 0L) {
                            Text(
                                text = rememberElapsedTime(uiState.startedAt),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showFinishDialog = true },
                icon = {},
                text = { Text("Finish Workout", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(
                    items = uiState.exerciseGroups,
                    key = { it.exerciseDefId }
                ) { group ->
                    ExerciseGroupCard(
                        group = group,
                        sessionId = uiState.sessionId,
                        navController = navController,
                        onUpdateSet = { set, w, r, d -> viewModel.updateSet(set, w, r, d) },
                        onDeleteSet = { viewModel.deleteSet(it) },
                        onAddSet = { viewModel.addSet(group) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun ExerciseGroupCard(
    group: ExerciseGroup,
    sessionId: Long,
    navController: NavController,
    onUpdateSet: (SessionSetEntity, Float?, Int?, Int?) -> Unit,
    onDeleteSet: (SessionSetEntity) -> Unit,
    onAddSet: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        navController.navigate(
                            Screen.ExerciseDetail.createRoute(
                                sessionId = sessionId,
                                exerciseDefId = group.exerciseDefId
                            )
                        )
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = group.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SetColumnHeaders(inputType = group.inputType)
                    group.sets.forEach { set ->
                        SetRow(
                            set = set,
                            inputType = group.inputType,
                            onUpdate = { w, r, d -> onUpdateSet(set, w, r, d) },
                            onDelete = { onDeleteSet(set) }
                        )
                    }
                    TextButton(
                        onClick = onAddSet,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Set")
                    }
                }
            }
        }
    }
}

@Composable
private fun SetColumnHeaders(inputType: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "SET",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(36.dp)
        )
        when (inputType) {
            "WEIGHT_REPS" -> {
                Text("KG", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text("REPS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            }
            "REPS_ONLY" -> {
                Text("REPS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            }
            "TIME" -> {
                Text("SECS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.width(40.dp))
    }
}

@Composable
private fun SetRow(
    set: SessionSetEntity,
    inputType: String,
    onUpdate: (Float?, Int?, Int?) -> Unit,
    onDelete: () -> Unit
) {
    var weightText by remember(set.id) { mutableStateOf(set.weightKg?.let { formatWeight(it) } ?: "") }
    var repsText by remember(set.id) { mutableStateOf(set.reps?.toString() ?: "") }
    var durationText by remember(set.id) { mutableStateOf(set.durationSeconds?.toString() ?: "") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = set.setNumber.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        when (inputType) {
            "WEIGHT_REPS" -> {
                SetTextField(
                    value = weightText,
                    onValueChange = { weightText = it; onUpdate(it.toFloatOrNull(), repsText.toIntOrNull(), null) },
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SetTextField(
                    value = repsText,
                    onValueChange = { repsText = it; onUpdate(weightText.toFloatOrNull(), it.toIntOrNull(), null) },
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
            }
            "REPS_ONLY" -> {
                SetTextField(
                    value = repsText,
                    onValueChange = { repsText = it; onUpdate(null, it.toIntOrNull(), null) },
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
            }
            "TIME" -> {
                SetTextField(
                    value = durationText,
                    onValueChange = { durationText = it; onUpdate(null, null, it.toIntOrNull()) },
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete set", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier.height(52.dp)
    )
}

private fun formatWeight(kg: Float): String {
    return if (kg == kotlin.math.floor(kg.toDouble()).toFloat()) {
        kg.toInt().toString()
    } else {
        "%.1f".format(kg)
    }
}