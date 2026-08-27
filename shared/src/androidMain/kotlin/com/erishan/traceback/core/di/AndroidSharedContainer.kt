package com.erishan.traceback.core.di

import android.content.Context
import com.erishan.traceback.ai.data.SecretStoreImpl
import com.erishan.traceback.core.db.getDatabaseBuilder
import com.erishan.traceback.core.db.getRoomDatabase
import com.erishan.traceback.settings.data.AppearanceStoreImpl

fun createAndroidSharedContainer(context: Context): SharedContainer {
    val appContext = context.applicationContext
    return SharedContainer(
        database = getRoomDatabase(getDatabaseBuilder(appContext)),
        secretStore = SecretStoreImpl(appContext),
        appearanceStore = AppearanceStoreImpl(appContext),
    )
}
