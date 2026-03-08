# iOS Widget Extension Setup - Visual Guide

## ⚠️ IMPORTANT NOTES

1. **Widget Files May Be Overwritten**: When you create the Widget Extension target in Step 1, Xcode will generate default files that overwrite our custom widget implementation. Don't worry - Step 2 will guide you through restoring the correct files.

2. **Framework Path Issue**: Steps 6-7 will initially link to the Debug framework only. After completing all steps, you **MUST** follow the guide in `docs/FIX_FRAMEWORK_PATHS.md` to fix Debug/Release build compatibility. Otherwise, Release builds will fail!

## Prerequisites
✅ All widget files are already created and in place
✅ Xcode project is at: `iosApp/iosApp.xcodeproj`

## Step-by-Step Visual Guide

### Step 1: Add Widget Extension Target

1. **Open the project in Xcode**
   ```bash
   open iosApp/iosApp.xcodeproj
   ```

2. **Add new target:**
   - Click on the **iosApp** project in the Project Navigator (left sidebar, top item with blue icon)
   - In the editor area, you'll see the project and targets list
   - At the bottom of the targets list, click the **"+"** button

   **Visual cue:** Look for the blue project icon → targets list → "+" at bottom

3. **Select Widget Extension:**
   - In the template chooser window that appears
   - Scroll down to **"Application Extension"** section
   - Select **"Widget Extension"**
   - Click **"Next"**

4. **Configure the widget:**
   - Product Name: `BrewWidget`
   - Language: **Swift** (should be default)
   - **UNCHECK** "Include Configuration Intent" ⚠️ (important!)
   - Click **"Finish"**

5. **Activate scheme:**
   - A dialog appears: "Activate "BrewWidget" scheme?"
   - Click **"Activate"**

### Step 2: Add Widget Files to Target

Xcode created default widget files, but we need to use our custom ones instead.

1. **Delete Xcode's generated files:**
   - In Project Navigator, find the **BrewWidget** folder
   - Select the auto-generated Swift file (usually `BrewWidgetBundle.swift` or similar)
   - Right-click → **Delete** → **Move to Trash**

2. **Add our widget files to the target:**
   - In Project Navigator, navigate to `iosApp/BrewWidget/`
   - You should see:
     - `BrewWidget.swift`
     - `BrewDataProvider.swift`
     - `Info.plist`
     - `BrewWidget.entitlements`

3. **Set target membership for Swift files:**
   - Click on **`BrewWidget.swift`**
   - In the **File Inspector** (right sidebar - looks like a document icon)
   - Find **"Target Membership"** section
   - **CHECK** the box next to **BrewWidget**
   - **UNCHECK** the box next to **iosApp** (if checked)

   Repeat for **`BrewDataProvider.swift`**

   **Visual cue:** Right sidebar → top tab (document icon) → scroll to Target Membership

### Step 3: Enable App Groups for Main App

1. **Select the main app target:**
   - Click **iosApp** project (blue icon at top of Navigator)
   - In the targets list, select **iosApp** (not BrewWidget)

2. **Add App Groups capability:**
   - Click the **"Signing & Capabilities"** tab at the top
   - Click **"+ Capability"** button (top left of editor area)
   - Type "app groups" in the search
   - Double-click **"App Groups"**

3. **Add the app group:**
   - In the newly added App Groups section
   - Click the **"+"** button
   - Enter: `group.com.paoapps.kombutime`
   - Press **Enter**
   - **CHECK** the checkbox next to the group name

   **Visual cue:** The group should show a checkmark ✓

### Step 4: Enable App Groups for Widget Extension

1. **Select the widget target:**
   - In the targets list, select **BrewWidget**

2. **Add App Groups capability:**
   - Click the **"Signing & Capabilities"** tab
   - Click **"+ Capability"** button
   - Type "app groups" in the search
   - Double-click **"App Groups"**

3. **Add the same app group:**
   - Click the **"+"** button
   - Enter: `group.com.paoapps.kombutime`
   - Press **Enter**
   - **CHECK** the checkbox

   ⚠️ **Important:** Must be the exact same group name as the main app!

### Step 5: Set Entitlements Files

#### For Main App:
1. **Select iosApp target** → **Build Settings** tab
2. In the search box (top right), type: `entitlements`
3. Find **"Code Signing Entitlements"** row
4. Double-click the value field (under "iosApp" column)
5. Enter: `iosApp/iosApp.entitlements`
6. Press **Enter**

#### For Widget:
1. **Select BrewWidget target** → **Build Settings** tab
2. Search for: `entitlements`
3. Find **"Code Signing Entitlements"** row
4. Double-click the value field (under "BrewWidget" column)
5. Enter: `BrewWidget/BrewWidget.entitlements`
6. Press **Enter**

