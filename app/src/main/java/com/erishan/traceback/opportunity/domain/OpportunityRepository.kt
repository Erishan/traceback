package com.erishan.traceback.opportunity.domain

import kotlinx.coroutines.flow.Flow

interface OpportunityRepository {
    suspend fun save(opportunity: Opportunity)
    fun observeById(id: String): Flow<Opportunity?>
    suspend fun delete(id: String)
    fun observeAll(): Flow<List<Opportunity>>
}