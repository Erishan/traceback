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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OpportunityMigrationTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()


    @get:Rule
    val helper = MigrationTestHelper(
        instrumentation = instrumentation,
        databaseClass = AppDatabase::class,
        driver = AndroidSQLiteDriver(),
        file = instrumentation.targetContext.getDatabasePath(TEST_DB),
    )

    @Test
    fun migrate1To2_addsCreatedAt_andKeepsExistingRows() = runBlocking {
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
            assertEquals(0L, stmt.getLong(2))
        }
        v2.close()
    }

    private companion object {
        const val TEST_DB = "migration-test.db"
    }
}
