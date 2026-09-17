# Expense Manager

A daily expense tracker for Android, built with **Kotlin** and **Jetpack Compose**.

Track expenses, view daily totals, and generate reports — all stored locally on-device with no cloud dependency.

## Features

- **Expense tracking** — add, edit and delete expenses with a title, amount, category, note and optional receipt photo.
- **Categories** — four built-in categories: Staff, Travel, Food and Utility.
- **Daily view** — see expenses grouped by date and the total spent per day.
- **Reports** — charts (drawn with Compose `Canvas`) plus the ability to export the report as a PDF and share it.
- **Offline-first** — all data is stored locally in a Room database.

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM with the Repository pattern
- **DI:** Hilt
- **Local storage:** Room
- **Navigation:** Navigation Compose

## Screens

- Splash screen
- Expense list (grouped by date)
- Expense entry / edit
- Expense details
- Expense report (charts, PDF export, share)

## Build

Requires Android Studio and JDK 17.

```bash
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/`.

## License

MIT — see [LICENSE](LICENSE).
