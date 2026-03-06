import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {

    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    func makeUIViewController(context: Context) -> UIViewController {
        // Set up notification delegate to handle actions
        UNUserNotificationCenter.current().delegate = context.coordinator

        let viewController = MainViewControllerKt.MainViewController(scheduleNotifications: { notifications in
            // Request notification permission
            UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
                if let error = error {
                    print("Error requesting notification permission: \(error)")
                    return
                }

                if granted {
                    // Set up notification categories with actions
                    self.setupNotificationCategories()

                    // Clear all scheduled notifications
                    UNUserNotificationCenter.current().removeAllPendingNotificationRequests()

                    // Schedule notifications for each Batch
                    for notification in notifications {
                        let content = UNMutableNotificationContent()
                        content.title = notification.title
                        content.body = notification.message
                        content.sound = UNNotificationSound.default
                        content.categoryIdentifier = "BREW_ACTIONS"

                        // Store brew info in userInfo for action handling
                        content.userInfo = [
                            "brewNameNumber": notification.brewNameNumber,
                            "notificationId": notification.id
                        ]

                        if let triggerDate = localDateToDate(localDateTime: notification.time) {
                            let trigger = UNCalendarNotificationTrigger(dateMatching: Calendar.current.dateComponents([.year, .month, .day, .hour, .minute], from: triggerDate), repeats: false)

                            let request = UNNotificationRequest(identifier: "\(notification.id)", content: content, trigger: trigger)

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

        context.coordinator.viewController = viewController
        return viewController
    }

    func setupNotificationCategories() {
        let completeAction = UNNotificationAction(
            identifier: "COMPLETE_ACTION",
            title: "Complete",
            options: [.destructive]
        )

        let extendAction = UNNotificationAction(
            identifier: "EXTEND_ACTION",
            title: "Extend 1 Day",
            options: []
        )

        let category = UNNotificationCategory(
            identifier: "BREW_ACTIONS",
            actions: [completeAction, extendAction],
            intentIdentifiers: [],
            options: []
        )

        UNUserNotificationCenter.current().setNotificationCategories([category])
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    class Coordinator: NSObject, UNUserNotificationCenterDelegate {
        var viewController: UIViewController?

        // Handle notification when app is in foreground
        func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
            completionHandler([.banner, .sound])
        }

        // Handle notification action
        func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
            let userInfo = response.notification.request.content.userInfo

            guard let brewNameNumber = userInfo["brewNameNumber"] as? Int32 else {
                completionHandler()
                return
            }

            // Use the NotificationActionHandler to process actions
            switch response.actionIdentifier {
            case "COMPLETE_ACTION":
                NotificationActionHandler.shared.completeBrewFromNotification(brewNameNumber: brewNameNumber)
            case "EXTEND_ACTION":
                NotificationActionHandler.shared.extendBrewFromNotification(brewNameNumber: brewNameNumber)
            default:
                break
            }

            completionHandler()
        }
    }

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
            .onAppear {
                // Ensure proper setup when view appears
                UIApplication.shared.isIdleTimerDisabled = false
            }
    }
}



