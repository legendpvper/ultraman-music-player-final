# 🦸 Ultraman Music Player — Android App

A native Android music player featuring **59 Ultraman opening and ending theme songs** (1966–2024),
organised by character and series, with background playback, a queue system, and local MP3 import support.

---

## 📱 Features

- **59 songs** — 31 openings and 28 endings spanning Ultraman (1966) to Ultraman Arc (2024)
- Browse songs by **Ultraman character**, grouped by series
- Songs tagged as **Opening** or **Ending** for easy identification
- **Background playback** via foreground music service
- Persistent **notification controls** (play/pause/next/prev/stop)
- **Queue system** — add any song to the queue from the song list or player screen
- **Local MP3 imports** — import your own MP3s from device storage, playable alongside Ultraman songs
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

3. **Add the assets folder**
   The `assets/` folder is not included in the repository (too large for GitHub).
   Place your 59 MP3 files in:
   ```
   app/src/main/assets/
   ```
   Files should be named `01_` through `59_` (01–31 are openings, 32–59 are endings).

4. **Connect your Android phone**
   - Enable **Developer Options** on your phone
   - Enable **USB Debugging**
   - Connect via USB

5. **Run the app**
   Click the green ▶ **Run** button, select your device.
   Android Studio will build and install the APK automatically.

---

### Option B — Build APK from command line

```powershell
# On Windows (PowerShell):
cd ultraman_app
.\gradlew assembleDebug
```

```bash
# On macOS/Linux:
cd ultraman_app
./gradlew assembleDebug
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

## 📦 Project Structure

```
ultraman_app/
├── app/
│   ├── build.gradle                        # Dependencies
│   └── src/main/
│       ├── AndroidManifest.xml             # Permissions & components
│       ├── assets/                         # 59 MP3 files (not in repo)
│       ├── java/com/ultraman/themes/
│       │   ├── model/
│       │   │   ├── UltramanSong.kt         # Song data model
│       │   │   ├── UltramanCharacter.kt    # Character grouping model
│       │   │   ├── SongRepository.kt       # All 59 songs & characters
│       │   │   ├── LocalSong.kt            # Imported local song model
│       │   │   └── LocalSongRepository.kt  # SharedPreferences persistence
│       │   ├── service/
│       │   │   └── MusicService.kt         # Foreground music service + queue
│       │   └── ui/
│       │       ├── MainActivity.kt         # Character list screen
│       │       ├── SongListActivity.kt     # Songs per character screen
│       │       ├── PlayerActivity.kt       # Music player screen
│       │       ├── QueueActivity.kt        # Queue management screen
│       │       └── LocalImportsActivity.kt # Local MP3 imports screen
│       └── res/
│           ├── layout/                     # UI layouts
│           ├── drawable/                   # Icons & shapes
│           ├── values/                     # Colors, strings, themes
│           └── mipmap-*/                   # App icons
├── build.gradle
├── settings.gradle
└── gradle.properties
```

---

## 🎵 Song Data

| # | Type | Series |
|---|------|--------|
| 01–31 | Opening | Ultraman (1966) → Ultraman Arc (2024) |
| 32–59 | Ending | Ultraman (1966) → Ultraman Arc (2024) |

Songs are defined in `SongRepository.kt` as `UltramanSong` objects with the following fields:

```kotlin
data class UltramanSong(
    val id: Int,
    val title: String,
    val artist: String,
    val series: String,         // must match exactly for character grouping
    val year: Int,
    val assetFileName: String,  // filename in assets/ folder
    val color: String,          // hex color for card UI
    val songType: String        // "Opening" or "Ending"
)
```

---

## 🔧 Local MP3 Imports

Users can import their own MP3 files from device storage:

- Tap the menu icon in **MainActivity** and select **Local Imports**
- Pick any MP3 from your phone — metadata is extracted automatically
- Imported songs are saved permanently via SharedPreferences
- Local songs can be played and added to the queue alongside Ultraman songs
- Local songs use `id = -1` as a sentinel value in the queue system

---

## 📋 Queue System

- Add any song to the queue via the **Add to Queue** button on the song list or player screen
- View and manage the queue via **QueueActivity**
- The player checks the queue first before auto-advancing to the next song
- Queue is managed in `MusicService` using an `ArrayDeque<UltramanSong>`

---

## ⚠️ Notes

- The `assets/` folder is excluded from GitHub — MP3 files live only on your local machine
- Songs are played from local assets, so **no internet connection is required**
- This app is for personal use only
