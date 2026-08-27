package com.erishan.traceback.ai.domain

import com.erishan.traceback.me.domain.UserContext

const val OPENAI_MODEL = "gpt-4o"

fun briefSystemPrompt(userContext: UserContext): String {
    val rateBand = userContext.rateBand?.takeIf { it.isNotBlank() } ?: "(none)"
    val pace = userContext.pace?.takeIf { it.isNotBlank() } ?: "(none)"
    return """
        Answer only with a JSON object. No markdown. No extra keys.
        Nested object, all values strings except technologies (string array).
        Required keys:
        fit.verdict, fit.summary,
        proposal,
        price.low, price.high, price.rationale,
        duration.range, duration.hours, duration.basis,
        approach.summary, approach.technologies
        Example:
        {"fit":{"verdict":"yes","summary":"..."},"proposal":"...","price":{"low":"...","high":"...","rationale":"..."},"duration":{"range":"...","hours":"...","basis":"typical"},"approach":{"summary":"...","technologies":["Compose"]}}

        Freelancer constraints:
        ABOUT:
        ${userContext.about}
        RATE BAND: $rateBand
        PACE: $pace

        Rules:
        - Use ABOUT as the freelancer's constraints. If the job fights what they won't do, verdict is "no" and proposal is a polite decline.
        - Price must respect rateBand when present; do not undercut it.
        - Duration: if pace is present, basis is "profile"; otherwise basis is "typical".
        - approach.summary is a few sentences. technologies is at most 5 strings.
        - Write proposal in the voice of ABOUT.
        - fit.verdict is one of: yes, stretch, no.
    """.trimIndent()
}

fun briefUserMessage(job: JobInput): String = buildString {
    appendLine("Title: ${job.title}")
    appendLine("Description: ${job.description.orEmpty()}")
    appendLine("Source: ${job.source}")
    val label = job.sourceLabel?.takeIf { it.isNotBlank() }
    if (label != null) {
        appendLine("Source label: $label")
    }
    val applied = job.appliedMessage?.takeIf { it.isNotBlank() }
    if (applied != null) {
        appendLine("Applied message: $applied")
    }
}.trim()
