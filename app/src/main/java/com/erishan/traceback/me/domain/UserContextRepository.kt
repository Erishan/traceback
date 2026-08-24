package com.erishan.traceback.me.domain

import kotlinx.coroutines.flow.Flow

interface UserContextRepository {
    fun observe(): Flow<UserContext>
    suspend fun save(userContext: UserContext)
}
