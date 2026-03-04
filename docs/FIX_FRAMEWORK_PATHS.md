# Fixing ComposeApp.framework for Debug/Release Builds

## The Problem

When you added `ComposeApp.framework` to the BrewWidget target, Xcode hardcoded the path to the Debug framework:
```
../composeApp/build/xcode-frameworks/Debug/iphonesimulator26.2/ComposeApp.framework
```

This means Release builds will fail because they need the Release framework instead.

## The Solution

Instead of linking to a specific Debug or Release framework, we need to:
1. Remove the hardcoded framework reference
2. Add a build phase that copies the correct framework based on configuration
3. Update the framework search paths

## Step-by-Step Fix

### Step 1: Remove the Hardcoded Framework Reference

1. **Open Xcode** → Select **BrewWidget** target
2. Go to **Build Phases** tab
3. Expand **"Link Binary With Libraries"**
4. Find `ComposeApp.framework` (the one with the specific path)
5. Click the **"-"** button to remove it
6. Also expand **"Copy Files"** section
7. Remove `ComposeApp.framework` from there too (click "-")

### Step 2: Add Framework Search Paths

1. **Still in BrewWidget target** → Go to **Build Settings** tab
2. Search for: `framework search paths`
3. Find **"Framework Search Paths"**
4. Double-click to edit
5. Click **"+"** to add a new path
6. Enter:
   ```
   $(SRCROOT)/../composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(PLATFORM_NAME)
   ```
7. Make sure it's set to **recursive** (checkbox should be checked)

**What this does:** It tells Xcode to look for frameworks in the directory that matches the current configuration (Debug or Release) and platform.

### Step 3: Add a "Run Script" Phase to Link the Framework

1. **BrewWidget target** → **Build Phases** tab
2. Click **"+"** at the top → **"New Run Script Phase"**
3. Drag this new phase to be **BEFORE** "Compile Sources"
4. Name it: **"Link ComposeApp Framework"**
5. In the script box, paste:

```bash
# Ensure ComposeApp.framework exists for the current configuration
FRAMEWORK_PATH="${SRCROOT}/../composeApp/build/xcode-frameworks/${CONFIGURATION}/${PLATFORM_NAME}/ComposeApp.framework"

if [ ! -d "$FRAMEWORK_PATH" ]; then
    echo "error: ComposeApp.framework not found at $FRAMEWORK_PATH"
    echo "error: Please build the framework first with:"
    echo "error:   ./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64"
    exit 1
fi

# Add framework to link flags
echo "Linking ComposeApp.framework from: $FRAMEWORK_PATH"
```

6. Expand **"Input Files"** and click "+" to add:
   ```
   $(SRCROOT)/../composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(PLATFORM_NAME)/ComposeApp.framework
   ```

7. Expand **"Output Files"** and click "+" to add:
   ```
   $(BUILT_PRODUCTS_DIR)/$(FRAMEWORKS_FOLDER_PATH)/ComposeApp.framework
   ```

### Step 4: Update the Copy Files Phase

1. **BrewWidget target** → **Build Phases** tab
2. Find the **"CopyFiles"** phase
3. Ensure **Destination** is set to: **Frameworks**
4. Click **"+"**
5. Click **"Add Other..."** → **"Add Files..."**
6. Navigate to:
   ```
   composeApp/build/xcode-frameworks/Debug/iphonesimulator26.2/
   ```
7. Select `ComposeApp.framework`
8. **IMPORTANT:** Before clicking "Add":
   - **Uncheck** "Copy items if needed"
   - Click **"Add"**

### Step 5: Make the Framework Reference Configuration-Aware

Actually, there's a simpler approach. Let me provide an alternative:

## **EASIER SOLUTION: Use a Build Script**

Instead of steps 3-4 above, do this:

### Alternative Step 3: Add Framework Link in Build Settings

1. **BrewWidget target** → **Build Settings**
2. Search for: `other linker flags`
3. Find **"Other Linker Flags"**
4. Double-click to edit
5. Click **"+"**
6. Add:
   ```
   -framework ComposeApp
   ```

### Alternative Step 4: Update Copy Files to Use Variable Path

Unfortunately, Xcode's Copy Files phase doesn't support variables well. The best approach is to use the main app's build phase.

## **RECOMMENDED SOLUTION: Use Main App's Framework**

The widget extension is embedded in the main app, so the main app already builds the ComposeApp.framework. We can reference that instead:

### Final Recommended Steps:

1. **BrewWidget target** → **Build Phases**
2. Remove `ComposeApp.framework` from:
   - "Link Binary With Libraries"
   - "Copy Files" phase

3. **BrewWidget target** → **Build Settings**
4. Search: `framework search paths`
5. Add:
   ```
   $(BUILD_DIR)/$(CONFIGURATION)$(EFFECTIVE_PLATFORM_NAME)
   ```

6. Search: `other linker flags`
7. Add:
   ```
   -framework ComposeApp
   ```

8. **BrewWidget target** → **Build Phases** → **Dependencies**
9. Click **"+"**
10. Add **"iosApp"** target as a dependency

This ensures:
- ✅ The main app builds ComposeApp.framework first
- ✅ The widget finds the framework regardless of Debug/Release
- ✅ No hardcoded paths
- ✅ Works for all configurations

## Verification

After making these changes:

1. **Clean Build Folder**: Product → Clean Build Folder (⌘+Shift+K)
2. **Build for Debug**: Select "iosApp" scheme → Build (⌘+B)
3. **Build for Release**:
   - Edit Scheme → Run → Change "Build Configuration" to "Release"
   - Build again
4. **Verify both work** without errors

## Troubleshooting

**"Framework not found ComposeApp"**
- Make sure Framework Search Paths includes the build directory
- Ensure iosApp target is built before BrewWidget

**"No such file or directory: ComposeApp.framework"**
- Build the framework first:
  ```bash
  ./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
  ```

**Release build fails**
- Make sure Framework Search Paths uses `$(CONFIGURATION)` variable
- Clean build folder and try again

## Quick Reference: What Each Variable Means

- `$(CONFIGURATION)` = "Debug" or "Release"
- `$(PLATFORM_NAME)` = "iphonesimulator", "iphoneos", etc.
- `$(SRCROOT)` = Path to the Xcode project directory
- `$(BUILD_DIR)` = Where Xcode puts build products
- `$(EFFECTIVE_PLATFORM_NAME)` = Platform name with hyphen (e.g., "-iphonesimulator")
