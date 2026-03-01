package com.reptrack.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_definition")
data class ExerciseDefinitionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val inputType: String, // WEIGHT_REPS | TIME | REPS_ONLY
    val notes: String = ""
)

@Entity(tableName = "workout_template")
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "template_exercise")
data class TemplateExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long,
    val exerciseDefId: Long,
    val orderIndex: Int,
    val defaultSets: Int = 3
)

@Entity(tableName = "workout_session")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long,
    val templateName: String,
    val startedAt: Long = System.currentTimeMillis(),
    val finishedAt: Long? = null,
    val notes: String = ""
)

@Entity(tableName = "session_set")
data class SessionSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseDefId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val weightKg: Float? = null,
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val inputType: String,
    val notes: String = ""
)