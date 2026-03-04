# KombuTime Feature Ideas

This document contains curated feature suggestions that align with KombuTime's minimalist philosophy. All ideas here maintain the "simple brewing tracker" core mission.

**For implemented features, see `FEATURES.md`.**

## 🎯 Status Legend

- ⭐ **High Priority** - High impact, low complexity, strongly aligned
- 🎨 **Polish** - Visual/UX improvements
- 🔔 **Notifications** - Notification system enhancements
- 📊 **Data** - Minimal data tracking (no heavy analytics)
- 🔧 **Technical** - Infrastructure improvements
- 💡 **Experimental** - Interesting ideas, needs validation

---

## ⭐ High Priority Features

### 1. Swipe Gestures
**Status**: Not implemented
**Effort**: Medium
**Impact**: High (UX improvement)

**Description:**
- **Swipe right** on brew card → Complete/advance to next stage
- **Swipe left** on brew card → Delete brew
- More intuitive than current tap-to-settings flow
- Common mobile UX pattern

**Implementation Notes:**
```kotlin
// Use Modifier.swipeable or Accompanist Swipe library
// Add to BrewsView.kt Row items
```

**Why it fits:**
- Improves existing functionality without adding features
- Makes common actions faster
- No new concepts to learn

---

### 2. Home Screen Widget
**Status**: Not implemented
**Effort**: High
**Impact**: High

**Description:**
- iOS/Android widgets showing active brews
- Display progress bars and days remaining
- "At-a-glance" view without opening app
- Tap widget to open app

**Variants:**
- Small: Shows most urgent brew
- Medium: Shows 2-3 brews
- Large: Shows all brews

**Why it fits:**
- Perfect for "quick check" use case mentioned in README
- No new features, just better access to existing data
- Aligns with "at-a-glance overview" feature

---

## 🎨 Polish & UX Improvements

### 4. Visual State Indicators
**Status**: Not implemented
**Effort**: Low
**Impact**: Medium

**Description:**
- Show "✓ Ready!" when brew is complete (days remaining = 0)
- Subtle pulsing animation for overdue brews
- Success confetti animation when completing a brew (optional, toggleable)
- Better empty state when no brews exist

**Why it fits:**
- Makes app more delightful to use
- Clearer visual communication
- Doesn't add complexity

---

### 5. Empty State Design
**Status**: Not implemented
**Effort**: Low
**Impact**: Low

**Description:**
- Friendly message when brew list is empty
- "Start your first brew!" with illustration
- Simple call-to-action

**Example:**
```
🫙
No active brews

Tap "Add" to start your first kombucha!
```

**Why it fits:**
- Better onboarding experience
- Encourages first use

---

### 6. Long Press for Settings
**Status**: Not implemented
**Effort**: Low
**Impact**: Medium

**Description:**
- Long-press brew card → Opens settings directly
- Faster than current tap-settings-icon flow
- Discoverable via haptic feedback

**Why it fits:**
- Improves existing feature access
- Common mobile pattern

---

## 🔔 Notification Enhancements

### 7. Notification Quick Actions
**Status**: Not implemented
**Effort**: Medium
**Impact**: High

**Description:**
- Add action buttons to notifications
- iOS: "Complete" | "Extend 1 Day"
- Android: Same via notification actions
- Complete brew without opening app

**Implementation Notes:**
```kotlin
// Android: PendingIntent for actions
// iOS: UNNotificationAction in ContentView.swift
```

**Why it fits:**
- Reduces friction for common actions
- Leverages existing notification system
- No new screens or complexity

---

### 8. Pre-Notification Reminders
**Status**: Not implemented
**Effort**: Low
**Impact**: Low-Medium

**Description:**
- Optional reminder 1 day before brew completes
- "Your [BrewName] is almost ready! (Tomorrow at 9:00 AM)"
- Helps users prepare bottles/flavoring
- Toggleable in settings

**Why it fits:**
- Small enhancement to notification system
- Practical benefit
- Simple on/off toggle

---

### 9. Smart Notification Time
**Status**: Not implemented
**Effort**: Low
**Impact**: Low

**Description:**
- Suggest notification time based on user's most active app usage time
- "You usually check the app around 10 AM. Set notifications for 10 AM?"
- One-time suggestion

**Why it fits:**
- Smart default
- Improves relevance
- Still simple

---

## 📊 Minimal Data Features

### 10. Simple Completion Counter
**Status**: Not implemented
**Effort**: Low
**Impact**: Low

**Description:**
- Display total completed brews: "23 brews completed! 🎉"
- Shown at bottom of main screen or in settings
- No detailed history, just a motivating number
- Increments when completing second fermentation
- Optional: Hide via settings toggle

**Why it fits:**
- Gamification without complexity
- Single number, no charts/graphs
- Aligns with "history-free" philosophy

---

### 11. Last Brew Date
**Status**: Not implemented
**Effort**: Low
**Impact**: Low

**Description:**
- "Last brew started: 3 days ago"
- Displayed at top of brew list
- Helps plan when to start next brew
- Only shows date of most recent brew added

**Why it fits:**
- Practical planning aid
- Minimal data tracking
- Single data point

---

### 12. Active Brew Summary
**Status**: Not implemented
**Effort**: Low
**Impact**: Low

