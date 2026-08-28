package com.erishan.traceback.ai.data

import com.erishan.traceback.ai.domain.KeyPresence
import com.erishan.traceback.ai.domain.SecretStore
import com.erishan.traceback.ai.domain.lastFourOf
import com.erishan.traceback.ai.domain.trimmedOpenAiKey
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class SecretStoreImpl : SecretStore {
    private val presence = MutableStateFlow(KeyPresence(hasKey = false, lastFour = null))

    override fun observe(): Flow<KeyPresence> = presence.asStateFlow()

    override suspend fun setOpenAiKey(value: String) {
        val trimmed = trimmedOpenAiKey(value)
            ?: throw IllegalArgumentException("OpenAI key must not be blank")
        withContext(Dispatchers.IO) { writeKey(trimmed) }
        presence.value = KeyPresence(hasKey = true, lastFour = lastFourOf(trimmed))
    }

    override suspend fun openAiKey(): String? = withContext(Dispatchers.IO) {
        readKey()
    }

    override suspend fun clearOpenAiKey() {
        withContext(Dispatchers.IO) { deleteKey() }
        presence.value = KeyPresence(hasKey = false, lastFour = null)
    }

    suspend fun warmUp() {
        val snapshot = withContext(Dispatchers.IO) {
            migrateFromUserDefaultsIfNeeded()
            readPresence()
        }
        presence.value = snapshot
    }

    private fun readPresence(): KeyPresence {
        val stored = readKey()
        return if (stored.isNullOrEmpty()) {
            KeyPresence(hasKey = false, lastFour = null)
        } else {
            KeyPresence(hasKey = true, lastFour = lastFourOf(stored))
        }
    }

    private fun migrateFromUserDefaultsIfNeeded() {
        val defaults = NSUserDefaults.standardUserDefaults
        val legacy = defaults.stringForKey(LEGACY_PREF_KEY) ?: return
        if (legacy.isEmpty()) {
            defaults.removeObjectForKey(LEGACY_PREF_KEY)
            defaults.synchronize()
            return
        }
        if (readKey() == null) {
            writeKey(legacy)
        }
        defaults.removeObjectForKey(LEGACY_PREF_KEY)
        defaults.synchronize()
    }

    private fun readKey(): String? {
        val query = mutableQuery()
        CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)
        return memScoped {
            val out = alloc<CFTypeRefVar>()
            when (val status = SecItemCopyMatching(query, out.ptr)) {
                errSecItemNotFound -> null
                errSecSuccess -> {
                    val data = CFBridgingRelease(out.value) as NSData
                    NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString()
                }
                else -> error("Keychain read failed: $status")
            }
        }
    }

    private fun writeKey(value: String) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            ?: error("Could not encode OpenAI key")
        deleteKey()
        val query = mutableQuery()
        CFDictionarySetValue(query, kSecValueData, CFBridgingRetain(data) as CFTypeRef)
        val status = SecItemAdd(query, null)
        check(status == errSecSuccess) { "Keychain write failed: $status" }
    }

    private fun deleteKey() {
        val status = SecItemDelete(mutableQuery())
        check(status == errSecSuccess || status == errSecItemNotFound) {
            "Keychain delete failed: $status"
        }
    }

    private fun mutableQuery() = CFDictionaryCreateMutable(kCFAllocatorDefault, 0, null, null).also {
        CFDictionarySetValue(it, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(it, kSecAttrService, CFBridgingRetain(SERVICE) as CFTypeRef)
        CFDictionarySetValue(it, kSecAttrAccount, CFBridgingRetain(ACCOUNT) as CFTypeRef)
    }

    private companion object {
        const val SERVICE = "com.erishan.traceback.ai"
        const val ACCOUNT = "openai_api_key"
        const val LEGACY_PREF_KEY = "openai_api_key"
        const val NSUTF8StringEncoding: ULong = 4u
    }
}
