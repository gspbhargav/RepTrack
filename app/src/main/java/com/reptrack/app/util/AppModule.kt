package com.reptrack.app

import android.content.Context
import androidx.room.Room
import com.reptrack.app.data.db.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RepTrackDatabase {
        return Room.databaseBuilder(
            context,
            RepTrackDatabase::class.java,
            "reptrack.db"
        ).build()
    }

    @Provides
    fun provideExerciseDefinitionDao(db: RepTrackDatabase): ExerciseDefinitionDao =
        db.exerciseDefinitionDao()

    @Provides
    fun provideWorkoutTemplateDao(db: RepTrackDatabase): WorkoutTemplateDao =
        db.workoutTemplateDao()

    @Provides
    fun provideTemplateExerciseDao(db: RepTrackDatabase): TemplateExerciseDao =
        db.templateExerciseDao()

    @Provides
    fun provideWorkoutSessionDao(db: RepTrackDatabase): WorkoutSessionDao =
        db.workoutSessionDao()

    @Provides
    fun provideSessionSetDao(db: RepTrackDatabase): SessionSetDao =
        db.sessionSetDao()
}