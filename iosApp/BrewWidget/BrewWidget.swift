//
//  BrewWidget.swift
//  BrewWidget
//
//  Created by Lammert Westerhoff on 04/03/26.
//  Copyright © 2026 orgName. All rights reserved.
//

import WidgetKit
import SwiftUI

// MARK: - Widget Container
private struct WidgetContainer<Content: View>: View {
    let alignment: Alignment
    let spacing: CGFloat
    let content: Content

    init(alignment: Alignment = .center, spacing: CGFloat = 8, @ViewBuilder content: () -> Content) {
        self.alignment = alignment
        self.spacing = spacing
        self.content = content()
    }

    var body: some View {
        VStack(alignment: alignment.horizontal, spacing: spacing) {
            content
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: alignment)
        .widgetBackground()
    }
}

extension Alignment {
    var horizontal: HorizontalAlignment {
        switch self {
        case .leading, .topLeading, .bottomLeading:
            return .leading
        case .trailing, .topTrailing, .bottomTrailing:
            return .trailing
        default:
            return .center
        }
    }
}

// MARK: - View Extension for Widget Background
extension View {
    @ViewBuilder
    func widgetBackground() -> some View {
        if #available(iOS 17.0, *) {
            self.containerBackground(.background, for: .widget)
        } else {
            self.padding()
        }
    }
}

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
                SmallWidgetView(brews: Array(entry.brews.prefix(2)))
            case .systemMedium:
                MediumWidgetView(brews: Array(entry.brews.prefix(2)))
            case .systemLarge:
                LargeWidgetView(brews: Array(entry.brews.prefix(4)))
            @unknown default:
                SmallWidgetView(brews: Array(entry.brews.prefix(2)))
            }
        }
    }
}

struct EmptyStateView: View {
    var body: some View {
        WidgetContainer {
            Text("🫙")
                .font(.system(size: 32))
            Text("No active brews")
                .font(.system(size: 14, weight: .medium))
                .foregroundColor(.primary)
            Text("Tap to start brewing!")
                .font(.system(size: 12))
                .foregroundColor(.secondary)
        }
    }
}

struct SmallWidgetView: View {
    let brews: [BrewData]

    var body: some View {
        WidgetContainer(alignment: .topLeading) {
            WidgetHeader()

            ForEach(brews) { brew in
                BrewRow(brew: brew, isCompact: true)
            }

            Spacer()
        }
    }
}

struct MediumWidgetView: View {
    let brews: [BrewData]

    var body: some View {
        WidgetContainer(alignment: .topLeading) {
            WidgetHeader()

            ForEach(brews) { brew in
                BrewRow(brew: brew, isCompact: false)
            }

            Spacer()
        }
    }
}

struct LargeWidgetView: View {
    let brews: [BrewData]

    var body: some View {
        WidgetContainer(alignment: .topLeading, spacing: 12) {
            WidgetHeader()

            ForEach(brews) { brew in
                BrewRow(brew: brew, isCompact: false)
            }

            Spacer()
        }
    }
}

struct WidgetHeader: View {
    var body: some View {
        Text("KombuTime")
            .font(.system(size: 16, weight: .bold))
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
