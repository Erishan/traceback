package com.erishan.traceback.core.net

import io.ktor.client.HttpClient

expect fun createPlatformHttpClient(): HttpClient
