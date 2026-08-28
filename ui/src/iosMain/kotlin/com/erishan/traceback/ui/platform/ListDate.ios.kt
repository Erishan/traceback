package com.erishan.traceback.ui.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone

internal actual fun formatListDate(epochMilliseconds: Long): String {
    val date = NSDate.dateWithTimeIntervalSince1970(epochMilliseconds / 1_000.0)
    val formatter = NSDateFormatter().apply {
        dateFormat = "d MMM yyyy"
        locale = NSLocale(localeIdentifier = "en_US_POSIX")
        timeZone = NSTimeZone.localTimeZone
    }
    return formatter.stringFromDate(date)
}
