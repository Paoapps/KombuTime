#!/bin/bash

# iOS Widget Extension Setup Script for KombuTime
# This script helps automate parts of the Xcode configuration

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
XCODE_PROJECT="$PROJECT_DIR/iosApp/iosApp.xcodeproj"

echo "🫙 KombuTime Widget Extension Setup"
echo "===================================="
echo ""
echo "Project: $XCODE_PROJECT"
echo ""

# Check if Xcode is installed
if ! command -v xcodebuild &> /dev/null; then
    echo "❌ Error: Xcode command line tools not found"
    echo "Please install Xcode from the App Store"
    exit 1
fi

echo "✅ Xcode found: $(xcodebuild -version | head -1)"
echo ""

# Unfortunately, adding a Widget Extension target requires Xcode GUI or complex pbxproj manipulation
# This script will guide you through the manual steps

echo "📋 Manual Steps Required in Xcode:"
echo ""
echo "Step 1: Add Widget Extension Target"
echo "  1. Open iosApp.xcodeproj in Xcode"
echo "  2. File → New → Target"
echo "  3. Select 'Widget Extension'"
echo "  4. Product Name: BrewWidget"
echo "  5. Language: Swift"
echo "  6. Uncheck 'Include Configuration Intent'"
echo "  7. Click Finish"
echo "  8. Click 'Activate' when prompted"
echo ""

echo "Step 2: Add Widget Files to Target"
echo "  1. In Project Navigator, select:"
echo "     - iosApp/BrewWidget/BrewWidget.swift"
echo "     - iosApp/BrewWidget/BrewDataProvider.swift"
echo "  2. In File Inspector (right panel):"
echo "     - Check 'BrewWidget' target membership"
echo "     - Keep 'iosApp' unchecked for these files"
echo ""

echo "Step 3: Enable App Groups (Main App)"
echo "  1. Select 'iosApp' target"
echo "  2. Signing & Capabilities tab"
echo "  3. Click '+ Capability'"
echo "  4. Add 'App Groups'"
echo "  5. Click '+' to add group"
echo "  6. Enter: group.com.paoapps.kombutime"
echo "  7. Check the checkbox for this group"
echo ""

echo "Step 4: Enable App Groups (Widget)"
echo "  1. Select 'BrewWidget' target"
echo "  2. Signing & Capabilities tab"
echo "  3. Click '+ Capability'"
echo "  4. Add 'App Groups'"
echo "  5. Click '+' to add group"
echo "  6. Enter: group.com.paoapps.kombutime"
echo "  7. Check the checkbox for this group"
echo ""

echo "Step 5: Set Entitlements Files"
echo "  1. Select 'iosApp' target → Build Settings"
echo "  2. Search for 'Code Signing Entitlements'"
echo "  3. Set to: iosApp/iosApp.entitlements"
echo "  4. Select 'BrewWidget' target → Build Settings"
echo "  5. Search for 'Code Signing Entitlements'"
echo "  6. Set to: BrewWidget/BrewWidget.entitlements"
echo ""

echo "Step 6: Link ComposeApp Framework"
echo "  1. Select 'BrewWidget' target"
echo "  2. Build Phases tab"
echo "  3. Expand 'Link Binary With Libraries'"
echo "  4. Click '+'"
echo "  5. Add 'ComposeApp.framework'"
echo ""

echo "Step 7: Copy ComposeApp Framework to Widget"
echo "  1. Select 'BrewWidget' target"
echo "  2. Build Phases tab"
echo "  3. Click '+' → 'New Copy Files Phase'"
echo "  4. Destination: Frameworks"
echo "  5. Click '+' and add 'ComposeApp.framework'"
echo ""

echo "Step 8: Build and Test"
echo "  1. Select 'BrewWidget' scheme"
echo "  2. Build (⌘+B)"
echo "  3. If successful, select 'iosApp' scheme"
echo "  4. Run on simulator or device"
echo "  5. Add widget to home screen"
echo ""

echo "📚 For detailed instructions, see: docs/WIDGET_SETUP.md"
echo ""

# Check if widget files exist
echo "Checking widget files..."
if [ -f "$PROJECT_DIR/iosApp/BrewWidget/BrewWidget.swift" ]; then
    echo "  ✅ BrewWidget.swift exists"
else
    echo "  ❌ BrewWidget.swift missing"
fi

if [ -f "$PROJECT_DIR/iosApp/BrewWidget/BrewDataProvider.swift" ]; then
    echo "  ✅ BrewDataProvider.swift exists"
else
    echo "  ❌ BrewDataProvider.swift missing"
fi

if [ -f "$PROJECT_DIR/iosApp/BrewWidget/Info.plist" ]; then
    echo "  ✅ Info.plist exists"
else
    echo "  ❌ Info.plist missing"
fi

if [ -f "$PROJECT_DIR/iosApp/BrewWidget/BrewWidget.entitlements" ]; then
    echo "  ✅ BrewWidget.entitlements exists"
else
    echo "  ❌ BrewWidget.entitlements missing"
fi

if [ -f "$PROJECT_DIR/iosApp/iosApp/iosApp.entitlements" ]; then
    echo "  ✅ iosApp.entitlements exists"
else
    echo "  ❌ iosApp.entitlements missing"
fi

echo ""
echo "🎯 Next Step: Open Xcode and follow the steps above"
echo "   Or see docs/WIDGET_SETUP.md for screenshots"
echo ""
echo "To open the project in Xcode, run:"
echo "   open $XCODE_PROJECT"
