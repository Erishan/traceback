package com.erishan.traceback.me.data

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserContextDao {
    @Query("SELECT * FROM user_context WHERE id = :id")
    fun observeById(id: String): Flow<UserContextEntity?>
    @Upsert
    suspend fun save(userContext: UserContextEntity)
}
