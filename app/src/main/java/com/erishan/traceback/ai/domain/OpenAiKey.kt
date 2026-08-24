package com.erishan.traceback.ai.domain

fun lastFourOf(secret: String): String = secret.takeLast(4)

fun trimmedOpenAiKey(value: String): String? =
    value.trim().takeIf { it.isNotEmpty() }
