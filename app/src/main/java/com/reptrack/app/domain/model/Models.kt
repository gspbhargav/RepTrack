package com.reptrack.app.domain.model

enum class InputType {
    WEIGHT_REPS, TIME, REPS_ONLY
}

data class ExerciseDefinition(
    val id: Long = 0,
    val name: String,
    val inputType: InputType,
    val notes: String = ""
)

data class WorkoutTemplate(
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class TemplateExercise(
    val id: Long = 0,
    val templateId: Long,
    val exerciseDefId: Long,
    val orderIndex: Int,
    val defaultSets: Int = 3
)

data class WorkoutSession(
    val id: Long = 0,
    val templateId: Long,
    val templateName: String,
    val startedAt: Long = System.currentTimeMillis(),
    val finishedAt: Long? = null,
    val notes: String = ""
)

data class SessionSet(
    val id: Long = 0,
    val sessionId: Long,
    val exerciseDefId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val weightKg: Float? = null,
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val inputType: InputType,
    val notes: String = ""
)