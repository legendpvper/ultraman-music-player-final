# 🦸 Ultraman Opening Themes — Android App

A native Android music player featuring **31 Ultraman opening theme songs** (1966–2024),  
streaming audio via the YouTube player library with **background playback** support.

---

## 📱 Features

- Full song list: Ultraman (1966) → Ultraman Arc (2024)
- In-app YouTube video/audio player with seek bar
- **Background playback** — music keeps playing when you switch apps
- Persistent **notification controls** (play/pause/next/prev/stop)
- Mini player bar at the bottom of the song list
- Auto-advances to the next song when one ends
- Color-coded song cards per series era

---

## 🛠 Build Requirements

| Tool | Version |
|------|---------|
| Android Studio | Hedgehog (2023.1.1) or newer |
| Android SDK | API 34 (compile), API 24 (minimum) |
| JDK | 17 |
| Gradle | 8.2 (auto-downloaded) |

---

## 🚀 How to Build & Install

### Option A — Android Studio (recommended)

1. **Open the project**  
   Open Android Studio → `File > Open` → select the `ultraman_app` folder

2. **Sync Gradle**  
   Android Studio will prompt to sync. Click **Sync Now**.  
   All dependencies will be downloaded automatically.

3. **Connect your Android phone**  
   - Enable **Developer Options** on your phone  
   - Enable **USB Debugging**  
   - Connect via USB

4. **Run the app**  
   Click the green ▶ **Run** button, select your device.  
   Android Studio will build and install the APK automatically.

---

### Option B — Build APK from command line

```bash
# On macOS/Linux:
cd ultraman_app
./gradlew assembleDebug

# On Windows:
cd ultraman_app
gradlew.bat assembleDebug
```

The APK will be output to:
```
app/build/outputs/apk/debug/app-debug.apk
```

Then install it with ADB:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚙️ First-Time Gradle Wrapper Setup

If `gradle-wrapper.jar` is missing (network was unavailable during generation):

**Option 1** — Android Studio handles this automatically on first sync.

**Option 2** — Run manually:
```bash
gradle wrapper --gradle-version 8.2
```
(requires Gradle installed locally)

---

## 📋 Song List

| # | Title | Series | Year | Artist |
|---|-------|--------|------|--------|
| 01 | Ultraman no Uta | Ultraman | 1966 | Mitsuko Horie & Columbia Yurikago-kai |
| 02 | Ultraseven no Uta | Ultraseven | 1967 | Michio Mamiya |
| 03 | Kaette Kita Ultraman no Uta | Return of Ultraman | 1971 | Mitsuko Horie |
| 04 | Ultraman Ace | Ultraman Ace | 1972 | Katsuhiko Kobayashi |
| 05 | Ultraman Taro | Ultraman Taro | 1973 | Saburo Kitajima |
| 06 | Fly! Ultraman Leo | Ultraman Leo | 1974 | Toru Funamura |
| 07 | Ultraman 80 no Uta | Ultraman 80 | 1980 | Junichi Konno |
| 08 | Ultraman Great | Ultraman Great | 1990 | Graham Dobbyn |
| 09 | Take Me Higher | Ultraman Tiga | 1996 | V6 |
| 10 | Brave Love, Tiga | Ultraman Tiga | 1996 | Sharan Q |
| 11 | Ultraman Dyna | Ultraman Dyna | 1997 | Hironobu Kageyama |
| 12 | Ultraman Gaia | Ultraman Gaia | 1998 | Masato Shimon |
| 13 | Ultraman Cosmos | Ultraman Cosmos | 2001 | Lip's |
| 14 | Ultra Man Next | Ultraman: The Next | 2004 | Yuji Shimomura |
| 15 | Ultraman Nexus | Ultraman Nexus | 2004 | Koji Kikkawa |
| 16 | Ultraman Max no Uta | Ultraman Max | 2005 | Project DMM |
| 17 | Ultraman Mebius no Uta | Ultraman Mebius | 2006 | Voyager |
| 18 | Ultraman Zero no Uta | Ultraman Zero | 2010 | Voyager |
| 19 | Ultraman Saga | Ultraman Saga | 2012 | Voyager |
| 20 | Ultraman Ginga no Uta | Ultraman Ginga | 2013 | Voyager |
| 21 | Ultraman Ginga S no Uta | Ultraman Ginga S | 2014 | Voyager |
| 22 | Ultraman X no Uta | Ultraman X | 2015 | Voyager |
| 23 | Ultraman Orb no Uta | Ultraman Orb | 2016 | Voyager |
| 24 | Ultraman Geed no Uta | Ultraman Geed | 2017 | Voyager |
| 25 | Ultraman R/B no Uta | Ultraman R/B | 2018 | Voyager |
| 26 | Ultraman Taiga no Uta | Ultraman Taiga | 2019 | Voyager |
| 27 | Ultraman Z no Uta | Ultraman Z | 2020 | Voyager |
| 28 | Ultraman Trigger no Uta | Ultraman Trigger | 2021 | Voyager |
| 29 | Ultraman Decker no Uta | Ultraman Decker | 2022 | Voyager |
| 30 | Ultraman Blazar no Uta | Ultraman Blazar | 2023 | Voyager |
| 31 | Ultraman Arc no Uta | Ultraman Arc | 2024 | Voyager |

---

## 🔧 Updating YouTube IDs

YouTube video IDs can go stale over time (videos get removed/re-uploaded).  
To update them, edit `SongRepository.kt` — each song has a `youtubeId` field:

```kotlin
UltramanSong(1, "Ultraman no Uta", ..., youtubeId = "YXNAA17jh-M", ...)
```

Replace the ID with the 11-character code from the YouTube URL:  
`https://www.youtube.com/watch?v=`**`YXNAA17jh-M`**

---

## 📦 Project Structure

```
ultraman_app/
├── app/
│   ├── build.gradle                    # Dependencies
│   └── src/main/
│       ├── AndroidManifest.xml         # Permissions & components
│       ├── java/com/ultraman/themes/
│       │   ├── model/
│       │   │   ├── UltramanSong.kt     # Data model
│       │   │   └── SongRepository.kt  # All 31 songs + YouTube IDs
│       │   ├── service/
│       │   │   └── MusicService.kt    # Background playback service
│       │   └── ui/
│       │       ├── MainActivity.kt    # Song list screen
│       │       └── PlayerActivity.kt  # Player screen
│       └── res/
│           ├── layout/                 # UI layouts
│           ├── drawable/               # Icons & shapes
│           ├── values/                 # Colors, strings, themes
│           └── mipmap-*/               # App icons
├── build.gradle
├── settings.gradle
└── gradle.properties
```

---

## ⚠️ Notes

- Requires an **internet connection** to stream from YouTube
- The app requests **notification permission** on Android 13+ for the media controls
- Ultraman Arc (2024) YouTube ID may need updating once an official upload is available
- This app streams copyrighted content — for personal use only
