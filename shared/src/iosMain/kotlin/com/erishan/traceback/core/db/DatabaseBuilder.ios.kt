package com.erishan.traceback.core.db

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = prepareDatabasePath()
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
    ).addCallback(
        object : RoomDatabase.Callback() {
            override suspend fun onOpen(connection: SQLiteConnection) {
                excludeDatabaseFilesFromBackup(dbFilePath)
            }
        },
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun prepareDatabasePath(): String {
    val dbDirectoryPath = noBackupDatabaseDirectory()
    val dbFilePath = "$dbDirectoryPath/$DB_NAME"
    migrateLegacyDatabaseIfNeeded(dbFilePath)
    excludeDatabaseFilesFromBackup(dbFilePath)
    return dbFilePath
}

@OptIn(ExperimentalForeignApi::class)
private fun noBackupDatabaseDirectory(): String {
    val appSupportDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    val appSupportPath = requireNotNull(appSupportDirectory?.path) {
        "Could not resolve NSApplicationSupportDirectory for Room"
    }
    val dbDirectoryPath = "$appSupportPath/$APP_SUPPORT_DIRECTORY/$NO_BACKUP_DIRECTORY"
    createDirectory(dbDirectoryPath)
    excludePathFromBackup(dbDirectoryPath, isDirectory = true)
    return dbDirectoryPath
}

@OptIn(ExperimentalForeignApi::class)
private fun migrateLegacyDatabaseIfNeeded(dbFilePath: String) {
    val legacyDbFilePath = "${documentDirectory()}/$DB_NAME"
    val migrationStagingDirectory = "$dbFilePath.migration"
    val fileManager = NSFileManager.defaultManager

    if (!fileManager.fileExistsAtPath(legacyDbFilePath)) {
        removePathIfExists(migrationStagingDirectory)
        return
    }

    excludeDatabaseFilesFromBackup(legacyDbFilePath)

    if (fileManager.fileExistsAtPath(migrationStagingDirectory)) {
        removeDatabaseFilesIfExists(dbFilePath)
        removePathIfExists(migrationStagingDirectory)
    }

    if (fileManager.fileExistsAtPath(dbFilePath)) {
        excludeDatabaseFilesFromBackup(dbFilePath)
        removeDatabaseFilesIfExists(legacyDbFilePath)
        return
    }

    createDirectory(migrationStagingDirectory)
    excludePathFromBackup(migrationStagingDirectory, isDirectory = true)

    val stagedDbFilePath = "$migrationStagingDirectory/$DB_NAME"
    DatabaseFileSuffixes.forEach { suffix ->
        copyPathIfExists(
            sourcePath = legacyDbFilePath + suffix,
            destinationPath = stagedDbFilePath + suffix,
        )
    }
    excludeDatabaseFilesFromBackup(stagedDbFilePath)

    DatabaseFileSuffixes.forEach { suffix ->
        movePathIfExists(
            sourcePath = stagedDbFilePath + suffix,
            destinationPath = dbFilePath + suffix,
        )
    }
    excludeDatabaseFilesFromBackup(dbFilePath)
    removeDatabaseFilesIfExists(legacyDbFilePath)
    removePathIfExists(migrationStagingDirectory)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(documentDirectory?.path) {
        "Could not resolve NSDocumentDirectory for Room"
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun createDirectory(path: String) {
    val fileManager = NSFileManager.defaultManager
    if (fileManager.fileExistsAtPath(path)) return

    val created = fileManager.createDirectoryAtPath(
        path = path,
        withIntermediateDirectories = true,
        attributes = null,
        error = null,
    )
    check(created) { "Could not create iOS Room database directory: $path" }
}

@OptIn(ExperimentalForeignApi::class)
private fun copyPathIfExists(sourcePath: String, destinationPath: String) {
    val fileManager = NSFileManager.defaultManager
    if (!fileManager.fileExistsAtPath(sourcePath)) return

    val copied = fileManager.copyItemAtPath(
        srcPath = sourcePath,
        toPath = destinationPath,
        error = null,
    )
    check(copied) { "Could not copy iOS Room database file from $sourcePath to $destinationPath" }
}

@OptIn(ExperimentalForeignApi::class)
private fun movePathIfExists(sourcePath: String, destinationPath: String) {
    val fileManager = NSFileManager.defaultManager
    if (!fileManager.fileExistsAtPath(sourcePath)) return

    val moved = fileManager.moveItemAtPath(
        srcPath = sourcePath,
        toPath = destinationPath,
        error = null,
    )
    check(moved) { "Could not move iOS Room database file from $sourcePath to $destinationPath" }
}

@OptIn(ExperimentalForeignApi::class)
private fun removeDatabaseFilesIfExists(dbFilePath: String) {
    DatabaseFileSuffixes.forEach { suffix ->
        removePathIfExists(dbFilePath + suffix)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun removePathIfExists(path: String) {
    val fileManager = NSFileManager.defaultManager
    if (!fileManager.fileExistsAtPath(path)) return

    val removed = fileManager.removeItemAtPath(
        path = path,
        error = null,
    )
    check(removed) { "Could not remove iOS Room database file: $path" }
}

@OptIn(ExperimentalForeignApi::class)
private fun excludeDatabaseFilesFromBackup(dbFilePath: String) {
    DatabaseFileSuffixes.forEach { suffix ->
        excludePathFromBackup(path = dbFilePath + suffix, isDirectory = false)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun excludePathFromBackup(path: String, isDirectory: Boolean) {
    if (!NSFileManager.defaultManager.fileExistsAtPath(path)) return

    val excluded = NSURL.fileURLWithPath(path = path, isDirectory = isDirectory).setResourceValue(
        value = true,
        forKey = NSURLIsExcludedFromBackupKey,
        error = null,
    )
    check(excluded) { "Could not exclude iOS Room database path from backup: $path" }
}

private val DatabaseFileSuffixes = listOf("", "-wal", "-shm", "-journal")

private const val APP_SUPPORT_DIRECTORY = "Traceback"
private const val NO_BACKUP_DIRECTORY = "NoBackup"
private const val DB_NAME = "traceback.db"
