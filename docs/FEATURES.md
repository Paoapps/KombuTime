# KombuTime Features

This document describes all implemented features in KombuTime.

## Core Features

### 1. Brew Tracking
**Description:**
Track multiple kombucha brews through both fermentation stages with clear visual indicators.

**Capabilities:**
- Create new brews with custom names
- Track first fermentation (default: 12 days)
- Track second fermentation (default: 3 days)
- Visual progress bars showing completion percentage
- Days remaining/overdue display
- Automatic state transitions

**Implementation:**
- `domain/Brew.kt`: Data models for brew state and settings
- `Model.kt`: Brew lifecycle management
- `BrewsView.kt`: Main UI with brew cards and progress indicators
- `BrewsViewModel.kt`: State management and UI logic

---

### 2. Notifications
**Description:**
Receive timely notifications when fermentation stages complete.

**Capabilities:**
- Notification when first fermentation completes (time to bottle)
- Notification when second fermentation completes (ready to drink)
- Custom notification time (default: 9:00 AM)
- Platform-native notification support

**Platform Implementation:**
- **Android**: `NotificationReceiver.kt` using AlarmManager
- **iOS**: `ContentView.swift` using UNUserNotificationCenter
- Notification permissions requested on first launch
- Notifications reschedule on brew state changes

---

### 3. Customizable Settings
**Description:**
Configure fermentation duration and notification preferences per brew.

**Capabilities:**
- Adjust first fermentation days (1-30 days)
- Adjust second fermentation days (1-14 days)
- Set notification time (24-hour format)
- Rename brews
- Settings persist across app restarts

**Implementation:**
- `SettingsView.kt`: Settings UI with steppers
- `SettingsViewModel.kt`: Settings state management
- `Model.kt`: Persistence using multiplatform-settings

---

### 4. Flavor Management
**Status**: ✅ Implemented (March 2026)

**Description:**
Track flavors used in second fermentation with an intelligent picker system.

**Capabilities:**

**Flavor Input:**
- Dialog prompts when completing first fermentation
- Dropdown picker showing saved flavors
- "No flavor" option to skip (default selection)
- "Custom" option to enter new flavor names
- Flavor displays in brew title during F2 (e.g., "My Brew - Blueberry")
- Only shows flavor when specified (no "Unflavored" text)

**Pre-populated Flavors:**
- Blueberry
- Ginger
- Lemon
- Strawberry
- Mango
- Raspberry
- Peach
- Pineapple

**Flavor Management:**
- Dedicated App Settings screen
- Add new flavors with validation
- Edit existing flavor names
- Delete flavors from saved list
- All flavors persist across sessions
- Automatic alphabetical sorting

**Settings:**
- Toggle to enable/disable flavor prompt
- When disabled, Complete button bypasses flavor dialog
- Preference persists across app sessions

**Implementation:**
- `BrewsView.kt`: ExposedDropdownMenuBox for flavor selection
- `AppSettingsView.kt`: Flavor management UI with CRUD operations
- `AppSettingsViewModel.kt`: Flavor CRUD logic and dialog state
- `Model.kt`: Saved flavors storage with multiplatform-settings
- `BrewState.SecondFermentation(flavor: String)`: Domain model

**Localization:**
Fully localized in:
- 🇬🇧 English
- 🇳🇱 Dutch (Nederlands)
- 🇩🇪 German (Deutsch)

All flavor-related strings:
- `flavor_dialog_title`, `flavor_dialog_message`
- `flavor_dialog_no_flavor`, `flavor_dialog_custom`
- `flavor_dialog_placeholder`, `flavor_dialog_confirm`, `flavor_dialog_cancel`
- `app_settings`, `app_settings_title`
- `saved_flavors`, `add_flavor`, `edit_flavor`, `delete_flavor`
- `flavor_name`, `flavor_prompt_setting`, `flavor_prompt_description`

---

## UI/UX Features

### 5. Material Design 3
**Description:**
Modern, polished UI using Material Design 3 guidelines.

**Capabilities:**
- Dynamic color scheme
- Light and dark mode support
- Consistent typography and spacing
- Material components (Cards, Buttons, FABs, etc.)
- Platform-appropriate styling

**Implementation:**
- `ui/theme/Theme.kt`: Material 3 theme configuration
- `ui/theme/Color.kt`: Color definitions
- Compose Multiplatform Material 3 library

---

### 6. Multiplatform Support
**Description:**
Shared codebase with platform-specific optimizations.

**Capabilities:**
- 95%+ code sharing between Android and iOS
- Platform-specific UI where appropriate
- Native notification systems
- Platform-native file system access

**Architecture:**
- `commonMain/`: Shared business logic and UI
- `androidMain/`: Android-specific implementations
- `iosMain/`: iOS-specific implementations
- `appleMain/`: Shared Apple platform code

---

### 7. Localization
**Description:**
Multi-language support with complete translations.

**Supported Languages:**
- 🇬🇧 English (default)
- 🇳🇱 Dutch (Nederlands)
- 🇩🇪 German (Deutsch)

**Coverage:**
- All UI strings localized
- Date formatting respects locale
- Pluralization support (days remaining)
- Compose Resources system

**Implementation:**
- `composeResources/values/strings.xml`
- `composeResources/values-nl/strings.xml`
- `composeResources/values-de/strings.xml`

---

## Data & Persistence

### 8. Local Storage
**Description:**
All data stored locally on device, no cloud dependency.

**Capabilities:**
- Brews persist across app restarts
- Settings saved automatically
- Saved flavors list persistence
- No internet connection required
- No user accounts needed

**Implementation:**
- Android: `SharedPreferences`
- iOS: `UserDefaults`
- Abstraction: `multiplatform-settings` library
- JSON serialization for complex objects

---

### 9. State Management
**Description:**
Reactive state management using Kotlin Flow.

**Architecture:**
- `Model.kt`: Single source of truth
- `StateFlow` for reactive updates
- ViewModels expose UI state
- Automatic UI updates on state changes
- No manual refresh needed

**Pattern:**
```kotlin
Model (StateFlow) → ViewModel (Output) → View (Composable)
```

---

## Technical Features

### 10. Dependency Injection
**Description:**
Koin framework for clean dependency management.

**Capabilities:**
- ViewModels automatically inject Model
- Testable architecture
- Single instance of Model
- Clean separation of concerns

**Implementation:**
- `App.kt`: Koin initialization
- ViewModels implement `KoinComponent`
- `inject()` delegates for dependencies

---

### 11. Type Safety
**Description:**
Compile-time safety for resources and navigation.

**Capabilities:**
- Type-safe string resources
- Type-safe drawable resources
- Type-safe navigation routes
- Compile-time errors for missing resources

**Implementation:**
- Compose Resources plugin
- Sealed classes for navigation
- Kotlin serialization for data models

---

## Philosophy

All features in KombuTime follow these principles:

1. **Simplicity First**: If it complicates the core workflow, it doesn't belong
2. **No Feature Bloat**: Each feature must have clear value for 80%+ of users
3. **Local-First**: No cloud services, no accounts, no analytics
4. **Respect User Time**: Minimal taps to complete tasks
5. **Platform Native**: Feel at home on both Android and iOS

For future feature ideas, see `FEATURE_IDEAS.md`.

For development details, see `DEVELOPMENT.md`.

For project philosophy, see `AGENT.md`.
