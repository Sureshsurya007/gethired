package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        JobEntity::class,
        EventEntity::class,
        AlertPreferenceEntity::class,
        AlertNotificationEntity::class,
        ResumeProfileEntity::class,
        InterviewPracticeEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GradLaunchDatabase : RoomDatabase() {
    abstract fun dao(): GradLaunchDao

    companion object {
        @Volatile
        private var INSTANCE: GradLaunchDatabase? = null

        fun getDatabase(context: Context): GradLaunchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GradLaunchDatabase::class.java,
                    "grad_launch_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
