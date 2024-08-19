import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController(scheduleNotifications: { notifications in
            // Request notification permission
            UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
                if let error = error {
                    print("Error requesting notification permission: \(error)")
                    return
                }

                if granted {
                    // Clear all scheduled notifications
                    UNUserNotificationCenter.current().removeAllPendingNotificationRequests()

                    // Schedule notifications for each Batch
                    for notification in notifications {
                        let content = UNMutableNotificationContent()
                        content.title = notification.title.localized()
                        content.body = notification.message.localized()
                        content.sound = UNNotificationSound.default

                        if let triggerDate = localDateToDate(localDateTime: notification.time) {
                            let trigger = UNCalendarNotificationTrigger(dateMatching: Calendar.current.dateComponents([.year, .month, .day, .hour, .minute], from: triggerDate), repeats: false)

                            let request = UNNotificationRequest(identifier: UUID().uuidString, content: content, trigger: trigger)

                            UNUserNotificationCenter.current().add(request) { error in
                                if let error = error {
                                    print("Error scheduling notification: \(error)")
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    func localDateToDate(localDateTime: Kotlinx_datetimeLocalDateTime) -> Date? {
        var dateComponents = DateComponents()
        dateComponents.year = Int(localDateTime.year)
        dateComponents.month = Int(localDateTime.monthNumber)
        dateComponents.day = Int(localDateTime.dayOfMonth)
        dateComponents.hour = Int(localDateTime.hour)
        dateComponents.minute = Int(localDateTime.minute)

        return Calendar.current.date(from: dateComponents)
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}