**Visual cue:** Build Settings tab → search box → double-click value → type path

### Step 6: Link ComposeApp Framework to Widget

1. **Select BrewWidget target**
2. Click **"Build Phases"** tab
3. Expand **"Link Binary With Libraries"** (click the triangle)
4. Click the **"+"** button at the bottom of the list
5. In the file chooser, look for `ComposeApp.framework`
   - It might be under **"Workspace"** or you may need to navigate
   - Path: `composeApp/build/XCFrameworks/debug/ComposeApp.framework`
6. Select it and click **"Add"**

**If ComposeApp.framework isn't visible:**
- You may need to build it first:
  ```bash
  ./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
  ```
- Then try adding it again

### Step 7: Embed ComposeApp Framework in Widget

This ensures the framework is included with the widget.

1. **Still in BrewWidget target → Build Phases**
2. Click **"+"** at the top left of the Build Phases area
3. Select **"New Copy Files Phase"**
4. In the new **"Copy Files"** section:
   - Change **Destination** dropdown to: **Frameworks**
5. Click the **"+"** button in this Copy Files section
6. Select **ComposeApp.framework**
7. Click **"Add"**

### Step 8: Configure Widget Info.plist

1. **Select BrewWidget target**
2. Click **"Build Settings"** tab
3. Search for: `info.plist`
4. Find **"Info.plist File"** row
5. Set value to: `BrewWidget/Info.plist`

### Step 9: Build and Test

1. **Build the widget:**
   - Select **BrewWidget** scheme (from scheme selector at top)
   - Press **⌘+B** to build
   - Check for any errors

2. **Build the main app:**
   - Select **iosApp** scheme
   - Press **⌘+B** to build

3. **Run on simulator:**
   - Press **⌘+R** to run
   - Once the app is running, press **Home** button (⌘+Shift+H)
   - Long-press on home screen
   - Click **"+"** button
   - Search for "KombuTime"
   - Select widget size
   - Add to home screen

## Troubleshooting

### "ComposeApp.framework not found"
**Solution:** Build the framework first:
```bash
cd /Users/lammertwesterhoff/Developer/Paoapps/KombuTime
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### "App Group not showing in capabilities"
**Solution:** Make sure you're signed in with your Apple ID in Xcode Preferences → Accounts

### Widget shows "No active brews" but app has brews
**Solution:** Check that both targets have the SAME app group identifier enabled:
- iosApp target: ✓ group.com.paoapps.kombutime
- BrewWidget target: ✓ group.com.paoapps.kombutime

### Build errors about missing imports
**Solution:** Make sure ComposeApp.framework is:
1. Linked in "Link Binary With Libraries"
2. Copied in "Copy Files" phase with Destination = Frameworks

### Widget doesn't update
**Solution:**
- The widget updates when you add/remove brews in the app
- You can also force refresh by removing and re-adding the widget

## Quick Checklist

Before running, verify:
- [ ] BrewWidget target exists
- [ ] BrewWidget.swift is in BrewWidget target (not iosApp)
- [ ] BrewDataProvider.swift is in BrewWidget target
- [ ] Both targets have App Groups capability
- [ ] Both targets use `group.com.paoapps.kombutime`
- [ ] Entitlements files are set for both targets
- [ ] ComposeApp.framework is linked to BrewWidget
- [ ] ComposeApp.framework is embedded (Copy Files phase)
- [ ] **Framework paths fixed for Debug/Release** (see `FIX_FRAMEWORK_PATHS.md`)
- [ ] BrewWidget scheme builds without errors
- [ ] iosApp scheme builds without errors

## ⚠️ CRITICAL: Fix Framework Paths for Release Builds

After completing the steps above, you **MUST** fix the framework linking to support both Debug and Release builds.

**See detailed instructions:** `docs/FIX_FRAMEWORK_PATHS.md`

**Why?** Steps 6-7 above hardcode the path to the Debug framework. This will cause Release builds to fail. The fix takes 2 minutes and ensures both configurations work.

## Common Xcode Navigation Tips

- **Project Navigator:** ⌘+1 (shows file tree)
- **File Inspector:** ⌘+Option+1 (shows file properties)
- **Build Settings:** Select target → "Build Settings" tab at top
- **Build Phases:** Select target → "Build Phases" tab at top
- **Search in Build Settings:** Use the search box at top right
- **Scheme Selector:** Click the scheme name next to play/stop buttons

## Next Steps

Once everything builds successfully:
1. Run the app (⌘+R)
2. Add a brew in the app
3. Go to home screen (⌘+Shift+H in simulator)
4. Long-press home screen → Add widget → KombuTime
5. Verify the brew appears in the widget!

---

**Need help?** See `docs/WIDGET_SETUP.md` for alternative instructions or open an issue.
