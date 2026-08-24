package com.erishan.traceback.opportunity.domain

import kotlin.time.Instant

data class Note(
    val id: String,
    val createdAt: Instant?,
    val text: String,
)
