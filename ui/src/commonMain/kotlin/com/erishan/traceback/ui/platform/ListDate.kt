package com.erishan.traceback.ui.platform

import kotlin.time.Instant

internal expect fun formatListDate(epochMilliseconds: Long): String

internal expect fun formatDetailTimestamp(instant: Instant): String
