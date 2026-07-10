## 2026-07-10 - [Insecure Backup Configuration]
**Vulnerability:** 'android:allowBackup' was set to 'true' in 'AndroidManifest.xml'.
**Learning:** Default Android configuration allows app data (databases, prefs) to be extracted via 'adb backup' or cloud sync, exposing sensitive vault metadata even if media is encrypted.
**Prevention:** Always set 'android:allowBackup="false"' in the manifest for security-critical applications and document the reasoning.
