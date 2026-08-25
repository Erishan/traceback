package com.erishan.traceback.opportunity.data

import com.erishan.traceback.ai.domain.decodeJobBriefJson
import com.erishan.traceback.ai.domain.encodeJobBriefJson
import com.erishan.traceback.opportunity.domain.JobBrief

internal fun String?.toJobBrief(): JobBrief? {
    if (isNullOrBlank()) return null
    return decodeJobBriefJson(this)
}

internal fun JobBrief?.toJobBriefColumn(): String? =
    this?.let(::encodeJobBriefJson)
