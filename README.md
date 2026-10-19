# 👑 Nah, I'd Win — Android Native APK

> **The Awakening Protocol** — An ultra-lightweight, blazing-fast native Android application engineered for elite student mindsets, focus sessions, and post-exam dominance.

[![Build & Release Signed APK](https://github.com/Ayoubvvch/nah-id-win/actions/workflows/build-release.yml/badge.svg)](https://github.com/Ayoubvvch/nah-id-win/actions/workflows/build-release.yml)
[![GitHub release](https://img.shields.io/github/v/release/Ayoubvvch/nah-id-win?color=00f0ff&style=flat-square)](https://github.com/Ayoubvvch/nah-id-win/releases/latest)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)

---

## ⚡ Highlights & Architecture

- **100% Content & Design Fidelity**: All original rituals, acts, texts, and sound engines from *The Awakening Protocol* are preserved with pixel-perfect accuracy.
- **Zero-Latency Instant Boot**: Programmatic UI construction in Kotlin without XML inflation overhead for instantaneous launch times.
- **Persistent Digital Keystore (V1 + V2 + V3 + V4)**: Digitally signed with a permanent 10,000-day keystore located at `app/keystore/release.jks`. All future releases can be installed directly over existing installs without uninstalling.
- **Post-Exam Surveillance Radar (Gojo vs Sukuna)**:
  - Dialogue sequence tailored for the exact moment after finishing a grueling math or science exam:
    1. *"Did you just leave it now?"*
    2. *"Did you fight seriously?"*
    3. *"Did you win?"*
    4. *"The surveillance camera caught you fighting it... do you want to see the footage?"*
    5. *"THIS IS YOU AGAINST IT"*
  - High-energy Gojo vs Sukuna anime edit video is pre-bundled directly inside the APK assets (`video/gojo_sukuna.mp4`) for **100% offline playback** with zero network requirement when leaving school.
- **Automated CI/CD via GitHub Actions**: Builds, signs, and packages release APKs automatically upon every push to `main` or tag, creating a GitHub Release with direct APK downloads.
- **Integrated In-App Updater**:
  - Live version check against GitHub Releases API.
  - Interactive download progress bar (percentage, downloaded MB, total MB).
  - Automated package installer trigger via Android `FileProvider`.

---

## 🛠️ Tech Stack & Components

| Component | Implementation |
|---|---|
| **Platform** | Android (minSdk 24, compileSdk 34, targetSdk 34) |
| **Language** | Kotlin 1.9.22 / Java 17 |
| **Engine** | Hardware-Accelerated WebView + AndroidX WebKit |
| **Asset Loader** | `WebViewAssetLoader` with HTTPS local asset domain |
| **Audio Synthesis** | Zero-dependency Web Audio API sound engines (gamma binaural beats, ascension chords) |
| **Video Engine** | Hardware-accelerated HTML5 video with local offline MP4 asset + remote fallback |
| **Signing** | V1 (Jar Signature), V2 (Full APK), V3 (Key Rotation), V4 (fs-verity) |

---

## 📦 Keystore Details

- **Path**: `app/keystore/release.jks`
- **Alias**: `nahidwin`
- **Signature Schemes**: V1 + V2 + V3 + V4
- **Validity**: 10,000 days

---

## 🚀 How to Build Locally

```bash
# Clone the repository
git clone https://github.com/Ayoubvvch/nah-id-win.git
cd nah-id-win

# Build release APK
./gradlew assembleRelease
```

The signed release APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`

---

## 📜 License

Created with sovereignty by **AcMods**. All rights reserved.
