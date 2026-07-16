package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FoloRepository(
    private val boussoleDao: BoussoleDao,
    private val decideDao: DecideDao,
    private val levelAssessmentDao: LevelAssessmentDao,
    private val moduleProgressDao: ModuleProgressDao
) {
    val boussoleFlow: Flow<BoussoleEntity?> = boussoleDao.getBoussole()
    val decideEntriesFlow: Flow<List<DecideEntity>> = decideDao.getAllDecideEntries()
    val assessmentsFlow: Flow<List<LevelAssessmentEntity>> = levelAssessmentDao.getAssessments()
    val progressesFlow: Flow<List<ModuleProgressEntity>> = moduleProgressDao.getProgresses()

    suspend fun saveBoussole(nord: String, sud: String, est: String, ouest: String) {
        boussoleDao.insertOrUpdate(
            BoussoleEntity(
                nordVision = nord,
                sudValeurs = sud,
                estRessources = est,
                ouestMenaces = ouest
            )
        )
    }

    suspend fun addDecideEntry(entry: DecideEntity) {
        decideDao.insert(entry)
    }

    suspend fun deleteDecideEntry(id: Int) {
        decideDao.deleteById(id)
    }

    suspend fun updateAssessment(level: Int, checked: Boolean) {
        levelAssessmentDao.updateAssessment(level, checked)
    }

    suspend fun updateModuleProgress(moduleId: String, completed: Boolean) {
        moduleProgressDao.updateCompletion(moduleId, completed)
    }

    suspend fun updateModuleDownload(moduleId: String, downloaded: Boolean) {
        moduleProgressDao.updateDownload(moduleId, downloaded)
    }

    // Ensures that the 5 levels of leadership checklist & module progress list is pre-seeded
    suspend fun ensurePreseededData(allModuleIds: List<String>) {
        val assessments = assessmentsFlow.first()
        if (assessments.isEmpty()) {
            val initial = (1..5).map { LevelAssessmentEntity(it, false) }
            levelAssessmentDao.insertAll(initial)
        }

        val progresses = progressesFlow.first()
        if (progresses.isEmpty() || progresses.size < allModuleIds.size) {
            allModuleIds.forEach { id ->
                if (progresses.none { it.moduleId == id }) {
                    moduleProgressDao.insertOrUpdate(ModuleProgressEntity(moduleId = id))
                }
            }
        }
    }
}
