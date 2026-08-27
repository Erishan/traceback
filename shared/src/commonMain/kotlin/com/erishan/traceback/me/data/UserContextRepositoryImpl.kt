package com.erishan.traceback.me.data

import com.erishan.traceback.core.db.AppDatabase
import com.erishan.traceback.me.domain.UserContext
import com.erishan.traceback.me.domain.UserContextRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserContextRepositoryImpl(
    database: AppDatabase,
) : UserContextRepository {
    private val userContextDao = database.userContextDao()

    override fun observe(): Flow<UserContext> {
        return userContextDao.observeById(USER_CONTEXT_ID).map { entity ->
            entity.toDomainOrEmpty()
        }
    }

    override suspend fun save(userContext: UserContext) {
        userContextDao.save(userContext.toEntity())
    }
}
