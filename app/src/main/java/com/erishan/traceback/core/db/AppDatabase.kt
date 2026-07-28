package com.erishan.traceback.core.db

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.erishan.traceback.opportunity.data.OpportunityDao
import com.erishan.traceback.opportunity.data.OpportunityEntity

@Database(entities = [OpportunityEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase(){
    abstract fun opportunityDao(): OpportunityDao
}