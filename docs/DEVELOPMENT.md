# KombuTime Development Guide

## Getting Started

### Prerequisites

- **JDK 17+** (Java Development Kit)
- **Android Studio** (latest stable version)
- **Xcode 15+** (for iOS development, macOS only)
- **Kotlin** 1.9+ (bundled with Android Studio)

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Paoapps/KombuTime.git
   cd KombuTime
   ```

2. **Open in Android Studio**
   - File → Open → Select KombuTime folder
   - Wait for Gradle sync to complete

3. **iOS Setup** (macOS only)
   - Ensure Xcode command line tools are installed:
     ```bash
     xcode-select --install
     ```
   - Open `iosApp/iosApp.xcodeproj` in Xcode

## Project Structure

```
KombuTime/
├── composeApp/                 # Shared Kotlin Multiplatform code
│   ├── build.gradle.kts        # Module build configuration
│   └── src/
│       ├── commonMain/         # Platform-agnostic code
│       │   ├── kotlin/
│       │   │   └── com/paoapps/kombutime/
│       │   │       ├── App.kt              # Main app entry point
│       │   │       ├── domain/             # Data models
│       │   │       │   └── Brew.kt
│       │   │       ├── model/              # Business logic
│       │   │       │   └── Model.kt
│       │   │       ├── ui/                 # UI components
│       │   │       │   ├── view/
│       │   │       │   │   ├── BrewsView.kt
│       │   │       │   │   ├── SettingsView.kt
│       │   │       │   │   └── AppSettingsView.kt  # Flavor management UI
│       │   │       │   └── theme/
│       │   │       │       ├── Color.kt
│       │   │       │       └── Theme.kt
│       │   │       ├── utils/              # Helper functions
│       │   │       └── viewmodel/          # UI logic
│       │   │           ├── BrewsViewModel.kt
│       │   │           ├── SettingsViewModel.kt
│       │   │           └── AppSettingsViewModel.kt  # Flavor management logic
│       │   └── composeResources/           # Resources
│       │       ├── drawable/
│       │       └── values/
│       │           ├── strings.xml
│       │           ├── values-de/          # German
│       │           └── values-nl/          # Dutch
│       ├── androidMain/        # Android-specific code
│       │   ├── AndroidManifest.xml
│       │   └── kotlin/
│       │       └── com/paoapps/kombutime/
│       │           ├── MainActivity.kt
│       │           └── NotificationReceiver.kt
│       ├── iosMain/            # iOS-specific code
│       └── appleMain/          # Shared Apple platform code
├── iosApp/                     # iOS app wrapper
│   ├── iosApp/
│   │   ├── ContentView.swift
│   │   ├── iOSApp.swift
│   │   └── Info.plist
│   └── iosApp.xcodeproj/
├── gradle/                     # Gradle configuration
│   └── libs.versions.toml      # Dependency versions
├── build.gradle.kts            # Root build configuration
└── settings.gradle.kts
```

## Key Features & Components

### Flavor Management System

The app includes a complete flavor tracking system for second fermentation:

**Components:**
- **BrewsView.kt**: Flavor input dialog with ExposedDropdownMenuBox
  - "No flavor" option (default selection)
  - Pre-populated flavor picker (Blueberry, Ginger, etc.)
  - Custom flavor input field
  - Auto-saves custom flavors to saved list

- **AppSettingsView.kt**: Dedicated settings screen
  - Toggle to enable/disable flavor prompt
  - Add/edit/delete saved flavors
  - Material 3 UI with FAB for adding flavors

- **AppSettingsViewModel.kt**: Manages flavor CRUD operations
  - `editingFlavor` state for add/edit dialog
  - `saveFlavor()`, `deleteFlavor()` methods
  - Integrates with Model's saved flavors

- **Model.kt**: Flavor persistence
  - `savedFlavors` StateFlow with 8 default flavors
  - `promptForFlavor` StateFlow (toggle in settings)
  - Uses `multiplatform-settings` for storage
  - `addSavedFlavor()`, `updateSavedFlavor()`, `deleteSavedFlavor()`

**User Flow:**
1. User completes first fermentation
2. If `promptForFlavor` is enabled, dialog appears
3. User selects from saved flavors, enters custom, or keeps "No flavor"
4. Custom flavors are automatically added to saved list
5. Flavor appears in brew title during F2 (e.g., "My Brew - Strawberry")
6. Empty flavor shows just brew name (no "Unflavored" suffix)

**Localization:**
All flavor strings are localized in EN, NL, and DE:
- `flavor_dialog_title`, `flavor_dialog_message`
- `flavor_dialog_no_flavor`, `flavor_dialog_custom`
- `app_settings`, `saved_flavors`, `add_flavor`, etc.

### Notification System

**Android** (`NotificationReceiver.kt`):
- Uses `AlarmManager` for precise timing
- Creates notification channel on first run
- Notification includes brew name and completion message

**iOS** (`ContentView.swift`):
- Uses `UNUserNotificationCenter`
- Requests permission on app launch
- Schedules local notifications with identifier

### Settings Persistence

Uses `multiplatform-settings` library:
- **Android**: Wraps `SharedPreferences`
- **iOS**: Wraps `UserDefaults`
- Stores: Brews list (JSON), saved flavors, prompt toggle

## Running the App

### Android

**Using Android Studio:**
1. Select "composeApp" configuration
2. Choose a device/emulator
3. Click Run (▶️)

**Using Gradle:**
```bash
# Debug build
./gradlew :composeApp:assembleDebug

