## 2025-06-19 - [Manifest Comment Build Failure]
**Vulnerability:** N/A (Build Issue)
**Learning:** Placing XML comments inside the `<application>` tag in `AndroidManifest.xml` can cause the Android Manifest Merger to fail with an "Error parsing" exception.
**Prevention:** Always place security-related XML comments outside of the tag they refer to (e.g., above the `<application>` tag) to ensure build compatibility.

## 2025-06-19 - [SDK Path Inconsistency]
**Vulnerability:** N/A (Environment Issue)
**Learning:** The build environment may have the Android SDK platform named `android-37.0` while the build scripts expect `android-37`, leading to "Failed to find target" errors.
**Prevention:** Symlink `android-37.0` to `android-37` in the SDK platforms directory when encountering this build failure in CI/headless environments.
