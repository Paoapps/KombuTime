# Release Notes - What's New

## App Store Release Notes (Short Version - 4000 character limit)

### What's New in This Release

**🏠 Home Screen Widgets**
View your active brews at a glance without opening the app! Add widgets to your home screen to see fermentation progress, days remaining, and which brews need attention. Available in small, medium, and large sizes.

**📜 Brew History**
Track your kombucha journey! All completed brews are automatically saved to your history. View statistics including total brews, average fermentation times, and most-used flavors. Export your data as CSV or JSON for your own analysis.

**🍋 Flavor Tracking**
Add optional flavor notes when bottling for second fermentation. Choose from pre-populated favorites (Blueberry, Ginger, Lemon, Mango, and more) or add your own custom flavors. Manage your flavor list in the new App Settings.

**🍵 Tea Type Tracking**
Track which tea you use for first fermentation! Similar to flavor tracking, you can optionally specify your tea type (Black, Green, Oolong, etc.) when starting a new brew. Fully customizable with your own tea varieties.

**⚡️ Quick Actions from Notifications**
Handle your brews without opening the app! When you receive a notification that a fermentation stage is complete, you can now tap "Complete" to advance to the next stage or "Extend 1 Day" to give it more time—all from the notification itself.

**🎨 Visual Improvements**
- Enhanced dark mode support with better contrast and readability
- Clear visual indicators showing when brews are ready (✓ Ready!), in progress, or overdue
- Friendly empty state messages when you're just getting started
- Improved Material Design 3 styling throughout

**🌍 Full Localization**
Complete translations in English, Dutch (Nederlands), and German (Deutsch) for all new features.

**⚙️ Better Settings Organization**
Settings have been reorganized with dedicated screens for:
- App Settings (Flavors & Tea Types management)
- Brew Settings (Per-brew fermentation times and notification preferences)
- History Settings (Export, clear history, toggle saving)

**🔧 Technical Improvements**
- Modernized to latest Kotlin Multiplatform and Compose Multiplatform
- Migrated to Material Design 3
- Better performance and stability
- Improved notification system with action handling
- Enhanced data persistence and widget updates

---

## Detailed Release Notes (Internal/Full Version)

### Version: March 2026 Release
**Release Date:** March 7, 2026
**Base Commit:** b3b82c2e05e294de3d5055c0136236995a8e00cb (Sept 1, 2025)

---

## Major New Features

### 1. Home Screen Widgets 🏠
**Added:** March 4, 2026

Transform your home screen into a brewing dashboard with native widgets for both iOS and Android.

**Features:**
- **Multiple Sizes:** Small (1 brew), Medium (2-3 brews), Large (4+ brews)
- **Smart Display:** Shows most urgent brews first (sorted by days remaining)
- **Rich Information:** 
  - Brew name with flavor/tea type
  - Fermentation stage (F1/F2) with icons
  - Visual progress bars (orange for F1, green for F2)
  - Days remaining with color coding (red=overdue, green=ready, black=in progress)
- **Automatic Updates:** Widget refreshes when brews change in the app
- **Clean Design:** Matches app's minimalist aesthetic
- **Empty State:** Friendly message when no brews are active

**Platform Implementation:**
- **Android:** Jetpack Glance widgets with Material 3 design
- **iOS:** WidgetKit implementation with App Groups for data sharing
- **Tap to Open:** Tapping widget opens the full app

**Technical:**
- Common `WidgetUpdater.kt` interface for cross-platform updates
- Platform-specific data providers for optimal performance
- Shared storage configuration via `SettingsFactory.kt`

---

### 2. Brew History Tracking 📜
**Added:** March 3, 2026

Automatically track and analyze all your completed brews with comprehensive statistics.

**Features:**
- **Automatic Saving:** Completed brews automatically saved to history
- **Bottom Tab Navigation:** 
  - Active tab (🫙): Current brews in progress
  - History tab (📜): All completed brews
- **Detailed History Records:**
  - Brew name with flavor and tea type
  - Completion date (prominently displayed)
  - Start date
  - Full fermentation timeline: "F1: X days → F2: Y days"
- **Statistics Dashboard:**
  - Total brews completed
  - Date of first brew ("Since...")
  - Most-used flavor with frequency count
  - Average F1 fermentation duration
  - Average F2 fermentation duration
- **Export Capabilities:**
  - Export as CSV (spreadsheet-compatible)
  - Export as JSON (structured format)
  - Platform-native share sheet integration
- **Privacy Controls:**
  - Toggle to enable/disable history saving
  - Clear all history with confirmation dialog
  - Storage information display
- **Empty State:** Encouraging message for new users
- **Lightweight Storage:** ~10-15 KB per 100 brews (JSON-based)

**Implementation:**
- `domain/HistoricalBrew.kt`: Data model
- `model/HistoryRepository.kt`: CRUD operations and JSON serialization
- `viewmodel/HistoryViewModel.kt`: Statistics calculation
- `ui/view/HistoryView.kt`: UI with list and statistics

---

### 3. Flavor Management 🍋
**Added:** March 3, 2026

