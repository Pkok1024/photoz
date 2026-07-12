# PhotoZ ROADMAP2 — Post-Beta Enhancement Plan

> Updated: 2026-07-07 (Security & Architecture Overhaul)

---

## Summary

Project is in a "Green Build" state with all security features implemented and the UI layer migrated to Compose (with placeholders for non-security UI).

| Status | Count | Items |
|---|---|---|
| ✅ DONE | 12 | #1, #2, #3, #4, #5, #6, #7, #8, #9, #10, #13, #16 |
| ⏸️ BLOCKED | 1 | #15 TFLite |
| ⏸️ DEFERRED | 3 | #11, #12, #14 |

---

## Completed Security Milestones

1. **Argon2id Upgrade**: Advanced KDF resistance against brute force.
2. **SQLCipher Architecture**: Full at-rest encryption for sensitive metadata.
3. **Plausible Deniability**: Multi-vault HMAC-SHA256 design with constant-time unlock.
4. **Environment Integrity**: Root and Debugger detection with UI warnings.
5. **Modern Architecture**: 100% Jetpack Compose, Fragment-less, hardware-backed keys.
