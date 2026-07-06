# PhotoZ ROADMAP2 — Post-Beta Enhancement Plan

> Updated: 2026-07-06 (Status Update)

---

## Summary

Sisa TODO yang perlu dikerjakan: 1 item (#15 TFLite activation)

| Status | Count | Items |
|---|---|---|
| ✅ DONE | 12 | #1, #2, #3, #4, #5, #6, #7, #8, #9, #10, #13, #16 |
| 📝 TODO | 1 | #15 TFLite |
| ⏸️ DEFERRED | 3 | #11, #12, #14 |

---

## Phase 1 — Security Hardening (Completed) ✅

1. **SQLCipher for Room DB encryption (TODO #6)**: Implemented with Keystore-backed key and Bootstrap DB.
2. **Argon2id KDF upgrade (TODO #3)**: Implemented for both password and recovery phrase protections.
3. **TEE key storage audit (TODO #4)**: Confirmed TEE usage for biometric KEK.

---

## Phase 2 — Architecture Improvements (Completed) ✅

4. **Chunked streaming encryption (TODO #2)**: Version 0x04 implemented.
5. **Rclone command stripping (TODO #13 Phase 2)**: Integrated into build script.
6. **M7 v2 full multi-vault registry merge (TODO #16)**: Implemented in HashRegistry.

---

## Phase 3 — Feature Activation (Ongoing)

7. **L6 TFLite model activation (TODO #15)**: Scaffold ready. Activation pending model file.
8. **Root/debugger detection (TODO #9)**: Implemented and integrated into Unlock flow. ✅

---

## Deferred

9. Hardware Key Attestation (TODO #11)
10. Play Integrity API (TODO #12)
11. M6 Modularization (TODO #14)
