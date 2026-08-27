package com.erishan.traceback.core.di

import android.content.Context
import com.erishan.traceback.ai.domain.BriefJobUseCase
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.me.domain.UserContextRepository
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import com.erishan.traceback.settings.domain.AppearanceStore

class AppContainer(context: Context) {
    private val shared: SharedContainer = createAndroidSharedContainer(context)

    val opportunityRepository: OpportunityRepository
        get() = shared.opportunityRepository

    val userContextRepository: UserContextRepository
        get() = shared.userContextRepository

    val secretStore: SecretStore
        get() = shared.secretStore

    val appearanceStore: AppearanceStore
        get() = shared.appearanceStore

    val briefJobUseCase: BriefJobUseCase
        get() = shared.briefJobUseCase
}
