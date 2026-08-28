package com.erishan.traceback.ui.platform

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ListDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)

internal actual fun formatListDate(epochMilliseconds: Long): String {
    val platform = Instant.ofEpochMilli(epochMilliseconds)
    return ListDateFormatter.format(LocalDateTime.ofInstant(platform, ZoneId.systemDefault()))
}
