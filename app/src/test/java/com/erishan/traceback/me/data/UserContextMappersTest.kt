package com.erishan.traceback.me.data

import com.erishan.traceback.me.domain.UserContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserContextMappersTest {

    @Test
    fun entity_mapsToDomain() {
        val domain = UserContextEntity(
            id = "me",
            about = "Android + Compose",
            rateBand = "mid",
            pace = "one at a time",
        ).toDomain()

        assertEquals("Android + Compose", domain.about)
        assertEquals("mid", domain.rateBand)
        assertEquals("one at a time", domain.pace)
    }

    @Test
    fun domain_mapsToTheSingletonRow() {
        val entity = UserContext(
            about = "Android + Compose",
            rateBand = "mid",
            pace = "one at a time",
        ).toEntity()

        assertEquals(USER_CONTEXT_ID, entity.id)
        assertEquals("Android + Compose", entity.about)
        assertEquals("mid", entity.rateBand)
        assertEquals("one at a time", entity.pace)
    }

    @Test
    fun blankOptionals_storeAsNull() {
        val entity = UserContext(
            about = "Android + Compose",
            rateBand = "  ",
            pace = "",
        ).toEntity()

        assertNull(entity.rateBand)
        assertNull(entity.pace)
    }

    @Test
    fun missingRow_readsAsEmptyContext() {
        val domain = (null as UserContextEntity?).toDomainOrEmpty()

        assertEquals("", domain.about)
        assertNull(domain.rateBand)
        assertNull(domain.pace)
    }
}
