//
//  BrewWidget.swift
//  BrewWidget
//
//  Created by Lammert Westerhoff on 04/03/26.
//  Copyright © 2026 orgName. All rights reserved.
//

import WidgetKit
import SwiftUI

struct Provider: TimelineProvider {
    func placeholder(in context: Context) -> BrewEntry {
        BrewEntry(date: Date(), brews: [])
    }

    func getSnapshot(in context: Context, completion: @escaping (BrewEntry) -> ()) {
        let brews = BrewDataProvider.shared.getBrews()
        let entry = BrewEntry(date: Date(), brews: brews)
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        let brews = BrewDataProvider.shared.getBrews()
        let entry = BrewEntry(date: Date(), brews: brews)

        // Update widget at midnight each day
        let calendar = Calendar.current
        let midnight = calendar.startOfDay(for: Date().addingTimeInterval(86400))

        let timeline = Timeline(entries: [entry], policy: .after(midnight))
        completion(timeline)
    }
}

struct BrewEntry: TimelineEntry {
    let date: Date
    let brews: [BrewData]
}

struct BrewData: Identifiable {
    let id = UUID()
    let name: String
    let flavor: String
    let isFirstFermentation: Bool
    let startDate: Date
    let fermentationDays: Int
    let daysRemaining: Int
    let progress: Float
}

struct BrewWidgetEntryView: View {
    var entry: Provider.Entry
    @Environment(\.widgetFamily) var family

    var body: some View {
        if entry.brews.isEmpty {
            EmptyStateView()
        } else {
            switch family {
            case .systemSmall:
                SmallWidgetView(brew: entry.brews.first!)
            case .systemMedium:
                MediumWidgetView(brews: Array(entry.brews.prefix(2)))
            case .systemLarge:
                LargeWidgetView(brews: Array(entry.brews.prefix(4)))
            @unknown default:
                SmallWidgetView(brew: entry.brews.first!)
            }
        }
    }
}

struct EmptyStateView: View {
    var body: some View {
        VStack(spacing: 8) {
            Text("🫙")
                .font(.system(size: 32))
            Text("No active brews")
                .font(.system(size: 14, weight: .medium))
                .foregroundColor(.primary)
            Text("Tap to start brewing!")
                .font(.system(size: 12))
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct SmallWidgetView: View {
    let brew: BrewData

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Text(brew.isFirstFermentation ? "🫙" : "🍾")
                    .font(.system(size: 20))
                Text(brew.name)
                    .font(.system(size: 12, weight: .medium))
                    .lineLimit(1)
                Spacer()
            }

            Spacer()

            Text(daysText(for: brew))
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(brew.daysRemaining < 0 ? .red : (brew.daysRemaining == 0 ? .green : .primary))

            ProgressBar(progress: brew.progress, isFirstFermentation: brew.isFirstFermentation)
        }
        .padding(16)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct MediumWidgetView: View {
    let brews: [BrewData]

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            WidgetHeader()

            ForEach(brews) { brew in
                BrewRow(brew: brew, isCompact: false)
            }

            Spacer()
        }
        .padding(16)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct LargeWidgetView: View {
    let brews: [BrewData]

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            WidgetHeader()

            ForEach(brews) { brew in
                BrewRow(brew: brew, isCompact: false)
            }

            Spacer()
        }
        .padding(16)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct WidgetHeader: View {
    var body: some View {
        HStack {
            Text("🫙")
                .font(.system(size: 20))
            Text("KombuTime")
                .font(.system(size: 16, weight: .bold))
        }
    }
}

struct BrewRow: View {
    let brew: BrewData
    let isCompact: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Text(brew.isFirstFermentation ? "🫙" : "🍾")
                    .font(.system(size: isCompact ? 16 : 20))

                VStack(alignment: .leading, spacing: 2) {
                    Text(brewTitle(for: brew))
                        .font(.system(size: isCompact ? 12 : 14, weight: .medium))
                        .lineLimit(1)

                    if !isCompact {
                        Text(brew.isFirstFermentation ? "First Fermentation" : "Second Fermentation")
                            .font(.system(size: 11))
                            .foregroundColor(.secondary)
                    }
                }

                Spacer()

                Text(daysText(for: brew))
                    .font(.system(size: isCompact ? 11 : 12, weight: .bold))
                    .foregroundColor(brew.daysRemaining < 0 ? .red : (brew.daysRemaining == 0 ? .green : .primary))
            }

            if !isCompact {
                ProgressBar(progress: brew.progress, isFirstFermentation: brew.isFirstFermentation)
            }
        }
    }

    private func brewTitle(for brew: BrewData) -> String {
        if brew.flavor.isEmpty || brew.isFirstFermentation {
            return brew.name
        } else {
            return "\(brew.name) - \(brew.flavor)"
        }
    }
}

struct ProgressBar: View {
    let progress: Float
    let isFirstFermentation: Bool

    var backgroundColor: Color {
        isFirstFermentation ? Color(red: 1.0, green: 0.878, blue: 0.698) : Color(red: 0.773, green: 0.882, blue: 0.647)
    }

    var progressColor: Color {
        isFirstFermentation ? Color(red: 1.0, green: 0.596, blue: 0.0) : Color(red: 0.545, green: 0.765, blue: 0.290)
    }

    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                Rectangle()
                    .fill(backgroundColor)
                    .frame(height: 8)

                if progress > 0 {
                    Rectangle()
                        .fill(progressColor)
                        .frame(width: geometry.size.width * CGFloat(min(progress, 1.0)), height: 8)
                }
            }
        }
        .frame(height: 8)
    }
}

private func daysText(for brew: BrewData) -> String {
    if brew.daysRemaining < 0 {
        return "\(-brew.daysRemaining) days overdue"
    } else if brew.daysRemaining == 0 {
        return "Ready today!"
    } else if brew.daysRemaining == 1 {
        return "1 day left"
    } else {
        return "\(brew.daysRemaining) days left"
    }
}

struct BrewWidget: Widget {
    let kind: String = "BrewWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            BrewWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("Active Brews")
        .description("View your active kombucha brews at a glance")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

struct BrewWidget_Previews: PreviewProvider {
    static var previews: some View {
        let sampleBrews = [
            BrewData(name: "Batch 1", flavor: "Ginger", isFirstFermentation: false, startDate: Date(), fermentationDays: 3, daysRemaining: 2, progress: 0.33),
            BrewData(name: "Batch 2", flavor: "", isFirstFermentation: true, startDate: Date(), fermentationDays: 12, daysRemaining: 8, progress: 0.33)
        ]

        BrewWidgetEntryView(entry: BrewEntry(date: Date(), brews: sampleBrews))
            .previewContext(WidgetPreviewContext(family: .systemMedium))
    }
}
