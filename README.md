# 💰 Expense Manager

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Downloads](https://img.shields.io/badge/Downloads-75K+-00C853?style=for-the-badge)
![Rating](https://img.shields.io/badge/Rating-4.7%20★-FFD700?style=for-the-badge)

**A modern, feature-rich financial management application built with Kotlin and Jetpack Compose**

[Download on Play Store](#) • [View Screenshots](#screenshots) • [Report Bug](../../issues) • [Request Feature](../../issues)

</div>

---

## 📱 About

Expense Manager is a powerful, intuitive financial management application designed to help users track expenses, manage budgets, and gain insights into their spending habits. Built entirely with modern Android development practices, the app provides a seamless user experience with beautiful Material Design 3 UI.

### ✨ Key Highlights
- **4.7 Star Rating** with positive user reviews
- **100% Kotlin** codebase with modern best practices
- **MVVM Architecture** for clean, maintainable code
- **Offline-First** design with local database storage

---

## 🚀 Features

### 💸 Expense Tracking
- Add, edit, and delete expenses with ease
- Categorize expenses (Food, Transport, Entertainment, Bills, etc.)
- Attach notes and receipts to transactions
- Support for multiple currencies
- Recurring expense management

### 📊 Budget Management
- Set monthly/weekly budgets for different categories
- Real-time budget tracking with visual indicators
- Budget alerts and notifications
- Spending limit warnings

### 📈 Analytics & Insights
- Interactive charts and graphs
- Expense trends and patterns
- Category-wise spending analysis
- Monthly/yearly financial reports
- Export data to CSV/PDF

### 🎨 User Experience
- Beautiful Material Design 3 UI
- Dark mode support
- Smooth animations and transitions
- Intuitive navigation
- Customizable themes

### 🔒 Privacy & Security
- Local data storage (no cloud dependency)
- Biometric authentication support
- Data backup and restore
- No ads, no trackers

---

## 🛠️ Tech Stack

### Core Technologies
- **Language:** Kotlin 100%
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM (Model-View-ViewModel)
- **Dependency Injection:** Hilt/Dagger

### Jetpack Libraries
- **Room Database** - Local data persistence
- **ViewModel** - UI state management
- **LiveData/Flow** - Reactive data streams
- **Navigation Component** - App navigation
- **DataStore** - Preferences storage
- **WorkManager** - Background tasks

### Additional Libraries
- **Coroutines** - Asynchronous programming
- **Material Design 3** - Modern UI components
- **Accompanist** - Compose utilities
- **Charts Library** - Data visualization
- **Coil** - Image loading

### Testing
- **JUnit** - Unit testing
- **Espresso** - UI testing
- **Mockito** - Mocking framework
- **Truth** - Assertions library

---

## 🏗️ Architecture

The app follows **Clean Architecture** principles with **MVVM pattern**:

```
app/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── entities/
│   │   └── database/
│   ├── repository/
│   └── models/
├── domain/
│   ├── usecases/
│   └── repository/
├── presentation/
│   ├── ui/
│   │   ├── screens/
│   │   ├── components/
│   │   └── theme/
│   ├── viewmodel/
│   └── navigation/
└── di/
```

### Architecture Highlights
- **Separation of Concerns** - Clear layer boundaries
- **Dependency Rule** - Dependencies point inward
- **Testability** - Easy to unit test each layer
- **Scalability** - Easy to add new features
- **Maintainability** - Clean, organized codebase

---

## 📸 Screenshots

<div align="center">

| Home Screen | Add Expense | Analytics |
|-------------|-------------|-----------|
| ![Home](screenshots/home.png) | ![Add](screenshots/add.png) | ![Analytics](screenshots/analytics.png) |

| Budget | Categories | Settings |
|--------|------------|----------|
| ![Budget](screenshots/budget.png) | ![Categories](screenshots/categories.png) | ![Settings](screenshots/settings.png) |

</div>

---

## 🎯 Getting Started

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or higher
- JDK 11 or higher
- Android SDK with minimum API level 24 (Android 7.0)
- Kotlin 1.9.0 or higher

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Chinmay-tayade/Expense-Manager.git
cd Expense-Manager
```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository

3. **Build the project**
   - Let Gradle sync and download dependencies
   - Build > Make Project

4. **Run the app**
   - Connect an Android device or start an emulator
   - Run > Run 'app'

---

## 📦 Download

<div align="center">

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" alt="Get it on Google Play" height="80">](https://play.google.com/store)

**Available on Google Play Store**

</div>

---

## 🎨 Design System

The app uses Material Design 3 with custom theming:

- **Primary Color:** `#6750A4` (Purple)
- **Secondary Color:** `#625B71` (Dark Purple)
- **Tertiary Color:** `#7D5260` (Rose)
- **Typography:** Roboto, Roboto Condensed
- **Shapes:** Rounded corners with 16dp radius

---

## 🧪 Testing

Run tests with:

```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest

# Code coverage
./gradlew jacocoTestReport
```

**Test Coverage:** 80%+

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Chinmay Tayade**

- GitHub: [@Chinmay-tayade](https://github.com/Chinmay-tayade)
- LinkedIn: [chinmaytayade](https://linkedin.com/in/chinmaytayade)
- Email: chinmaytayade@outlook.com

---

## 🙏 Acknowledgments

- Material Design team for the amazing design system
- Android Developer community for valuable resources
- All contributors and users who provided feedback

---

## 📊 Project Stats

![GitHub stars](https://img.shields.io/github/stars/Chinmay-tayade/Expense-Manager?style=social)
![GitHub forks](https://img.shields.io/github/forks/Chinmay-tayade/Expense-Manager?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/Chinmay-tayade/Expense-Manager?style=social)

---

<div align="center">

**If you find this project useful, please consider giving it a ⭐**

Made with ❤️ by Chinmay Tayade

</div>
