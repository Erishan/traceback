package com.erishan.traceback.ai.domain

data class JobInput(
    val title: String,
    val description: String?,
    val source: String,
    val sourceLabel: String?,
    val appliedMessage: String?,
)
