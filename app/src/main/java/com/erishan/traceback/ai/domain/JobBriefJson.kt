package com.erishan.traceback.ai.domain

import com.erishan.traceback.opportunity.domain.Approach
import com.erishan.traceback.opportunity.domain.DurationEstimate
import com.erishan.traceback.opportunity.domain.Fit
import com.erishan.traceback.opportunity.domain.JobBrief
import com.erishan.traceback.opportunity.domain.Price
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private val briefJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

internal const val VERDICT_YES = "yes"
internal const val VERDICT_STRETCH = "stretch"
internal const val VERDICT_NO = "no"
internal const val BASIS_PROFILE = "profile"
internal const val BASIS_TYPICAL = "typical"

@Serializable
internal data class JobBriefDto(
    val generatedAtEpochMillis: Long? = null,
    val model: String? = null,
    val fit: FitDto? = null,
    val proposal: String? = null,
    val price: PriceDto? = null,
    val duration: DurationDto? = null,
    val approach: ApproachDto? = null,
)

@Serializable
internal data class FitDto(val verdict: String? = null, val summary: String? = null)

@Serializable
internal data class PriceDto(
    val low: String? = null,
    val high: String? = null,
    val rationale: String? = null,
)

@Serializable
internal data class DurationDto(
    val range: String? = null,
    val hours: String? = null,
    val basis: String? = null,
)

@Serializable
internal data class ApproachDto(
    val summary: String? = null,
    val technologies: List<String>? = null,
)

fun decodeJobBriefJson(json: String): JobBrief? {
    if (json.isBlank()) return null
    return try {
        briefJson.decodeFromString<JobBriefDto>(json).toDomainOrNull()
    } catch (_: Exception) {
        null
    }
}

fun encodeJobBriefJson(brief: JobBrief): String =
    briefJson.encodeToString(brief.toDto())

fun jobBriefFromModelJson(
    json: String,
    generatedAtEpochMillis: Long,
    model: String,
    pace: String?,
): JobBrief? {
    if (json.isBlank()) return null
    return try {
        val root = parseBriefObject(json) ?: return null
        val dto = JobBriefDto(
            generatedAtEpochMillis = generatedAtEpochMillis,
            model = model,
            fit = FitDto(
                verdict = root.text("fit.verdict", "verdict"),
                summary = root.text("fit.summary"),
            ),
            proposal = root.text("proposal"),
            price = PriceDto(
                low = root.text("price.low"),
                high = root.text("price.high"),
                rationale = root.text("price.rationale"),
            ),
            duration = DurationDto(
                range = root.text("duration.range"),
                hours = root.text("duration.hours"),
                basis = root.text("duration.basis"),
            ),
            approach = ApproachDto(
                summary = root.text("approach.summary"),
                technologies = root.stringList("approach.technologies", "technologies"),
            ),
        )
        dto.toDomainOrNull(
            generatedAtEpochMillis = generatedAtEpochMillis,
            model = model,
            pace = pace,
            overrideMetadata = true,
        )
    } catch (_: Exception) {
        null
    }
}

fun assistantMessageContent(chatCompletionsBody: String): String? {
    if (chatCompletionsBody.isBlank()) return null
    return try {
        val root = briefJson.parseToJsonElement(unwrapJsonPayload(chatCompletionsBody)) as? JsonObject
            ?: return@assistantMessageContent null
        val choices = root["choices"] as? JsonArray ?: return@assistantMessageContent null
        val message = (choices.firstOrNull() as? JsonObject)?.get("message") as? JsonObject
            ?: return@assistantMessageContent null
        when (val content = message["content"]) {
            null, JsonNull -> null
            is JsonPrimitive -> content.content.trim().takeIf { it.isNotEmpty() }
            is JsonArray -> content.mapNotNull { part ->
                when (part) {
                    is JsonPrimitive -> part.content.takeIf { it.isNotEmpty() }
                    is JsonObject -> part.text("text")
                    else -> null
                }
            }.joinToString("").trim().takeIf { it.isNotEmpty() }
            is JsonObject -> content.toString().takeIf { it.isNotBlank() }
        }
    } catch (_: Exception) {
        null
    }
}

internal fun normalizeVerdict(raw: String?): String {
    return when (raw?.trim()?.lowercase()) {
        VERDICT_YES -> VERDICT_YES
        VERDICT_NO -> VERDICT_NO
        VERDICT_STRETCH -> VERDICT_STRETCH
        else -> VERDICT_STRETCH
    }
}

internal fun basisFor(pace: String?): String =
    if (pace.isNullOrBlank()) BASIS_TYPICAL else BASIS_PROFILE

internal fun normalizeBasis(raw: String?): String {
    return when (raw?.trim()?.lowercase()) {
        BASIS_PROFILE -> BASIS_PROFILE
        BASIS_TYPICAL -> BASIS_TYPICAL
        else -> BASIS_TYPICAL
    }
}

