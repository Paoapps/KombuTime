# Feature Migration Process

## When a Feature is Implemented

**DO NOT** just mark it as "✅ IMPLEMENTED" in `FEATURE_IDEAS.md`

**INSTEAD**, follow this 2-step process:

---

## Step 1: Remove from FEATURE_IDEAS.md

**Remove the entire feature section** from `docs/FEATURE_IDEAS.md`, including:
- The feature title
- Status line
- Effort/Impact
- Description
- Implementation notes
- "Why it fits" section

**Also update:**
- Renumber remaining features in that section
- Remove from implementation priority list at bottom
- Any cross-references to that feature number

---

## Step 2: Add to FEATURES.md

**Add a new section** to `docs/FEATURES.md` with:

### Required Information:
1. **Feature Name** (as heading)
2. **Status**: ✅ Implemented (with date)
3. **Description**: Clear explanation of what it does
4. **Capabilities**: Bullet list of what users can do
5. **Implementation Details**:
   - File names where implemented
   - Key classes/functions
   - Platform-specific notes (Android/iOS differences)
   - Any important technical details

### Template:
```markdown
### X. [Feature Name]
**Status**: ✅ Implemented ([Month Year])

**Description:**
[1-2 sentence summary]

**Capabilities:**
- [What users can do]
- [Key functionality]
- [Important behaviors]

**Implementation:**
- `FileName.kt`: [What it does]
- Platform-specific details
- Key technical notes
```

---

## Example: Notification Quick Actions

### ❌ WRONG (What was done before):
```markdown
### 7. Notification Quick Actions
**Status**: ✅ **IMPLEMENTED** (March 2026)
[...kept in FEATURE_IDEAS.md...]
```

### ✅ CORRECT (What should be done):

**In FEATURE_IDEAS.md:**
- Entire section removed
- Feature #8 becomes #7
- Feature #9 becomes #8
- etc.
- Remove from priority list

**In FEATURES.md:**
- Add comprehensive section under "Notifications"
- Include all implementation details
- Document both Android and iOS specifics
- List actual file names and methods

---

## Quick Checklist

When implementing a feature:

- [ ] Remove entire feature section from `FEATURE_IDEAS.md`
- [ ] Renumber subsequent features in that category
- [ ] Update implementation priority list
- [ ] Add comprehensive section to `FEATURES.md`
- [ ] Include actual file names and implementation details
- [ ] Document platform-specific differences (if any)
- [ ] Update table of contents (if exists)

---

## Why This Matters

1. **FEATURE_IDEAS.md** = Future roadmap (unimplemented)
2. **FEATURES.md** = Current documentation (implemented)

Keeping them separate ensures:
- Clear distinction between "planned" and "done"
- Users know what the app currently does
- Developers know what's still on the roadmap
- No confusion about implementation status

---

## Files to Check

After implementing a feature, verify:
- [ ] `docs/FEATURE_IDEAS.md` - Feature removed, numbering updated
- [ ] `docs/FEATURES.md` - Feature added with full details
- [ ] `README.md` - Feature list updated (if mentioned)
- [ ] Release notes or changelog (if maintained)
