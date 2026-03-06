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
- **Quick Actions**: Complete brew or extend fermentation by 1 day directly from notification

**Platform Implementation:**
- **Android**: `NotificationReceiver.kt` using AlarmManager, `NotificationActionReceiver.kt` for quick actions
- **iOS**: `ContentView.swift` using UNUserNotificationCenter with UNNotificationAction
- Notification permissions requested on first launch
- Notifications reschedule on brew state changes
- Quick action buttons: "Complete" and "Extend 1 Day"

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

### 5. Home Screen Widgets
**Status**: ✅ Implemented (March 2026)

**Description:**
View active brews at a glance from your home screen without opening the app. Widgets display brew progress, days remaining, and fermentation status.

**Capabilities:**

**Widget Sizes:**
- **Small**: Shows most urgent brew with basic info
- **Medium**: Shows 2-3 brews with full details
- **Large**: Shows up to 4 brews with all information

**Display Information:**
- Brew name and optional flavor
- Fermentation stage (F1/F2) with visual icons
- Progress bars (orange for F1, green for F2)
- Days remaining/overdue with color coding:
  - Red: Overdue
  - Green: Ready today
  - Black: In progress
- Empty state when no brews active

**Updates:**
- Automatic update when brews change in app
- Daily refresh at midnight
- Platform-appropriate update intervals
- Tap widget to open app

**Platform-Specific:**

**Android (Jetpack Glance):**
- Responsive sizing (adapts to widget dimensions)
- Material 3 design matching app
- Updates via GlanceAppWidget
- No configuration needed

**iOS (WidgetKit):**
- Small, Medium, Large variants
- Matches iOS design guidelines
- Timeline-based updates
- App Groups for data sharing
- Requires Xcode widget extension setup

**Implementation:**
- **Common**: `WidgetUpdater.kt` - Platform-agnostic update trigger
- **Common**: `SettingsFactory.kt` - Shared storage configuration
- **Android**: `widget/BrewWidget.kt` - Glance widget UI
- **Android**: `widget/BrewWidgetDataProvider.kt` - Data access layer
- **Android**: `widget/BrewWidgetUpdater.kt` - Update coordinator
- **iOS**: `BrewWidget/BrewWidget.swift` - WidgetKit implementation
- **iOS**: `BrewWidget/BrewDataProvider.swift` - iOS data provider
- **iOS**: App Groups for data sharing between app and widget

**Setup:**
- Android: Add widget from home screen long-press menu
- iOS: Requires Xcode configuration (see `docs/WIDGET_SETUP.md`)

**Design Philosophy:**
- No new data, only displays existing brew information
- Maintains app's minimalist aesthetic
- Perfect for "at-a-glance" use case
- No configuration or settings needed
- Tap to open app for full interaction

---

## UI/UX Features

### 6. Visual State Indicators
**Status**: ✅ Implemented (March 2026)

**Description:**
Clear visual feedback showing the current state of each brew with color-coded indicators.

**Capabilities:**
- **Ready State** (0 days remaining):
  - Displays "✓ Ready!" message
  - Green text color (#2E7D32)
  - Indicates brew is ready for next step
- **Overdue State** (< 0 days):
  - Shows "X days overdue" message
  - Red text color
  - Alerts user to delayed fermentation
- **In Progress State** (> 0 days):
  - Shows "X days left" message
  - Black text color
  - Normal brewing progress

**Implementation:**
- `viewmodel/BrewsViewModel.kt`: Logic for determining state and colors
- `composeResources/values/strings.xml`: "brew_ready" string resource
- Consistent across main app and widgets

**Why it fits:**
- Improves visual communication without complexity
- Makes it obvious when action is needed
- Delightful user experience

---

### 7. Empty State Design
**Status**: ✅ Implemented (March 2026)

**Description:**
Friendly, welcoming message when the brew list is empty, encouraging users to start their first brew.

**Capabilities:**
- Large jar emoji (🫙) for visual appeal
- Clear "No active brews" heading
- Helpful call-to-action: "Tap 'Add' to start your first kombucha!"
- Centered layout with proper spacing
- Multilingual support (English, Dutch, German)

**Implementation:**
- `ui/view/BrewsView.kt`: Empty state UI component
- `composeResources/values/strings.xml`: Localized strings
  - `empty_state_emoji`: 🫙
  - `empty_state_title`: "No active brews"
  - `empty_state_message`: "Tap 'Add' to start your first kombucha!"

**Why it fits:**
- Better onboarding for new users
- Reduces confusion on first launch
- Maintains minimalist aesthetic
- Encourages engagement

---

### 8. Material Design 3
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

### 9. Multiplatform Support
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

### 8. Localization
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

### 9. Local Storage
**Description:**
All data stored locally on device, no cloud dependency.

**Capabilities:**
- Brews persist across app restarts
- Settings saved automatically
- Saved flavors list persistence
- No internet connection required
- No user accounts needed
- **Widget data sharing** (iOS App Groups, Android SharedPreferences)

**Implementation:**
- Android: `SharedPreferences`
- iOS: `UserDefaults` with App Groups
- Abstraction: `multiplatform-settings` library
- JSON serialization for complex objects
- `SettingsFactory.kt`: Platform-specific storage configuration

---

### 10. State Management
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

### 11. Dependency Injection
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

### 12. Type Safety
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
