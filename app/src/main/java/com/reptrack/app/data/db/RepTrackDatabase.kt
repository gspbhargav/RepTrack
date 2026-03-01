package com.reptrack.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ExerciseDefinitionEntity::class,
        WorkoutTemplateEntity::class,
        TemplateExerciseEntity::class,
        WorkoutSessionEntity::class,
        SessionSetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RepTrackDatabase : RoomDatabase() {
    abstract fun exerciseDefinitionDao(): ExerciseDefinitionDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun templateExerciseDao(): TemplateExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun sessionSetDao(): SessionSetDao
}