# Expense Manager

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**A modern, feature-rich financial management application built with Kotlin and Jetpack Compose**

[Report Bug](../../issues) • [Request Feature](../../issues)

</div>

---

## About

Expense Manager is a powerful, intuitive financial management application designed to help users track expenses, manage budgets, and gain insights into their spending habits. Built entirely with modern Android development practices, the app provides a seamless user experience with beautiful Material Design 3 UI.

### Key Highlights

- **100% Kotlin** codebase with modern best practices
- **MVVM Architecture** for clean, maintainable code
- **Jetpack Compose** for declarative UI development
- **Offline-First** design with local database storage
- **Material Design 3** following latest design guidelines

---

## Features

### Expense Tracking
- Add, edit, and delete expenses with ease
- Categorize expenses (Food, Transport, Entertainment, Bills, Shopping, etc.)
- Attach notes and descriptions to transactions
- Support for multiple currencies
- Date and time tracking for each expense
- Recurring expense management

### Budget Management
- Set monthly/weekly budgets for different categories
- Real-time budget tracking with visual progress indicators
- Budget alerts and notifications
- Spending limit warnings
- Historical budget performance tracking

### Analytics & Insights
- Interactive charts and graphs using modern chart libraries
- Expense trends and spending patterns analysis
- Category-wise spending breakdown
- Monthly and yearly financial reports
- Time-based spending comparisons
- Export data to CSV format

### User Experience
- Beautiful Material Design 3 UI with dynamic theming
- Dark mode support with seamless switching
- Smooth animations and transitions using Compose
- Intuitive gesture-based navigation
- Customizable color themes
- Responsive layouts for different screen sizes

### Privacy & Security
- Local data storage with Room Database (no cloud dependency)
- Data encryption for sensitive information
- Backup and restore functionality
- No ads, no third-party trackers
- Complete offline functionality

---

## Tech Stack

### Core Technologies
- **Language:** Kotlin 1.9+
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM (Model-View-ViewModel)
- **Minimum SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

### Jetpack Libraries
- **Room Database** - Local data persistence with SQLite
- **ViewModel** - UI state management and lifecycle awareness
- **LiveData/Flow** - Reactive data streams for UI updates
- **Navigation Component** - Type-safe navigation with Compose
- **DataStore** - Modern preferences storage (replaces SharedPreferences)
- **WorkManager** - Scheduled background tasks for reminders

### Asynchronous Programming
- **Kotlin Coroutines** - Structured concurrency for async operations
- **Flow** - Reactive streams for data observation
- **StateFlow** - State management in ViewModels

### Additional Libraries
- **Material Design 3** - Latest Material components for Compose
- **Accompanist** - Compose utilities for system UI and more
- **MPAndroidChart / Vico** - Beautiful charts and data visualization
- **Coil** - Image loading and caching

### Dependency Injection
- **Hilt/Dagger** - Compile-time dependency injection

### Testing
- **JUnit 4/5** - Unit testing framework
- **Espresso** - UI testing for Android
- **Mockito/MockK** - Mocking framework for unit tests
- **Truth** - Fluent assertions library
- **Turbine** - Testing Kotlin Flow

---

## Architecture

The app follows **Clean Architecture** principles with **MVVM pattern**:

```
app/
├── data/
│   ├── local/
│   │   ├── dao/              # Room DAOs
│   │   ├── entities/         # Database entities
│   │   └── database/         # Database instance
│   ├── repository/           # Repository implementations
│   └── models/               # Data models
│
├── domain/
│   ├── usecases/             # Business logic
│   ├── repository/           # Repository interfaces
│   └── models/               # Domain models
│
├── presentation/
│   ├── ui/
│   │   ├── screens/          # Composable screens
│   │   ├── components/       # Reusable UI components
│   │   └── theme/            # Material theming
│   ├── viewmodel/            # ViewModels
│   └── navigation/           # Navigation graphs
│
├── di/                       # Dependency injection modules
└── util/                     # Utility classes and extensions
```

### Architecture Benefits
- **Separation of Concerns** - Each layer has a specific responsibility
- **Testability** - Easy to unit test business logic independently
- **Maintainability** - Clear structure makes code easy to understand
- **Scalability** - Simple to add new features without breaking existing code
- **Reusability** - Components can be reused across the app

### Data Flow
```
UI Layer (Composables) 
    ↓
ViewModel (State Management)
    ↓
UseCase (Business Logic)
    ↓
Repository (Data Source Abstraction)
    ↓
Local Database (Room)
```

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 11 or higher
- Android SDK with minimum API level 24
- Kotlin 1.9.0 or higher
- Gradle 8.0+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Chinmay-tayade/Expense-Manager.git
cd Expense-Manager
```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository folder

3. **Sync Gradle**
   - Let Gradle sync and download all dependencies
   - This may take a few minutes on first run

4. **Build the project**
```bash
./gradlew build
```

5. **Run the app**
   - Connect an Android device via USB (with USB debugging enabled)
   - Or start an Android emulator
   - Click Run > Run 'app' or press Shift + F10

---

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/`

---

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Generate Code Coverage Report
```bash
./gradlew jacocoTestReport
```

Coverage reports will be available in `app/build/reports/jacoco/`

### Test Structure
```
test/                    # Unit tests
└── java/
    ├── viewmodel/      # ViewModel tests
    ├── repository/     # Repository tests
    └── usecase/        # UseCase tests

androidTest/            # Instrumentation tests
└── java/
    ├── ui/            # UI tests with Compose
    └── dao/           # Database tests
```

---

## Code Style

This project follows the official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) and [Android Kotlin style guide](https://developer.android.com/kotlin/style-guide).

### Key Conventions
- 4 spaces for indentation
- Maximum line length: 120 characters
- Use camelCase for variable and function names
- Use PascalCase for class names
- Organize imports alphabetically

### Code Formatting
```bash
# Format code with ktlint
./gradlew ktlintFormat

# Check code style
./gradlew ktlintCheck
```

---

## Contributing

Contributions are welcome! Here's how you can help:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Contribution Guidelines
- Write meaningful commit messages
- Add unit tests for new features
- Update documentation if needed
- Follow the existing code style
- Keep pull requests focused on a single feature

---

## Roadmap

- [ ] Cloud sync with Firebase
- [ ] Multiple account support
- [ ] Bill splitting feature
- [ ] Widget support for quick expense entry
- [ ] Expense receipt scanning with ML
- [ ] Financial goal tracking
- [ ] Investment tracking
- [ ] Multi-language support
- [ ] Wear OS companion app

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Chinmay Tayade

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## Author

**Chinmay Tayade**

- GitHub: [@Chinmay-tayade](https://github.com/Chinmay-tayade)
- LinkedIn: [chinmaytayade](https://linkedin.com/in/chinmaytayade)
- Email: chinmaytayade@outlook.com

---

## Acknowledgments

- Android Jetpack team for amazing libraries
- Material Design team for beautiful design components
- Kotlin team for the fantastic language
- Android developer community for continuous support

---

## Support

If you find this project helpful, please consider:
- Giving it a star on GitHub
- Sharing it with others
- Contributing to the codebase
- Reporting bugs or suggesting features

---

<div align="center">

**Built with Kotlin and Jetpack Compose**

Made by Chinmay Tayade

</div>
