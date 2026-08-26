package com.erishan.traceback.core.di

import android.content.Context
import androidx.room3.Room
import com.erishan.traceback.ai.data.KtorOpenAiClient
import com.erishan.traceback.ai.data.SecretStoreImpl
import com.erishan.traceback.ai.domain.BriefJobUseCase
import com.erishan.traceback.ai.domain.OpenAiClient
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.core.db.AppDatabase
import com.erishan.traceback.me.data.UserContextRepositoryImpl
import com.erishan.traceback.me.domain.UserContextRepository
import com.erishan.traceback.opportunity.data.OpportunityRepositoryImpl
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import com.erishan.traceback.settings.data.AppearanceStoreImpl
import com.erishan.traceback.settings.domain.AppearanceStore

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context = appContext,
            klass = AppDatabase::class.java,
            name = "traceback.db"
        ).build()
    }

    val opportunityRepository: OpportunityRepository by lazy {
        OpportunityRepositoryImpl(database)
    }

    val userContextRepository: UserContextRepository by lazy {
        UserContextRepositoryImpl(database)
    }

    val secretStore: SecretStore by lazy {
        SecretStoreImpl(appContext)
    }

    val appearanceStore: AppearanceStore by lazy {
        AppearanceStoreImpl(appContext)
    }

    private val openAiClient: OpenAiClient by lazy {
        KtorOpenAiClient()
    }

    val briefJobUseCase: BriefJobUseCase by lazy {
        BriefJobUseCase(
            secretStore = secretStore,
            openAiClient = openAiClient,
        )
    }
}