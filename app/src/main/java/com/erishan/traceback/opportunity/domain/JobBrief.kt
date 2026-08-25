package com.erishan.traceback.opportunity.domain

data class JobBrief(
    val generatedAtEpochMillis: Long,
    val model: String,
    val fit: Fit,
    val proposal: String,
    val price: Price,
    val duration: DurationEstimate,
    val approach: Approach,
)

data class Fit(val verdict: String, val summary: String)
data class Price(val low: String, val high: String, val rationale: String)
data class DurationEstimate(val range: String, val hours: String, val basis: String)
data class Approach(val summary: String, val technologies: List<String>)
