package com.erishan.traceback.opportunity.domain

import kotlinx.coroutines.flow.Flow

interface OpportunityRepository {
    suspend fun save(opportunity: Opportunity)
    suspend fun update(id: String, transform: (Opportunity) -> Opportunity): Boolean
    suspend fun delete(id: String)
    fun observeById(id: String): Flow<Opportunity?>
    fun observeAll(): Flow<List<Opportunity>>
}