package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BoussoleDao {
    @Query("SELECT * FROM boussole WHERE id = 1 LIMIT 1")
    fun getBoussole(): Flow<BoussoleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(boussole: BoussoleEntity)
}

@Dao
interface DecideDao {
    @Query("SELECT * FROM decide_entries ORDER BY timestamp DESC")
    fun getAllDecideEntries(): Flow<List<DecideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DecideEntity)

    @Update
    suspend fun update(entry: DecideEntity)

    @Query("DELETE FROM decide_entries WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface LevelAssessmentDao {
    @Query("SELECT * FROM level_assessments")
    fun getAssessments(): Flow<List<LevelAssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assessments: List<LevelAssessmentEntity>)

    @Query("UPDATE level_assessments SET checked = :checked WHERE level = :level")
    suspend fun updateAssessment(level: Int, checked: Boolean)
}

@Dao
interface ModuleProgressDao {
    @Query("SELECT * FROM module_progress")
    fun getProgresses(): Flow<List<ModuleProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: ModuleProgressEntity)

    @Query("UPDATE module_progress SET completed = :completed WHERE moduleId = :moduleId")
    suspend fun updateCompletion(moduleId: String, completed: Boolean)

    @Query("UPDATE module_progress SET downloaded = :downloaded WHERE moduleId = :moduleId")
    suspend fun updateDownload(moduleId: String, downloaded: Boolean)
}

@Dao
interface ProspectDao {
    @Query("SELECT * FROM prospects ORDER BY timestamp DESC")
    fun getAllProspects(): Flow<List<ProspectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prospect: ProspectEntity)

    @Update
    suspend fun update(prospect: ProspectEntity)

    @Query("DELETE FROM prospects WHERE id = :id")
    suspend fun deleteById(id: Int)
}

