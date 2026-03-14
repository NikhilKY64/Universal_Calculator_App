[![Platform](https://img.shields.io/badge/platform-Android-green)](https://github.com/NikhilKY64/Universal_Calculator_App/releases)

[![Downloads](https://img.shields.io/github/downloads/NikhilKY64/Universal_Calculator_App/v1.2/total)](https://github.com/NikhilKY64/Universal_Calculator_App/releases/tag/v1.2)
[![Downloads](https://img.shields.io/github/downloads/NikhilKY64/Universal_Calculator_App/v1.4/total)](https://github.com/NikhilKY64/Universal_Calculator_App/releases/tag/v1.4)

# Universal Calculator App

Universal Calculator is a modern Android calculator application built with **Kotlin** and **Jetpack Compose**.
It combines multiple calculator tools in one app, including standard calculations, scientific operations, unit conversions, graphing, and date calculations.

The goal of the project is to provide a **fast, lightweight, and easy-to-use calculator** with multiple advanced utilities in a single interface.

---

## Features

### Standard Calculator

* Basic arithmetic operations
* Clean button layout
* Fast expression evaluation

### Scientific Calculator

* Trigonometric functions
* Logarithmic calculations
* Advanced mathematical operations

### Unit Converter

Supports multiple types of conversions such as:

* Length
* Weight
* Temperature
* Other common units

### Graphing Calculator

* Plot mathematical functions
* Visual representation of equations

### Date Calculator

* Calculate difference between dates
* Perform date-based calculations

### Calculation History

* Stores previous calculations
* Easy access to past results

### Settings

* App preferences management
* Saved settings using DataStore

### Ad Integration

* Banner ads
* Native ads support

---

## Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose
* **Architecture:** MVVM (Model–View–ViewModel)
* **Navigation:** Jetpack Navigation
* **Storage:** DataStore Preferences
* **Ads:** Google AdMob

---

## Project Structure

```
app/
 ├── core/
 │   ├── converter/
 │   │   └── ConversionLogic.kt
 │   └── math/
 │       └── ExpressionEvaluator.kt
 │
 ├── data/
 │   ├── datastore/
 │   │   └── PreferencesManager.kt
 │   └── history/
 │       └── HistoryRepository.kt
 │
 ├── navigation/
 │   ├── NavGraph.kt
 │   └── Screen.kt
 │
 ├── ui/
 │   ├── calculator/
 │   │   ├── StandardCalculatorScreen.kt
 │   │   ├── ScientificCalculatorScreen.kt
 │   │   └── DateCalculationScreen.kt
 │   │
 │   ├── converter/
 │   │   └── ConverterScreen.kt
 │   │
 │   ├── graphing/
 │   │   ├── GraphView.kt
 │   │   └── GraphingCalculatorScreen.kt
 │   │
 │   ├── ads/
 │   │   ├── AdConfig.kt        ← Centralized AdMob IDs
 │   │   ├── AdBanner.kt
 │   │   └── NativeAdBanner.kt
 │   │
 │   ├── components/
 │   │   └── DrawerContent.kt
 │   │
 │   ├── settings/
 │   │   └── SettingsScreen.kt
 │   │
 │   └── theme/
 │       ├── Color.kt
 │       ├── Theme.kt
 │       └── Type.kt
 │
 └── viewmodel/
     ├── CalculatorViewModel.kt
     ├── ConverterViewModel.kt
     └── GraphingViewModel.kt
```

---

## Installation

1. Clone the repository

```bash
git clone https://github.com/yourusername/Universal_Calculator_App.git
```

2. Open the project in **Android Studio**

3. Sync Gradle

4. Run the app on:

* Android Emulator
* Physical Android device

---

## AdMob Configuration

All AdMob ad unit IDs are centralized in a single file so you never have to hunt through multiple source files.

### Step 1 — Locate `AdConfig.kt`

```
app/src/main/java/com/universalcalculator/ui/ads/AdConfig.kt
```

### Step 2 — Replace the ad unit IDs

Open `AdConfig.kt` and replace the placeholder values with your real AdMob IDs:

```kotlin
object AdConfig {

    // Replace with your real Banner Ad Unit ID from AdMob dashboard
    const val BANNER_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"

    // Replace with your real Native Ad Unit ID from AdMob dashboard
    const val NATIVE_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
}
```

### Step 3 — Set your AdMob App ID in the Manifest

The App ID must be set separately in `AndroidManifest.xml` (Google requires it there at runtime).
Open `app/src/main/AndroidManifest.xml` and replace the value:

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"/> <!-- Your AdMob App ID -->
```

> **Where to find your IDs?**
> Log in to [AdMob](https://admob.google.com) → Apps → your app → **App settings** for the App ID,
> and **Ad units** for the Banner / Native ad unit IDs.

> **Using test ads during development?**
> Use Google's official test IDs so you don't risk policy violations:
> - **App ID:** `ca-app-pub-3940256099942544~3347511713`
> - **Banner:** `ca-app-pub-3940256099942544/6300978111`
> - **Native:** `ca-app-pub-3940256099942544/2247696110`
> But for real ads you have to use Real ad unit IDs

---

## Requirements

* Android Studio (latest recommended)
* Android SDK
* Minimum Android version supported by the project
* Internet connection for AdMob ads

---


## Future Improvements

* More unit conversion categories
* Better graph customization
* Dark / light theme toggle
* Improved calculation history UI


## Author

Developed by **Nikhil**

If you like this project, consider giving it a ⭐ on GitHub.
