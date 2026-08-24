package com.erishan.traceback.core.di

import android.content.Context
import androidx.room3.Room
import com.erishan.traceback.core.db.AppDatabase
import com.erishan.traceback.opportunity.data.OpportunityRepositoryImpl
import com.erishan.traceback.opportunity.domain.OpportunityRepository

class AppContainer(context: Context) {
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context = context.applicationContext,
            klass = AppDatabase::class.java,
            name = "traceback.db"
        ).build()
    }

    val opportunityRepository: OpportunityRepository by lazy {
        OpportunityRepositoryImpl(database)
    }
}