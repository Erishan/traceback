package com.erishan.traceback.me.data

import com.erishan.traceback.me.domain.UserContext

fun UserContextEntity.toDomain(): UserContext {
    return UserContext(
        about = about,
        rateBand = rateBand,
        pace = pace,
    )
}

fun UserContext.toEntity(): UserContextEntity {
    return UserContextEntity(
        id = USER_CONTEXT_ID,
        about = about,
        rateBand = rateBand?.trim()?.takeIf { it.isNotEmpty() },
        pace = pace?.trim()?.takeIf { it.isNotEmpty() },
    )
}

fun UserContextEntity?.toDomainOrEmpty(): UserContext {
    return this?.toDomain() ?: UserContext(about = "", rateBand = null, pace = null)
}
