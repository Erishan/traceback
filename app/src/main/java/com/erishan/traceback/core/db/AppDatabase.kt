package com.erishan.traceback.core.db

import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.erishan.traceback.me.data.UserContextDao
import com.erishan.traceback.me.data.UserContextEntity
import com.erishan.traceback.opportunity.data.OpportunityDao
import com.erishan.traceback.opportunity.data.OpportunityEntity

@Database(
    entities = [OpportunityEntity::class, UserContextEntity::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun opportunityDao(): OpportunityDao
    abstract fun userContextDao(): UserContextDao
}