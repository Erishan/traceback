package com.erishan.traceback.opportunity.data

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface OpportunityDao {
    @Upsert
    suspend fun save(opportunity: OpportunityEntity)
    @Query("SELECT * FROM opportunities WHERE id = :id")
    suspend fun getById(id: String): OpportunityEntity?
    @Query("SELECT * FROM opportunities WHERE id = :id")
    fun observeById(id: String): Flow<OpportunityEntity?>
    @Query("DELETE FROM opportunities WHERE id = :id")
    suspend fun delete(id: String)
    @Query("SELECT * FROM opportunities")
    fun observeAll(): Flow<List<OpportunityEntity>>
}
