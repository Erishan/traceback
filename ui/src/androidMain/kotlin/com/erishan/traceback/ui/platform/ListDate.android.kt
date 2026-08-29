package com.erishan.traceback.ui.platform

import java.time.Instant as JavaInstant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Instant

private val ListDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)

private val DetailDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale.ENGLISH)

internal actual fun formatListDate(epochMilliseconds: Long): String {
    val platform = JavaInstant.ofEpochMilli(epochMilliseconds)
    return ListDateFormatter.format(LocalDateTime.ofInstant(platform, ZoneId.systemDefault()))
}

internal actual fun formatDetailTimestamp(instant: Instant): String {
    val platformInstant = JavaInstant.ofEpochMilli(instant.toEpochMilliseconds())
    val local = LocalDateTime.ofInstant(platformInstant, ZoneId.systemDefault())
    return DetailDateFormatter.format(local)
}
