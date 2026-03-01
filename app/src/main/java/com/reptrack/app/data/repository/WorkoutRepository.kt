package com.reptrack.app.data.repository

import com.reptrack.app.data.db.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
@Singleton
class WorkoutRepository @Inject constructor(
    private val exerciseDefDao: ExerciseDefinitionDao,
    private val templateDao: WorkoutTemplateDao,
    private val templateExerciseDao: TemplateExerciseDao,
    private val sessionDao: WorkoutSessionDao,
    private val sessionSetDao: SessionSetDao
) {
    // --- Exercise Definitions ---
    fun getAllExercises(): Flow<List<ExerciseDefinitionEntity>> = exerciseDefDao.getAll()
    suspend fun insertExercise(entity: ExerciseDefinitionEntity) = exerciseDefDao.insert(entity)
    suspend fun updateExercise(entity: ExerciseDefinitionEntity) = exerciseDefDao.update(entity)
    suspend fun deleteExercise(entity: ExerciseDefinitionEntity) = exerciseDefDao.delete(entity)

    // --- Templates ---
    fun getAllTemplates(): Flow<List<WorkoutTemplateEntity>> = templateDao.getAll()
    suspend fun insertTemplate(entity: WorkoutTemplateEntity): Long = templateDao.insert(entity)
    suspend fun updateTemplate(entity: WorkoutTemplateEntity) = templateDao.update(entity)
    suspend fun deleteTemplate(entity: WorkoutTemplateEntity) = templateDao.delete(entity)

    // --- Template Exercises ---
    fun getTemplateExercises(templateId: Long): Flow<List<TemplateExerciseEntity>> =
        templateExerciseDao.getByTemplate(templateId)
    suspend fun insertTemplateExercise(entity: TemplateExerciseEntity) = templateExerciseDao.insert(entity)
    suspend fun insertAllTemplateExercises(entities: List<TemplateExerciseEntity>) = templateExerciseDao.insertAll(entities)
    suspend fun updateTemplateExercise(entity: TemplateExerciseEntity) = templateExerciseDao.update(entity)
    suspend fun deleteTemplateExercise(entity: TemplateExerciseEntity) = templateExerciseDao.delete(entity)
    suspend fun deleteTemplateExercisesByTemplate(templateId: Long) = templateExerciseDao.deleteByTemplate(templateId)

    // --- Sessions ---
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>> = sessionDao.getAll()
    suspend fun getSessionById(id: Long): WorkoutSessionEntity? = sessionDao.getById(id)
    suspend fun insertSession(entity: WorkoutSessionEntity): Long = sessionDao.insert(entity)
    suspend fun updateSession(entity: WorkoutSessionEntity) = sessionDao.update(entity)
    suspend fun deleteSession(entity: WorkoutSessionEntity) = sessionDao.delete(entity)

    // --- Session Sets ---
    fun getSessionSets(sessionId: Long): Flow<List<SessionSetEntity>> = sessionSetDao.getBySession(sessionId)
    suspend fun insertSessionSet(entity: SessionSetEntity) = sessionSetDao.insert(entity)
    suspend fun updateSessionSet(entity: SessionSetEntity) = sessionSetDao.update(entity)
    suspend fun deleteSessionSet(entity: SessionSetEntity) = sessionSetDao.delete(entity)
    suspend fun deleteSessionSetsBySession(sessionId: Long) = sessionSetDao.deleteBySession(sessionId)

    // --- Seed ---
    suspend fun seedIfEmpty() {
        val existing = exerciseDefDao.getAll().first()
        if (existing.isNotEmpty()) return

        val exerciseIds = SeedData.exercises.map { exerciseDefDao.insert(it) }
        val templateIds = SeedData.templates.map { templateDao.insert(it) }

        SeedData.templateExercises.forEach { (templateIndex, exerciseList) ->
            exerciseList.forEachIndexed { orderIndex, (exerciseIndex, defaultSets) ->
                templateExerciseDao.insert(
                    TemplateExerciseEntity(
                        templateId = templateIds[templateIndex],
                        exerciseDefId = exerciseIds[exerciseIndex],
                        orderIndex = orderIndex,
                        defaultSets = defaultSets
                    )
                )
            }
        }
    }

    // --- Start New Session (clone last) ---
    suspend fun startNewSession(templateId: Long, templateName: String): Long {
        val newSession = WorkoutSessionEntity(
            templateId = templateId,
            templateName = templateName
        )
        val newSessionId = sessionDao.insert(newSession)

        val lastSession = sessionDao.getLastFinished(templateId)

        if (lastSession != null) {
            val lastSets = sessionSetDao.getBySessionSuspend(lastSession.id)
            val clonedSets = lastSets.map { it.copy(id = 0, sessionId = newSessionId) }
            sessionSetDao.insertAll(clonedSets)
        } else {
            val templateExercises = templateExerciseDao.getByTemplateSuspend(templateId)
            templateExercises.forEachIndexed { _, te ->
                val exerciseDef = exerciseDefDao.getById(te.exerciseDefId) ?: return@forEachIndexed
                val exerciseIndex = SeedData.exercises.indexOfFirst { it.name == exerciseDef.name }
                repeat(te.defaultSets) { i ->
                    val setNumber = i + 1
                    val defaults = if (exerciseIndex >= 0) SeedData.defaultSets[exerciseIndex to setNumber] else null
                    sessionSetDao.insert(
                        SessionSetEntity(
                            sessionId = newSessionId,
                            exerciseDefId = te.exerciseDefId,
                            exerciseName = exerciseDef.name,
                            setNumber = setNumber,
                            weightKg = defaults?.first,
                            reps = defaults?.second,
                            durationSeconds = defaults?.third,
                            inputType = exerciseDef.inputType
                        )
                    )
                }
            }
        }
        return newSessionId
    }

    // --- Finish Session ---
    suspend fun finishSession(sessionId: Long) {
        val session = sessionDao.getById(sessionId) ?: return
        sessionDao.update(session.copy(finishedAt = System.currentTimeMillis()))
    }
}