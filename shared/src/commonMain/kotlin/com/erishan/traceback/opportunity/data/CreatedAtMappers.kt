package com.erishan.traceback.opportunity.data

import kotlin.time.Instant

internal const val UNKNOWN_CREATED_AT_MILLIS = 0L
internal const val UNKNOWN_CREATED_AT_SQL_DEFAULT = "0"

internal fun Long.toKnownInstantOrNull(): Instant? =
    takeIf { it > UNKNOWN_CREATED_AT_MILLIS }?.let(Instant::fromEpochMilliseconds)

internal fun Instant?.toStoredEpochMillis(): Long =
    this?.toEpochMilliseconds() ?: UNKNOWN_CREATED_AT_MILLIS
