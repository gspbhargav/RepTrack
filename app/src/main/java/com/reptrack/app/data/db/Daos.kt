package com.reptrack.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDefinitionDao {
    @Query("SELECT * FROM exercise_definition ORDER BY name ASC")
    fun getAll(): Flow<List<ExerciseDefinitionEntity>>

    @Query("SELECT * FROM exercise_definition WHERE id = :id")
    suspend fun getById(id: Long): ExerciseDefinitionEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ExerciseDefinitionEntity): Long

    @Update
    suspend fun update(entity: ExerciseDefinitionEntity)

    @Delete
    suspend fun delete(entity: ExerciseDefinitionEntity)
}

@Dao
interface WorkoutTemplateDao {
    @Query("SELECT * FROM workout_template ORDER BY createdAt ASC")
    fun getAll(): Flow<List<WorkoutTemplateEntity>>

    @Query("SELECT * FROM workout_template WHERE id = :id")
    suspend fun getById(id: Long): WorkoutTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WorkoutTemplateEntity): Long

    @Update
    suspend fun update(entity: WorkoutTemplateEntity)

    @Delete
    suspend fun delete(entity: WorkoutTemplateEntity)
}

@Dao
interface TemplateExerciseDao {
    @Query("SELECT * FROM template_exercise WHERE templateId = :templateId ORDER BY orderIndex ASC")
    fun getByTemplate(templateId: Long): Flow<List<TemplateExerciseEntity>>

    @Query("SELECT * FROM template_exercise WHERE templateId = :templateId ORDER BY orderIndex ASC")
    suspend fun getByTemplateSuspend(templateId: Long): List<TemplateExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TemplateExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<TemplateExerciseEntity>)

    @Update
    suspend fun update(entity: TemplateExerciseEntity)

    @Delete
    suspend fun delete(entity: TemplateExerciseEntity)

    @Query("DELETE FROM template_exercise WHERE templateId = :templateId")
    suspend fun deleteByTemplate(templateId: Long)
}

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_session ORDER BY startedAt DESC")
    fun getAll(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_session WHERE id = :id")
    suspend fun getById(id: Long): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_session WHERE templateId = :templateId AND finishedAt IS NOT NULL ORDER BY finishedAt DESC LIMIT 1")
    suspend fun getLastFinished(templateId: Long): WorkoutSessionEntity?

    @Insert
    suspend fun insert(entity: WorkoutSessionEntity): Long

    @Update
    suspend fun update(entity: WorkoutSessionEntity)

    @Delete
    suspend fun delete(entity: WorkoutSessionEntity)
}

@Dao
interface SessionSetDao {
    @Query("SELECT * FROM session_set WHERE sessionId = :sessionId ORDER BY exerciseName ASC, setNumber ASC")
    fun getBySession(sessionId: Long): Flow<List<SessionSetEntity>>

    @Query("SELECT * FROM session_set WHERE sessionId = :sessionId ORDER BY exerciseName ASC, setNumber ASC")
    suspend fun getBySessionSuspend(sessionId: Long): List<SessionSetEntity>

    @Insert
    suspend fun insert(entity: SessionSetEntity): Long

    @Insert
    suspend fun insertAll(entities: List<SessionSetEntity>)

    @Update
    suspend fun update(entity: SessionSetEntity)

    @Delete
    suspend fun delete(entity: SessionSetEntity)

    @Query("DELETE FROM session_set WHERE sessionId = :sessionId")
    suspend fun deleteBySession(sessionId: Long)
}