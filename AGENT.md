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
- Track completed brews in history with statistics

### What KombuTime Explicitly Does NOT Do:
- Complex analytics or detailed charts (minimal stats only)
- Ingredient tracking or recipe management
- Taste notes, ratings, or reviews (beyond history saves basic data)
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
        /domain       # Data models (Brew, BrewState, BrewSettings, HistoricalBrew)
        /model        # Business logic (Model.kt, HistoryRepository.kt)
        /viewmodel    # UI logic (BrewsViewModel, SettingsViewModel, AppViewModel, HistoryViewModel)
        /ui           # Compose UI
          /view       # Screens (BrewsView, SettingsView, HistoryView, AppSettingsView)
          /theme      # Colors, Theme
        /utils        # Utilities (DateFormatting, UiText)
    /androidMain      # Android-specific code
    /iosMain          # iOS-specific code
    /appleMain        # Shared iOS/macOS code

/iosApp               # iOS app wrapper
```

### Key Components

#### Domain Models (`domain/Brew.kt`, `domain/HistoricalBrew.kt`)
- `Brew`: Represents a brewing batch with start date, settings, and state
- `BrewState`: Sealed class - FirstFermentation or SecondFermentation(flavor)
- `BrewSettings`: Configuration (firstFermentationDays, secondFermentationDays, name)
- `HistoricalBrew`: Completed brew data (dates, fermentation days, tea type, flavor)

#### Model (`model/Model.kt`, `model/HistoryRepository.kt`)
- `Model`: Central data repository for active brews
- Manages brew list state
- Handles persistence via Settings
- Schedules notifications
- Business logic for brew lifecycle
- Automatically saves to history on F2 completion
- `HistoryRepository`: Manages historical brew data
- JSON storage for completed brews
- Statistics calculation
- Export functionality (CSV/JSON)
- Clear history operation

#### ViewModels
- `BrewsViewModel`: Main screen logic, calculates progress/remaining days
- `SettingsViewModel`: Per-brew settings management
- `AppViewModel`: App-level coordination
- `HistoryViewModel`: History screen state, statistics formatting

#### UI Views
- `BrewsView`: Main screen showing all active brews
- `SettingsView`: Edit individual brew settings (days, start date, notification time)
- `HistoryView`: Completed brews list with statistics header
- `AppSettingsView`: App-wide settings including history management

#### Navigation
- Bottom tab navigation (Active/History tabs) integrated in `App.kt`
- Material 3 NavigationBar for tab switching
- Maintains backward compatibility with existing settings navigation

## Data Flow

2. **User Action** (e.g., "Add Brew") → ViewModel
3. **ViewModel** → Model (business logic)
4. **Model** updates StateFlow → saves to Settings
5. On F2 completion → Model saves to HistoryRepository
6. **StateFlow** change → ViewModel recomputes UI state
7. **UI State** → Compose recomposes view
8. **Model** schedules platform notifications via callback

## History System

### Data Storage
- Uses JSON serialization via `kotlinx.serialization`
- Stored in platform Settings (SharedPreferences/UserDefaults)
- Key: `"history"` contains array of `HistoricalBrew` objects
- Lightweight: ~10-15 KB per 100 brews
- Automatic save on F2 completion (if enabled)

### History Capture
When user completes a brew in second fermentation:
1. `Model.complete(index)` detects F2 brew
2. Calls `HistoryRepository.saveCompletedBrew(brew)`
3. Repository creates `HistoricalBrew` with:
   - Original F1 start date (calculated backwards)
   - Bottled date (when F1 completed = F2 start date)
   - Completed date (current date)
   - Actual fermentation days used
   - Tea type and flavor (if specified)
4. Saves to storage and updates StateFlow

### Statistics Calculation
- Total brews: Simple count
- First brew date: Minimum startDate in history
- Most used flavor: Group by flavor, find max count
- Average F1/F2 days: Simple average of all brews

### Export
- **CSV**: Headers + comma-separated values, platform share sheet
- **JSON**: Pretty-printed array of HistoricalBrew objects
- Export buttons only visible when history exists

### UI Navigation
- Bottom tabs show Active (brewing) vs History (completed)
- Tab state managed in `App.kt` composable
- Settings/AppSettings accessible from top bar on both tabs
- History settings in AppSettingsView

## Notification System

- Notifications scheduled for end of each fermentation stage
- Platform-specific implementation:
  - **Android**: AlarmManager with PendingIntent, NotificationActionReceiver for quick actions
  - **iOS**: UNUserNotificationCenter with UNNotificationAction
- Configurable notification time (default: 9:00 AM)
- All notifications rescheduled on app state changes
- Quick actions: "Complete" and "Extend 1 Day" buttons on notifications
- Actions handled outside app lifecycle using Koin for Model access

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

### Build Verification
**CRITICAL**: When making ANY code changes, ALWAYS verify the build passes:
1. **MANDATORY**: Use `mcp_xcode_BuildProject` tool after every code change
2. Check for errors before committing
3. Never assume changes work without testing both platforms
4. iOS Kotlin/Native has different APIs than JVM - verify platform-specific code compiles
5. Build verification is NOT optional - it must be done for every modification

**AI Agent Rule**: After making code changes, you MUST call `mcp_xcode_BuildProject` before considering the task complete.

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

### Feature Considerations
When evaluating new features, consider:
- **Does it maintain core simplicity?** The main flow (add brew → track → complete) must remain dead simple
- **Is it optional?** Advanced features should be opt-in, not forced on all users
- **Does it add cognitive load?** Avoid cluttering the main interface
- **Can it be implemented without compromising performance or reliability?**

**History Feature Rationale:**
The history feature was implemented because:
✅ Completely optional (can be disabled via toggle)
✅ Zero cognitive load for basic users (hidden behind separate tab)
✅ High user value for engaged brewers (understand patterns, export data)
✅ Minimal storage footprint (JSON, ~10-15 KB per 100 brews)
✅ No impact on core workflow (automatic, non-intrusive capture)
✅ Maintains philosophy: track fermentation simply, now with context over time

Features that were previously considered unacceptable may be acceptable if:
✅ They're optional/hidden from the default experience
✅ They don't slow down or complicate the core workflow
✅ They're clearly separated from essential functionality
✅ They add value without requiring ongoing maintenance complexity

Examples of potentially acceptable advanced features:
- Historical analysis (if opt-in, doesn't require complex UI in main flow)
- Social features (if completely optional, doesn't require account creation for core app)
- Recipe management (if it's a separate optional section)
- Cloud sync (if it's transparent and works without user configuration)

Still generally avoid:
❌ Features that make the core app harder to use
❌ Mandatory account creation or login
❌ Complex multi-step workflows for basic tasks
❌ Features that significantly increase maintenance burden

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
