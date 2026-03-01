package com.reptrack.app.data.db

object SeedData {

    val exercises = listOf(
        ExerciseDefinitionEntity(name = "DB Chest Press", inputType = "WEIGHT_REPS", notes = "Log weight per dumbbell"),
        ExerciseDefinitionEntity(name = "DB Shoulder Press", inputType = "WEIGHT_REPS", notes = "Log weight per dumbbell"),
        ExerciseDefinitionEntity(name = "Lat Pulldown", inputType = "WEIGHT_REPS", notes = ""),
        ExerciseDefinitionEntity(name = "Deadlift", inputType = "WEIGHT_REPS", notes = ""),
        ExerciseDefinitionEntity(name = "Squats", inputType = "REPS_ONLY", notes = "Bodyweight"),
        ExerciseDefinitionEntity(name = "Lunges", inputType = "WEIGHT_REPS", notes = "Log weight per dumbbell"),
        ExerciseDefinitionEntity(name = "Walking", inputType = "TIME", notes = ""),
        ExerciseDefinitionEntity(name = "Plank", inputType = "TIME", notes = "")
    )

    val templates = listOf(
        WorkoutTemplateEntity(name = "Push Day"),
        WorkoutTemplateEntity(name = "Pull Day"),
        WorkoutTemplateEntity(name = "Leg Day"),
        WorkoutTemplateEntity(name = "Conditioning")
    )

    // templateIndex -> list of (exerciseIndex, defaultSets)
    val templateExercises = mapOf(
        0 to listOf(0 to 3, 1 to 3),       // Push: DB Chest Press, DB Shoulder Press
        1 to listOf(2 to 3, 3 to 3),       // Pull: Lat Pulldown, Deadlift
        2 to listOf(4 to 2, 5 to 2),       // Legs: Squats, Lunges
        3 to listOf(6 to 1, 7 to 3)        // Conditioning: Walking, Plank
    )

    // (exerciseIndex, setNumber) -> (weightKg, reps, durationSeconds)
    val defaultSets = mapOf(
        (0 to 1) to Triple(5f, 12, null),
        (0 to 2) to Triple(7.5f, 10, null),
        (0 to 3) to Triple(10f, 8, null),
        (1 to 1) to Triple(5f, 12, null),
        (1 to 2) to Triple(7.5f, 10, null),
        (1 to 3) to Triple(10f, 8, null),
        (2 to 1) to Triple(20f, 12, null),
        (2 to 2) to Triple(20f, 12, null),
        (2 to 3) to Triple(20f, 12, null),
        (3 to 1) to Triple(15f, 12, null),
        (3 to 2) to Triple(15f, 12, null),
        (3 to 3) to Triple(15f, 12, null),
        (4 to 1) to Triple(null, 12, null),
        (4 to 2) to Triple(null, 12, null),
        (5 to 1) to Triple(5f, 8, null),
        (5 to 2) to Triple(5f, 8, null),
        (6 to 1) to Triple(null, null, 1200),
        (7 to 1) to Triple(null, null, 30),
        (7 to 2) to Triple(null, null, 30),
        (7 to 3) to Triple(null, null, 30)
    )
}