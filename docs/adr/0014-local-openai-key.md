# ADR-0014 — Local OpenAI Key, No Backend

Status: Accepted · 2026-08-25

## Context

v1.1 needs a model to brief a job. The product never holds a secret that
belongs to the user. A ChatGPT subscription is not API access. Other providers
wait, so the first release has one client and one key slot.

## Decision

- The user pastes an OpenAI API key on the Me screen. Android stores it with
  `EncryptedSharedPreferences`; iOS stores it as a Keychain generic password
  with `kSecAttrAccessibleWhenUnlockedThisDeviceOnly`. It is not a Room column,
  not in logs and not in backup or device-to-device migration.
- After save the UI shows only the last four characters. Clear removes the
  stored value.
- There is no Traceback server, proxy or analytics host. The process may talk
  only to `https://api.openai.com`, and only when the user runs a brief.
- One model is fixed in code, `gpt-4o`. There is no picker.
- The domain sees a `SecretStore` interface. Only the implementation is
  platform-specific.

## Consequences

- Losing the keystore or device-only Keychain item loses the key, and the user
  pastes it again. That is accepted, because no copy is kept.
- A wrong key shows up on the first brief as a 401 and not on save, because
  save does not use the network.
- Adding another provider later is a second store and a second client. The
  OpenAI client must not read as the only model in the world, only as the only
  one wired today.

## Reverse cost

Cheap to add a provider. Expensive to add a backend that sees keys, because
every screen and the claim in the README would have to change.
