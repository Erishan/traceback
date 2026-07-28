package com.erishan.traceback.opportunity.data

import com.erishan.traceback.opportunity.domain.Opportunity
import com.erishan.traceback.opportunity.domain.OpportunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OpportunityRepositoryImpl(
    private val opportunityDao: OpportunityDao
) : OpportunityRepository {
    override suspend fun save(opportunity: Opportunity) {
        val entity = opportunity.toEntity()
        opportunityDao.save(entity)
    }

    override suspend fun delete(id: String) {
        opportunityDao.delete(id)
    }

    override fun observeById(id: String): Flow<Opportunity?> {
        return opportunityDao.observeById(id).map {
            entity -> entity?.toDomain()
        }
    }

    override fun observeAll(): Flow<List<Opportunity>> {
        return opportunityDao.observeAll().map {
            entities -> entities.toDomainList()
        }
    }
}