Track flavors used in second fermentation with smart picker system.

**Features:**
- **Flavor Dialog:** Prompts when completing first fermentation (can be disabled in settings)
- **Smart Picker:**
  - "No flavor" option to skip (default selection)
  - Pre-populated favorites: Blueberry, Ginger, Lemon, Strawberry, Mango, Raspberry, Peach, Pineapple
  - "Custom" option to enter new flavors
  - Automatically sorted alphabetically
- **Display:** Flavor shows in brew title during F2 (e.g., "Brew #1 - Blueberry")
- **Management:**
  - Add new flavors with validation
  - Edit existing flavor names
  - Delete unused flavors
  - All changes persist across sessions
- **Settings:**
  - Toggle to enable/disable flavor prompt
  - When disabled, Complete button bypasses flavor dialog

**Implementation:**
- `BrewsView.kt`: ExposedDropdownMenuBox for flavor selection
- `AppSettingsView.kt`: Flavor management UI
- `AppSettingsViewModel.kt`: CRUD logic
- `Model.kt`: Persistent storage
- `BrewState.SecondFermentation(flavor: String)`: Domain model

---

### 4. Tea Type Tracking 🍵
**Added:** March 4, 2026

Track which tea variety you use for first fermentation.

**Features:**
- **Tea Type Dialog:** Optional prompt when adding new brews
- **Smart Picker:**
  - "No tea type" option to skip (default)
  - Pre-populated types: Black, Green, Oolong, White, Pu-erh
  - "Custom" option for specialty teas
  - Alphabetically sorted
- **Display:** Tea type shows in brew title during F1 (e.g., "Brew #2 - Green Tea")
- **Management:**
  - Dedicated Tea Type Management screen
  - Add, edit, delete tea types
  - Full CRUD operations
  - Persistent storage
- **Settings:**
  - Toggle to enable/disable tea type prompt
  - Accessible via App Settings menu

**Implementation:**
- `TeaTypeManagementView.kt`: Management UI
- `TeaTypeManagementViewModel.kt`: Logic and state
- `BrewState.FirstFermentation(teaType: String)`: Domain model
- Widget integration (shows tea type in home screen widgets)

---

### 5. Notification Quick Actions ⚡️
**Added:** March 6, 2026

Complete or extend fermentation directly from notifications without opening the app.

**Features:**
- **Complete Action:** Advances brew to next stage (F1→F2 or F2→Completed)
- **Extend 1 Day Action:** Adds one day to current fermentation stage
- **No App Required:** Actions work from notification without launching app
- **Smart Identification:** Uses brew number for accurate targeting
- **Automatic Updates:** Widgets and app state update immediately

**Platform Implementation:**
- **Android:** `NotificationActionReceiver.kt` handles intent-based actions
- **iOS:** `UNNotificationAction` with action handlers in `ContentView.swift`

**Model Methods:**
- `completeByNameNumber(brewNameNumber: Int)`
- `extendFermentationByNameNumber(brewNameNumber: Int, days: Int)`

**Fix Applied:** March 7, 2026 - Complete action now properly handles fermentation stages

---

## UI/UX Improvements

### Visual State Indicators
**Added:** March 4, 2026

