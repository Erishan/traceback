# ADR-0014 — Local OpenAI Key, No Backend

Status: Accepted · 2026-08-25

## Context

v1.1 needs a model to brief a job. The product is a showcase that never holds
user secrets. ChatGPT Plus is a chat subscription, not API access. Other
providers are deferred so the first ship stays one client and one key slot.

## Decision

- The user pastes an OpenAI API key on the Me screen. It is stored with
  `EncryptedSharedPreferences` (Android Keystore). It is not a Room column,
  not in logs, not in backup (`allowBackup` is already false).
- After save, the UI shows only the last four characters. Clear removes the
  ciphertext.
- There is no Traceback server, proxy, or analytics host. The process may
  talk only to `https://api.openai.com` and only when the user runs a brief.
- One model is hardcoded: `gpt-4o`. No picker. Claude and Gemini are out of
  v1.1.
- The domain sees a `SecretStore` (`hasKey`, `lastFour`, `set`, `clear`). The
  implementation is Android-specific; the interface is not.

## Consequences

- Losing the keystore (app uninstall, failed backup) loses the key. Re-paste
  is the recovery. That is accepted: we do not hold a copy.
- A wrong key is discovered on the first brief (401), not on save. Save does
  not hit the network.
- Adding Anthropic or Google later is a second `SecretStore` slot and a second
  client. The OpenAI client must not encode “the only LLM in the world”, only
  “the only one wired today”.

## Reverse cost

Cheap to add a provider. Expensive to introduce a backend that sees keys —
every screen and the README’s “we never hold them” claim would have to change.
