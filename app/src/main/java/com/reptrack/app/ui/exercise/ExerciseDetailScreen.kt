package com.reptrack.app.ui.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    viewModel: ExerciseDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.exerciseName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = when (uiState.inputType) {
                                "WEIGHT_REPS" -> "Weight × Reps"
                                "REPS_ONLY" -> "Reps only"
                                "TIME" -> "Timed"
                                else -> ""
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
            FloatingActionButton(onClick = { viewModel.addSet() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Set")
            }
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
                // Personal Best card
                uiState.personalBest?.let { pb ->
                    item {
                        PersonalBestCard(
                            pb = pb,
                            inputType = uiState.inputType
                        )
                    }
                }

                // Sets header
                item {
                    Text(
                        text = "THIS SESSION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Column headers
                item {
                    SetColumnHeaders(inputType = uiState.inputType)
                }

                // Set rows
                items(
                    items = uiState.currentSets,
                    key = { it.id }
                ) { set ->
                    SetRow(
                        set = set,
                        inputType = uiState.inputType,
                        onUpdate = { w, r, d -> viewModel.updateSet(set, w, r, d) },
                        onDelete = { viewModel.deleteSet(set) }
                    )
                }

                // Bottom spacer so FAB doesn't cover last row
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// ─── Personal Best Card ───────────────────────────────────────────────────────

@Composable
private fun PersonalBestCard(pb: PersonalBest, inputType: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🏆  Personal Best",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (inputType) {
                    "WEIGHT_REPS" -> "${pb.weightKg ?: "-"} kg × ${pb.reps ?: "-"} reps"
                    "REPS_ONLY" -> "${pb.reps ?: "-"} reps"
                    "TIME" -> "${pb.durationSeconds ?: "-"} seconds"
                    else -> "-"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// ─── Column Headers ───────────────────────────────────────────────────────────

@Composable
private fun SetColumnHeaders(inputType: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                Text(
                    text = "KG",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "REPS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
            "REPS_ONLY" -> {
                Text(
                    text = "REPS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
            "TIME" -> {
                Text(
                    text = "SECS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.width(40.dp))
    }
}

// ─── Set Row ──────────────────────────────────────────────────────────────────

@Composable
private fun SetRow(
    set: SessionSetEntity,
    inputType: String,
    onUpdate: (Float?, Int?, Int?) -> Unit,
    onDelete: () -> Unit
) {
    var weightText by remember(set.id) {
        mutableStateOf(set.weightKg?.let { formatWeight(it) } ?: "")
    }
    var repsText by remember(set.id) {
        mutableStateOf(set.reps?.toString() ?: "")
    }
    var durationText by remember(set.id) {
        mutableStateOf(set.durationSeconds?.toString() ?: "")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                    onValueChange = {
                        weightText = it
                        onUpdate(it.toFloatOrNull(), repsText.toIntOrNull(), null)
                    },
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SetTextField(
                    value = repsText,
                    onValueChange = {
                        repsText = it
                        onUpdate(weightText.toFloatOrNull(), it.toIntOrNull(), null)
                    },
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
            }
            "REPS_ONLY" -> {
                SetTextField(
                    value = repsText,
                    onValueChange = {
                        repsText = it
                        onUpdate(null, it.toIntOrNull(), null)
                    },
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
            }
            "TIME" -> {
                SetTextField(
                    value = durationText,
                    onValueChange = {
                        durationText = it
                        onUpdate(null, null, it.toIntOrNull())
                    },
                    placeholder = "0",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete set",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ─── Text Field ───────────────────────────────────────────────────────────────

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
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier.height(52.dp)
    )
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun formatWeight(kg: Float): String {
    return if (kg == kotlin.math.floor(kg.toDouble()).toFloat()) {
        kg.toInt().toString()
    } else {
        "%.1f".format(kg)
    }
}