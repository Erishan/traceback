package com.erishan.traceback.me.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

internal const val USER_CONTEXT_ID = "me"

@Entity(tableName = "user_context")
data class UserContextEntity(
    @PrimaryKey val id: String,
    val about: String,
    val rateBand: String?,
    val pace: String?,
)
