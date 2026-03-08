# Kombu Time Website

This directory contains the public-facing website for Kombu Time, hosted on GitHub Pages.

## Structure

```
docs/
├── index.html          # Main landing page (English)
├── index-nl.html       # Dutch localized landing page
├── index-de.html       # German localized landing page
├── privacy.html        # Privacy policy page
├── styles.css          # Stylesheet with responsive design
├── script.js           # JavaScript for interactions
├── CNAME               # Custom domain configuration
├── README.md           # This file
└── assets/             # Images, icons, and other assets
    ├── app-icon.png
    ├── hero-image.png
    ├── app-store-badge.svg
    ├── google-play-badge.png
    └── og-image.png
```

## Localization

The website is available in three languages:
- **English**: `index.html`
- **Dutch**: `index-nl.html`
- **German**: `index-de.html`

All pages share the same `styles.css` and `script.js` files.

## Assets Needed

To complete the website, you'll need to add the following assets to the `assets/` folder:

1. **app-icon.png** - App icon (512x512px recommended)
2. **hero-image.png** - Screenshot or mockup of the app
3. **app-store-badge.svg** - App Store download badge
4. **google-play-badge.png** - Google Play store download badge
5. **og-image.png** - Open Graph image for social media sharing (1200x630px)

You can download the official badges from:
- App Store: https://developer.apple.com/app-store/marketing/guidelines/
- Google Play: https://play.google.com/intl/en_us/badges/

## GitHub Pages Setup

### Enabling GitHub Pages

1. Go to your repository settings on GitHub
2. Navigate to "Pages" in the left sidebar
3. Under "Source", select the branch you want to deploy (usually `main`)
4. Select the `/docs` folder as the source directory
5. Click "Save"

Your site will be published at: `https://paoapps.github.io/KombuTime/`

### Custom Domain Setup

The `CNAME` file is configured for `kombutime.app`. To use this custom domain:

1. Add the custom domain in your repository settings under "Pages"
2. Configure your DNS provider to point to GitHub Pages:
   - Add an `A` record pointing to GitHub's IP addresses:
     - 185.199.108.153
     - 185.199.109.153
     - 185.199.110.153
     - 185.199.111.153
   - Or add a `CNAME` record pointing to `paoapps.github.io`
3. Enable "Enforce HTTPS" in repository settings

## Local Development

To preview the website locally, you can use any simple HTTP server:

### Using Python
```bash
cd docs
python3 -m http.server 8000
```

Then open `http://localhost:8000` in your browser.

### Using Node.js
```bash
cd docs
npx http-server
```

## Design

The website uses:
- **Color Scheme**: Earthy tones (browns and tans) appropriate for a kombucha brewing app
  - Primary: `#8B7355`
  - Accent: `#D4A574`
- **Typography**: System fonts for native feel
- **Responsive Design**: Mobile-first approach with breakpoints at 640px and 968px
- **Dark Mode**: Automatic support based on system preferences

## Features

- ✅ Multi-language support (EN, NL, DE)
- ✅ Responsive design for all screen sizes
- ✅ Dark mode support
- ✅ Smooth scrolling navigation
- ✅ Fade-in animations on scroll
- ✅ SEO optimized with meta tags and structured data
- ✅ Privacy policy page
- ✅ Download links to App Store and Google Play Store

## Maintenance

### Updating Content

- **App Store Description**: Update content in the respective language files
- **Features**: Edit the features section in each HTML file
- **FAQ**: Modify the FAQ section in each HTML file
- **Privacy Policy**: Update `privacy.html`

### Adding New Languages

1. Duplicate `index.html` and rename it (e.g., `index-fr.html` for French)
2. Translate all text content
3. Update the language switcher in the navigation
4. Add the new language to the README

## Project Documentation

For development documentation, technical specifications, and feature details, see the `project-docs/` folder in the root of the repository.

## License

© 2026 Paoapps. All rights reserved.