# Install on connected device
./gradlew :composeApp:installDebug
```

### iOS

**Using Xcode:**
1. Open `iosApp/iosApp.xcodeproj`
2. Select a simulator or device
3. Click Run (▶️)

**Using Gradle (build framework only):**
```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

## Building for Release

### Android

1. **Create Release AAB** (for Play Store)
   ```bash
   ./gradlew :composeApp:bundleRelease
   ```
   Output: `composeApp/release/composeApp-release.aab`

2. **Create Release APK** (for direct installation)
   ```bash
   ./gradlew :composeApp:assembleRelease
   ```

### iOS

1. **Build framework**
   ```bash
   ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
   ```

2. **Archive in Xcode**
   - Product → Archive
   - Distribute App → App Store Connect

## Development Workflow

### Adding a New Feature

1. **Define the data model** (if needed)
   - Update `domain/Brew.kt` or create new model
   - Ensure `@Serializable` annotation for persistence

2. **Implement business logic**
   - Add methods to `Model.kt`
   - Update state flows

3. **Create ViewModel logic**
   - Add/update ViewModel in `viewmodel/`
   - Define `Output` data class for UI state
   - Map domain models to UI-friendly state

4. **Build UI**
   - Create/update Composable in `ui/view/`
   - Use ViewModel's output state
   - Keep UI pure (no business logic)

5. **Add string resources**
   - Add to `composeResources/values/strings.xml`
   - Translate in `values-nl/` and `values-de/`

6. **Test on both platforms**
   - Run on Android device/emulator
   - Run on iOS simulator/device
   - Test notifications specifically

### Example: Adding a New Stepper Setting

```kotlin
// 1. Update BrewSettings in domain/Brew.kt
@Serializable
data class BrewSettings(
    val firstFermentationDays: Int = 12,
    val secondFermentationDays: Int = 3,
    val newSetting: Int = 5,  // NEW
    val name: String
)

// 2. Add methods to Model.kt
fun incrementNewSetting(brewIndex: Int) {
    val brew = _brews.value[brewIndex]
    _brews.value = _brews.value.map {
        if (it.settings.name == brew.settings.name) {
            it.copy(settings = it.settings.copy(
                newSetting = it.settings.newSetting + 1
            ))
        } else {
            it
        }
    }
    save()
}

// 3. Update SettingsViewModel.kt Output
brewSettingsSteppers = listOf(
    // ... existing steppers ...
    Output.Stepper(
        label = Res.string.new_setting.toUiText(),
        value = brew.settings.newSetting.toString(),
        onIncrement = { model.incrementNewSetting(brewIndex) },
        onDecrement = { model.decrementNewSetting(brewIndex) }
    )
)

// 4. Add string resource
// In composeResources/values/strings.xml:
// <string name="new_setting">New Setting</string>
```

## Code Style

### Kotlin Conventions

- **Indentation**: 4 spaces
- **Line length**: 120 characters max
- **File naming**: UpperCamelCase matching class name
- **Package naming**: lowercase, no underscores

### Composable Conventions

