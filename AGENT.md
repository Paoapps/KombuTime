# AI Agent Context for KombuTime

## Project Overview

KombuTime is a minimalist Kotlin Multiplatform kombucha brewing tracker for iOS and Android. The app focuses on simplicity and essential features only.

## Core Philosophy

**"Do one thing well"** - Track kombucha fermentation stages and send timely notifications. No feature bloat.

### What KombuTime Does:
- Track multiple kombucha brews simultaneously
- Manage first fermentation (F1) and second fermentation (F2) stages
- Send notifications when fermentation stages complete
- Allow customization of fermentation duration
- Provide at-a-glance status of all active brews

### What KombuTime Explicitly Does NOT Do:
- Detailed history/logs of past brews
- Ingredient tracking or recipe management
- Taste notes, ratings, or reviews
- Social features or sharing
- Photo uploads
- Complex analytics or graphs
- Brew quality tracking

## Architecture

### Technology Stack
- **Framework**: Kotlin Multiplatform (KMP)
- **UI**: Compose Multiplatform
- **Platforms**: Android & iOS
- **State Management**: Kotlin Flow + ViewModel
- **Dependency Injection**: Koin
- **Serialization**: kotlinx.serialization
- **Storage**: multiplatform-settings (SharedPreferences/UserDefaults)
- **Notifications**: Platform-specific (AlarmManager/UNUserNotificationCenter)

### Project Structure
```
/composeApp
  /src
    /commonMain       # Shared KMP code
      /kotlin
        /domain       # Data models (Brew, BrewState, BrewSettings)
        /model        # Business logic (Model.kt)
        /viewmodel    # UI logic (BrewsViewModel, SettingsViewModel, AppViewModel)
        /ui           # Compose UI
          /view       # Screens (BrewsView, SettingsView)
          /theme      # Colors, Theme
        /utils        # Utilities (DateFormatting, UiText)
    /androidMain      # Android-specific code
    /iosMain          # iOS-specific code
    /appleMain        # Shared iOS/macOS code

/iosApp               # iOS app wrapper
```

### Key Components

#### Domain Models (`domain/Brew.kt`)
- `Brew`: Represents a brewing batch with start date, settings, and state
- `BrewState`: Sealed class - FirstFermentation or SecondFermentation(flavor)
- `BrewSettings`: Configuration (firstFermentationDays, secondFermentationDays, name)

#### Model (`model/Model.kt`)
- Central data repository
- Manages brew list state
- Handles persistence via Settings
- Schedules notifications
- Business logic for brew lifecycle

#### ViewModels
- `BrewsViewModel`: Main screen logic, calculates progress/remaining days
- `SettingsViewModel`: Per-brew settings management
- `AppViewModel`: App-level coordination

#### UI Views
- `BrewsView`: Main screen showing all active brews
- `SettingsView`: Edit individual brew settings (days, start date, notification time)

## Data Flow

1. **User Action** (e.g., "Add Brew") → ViewModel
2. **ViewModel** → Model (business logic)
3. **Model** updates StateFlow → saves to Settings
4. **StateFlow** change → ViewModel recomputes UI state
5. **UI State** → Compose recomposes view
6. **Model** schedules platform notifications via callback

## Notification System

- Notifications scheduled for end of each fermentation stage
- Platform-specific implementation:
  - **Android**: AlarmManager with PendingIntent
  - **iOS**: UNUserNotificationCenter
- Configurable notification time (default: 9:00 AM)
- All notifications rescheduled on app state changes

## Coding Conventions

### File Organization
- One class per file
- Files named after their primary class
- Group related functionality in packages

### Naming
- ViewModels: `<Feature>ViewModel`
- Views: `<Feature>View`
- Models: Descriptive nouns (Brew, BrewState)

### State Management
- Use `StateFlow` for observable state
- `combine()` flows for computed state
- `stateIn()` with WhileSubscribed for UI state

### UI Components
- Composables named as nouns (BrewsView, Stepper)
- Pass ViewModels only to top-level screens
- Use `Output` data classes for ViewModel → View communication

## Feature Development Guidelines

### Before Adding a Feature
1. **Does it align with the core mission?** (Track fermentation, send notifications)
2. **Is it simple?** (Can it be explained in one sentence?)
3. **Does it avoid feature creep?** (No history, notes, ratings, etc.)
4. **Will it be used frequently?** (Not edge cases)

### Acceptable Feature Types
✅ Better UX for existing features (swipe gestures, widgets)
✅ Visual polish (animations, empty states)
✅ Smart defaults (remember settings, presets)
✅ Notification improvements (quick actions, reminders)
✅ Accessibility improvements

### Unacceptable Feature Types
❌ Anything requiring a database
❌ Cloud sync or accounts
❌ Social features
❌ Complex data entry
❌ Historical analysis
❌ Recipe management

## Testing Strategy

### Current State
- Manual testing on iOS Simulator and Android Emulator
- Real device testing before releases

### Future Considerations
- Unit tests for Model business logic
- UI tests for critical flows (add brew, complete fermentation)
- Snapshot tests for UI components

## Build & Deployment

### Android
- Build: `./gradlew :composeApp:assembleRelease`
- Bundle: `./gradlew :composeApp:bundleRelease`
- Output: `composeApp/release/composeApp-release.aab`

### iOS
- Build framework: `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`
- Open Xcode project: `iosApp/iosApp.xcodeproj`
- Archive & upload via Xcode

### Version Management
- Version name: `1.0.2` (in `composeApp/build.gradle.kts`)
- Version code: `4` (Android)
- Build number: `5` (iOS, in `Info.plist`)

## Common Tasks

### Adding a New Brew Property
1. Update `Brew` or `BrewSettings` in `domain/Brew.kt`
2. Update serialization (automatic with @Serializable)
3. Add UI in `SettingsView.kt`
4. Add logic in `Model.kt`
5. Update `SettingsViewModel` to expose new property

### Adding a String Resource
1. Add to `composeApp/src/commonMain/composeResources/values/strings.xml`
2. Add translations in `values-nl/strings.xml`, `values-de/strings.xml`
3. Access via `stringResource(Res.string.your_key)`

### Modifying Notification Logic
1. Update `Model.save()` notification scheduling
2. Test on both platforms (behavior differs!)
3. Verify notification permissions are requested

## Known Limitations

- No offline/online sync (by design)
- No backup (user responsibility to not uninstall)
- Maximum practical brews: ~10-20 (UI becomes crowded)
- Notification accuracy: ~15 min on Android due to doze mode
- No brew history beyond active brews

## Future Enhancements (Aligned with Philosophy)

See `docs/FEATURE_IDEAS.md` for curated list of potential improvements that maintain the minimalist approach.

## Resources

- [Kotlin Multiplatform Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/)
- [Compose Multiplatform Docs](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Material 3 Guidelines](https://m3.material.io/)

## Contact & Contribution

- **Author**: Lammert Westerhoff
- **Repo**: github.com/Paoapps/KombuTime
- **Philosophy**: Keep it simple, keep it focused