private fun parseBriefObject(json: String): JsonObject? {
    val element = briefJson.parseToJsonElement(unwrapJsonPayload(json))
    val obj = element as? JsonObject ?: return null
    return obj.briefRoot()
}

private fun unwrapJsonPayload(raw: String): String {
    val trimmed = raw.trim()
    val fenced = Regex(
        """^```(?:json)?\s*([\s\S]*?)```$""",
        RegexOption.IGNORE_CASE,
    ).find(trimmed)
    return fenced?.groupValues?.get(1)?.trim() ?: trimmed
}

private fun JsonObject.briefRoot(): JsonObject {
    if (looksLikeBrief()) return this
    return values.filterIsInstance<JsonObject>().firstOrNull { it.looksLikeBrief() } ?: this
}

private fun JsonObject.looksLikeBrief(): Boolean =
    containsKey("fit") ||
        containsKey("proposal") ||
        containsKey("fit.verdict") ||
        containsKey("price") ||
        containsKey("price.low")

private fun JsonObject.text(vararg paths: String): String? {
    for (path in paths) {
        at(path).asText()?.let { return it }
    }
    return null
}

private fun JsonObject.stringList(vararg paths: String): List<String> {
    for (path in paths) {
        val values = at(path).asStringList()
        if (values.isNotEmpty()) return values.take(5)
    }
    return emptyList()
}

private fun JsonObject.at(path: String): JsonElement? {
    this[path]?.let { return it }
    var current: JsonElement = this
    for (part in path.split('.')) {
        current = (current as? JsonObject)?.get(part) ?: return null
    }
    return current
}

private fun JsonElement?.asText(): String? = when (this) {
    null, JsonNull -> null
    is JsonPrimitive -> content.trim().takeIf { it.isNotEmpty() }
    is JsonArray -> mapNotNull { it.asText() }.joinToString(", ").takeIf { it.isNotEmpty() }
    is JsonObject -> null
}

private fun JsonElement?.asStringList(): List<String> = when (this) {
    null, JsonNull -> emptyList()
    is JsonArray -> mapNotNull { it.asText() }.filter { it.isNotEmpty() }
    is JsonPrimitive -> content
        .split(',', ';')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    else -> emptyList()
}

private fun JobBriefDto.toDomainOrNull(
    generatedAtEpochMillis: Long? = this.generatedAtEpochMillis,
    model: String? = this.model,
    pace: String? = null,
    overrideMetadata: Boolean = false,
): JobBrief? {
    val fitDto = fit ?: return null
    val priceDto = price ?: return null
    val durationDto = duration ?: return null
    val approachDto = approach ?: return null
    val proposalText = proposal?.trim().orEmpty()
    if (proposalText.isEmpty()) return null
    val fitSummary = fitDto.summary?.trim().orEmpty()
    val priceLow = priceDto.low?.trim().orEmpty()
    val priceHigh = priceDto.high?.trim().orEmpty()
    val priceRationale = priceDto.rationale?.trim().orEmpty()
    val durationRange = durationDto.range?.trim().orEmpty()
    val durationHours = durationDto.hours?.trim().orEmpty()
    val approachSummary = approachDto.summary?.trim().orEmpty()
    if (fitSummary.isEmpty() ||
        priceLow.isEmpty() ||
        priceHigh.isEmpty() ||
        priceRationale.isEmpty() ||
        durationRange.isEmpty() ||
        durationHours.isEmpty() ||
        approachSummary.isEmpty()
    ) {
        return null
    }
    val generatedAt = generatedAtEpochMillis ?: return null
    val modelName = model?.trim().orEmpty()
    if (modelName.isEmpty()) return null
    val basis = if (overrideMetadata) basisFor(pace) else normalizeBasis(durationDto.basis)
    return JobBrief(
        generatedAtEpochMillis = generatedAt,
        model = modelName,
        fit = Fit(
            verdict = normalizeVerdict(fitDto.verdict),
            summary = fitSummary,
        ),
        proposal = proposalText,
        price = Price(
            low = priceLow,
            high = priceHigh,
            rationale = priceRationale,
        ),
        duration = DurationEstimate(
            range = durationRange,
            hours = durationHours,
            basis = basis,
        ),
        approach = Approach(
            summary = approachSummary,
            technologies = approachDto.technologies
                .orEmpty()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .take(5),
        ),
    )
}

private fun JobBrief.toDto() = JobBriefDto(
    generatedAtEpochMillis = generatedAtEpochMillis,
    model = model,
    fit = FitDto(verdict = fit.verdict, summary = fit.summary),
    proposal = proposal,
    price = PriceDto(low = price.low, high = price.high, rationale = price.rationale),
    duration = DurationDto(
        range = duration.range,
        hours = duration.hours,
        basis = duration.basis,
    ),
    approach = ApproachDto(
        summary = approach.summary,
        technologies = approach.technologies,
    ),
)
