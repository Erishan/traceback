package com.erishan.traceback.core.db

import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.erishan.traceback.opportunity.data.OpportunityDao
import com.erishan.traceback.opportunity.data.OpportunityEntity

@Database(
    entities = [OpportunityEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun opportunityDao(): OpportunityDao
}