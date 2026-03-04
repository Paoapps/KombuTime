# Home Screen Widget Setup Guide

This guide explains how to set up and configure the home screen widgets for KombuTime on both Android and iOS.

## Overview

KombuTime now supports home screen widgets that display your active brews at a glance, showing:
- Brew names and flavors
- Progress bars indicating fermentation progress
- Days remaining (or overdue)
- Visual indicators for first vs. second fermentation

## Android Widget Setup

### Requirements
- Android 12 (API 31) or higher
- Jetpack Glance library (already included)

### Widget Sizes
- **Small** (2x1): Shows the most urgent brew
- **Medium** (3x3): Shows up to 2 brews
- **Large** (4x4): Shows up to 4 brews

### Adding Widget to Home Screen
1. Long-press on your home screen
2. Tap "Widgets"
3. Find "KombuTime" in the widget list
4. Select the desired widget size
5. Drag it to your home screen

### Widget Updates
The widget automatically updates when:
- You add, complete, or delete a brew
- The day changes (midnight)
- You modify brew settings

### Manual Refresh
Widgets refresh automatically, but you can force a refresh by:
1. Tapping the widget (opens the app)
2. Making any change to brews
3. Closing and reopening the app

## iOS Widget Setup

### Requirements
- iOS 14 or higher
- Widget Extension target (needs to be added in Xcode)

### Xcode Configuration Steps

