# Personal Task Manager  nowoczesny

> A modern Android application for managing daily tasks, built with Clean Architecture and MVVM.

<br>

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF.svg?style=for-the-badge&logo=kotlin)
![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=for-the-badge&logo=android)
![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=for-the-badge)

<br>

---

## ✨ Features

- **Full CRUD Functionality:** Create, read, update, and delete tasks with ease.
- **Task Reminders:** Schedule precise notifications for important tasks using `AlarmManager`.
- **Automatic Background Sync:** Utilizes `WorkManager` to periodically sync local data with a remote API, ensuring data consistency.
- **Persistent User Preferences:** Saves user settings (like sort order) across app launches using **Jetpack DataStore**.
- **Dynamic Sorting:** Easily sort tasks by newest or oldest first.
- **Modern & Intuitive UI:** A clean, user-friendly interface designed with **Material 3** guidelines.

---

## 🏛️ Architecture

This project is built upon a multi-module **Clean Architecture**, which separates the code into independent layers to improve scalability, maintainability, and testability.

> **UI → Domain ← Data**

- **`:presentation` (UI Layer):** Built with **XML** and the **MVVM** pattern. It observes state from ViewModels and is responsible for all user interactions.
- **`:domain` (Domain Layer):** The core of the application. Contains business logic, models, and use cases. This is a pure Kotlin module with no dependencies on the Android framework.
- **`:data` (Data Layer):** Acts as the Single Source of Truth. It manages data from various sources, including the network API and local database, and exposes it to the domain layer.

---

## 🛠️ Tech Stack & Libraries

This project leverages a range of modern tools and libraries from the Android ecosystem.

| Category | Technologies Used |
| :--- | :--- |
| **Architecture** | Clean Architecture, MVVM (Model-View-ViewModel) |
| **Languages** | Kotlin (Primary), Java |
| **UI** | XML, Material 3 |
| **Asynchronous** | Coroutines, Flow, LiveData |
| **Dependency Injection** | Hilt |
| **Networking** | Retrofit |
| **Database** | Room |
| **Background Jobs** | WorkManager |
| **Settings Persistence**| Jetpack DataStore |
| **Task Scheduling** | AlarmManager |

---

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

- Android Studio (latest stable version)
- Git

### Installation & Setup

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/Pooya-Jannati-Poor/Personal-Task-Manager.git
    ```
2.  **Open with Android Studio:**
    - Launch Android Studio.
    - Select `File` > `Open`.
    - Navigate to the directory where you cloned the project and select it.

3.  **Build & Run:**
    - Allow Android Studio to download all the necessary Gradle dependencies.
    - Select a target device (either a physical device or an emulator).
    - Click the **Run** button (▶️) to build and install the application.
