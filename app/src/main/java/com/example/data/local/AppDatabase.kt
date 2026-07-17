package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BoussoleEntity::class,
        DecideEntity::class,
        LevelAssessmentEntity::class,
        ModuleProgressEntity::class,
        ProspectEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boussoleDao(): BoussoleDao
    abstract fun decideDao(): DecideDao
    abstract fun levelAssessmentDao(): LevelAssessmentDao
    abstract fun moduleProgressDao(): ModuleProgressDao
    abstract fun prospectDao(): ProspectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "folo_coaching_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