#### 1. Add Widget Extension Target
1. Open `iosApp.xcodeproj` in Xcode
2. Go to File > New > Target
3. Select "Widget Extension"
4. Name it "BrewWidget"
5. Uncheck "Include Configuration Intent" (we don't need it)
6. Finish and activate the scheme

#### 2. Configure App Groups
Both the main app and widget extension need to share data via App Groups.

**For Main App:**
1. Select the `iosApp` target
2. Go to "Signing & Capabilities"
3. Click "+ Capability"
4. Add "App Groups"
5. Enable the checkbox for `group.com.paoapps.kombutime`

**For Widget Extension:**
1. Select the `BrewWidget` target
2. Go to "Signing & Capabilities"
3. Click "+ Capability"
4. Add "App Groups"
5. Enable the checkbox for `group.com.paoapps.kombutime`

#### 3. Add Widget Files to Target
1. In Xcode, select the created widget files:
   - `iosApp/BrewWidget/BrewWidget.swift`
   - `iosApp/BrewWidget/BrewDataProvider.swift`
   - `iosApp/BrewWidget/Info.plist`
   - `iosApp/BrewWidget/BrewWidget.entitlements`
2. In the File Inspector (right panel), ensure "BrewWidget" target is checked

#### 4. Link ComposeApp Framework
The widget needs access to the shared Kotlin/Compose code:
1. Select the `BrewWidget` target
2. Go to "Build Phases"
3. Expand "Link Binary With Libraries"
4. Click "+" and add `ComposeApp.framework`

#### 5. Set Entitlements
1. Select the `BrewWidget` target
2. Go to "Build Settings"
3. Search for "Code Signing Entitlements"
4. Set it to `BrewWidget/BrewWidget.entitlements`

**For Main App:**
1. Select the `iosApp` target
2. Go to "Build Settings"
3. Search for "Code Signing Entitlements"
4. Set it to `iosApp/iosApp.entitlements`

#### 6. Update Info.plist
The widget's Info.plist is already created at `iosApp/BrewWidget/Info.plist`.
Make sure it's set as the target's Info.plist in Build Settings.

### Widget Sizes
- **Small**: Shows the most urgent brew
- **Medium**: Shows up to 2 brews
- **Large**: Shows up to 4 brews

### Adding Widget to Home Screen
1. Long-press on your home screen
2. Tap the "+" button in the top-left
3. Search for "KombuTime"
4. Select the desired widget size
5. Tap "Add Widget"

### Widget Updates
The widget automatically updates when:
- You add, complete, or delete a brew (via WidgetKit reload)
- The day changes (scheduled at midnight)
- iOS refreshes widget timelines (typically every 15-60 minutes)

## Troubleshooting

### Android

**Widget shows "No active brews" but I have brews:**
- Ensure you've opened the app at least once
- Check that storage permissions are granted
- Try removing and re-adding the widget

**Widget doesn't update:**
- The widget updates when brews change in the app
- Check that you're running Android 12+
- Try force-stopping and restarting the app

### iOS

**Widget extension won't build:**
- Ensure App Groups capability is enabled for both targets
- Verify `group.com.paoapps.kombutime` is selected in both
- Check that ComposeApp.framework is linked to the widget target
- Clean build folder (⌘+Shift+K) and rebuild

**Widget shows "No active brews" but I have brews:**
- Ensure App Groups are configured correctly
- Check that both app and widget use the same group identifier
- Try deleting the app and reinstalling
- Verify entitlements files are set correctly

**Widget doesn't update:**
- iOS controls widget update frequency
- Updates are guaranteed within a few hours
- Opening the app triggers an immediate update
- Widget timelines refresh at midnight automatically

**ComposeApp.framework not found:**
- Run the Gradle task to build the framework first:
  ```bash
  ./gradlew :composeApp:assembleXCFramework
  ```
- Or use the Xcode build phase that runs the Gradle task

## Technical Details

### Data Sharing

**Android:**
- Uses SharedPreferences (standard Android storage)
- Widget reads from the same storage as the main app
- No special configuration needed

**iOS:**
- Uses App Groups to share UserDefaults between app and widget
- App Group ID: `group.com.paoapps.kombutime`
- Both targets must be configured with the same App Group
- Data is stored in shared container

### Widget Refresh Strategy

**Android:**
- Immediate update on data changes (via GlanceAppWidget.update())
- Periodic updates every hour (configured in widget provider)
- Daily update at midnight (to refresh "days remaining")

**iOS:**
- Immediate update on data changes (via WidgetCenter.reloadAllTimelines())
- Timeline-based updates configured in Provider
- Next update scheduled for midnight each day
- iOS may throttle updates based on system resources

### Widget Content

Both platforms show identical information:
- Brew icon (🫙 for first fermentation, 🍾 for second)
- Brew name and optional flavor
- Fermentation stage label
- Days remaining (with color coding: red=overdue, green=ready, black=in progress)
- Progress bar (orange for F1, green for F2)

### Empty State

When no brews are active:
- Shows a friendly empty state
- Displays 🫙 icon
- Message: "No active brews"
- Call to action: "Tap to start brewing!"

## Development Notes

### Adding New Widget Features

When adding new features to widgets:

1. **Update both platforms** - Keep Android (Kotlin/Glance) and iOS (Swift/WidgetKit) in sync
2. **Test all sizes** - Ensure the feature works in small, medium, and large widgets
3. **Consider data sharing** - New data must be accessible via the shared storage mechanism
4. **Update BrewDataProvider** - Both platform-specific data providers need to parse new fields
5. **Test updates** - Verify widgets refresh when data changes

### File Locations

**Common/Shared Code:**
- `composeApp/src/commonMain/kotlin/com/paoapps/kombutime/SettingsFactory.kt`
- `composeApp/src/commonMain/kotlin/com/paoapps/kombutime/WidgetUpdater.kt`

**Android:**
- `composeApp/src/androidMain/kotlin/com/paoapps/kombutime/widget/BrewWidget.kt`
- `composeApp/src/androidMain/kotlin/com/paoapps/kombutime/widget/BrewWidgetDataProvider.kt`
- `composeApp/src/androidMain/kotlin/com/paoapps/kombutime/widget/BrewWidgetUpdater.kt`
- `composeApp/src/androidMain/res/xml/brew_widget_info.xml`

**iOS:**
- `iosApp/BrewWidget/BrewWidget.swift`
- `iosApp/BrewWidget/BrewDataProvider.swift`
- `iosApp/BrewWidget/Info.plist`
- `iosApp/BrewWidget/BrewWidget.entitlements`
- `iosApp/iosApp/iosApp.entitlements`

## Feature Alignment

This implementation follows the feature spec in `docs/FEATURE_IDEAS.md`:

✅ **Small widget**: Shows most urgent brew
✅ **Medium widget**: Shows 2-3 brews
✅ **Large widget**: Shows all brews (up to 4)
✅ **Progress bars**: Visual fermentation progress
✅ **Days remaining**: Clear countdown
✅ **At-a-glance view**: No need to open app
✅ **Tap to open**: Widget opens app when tapped
✅ **Platform-native**: Glance for Android, WidgetKit for iOS
✅ **No new features**: Only displays existing brew data
✅ **Maintains simplicity**: Follows minimalist philosophy

## Next Steps

After setting up the widget extension in Xcode:
1. Build and run the widget extension
2. Add the widget to your home screen
3. Add a brew in the app and verify the widget updates
4. Test all three widget sizes
5. Verify empty state when no brews exist
