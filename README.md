# 👑 Nah, I'd Win — Android Native APK

> **The Post-Battle Declassification Protocol** — An ultra-lightweight, blazing-fast native Android application dedicated 100% to the exact moment of stepping out through the school gates after conquering the exam.

[![Build & Release Signed APK](https://github.com/Ayoubvvch/nah-id-win/actions/workflows/build-release.yml/badge.svg)](https://github.com/Ayoubvvch/nah-id-win/actions/workflows/build-release.yml)
[![GitHub release](https://img.shields.io/github/v/release/Ayoubvvch/nah-id-win?color=00f0ff&style=flat-square)](https://github.com/Ayoubvvch/nah-id-win/releases/latest)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)

---

## ⚡ Highlights & Key Capabilities

- **100% Dedicated to the Exit Moment**: Pure post-exam debrief flow designed specifically for the moment you walk out of the exam hall.
- **Automatic Immersive Fullscreen**: Boots automatically into full immersive sticky fullscreen mode (`WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`) with hidden status and navigation bars.
- **100% English Interface**: Clean, internationalized cyberpunk typography, zero clutter, and pure focus.
- **The 4-Stage Post-Combat Inquiry**:
  1. **The Gate Breach**: *"Did you just walk out of it?"*
  2. **The Combat Rigor**: *"Did you fight seriously?"*
  3. **The Supreme Verdict**: *"Did you win? — Nah, I'd win."*
  4. **The Security Intercept**: *"The surveillance camera caught you fighting it... do you want to see the footage?"*
- **Offline High-Energy Battle Video**: The legendary Gojo vs Sukuna anime edit is pre-bundled inside the APK assets (`video/gojo_sukuna.mp4`) for **100% offline playback** with zero network requirement.
- **Persistent Digital Signing (V1+V2+V3+V4)**: Signed permanently with `app/keystore/release.jks`, allowing any future updates to install directly over existing versions without uninstalling.
- **Automated CI/CD via GitHub Actions**: Builds, signs, and releases new APKs automatically to GitHub Releases upon every push.
- **Integrated In-App Updater**: Built-in GitHub Releases checker with live download progress bar and direct APK installer launch via Android `FileProvider`.

---

## 🛠️ Architecture & Specifications

| Component | Specification |
|---|---|
| **Platform** | Android (minSdk 24, compileSdk 34, targetSdk 34) |
| **Language** | Kotlin 1.9.22 / Java 17 |
| **Display Mode** | Edge-to-Edge Immersive Sticky Fullscreen |
| **Engine** | Hardware-Accelerated WebView + AndroidX WebKit |
| **Asset Delivery** | `WebViewAssetLoader` with secure internal origin |
| **Audio Engine** | Zero-dependency Web Audio API (tactile bass drops & ascension chords) |
| **Signing** | V1 (Jar), V2 (Full APK), V3 (Key Rotation), V4 (fs-verity) |

---

## 📦 Keystore Details

- **Path**: `app/keystore/release.jks`
- **Alias**: `nahidwin`
- **Validity**: 10,000 days

---

## 🚀 Building Locally

```bash
git clone https://github.com/Ayoubvvch/nah-id-win.git
cd nah-id-win
./gradlew assembleRelease
```

---

## 📜 License

Created with sovereignty by **AcMods**. All rights reserved.
