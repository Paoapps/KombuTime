import SwiftUI
import WidgetKit

@main
struct iOSApp: App {
    init() {
        // Listen for brew data changes and reload widgets
        NotificationCenter.default.addObserver(
            forName: NSNotification.Name("BrewDataChanged"),
            object: nil,
            queue: .main
        ) { _ in
            WidgetCenter.shared.reloadAllTimelines()
        }
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}