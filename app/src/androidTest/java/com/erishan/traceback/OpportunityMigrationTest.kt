package com.erishan.traceback

import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.erishan.traceback.core.db.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OpportunityMigrationTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Before
    fun deleteStaleMigrationDatabases() {
        val context = instrumentation.targetContext
        context.deleteDatabase(MIGRATE_1_TO_2_DB)
        context.deleteDatabase(MIGRATE_2_TO_3_DB)
        context.deleteDatabase(MIGRATE_3_TO_4_DB)
        context.deleteDatabase(LEGACY_SHARED_DB)
    }

    @Test
    fun migrate1To2_addsCreatedAt_andKeepsExistingRows() = runBlocking {
        val helper = migrationHelper(MIGRATE_1_TO_2_DB)
        helper.createDatabase(1).use { v1 ->
            v1.execSQL(
                """
                INSERT INTO opportunities
                    (id, title, description, source, sourceLabel, pipelineStage, notes, appliedMessage)
                VALUES
                    ('op-1', 'Legacy opportunity', NULL, 'UPWORK', NULL, 'DRAFT', 'a note from v1', NULL)
                """.trimIndent()
            )
        }

        val v2: SQLiteConnection = helper.runMigrationsAndValidate(2, emptyList())

        v2.prepare("SELECT title, notes, createdAt FROM opportunities WHERE id = 'op-1'").use { stmt ->
            assertTrue("expected the legacy row to survive the migration", stmt.step())
            assertEquals("Legacy opportunity", stmt.getText(0))
            assertEquals("a note from v1", stmt.getText(1))
            assertEquals(
                "legacy rows should keep the unknown-date sentinel in storage",
                0L,
                stmt.getLong(2)
            )
        }
        v2.close()
    }

    @Test
    fun migrate2To3_addsUserContext_andKeepsExistingRows() = runBlocking {
        val helper = migrationHelper(MIGRATE_2_TO_3_DB)
        helper.createDatabase(2).use { v2 ->
            v2.execSQL(
                """
                INSERT INTO opportunities
                    (id, title, description, source, sourceLabel, pipelineStage, notes, createdAt, appliedMessage)
                VALUES
                    ('op-1', 'Legacy opportunity', NULL, 'UPWORK', NULL, 'DRAFT', 'a note from v1', 0, NULL)
                """.trimIndent()
            )
        }

        val v3: SQLiteConnection = helper.runMigrationsAndValidate(3, emptyList())

        v3.prepare("SELECT title, notes, createdAt FROM opportunities WHERE id = 'op-1'").use { stmt ->
            assertTrue("expected the legacy row to survive the migration", stmt.step())
            assertEquals("Legacy opportunity", stmt.getText(0))
            assertEquals("a note from v1", stmt.getText(1))
            assertEquals(0L, stmt.getLong(2))
        }
        v3.prepare("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'user_context'").use { stmt ->
            assertTrue("expected user_context to exist after migrating to 3", stmt.step())
        }
        v3.prepare("SELECT COUNT(*) FROM user_context").use { stmt ->
            assertTrue(stmt.step())
            assertEquals("new profile table should start empty", 0L, stmt.getLong(0))
        }
        v3.close()
    }

    @Test
    fun migrate3To4_addsAiBrief_andKeepsExistingRows() = runBlocking {
        val helper = migrationHelper(MIGRATE_3_TO_4_DB)
        helper.createDatabase(3).use { v3 ->
            v3.execSQL(
                """
                INSERT INTO opportunities
                    (id, title, description, source, sourceLabel, pipelineStage, notes, createdAt, appliedMessage)
                VALUES
                    ('op-1', 'Legacy opportunity', NULL, 'UPWORK', NULL, 'DRAFT', 'a note from v1', 0, NULL)
                """.trimIndent()
            )
        }

        val v4: SQLiteConnection = helper.runMigrationsAndValidate(4, emptyList())

        v4.prepare(
            "SELECT title, notes, createdAt, aiBrief FROM opportunities WHERE id = 'op-1'"
        ).use { stmt ->
            assertTrue("expected the legacy row to survive the migration", stmt.step())
            assertEquals("Legacy opportunity", stmt.getText(0))
            assertEquals("a note from v1", stmt.getText(1))
            assertEquals(0L, stmt.getLong(2))
            assertTrue("legacy rows should have a null aiBrief", stmt.isNull(3))
        }
        v4.close()
    }

    private fun migrationHelper(fileName: String) = MigrationTestHelper(
        instrumentation = instrumentation,
        databaseClass = AppDatabase::class,
        driver = AndroidSQLiteDriver(),
        file = instrumentation.targetContext.getDatabasePath(fileName),
    )

    private companion object {
        const val MIGRATE_1_TO_2_DB = "migration-1-2.db"
        const val MIGRATE_2_TO_3_DB = "migration-2-3.db"
        const val MIGRATE_3_TO_4_DB = "migration-3-4.db"
        const val LEGACY_SHARED_DB = "migration-test.db"
    }
}