```kotlin
// ✅ Good
@Composable
fun BrewCard(
    brew: BrewViewModel.Output.Brew,
    modifier: Modifier = Modifier
) {
    // Implementation
}

// ❌ Avoid
@Composable
fun brewCard(brew: BrewViewModel.Output.Brew) { } // Wrong naming
```

### State Management

```kotlin
// ✅ Good - StateFlow in ViewModel
class BrewsViewModel : ViewModel() {
    private val _output = combine(sources...) { ... }
    val output: StateFlow<Output> = _output.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Output()
    )
}

// ❌ Avoid - Mutable state in Composable
@Composable
fun MyView() {
    var state by remember { mutableStateOf(0) } // Prefer ViewModel
}
```

## Common Tasks

### Adding a String Resource

1. **English** (`composeResources/values/strings.xml`):
   ```xml
   <string name="my_new_string">Hello World</string>
   ```

2. **Dutch** (`composeResources/values-nl/strings.xml`):
   ```xml
   <string name="my_new_string">Hallo Wereld</string>
   ```

3. **German** (`composeResources/values-de/strings.xml`):
   ```xml
   <string name="my_new_string">Hallo Welt</string>
   ```

4. **Usage**:
   ```kotlin
   Text(text = stringResource(Res.string.my_new_string))
   ```

### Adding a Drawable Resource

1. Place image in `composeResources/drawable/`
   - Use vector drawables (XML) when possible
   - PNG/JPG supported but increase app size

2. **Usage**:
   ```kotlin
   Image(
       painter = painterResource(Res.drawable.my_image),
       contentDescription = "Description"
   )
   ```

### Updating Dependencies

Edit `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "1.9.22"          # Update version
compose = "1.6.0"

[libraries]
androidx-lifecycle = { module = "...", version.ref = "lifecycle" }
```

Then sync Gradle.

## Debugging

### Android

**Logcat:**
```kotlin
println("Debug: $value")  // Simple logging
```

**Breakpoints:**
- Set in Android Studio gutter
- Run in Debug mode (🐛)

### iOS

**Console logs:**
```kotlin
println("Debug: $value")  // Appears in Xcode console
```

**Xcode Debugger:**
- Set breakpoints in Swift code
- Kotlin debugging limited

### Common Issues

**Build fails:**
```bash
# Clean and rebuild
./gradlew clean
./gradlew :composeApp:build
```

**iOS framework issues:**
```bash
# Clean Xcode derived data
rm -rf ~/Library/Developer/Xcode/DerivedData/iosApp-*

# Rebuild framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

**Gradle issues:**
```bash
# Stop all Gradle daemons
./gradlew --stop

# Clear Gradle cache
rm -rf ~/.gradle/caches/
```

## Testing

### Manual Testing Checklist

- [ ] Add a brew
- [ ] Complete first fermentation
- [ ] Complete second fermentation
- [ ] Delete a brew
- [ ] Adjust fermentation days
- [ ] Adjust start date
- [ ] Change notification time
- [ ] Verify notifications appear
- [ ] Test on different screen sizes
- [ ] Test in light/dark mode
- [ ] Test with multiple brews (5+)

### Platform-Specific Testing

**Android:**
- Test on different API levels (min SDK 26+)
- Test with Doze mode
- Verify notification channels

**iOS:**
- Test on different screen sizes (SE, Pro Max)
- Verify notification permissions
- Test with background app refresh

## Version Management

### Updating Version

1. **Android** (`composeApp/build.gradle.kts`):
   ```kotlin
   defaultConfig {
       versionCode = 5      // Increment for each release
       versionName = "1.0.3" // User-facing version
   }
   ```

2. **iOS** (`iosApp/iosApp/Info.plist`):
   ```xml
   <key>CFBundleShortVersionString</key>
   <string>1.0.3</string>
   <key>CFBundleVersion</key>
   <string>6</string>
   ```

### Version Scheme

- **Major.Minor.Patch** (e.g., 1.0.3)
- **Major**: Breaking changes or major features
- **Minor**: New features, backwards compatible
- **Patch**: Bug fixes

## Resources

- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Flow Guide](https://kotlinlang.org/docs/flow.html)

## Getting Help

- Check existing issues on GitHub
- Review AGENT.md for project philosophy
- Consult FEATURE_IDEAS.md for approved enhancement directions
