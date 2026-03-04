# Widget Debugging Guide

## How to Run and Test the Widget

### ❌ Don't Do This
- **Do NOT run the "BrewWidgetExtension" scheme** - This will fail with the error you're seeing
- Widget extensions cannot be run directly like apps

### ✅ Do This Instead

#### Method 1: Run the Main App (Recommended for Development)
1. In Xcode, select the **iosApp** scheme (not BrewWidgetExtension)
2. Select your simulator or device
3. Click Run (⌘R)
4. Once the app is running:
   - Go to the simulator's home screen (⌘⇧H)
   - Long press on empty space to enter "jiggle mode"
   - Tap the "+" button in the top left
   - Search for "Kombu Time"
   - Select your widget and add it to the home screen

#### Method 2: Use Widget Preview in Xcode
1. Open `iosApp/BrewWidget/BrewWidget.swift`
2. Look for the `BrewWidget_Previews` struct at the bottom
3. Click the "Resume" button in the canvas (if not visible, go to Editor → Canvas)
4. The widget will render in the preview pane
5. You can change the preview family in the code to test different sizes

#### Method 3: Debug Widget Extension
If you need to debug the widget specifically:
1. Run the **iosApp** scheme first
2. Add the widget to the home screen (see Method 1)
3. Stop the app
4. Switch to the **BrewWidgetExtension** scheme
5. Run it - Xcode will ask you to choose the widget to debug
6. Select your widget from the home screen
7. Now you can set breakpoints in the widget code

## Troubleshooting

### Widget Not Showing Up in Widget Gallery
- Make sure the main app has been run at least once
- The widget extension needs to be installed with the app

### Widget Shows "No Brews"
- Add some brews in the main app first
- The widget reads data from the shared App Groups container
- Make sure App Groups entitlements are configured correctly

### Widget Not Updating
- iOS widgets update on their own timeline (every 15+ minutes typically)
- For testing, remove and re-add the widget to force an update
- Or use the widget preview in Xcode for instant updates

## Widget Sizes Supported
- **Small**: Shows 1 brew with progress bar
- **Medium**: Shows up to 2 brews with progress bars
- **Large**: Shows up to 4 brews with progress bars

All sizes show:
- Brew name
- Flavor (if in second fermentation)
- Days remaining
- Progress bar with color coding (green > 3 days, orange 1-3 days, red < 1 day)
