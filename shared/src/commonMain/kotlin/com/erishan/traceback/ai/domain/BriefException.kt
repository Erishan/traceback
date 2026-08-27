package com.erishan.traceback.ai.domain

class BriefException(val kind: Kind) : Exception() {
    enum class Kind {
        MissingKey,
        Unauthorized,
        RateLimited,
        InvalidResponse,
        Network,
    }
}
