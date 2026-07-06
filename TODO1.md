# PhotoZ TODO List

## High Priority

### 1. QtFastStart — MP4 MOOV relocation untuk progressive video streaming
- **Status**: ✅ DONE (Commit `80b8838e`)

### 2. Chunked streaming encryption untuk file besar (video)
- **Status**: ✅ DONE — Version 0x04 implemented with per-chunk nonce + tag.

### 3. KDF upgrade: PBKDF2 → Argon2id
- **Status**: ✅ DONE — Argon2id via Bouncy Castle implemented for new vaults and recovery phrases.

## Medium Priority

### 4. Key storage audit: confirm VMK di TEE (bukan StrongBox)
- **Status**: ✅ VERIFIED — Biometric KEK is TEE-backed (non-StrongBox) for performance.

### 5. Jetpack Security deprecation check
- **Status**: ✅ VERIFIED — PhotoZ does not use `security-crypto`.

## Anti-Forensic Hardening

### 6. SQLCipher untuk Room DB encryption
- **Status**: ✅ DONE — SQLCipher enabled with Keystore-backed passphrase. Bootstrap DB handles plaintext metadata.

### 7. VMK zeroing dari memory (explicit ByteArray.fill(0))
- **Status**: ✅ DONE (Commit `31d74f1a`)

### 8. Screenshot prevention (FLAG_SECURE audit)
- **Status**: ✅ DONE (Commit `31d74f1a`)

### 9. Root detection + debugger detection
- **Status**: ✅ DONE — SecurityChecker integrated into Unlock flow with UI warnings.

### 10. Duress PIN / Panic password
- **Status**: ✅ DONE (M7+P3+L2 covers this)

## Deferred / Low Priority

### 11. Hardware Key Attestation
- **Status**: ⏸️ DEFERRED (Enterprise only)

### 12. Play Integrity API
- **Status**: ⏸️ DEFERRED (Play Store only)

### 13. Rclone binary optimization
- **Status**: ✅ Phase 2 DONE — Command stripping in `build-rclone.sh`. Phase 3 (JNI) evaluated.

### 14. M6 Modularization
- **Status**: ⏸️ DEFERRED (Large refactor)

### 15. L6 TFLite model activation
- **Status**: 📝 TODO — Scaffold ready, needs `mobilenet_v2.tflite` in assets/.

### 16. M7 v2 full multi-vault registry merge
- **Status**: ✅ DONE — `HashRegistry` now merges entries from other vaults.
