package com.erishan.traceback.core.di

import com.erishan.traceback.ai.data.SecretStoreImpl
import com.erishan.traceback.core.db.getDatabaseBuilder
import com.erishan.traceback.core.db.getRoomDatabase
import com.erishan.traceback.settings.data.AppearanceStoreImpl

fun createIosSharedContainer(): SharedContainer {
    return SharedContainer(
        database = getRoomDatabase(getDatabaseBuilder()),
        secretStore = SecretStoreImpl(),
        appearanceStore = AppearanceStoreImpl(),
    )
}
