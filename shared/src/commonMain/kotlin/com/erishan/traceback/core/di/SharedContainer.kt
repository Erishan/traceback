package com.erishan.traceback.core.di

import com.erishan.traceback.ai.data.KtorOpenAiClient
import com.erishan.traceback.ai.domain.BriefJobUseCase
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.core.db.AppDatabase
import com.erishan.traceback.me.data.UserContextRepositoryImpl
import com.erishan.traceback.me.domain.UserContextRepository
import com.erishan.traceback.opportunity.data.OpportunityRepositoryImpl
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import com.erishan.traceback.settings.domain.AppearanceStore

class SharedContainer(
    database: AppDatabase,
    val secretStore: SecretStore,
    val appearanceStore: AppearanceStore,
) {
    val opportunityRepository: OpportunityRepository =
        OpportunityRepositoryImpl(database)

    val userContextRepository: UserContextRepository =
        UserContextRepositoryImpl(database)

    val briefJobUseCase: BriefJobUseCase by lazy {
        BriefJobUseCase(
            secretStore = secretStore,
            openAiClient = KtorOpenAiClient(),
        )
    }
}
