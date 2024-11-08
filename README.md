# The App

# The Story

After brewing kombucha for about two years with a few batches under my belt, I realized I needed a simple way to keep track of my brewing schedule. I’m not a fan of overcomplicating things, and initially, I just tried to remember when I started each batch. When I began brewing multiple batches, I used a repeating calendar event to stay organized. But as my brewing became more frequent, this system started to fall out of sync, especially when life got in the way, and I started a batch a day later than planned.

So, I created a super simple app, Kombu Time, to keep track of my brewing process. It’s free, open-source, and designed to do just one thing: track when you start your batches and notify you when it’s time to start or end the second fermentation. No complicated features like ingredients, notes, ratings, or history—just what’s needed to make brewing easier. Once you set it up (like deciding how many days you want for fermentation), starting a new batch is as easy as tapping a button.

If you’re looking for a straightforward kombucha tracking app like I was, feel free to check it out:

iOS: Kombu Time on the App Store
Android: Kombu Time on Google Play

Happy brewing!

# The project

This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.

* `/iosApp` contains iOS applications.

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

# Sponsor

If you like the app or the project, consider buying me a cup of coffee:
<a href="https://www.buymeacoffee.com/lwesterhoff" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a>
