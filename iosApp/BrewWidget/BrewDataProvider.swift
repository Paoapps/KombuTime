import Foundation

class BrewDataProvider {
    static let shared = BrewDataProvider()

    private init() {}

    func getBrews() -> [BrewData] {
        // Use UserDefaults with app group to share data between app and widget
        let defaults = UserDefaults(suiteName: "group.com.paoapps.kombutime") ?? UserDefaults.standard

        // Debug: Print all keys in the shared container
        print("=== BrewWidget Debug ===")
        print("All keys in App Group UserDefaults:")
        if let allKeys = defaults.dictionaryRepresentation().keys.sorted() as? [String] {
            for key in allKeys {
                print("  - \(key): \(String(describing: defaults.object(forKey: key)))")
            }
        }

        guard let brewsJson = defaults.string(forKey: "brews") else {
            print("ERROR: No 'brews' key found in UserDefaults")
            return []
        }

        print("Found brews JSON: \(brewsJson)")

        guard let data = brewsJson.data(using: .utf8) else {
            print("ERROR: Could not convert brews string to data")
            return []
        }

        do {
            // Parse the JSON data
            if let json = try JSONSerialization.jsonObject(with: data) as? [[String: Any]] {
                print("Successfully parsed JSON array with \(json.count) items")
                let brews = json.compactMap { brewDict -> BrewData? in
                    print("Parsing brew dict: \(brewDict)")
                    let result = parseBrewFromDict(brewDict)
                    if result == nil {
                        print("ERROR: Failed to parse brew from dict")
                    } else {
                        print("SUCCESS: Parsed brew: \(result!.name)")
                    }
                    return result
                }
                print("Total brews parsed: \(brews.count)")
                return brews
            } else {
                print("ERROR: JSON is not an array of dictionaries")
            }
        } catch {
            print("Error parsing brews: \(error)")
        }

        return []
    }

    private func parseBrewFromDict(_ dict: [String: Any]) -> BrewData? {
        print("  Checking for 'settings' key...")
        guard let settingsDict = dict["settings"] as? [String: Any] else {
            print("  ERROR: Missing or invalid 'settings' key")
            return nil
        }

        print("  Checking for 'name' in settings...")
        guard let name = settingsDict["name"] as? String else {
            print("  ERROR: Missing or invalid 'name' in settings")
            return nil
        }
        print("  Found name: \(name)")

        print("  Checking for fermentation days (with defaults)...")
        let firstFermentationDays = settingsDict["firstFermentationDays"] as? Int ?? 12
        let secondFermentationDays = settingsDict["secondFermentationDays"] as? Int ?? 3
        print("  First fermentation days: \(firstFermentationDays), Second: \(secondFermentationDays)")

        print("  Checking for 'startDate'...")
        guard let startDateStr = dict["startDate"] as? String else {
            print("  ERROR: Missing or invalid 'startDate'")
            return nil
        }
        print("  Found startDate: \(startDateStr)")

        print("  Checking for 'state' (with default)...")
        let stateDict = dict["state"] as? [String: Any]
        let stateType = stateDict?["type"] as? String ?? "com.paoapps.kombutime.domain.BrewState.FirstFermentation"
        print("  State type: \(stateType)")

        let isFirstFermentation = stateType == "com.paoapps.kombutime.domain.BrewState.FirstFermentation"
        let flavor = (stateDict?["flavor"] as? String) ?? ""
        let fermentationDays = isFirstFermentation ? firstFermentationDays : secondFermentationDays

        // Parse date string (format: "YYYY-MM-DD")
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        guard let startDate = dateFormatter.date(from: startDateStr) else {
            return nil
        }

        // Calculate days remaining
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())
        let start = calendar.startOfDay(for: startDate)
        let daysPassed = calendar.dateComponents([.day], from: start, to: today).day ?? 0
        let daysRemaining = fermentationDays - daysPassed
        let progress = Float(daysPassed) / Float(fermentationDays)

        return BrewData(
            name: name,
            flavor: flavor,
            isFirstFermentation: isFirstFermentation,
            startDate: startDate,
            fermentationDays: fermentationDays,
            daysRemaining: daysRemaining,
            progress: max(0, progress)
        )
    }
}