Color-coded status messages make brew states immediately obvious:
- **✓ Ready!** (0 days) - Green (#2E7D32)
- **X days overdue** (< 0 days) - Red
- **X days left** (> 0 days) - Black

### Empty State Design
**Added:** March 4, 2026

Welcoming first-launch experience:
- Large jar emoji (🫙) for visual appeal
- Clear "No active brews" heading
- Helpful call-to-action: "Tap 'Add' to start your first kombucha!"
- Centered layout with proper spacing

### Dark Mode Improvements
**Added:** March 7, 2026

Enhanced dark mode support:
- Better contrast ratios
- Improved readability
- Consistent theming across all screens
- Material Design 3 dynamic colors

### Settings Reorganization
**Updated:** March 2026

Better organization with dedicated sections:
- **App Settings:** Manage Flavors, Tea Types, History preferences
- **Brew Settings:** Per-brew fermentation times, renaming, deletion
- **Better Spacing:** Added spacing around separators and before delete button
- **Safety Features:** Confirmation dialog before deleting brews

---

## Technical Improvements

### Architecture & Dependencies
**Updated:** March 3, 2026

- **Kotlin:** Updated to 2.1.0
- **Compose Multiplatform:** Updated to 1.7.1
- **Material Design 3:** Complete migration from Material 2
- **Compose Resources:** Migrated from moko-resources to official Compose resources
- **Gradle:** Updated to 8.10.2

### Brew Naming System
**Refactored:** March 4, 2026

Migrated from string-based names to number-based system:
- **Old:** Custom string names stored as keys
- **New:** Sequential numbers (Brew #1, Brew #2, etc.)
- **Benefits:** 
  - Consistent notification targeting
  - Simpler data model
  - Better widget integration
  - Automatic renaming support

### Data Migration
**Fixed:** March 4, 2026

Improved migration from old "batch" terminology to "brew":
- Safe migration of existing data
- No data loss during terminology change
- Backwards compatibility handled

### Widget Architecture
**Implemented:** March 4, 2026

Platform-agnostic widget system:
- Common `WidgetUpdater` interface
- Platform-specific implementations (Glance for Android, WidgetKit for iOS)
- Shared settings via `SettingsFactory`
- App Groups for iOS data sharing
- Automatic updates on brew changes

---

## Localization

**Complete translations in:**
- 🇬🇧 **English** (default)
- 🇳🇱 **Dutch** (Nederlands)
- 🇩🇪 **German** (Deutsch)

**All new features fully localized:**
- Widget text and labels
- History view and statistics
- Flavor management UI
- Tea type management UI
- Notification actions
- Empty states and error messages

**Total new strings:** 126 strings per language (378 total)

---

## Bug Fixes

### Notification Action Handling
**Fixed:** March 7, 2026
- Complete action now properly handles all fermentation stages
- Fixed edge cases in stage transitions
- Improved error handling for invalid brew numbers

### Widget Updates
**Fixed:** March 5, 2026
- iOS widget now updates immediately when brews change
- Fixed containerBackground API compatibility for iOS 17+
- Removed redundant jar emoji from Android widget header

### Statistics Formatting
**Fixed:** March 7, 2026
- Fixed number formatting in history view statistics
- Improved decimal precision for averages
- Better display of fermentation day calculations

### iOS Build Configuration
**Fixed:** March 3, 2026
- Resolved Compose resources export configuration
- Fixed syncComposeResourcesForIos task conflicts
- Removed unnecessary build workarounds

---

## Documentation

**New comprehensive documentation:**
- `docs/FEATURES.md` - Complete feature documentation
- `docs/FEATURE_IDEAS.md` - Future feature roadmap
- `docs/FEATURE_MIGRATION_PROCESS.md` - Feature lifecycle process
- `docs/DEVELOPMENT.md` - Developer guide
- `docs/HISTORY_FEATURE.md` - History feature deep-dive
- `docs/WIDGET_SETUP.md` - Widget setup instructions
- `docs/WIDGET_IMPLEMENTATION.md` - Widget technical details
- `docs/WIDGET_STATUS.md` - Widget development status
- `docs/WIDGET_DEBUGGING.md` - Widget troubleshooting
- `docs/XCODE_WIDGET_GUIDE.md` - Xcode widget configuration
- `AGENT.md` - AI agent instructions (309 lines)

---

## Files Changed

**88 files changed:**
- **9,468 insertions**
- **811 deletions**
- **Net change:** +8,657 lines

**Key areas:**
- New features: Widgets, History, Flavor/Tea Type management
- UI improvements: Visual states, empty states, dark mode
- Architecture: Dependency updates, refactoring, migrations
- Documentation: Comprehensive developer and user docs
- Localization: 126 new strings × 3 languages

---

## Migration Notes

### For Existing Users

**Automatic migrations:**
- ✅ Brew naming system (string → number)
- ✅ "Batch" → "Brew" terminology
- ✅ Settings persistence preserved
- ✅ Active brews maintained

**New opt-in features:**
- History tracking (enabled by default, can be disabled)
- Flavor prompts (enabled by default, can be disabled)
- Tea type prompts (disabled by default, can be enabled)

**Widget setup:**
- **Android:** Long-press home screen → Add widget → Select KombuTime
- **iOS:** Requires Xcode setup (see `docs/WIDGET_SETUP.md`)

---

## Testing Recommendations

**Key areas to test:**
1. ✅ Widget display and updates (all sizes)
2. ✅ Notification quick actions (Complete & Extend)
3. ✅ History statistics accuracy
4. ✅ Flavor/Tea type CRUD operations
5. ✅ Dark mode consistency
6. ✅ Data migration from previous version
7. ✅ Export functionality (CSV/JSON)
8. ✅ Localization in all 3 languages
9. ✅ Empty states and visual indicators
10. ✅ Settings persistence across app restarts

---

## Known Issues

**iOS Widget:**
- Requires manual Xcode configuration (documented in WIDGET_SETUP.md)
- Not available through standard app deployment

**Android Widget:**
- First widget add may require manual refresh
- Works seamlessly after initial setup

---

## Credits

**Development Period:** September 2025 - March 2026 (6 months)
**Total Commits:** 26 commits
**Primary Developer:** Lammert Westerhoff

**Key Milestones:**
- Sep 1, 2025: Last pre-feature-release commit (Kotlin time fixes)
- Mar 3, 2026: Foundation (Modernization, Dutch localization, Flavor feature)
- Mar 4, 2026: Widget implementation
- Mar 6, 2026: Notification quick actions
- Mar 7, 2026: Final polish and bug fixes

---

## Future Roadmap

See `docs/FEATURE_IDEAS.md` for planned features including:
- Enhanced statistics and charts
- Brewing templates
- Batch notes and photos
- Advanced notifications
- And more!

---

*This release represents a major evolution of KombuTime while maintaining its core philosophy: simple, focused, and delightful kombucha brew tracking.*