**Description:**
- Header showing: "3 active brews (1 F1, 2 F2)"
- Quick overview before scrolling
- Especially useful with many brews

**Why it fits:**
- Better organization
- No new data, just reorganization

---

## 🔧 Technical Improvements

### 13. Backup & Restore
**Status**: Not implemented
**Effort**: Medium
**Impact**: Medium

**Description:**
- Export brews as JSON file
- Import from JSON file
- Manual backup (no auto-sync)
- Useful for device switching

**Implementation:**
- Share sheet integration (iOS/Android)
- File picker for import
- Simple JSON format

**Why it fits:**
- Addresses user pain point (losing data on uninstall)
- Maintains local-only philosophy
- No cloud dependency

---

### 14. iCloud/Google Drive Backup
**Status**: Not implemented
**Effort**: High
**Impact**: Low

**Description:**
- Automatic backup to platform storage
- iOS: iCloud Drive
- Android: Google Drive
- Restore on reinstall

**Why it fits:**
- Seamless backup
- No user accounts needed
- Platform-native

**Concerns:**
- Adds complexity
- May conflict with "simple" philosophy

---

### 15. Dark Mode Refinement
**Status**: Partial (colors defined, needs testing)
**Effort**: Low
**Impact**: Medium

**Description:**
- Ensure perfect contrast in dark mode
- Test all colors for accessibility
- Color-blind friendly palette
- WCAG AAA compliance

**Why it fits:**
- Accessibility improvement
- Polishes existing feature

---

## 💡 Optional Enhancements (Toggleable)

### 16. Brew Templates
**Status**: Not implemented
**Effort**: Medium
**Impact**: Low-Medium

**Description:**
- Save favorite configurations (max 3-5)
- Examples: "Classic 12/3", "Quick Summer 7/2", "Winter 14/4"
- One-tap to start brew with saved settings
- Simple list in settings

**Why it fits:**
- Speeds up common workflows
- Optional (hidden by default?)
- Limited to prevent bloat

---

### 17. Custom Emoji/Icons per Brew
**Status**: Not implemented
**Effort**: Low
**Impact**: Low

**Description:**
- Optional emoji picker when creating brew
- Displayed next to brew name
- Examples: 🍓🫐🍋🥭🍑
- Visual differentiation

**Why it fits:**
- Fun personalization
- No additional screens
- Simple emoji picker

---

### 18. Quiet Hours
**Status**: Not implemented
**Effort**: Low
**Impact**: Low

**Description:**
- Set time range for no notifications (e.g., 10 PM - 7 AM)
- Notifications delayed until quiet hours end
- Simple time picker in settings

**Why it fits:**
- Practical benefit
- Respects user preferences
- Single setting

---

## ❌ Explicitly Rejected Ideas

These ideas violate the minimalist philosophy:

### Not Aligned
- **Detailed brew history** - Too complex, database required
- **Ingredient tracking** - Recipe management scope creep
- **Taste notes/ratings** - Logging feature bloat
- **Photo uploads** - Adds storage complexity
- **Social features** - Completely different product
- **Cloud sync with accounts** - Over-engineered
- **Brew analytics/graphs** - Data analysis bloat
- **Recipe suggestions** - Content management burden
- **Temperature tracking** - Sensor integration complexity
- **pH tracking** - Lab equipment integration
- **Batch comparison** - Requires history database
- **Fermentation curves** - Complex data visualization

---

## 📋 Implementation Priority

### Phase 1: Quick Wins (1-2 weeks)
1. 🎨 Empty state design
2. 🎨 Visual state indicators (Ready! checkmark)
3. 📊 Simple completion counter

### Phase 2: UX Improvements (2-4 weeks)
4. ⭐ Swipe gestures
5. 🎨 Long press for settings
6. 🔔 Pre-notification reminders
7. 🔧 Dark mode refinement

### Phase 3: Major Features (1-2 months)
8. ⭐ Home screen widgets
9. 🔔 Notification quick actions
10. 🔧 Backup & restore

### Phase 4: Nice-to-Haves (Future)
11. 💡 Brew templates
12. 💡 Custom emoji per brew
13. 💡 Quiet hours
14. 📊 Last brew date indicator

---

## 💭 Feature Request Guidelines

When suggesting new features:

1. **Ask: Does it track fermentation or send notifications?**
   - If no → Probably doesn't fit

2. **Ask: Can it be explained in one sentence?**
   - If no → Too complex

3. **Ask: Does it require a database?**
   - If yes → Think harder about simplification

4. **Ask: Will 80% of users use it weekly?**
   - If no → Maybe optional or reject

5. **Ask: Does it maintain the "open app, tap once" simplicity?**
   - If no → Needs redesign

---

## 🤝 Contributing Ideas

To suggest a feature:

1. Check this list first
2. Ensure it aligns with philosophy (see AGENT.md)
3. Open GitHub issue with:
   - Clear description
   - Why it fits the minimalist approach
   - Rough implementation idea
   - Impact vs. effort estimate

**Remember**: The best feature is often the one you don't add. KombuTime's strength is its simplicity.

---

## 📚 Reference

- **Implemented features**: `docs/FEATURES.md`
- **Project philosophy**: `AGENT.md`
- **Development guide**: `docs/DEVELOPMENT.md`
- **Main README**: `README.md`
