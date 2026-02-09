# Antar - Device Info

Antar is a comprehensive Android application designed to provide detailed insights into your device's hardware and software. It features a modern, fluid user interface built with Jetpack Compose, offering real-time monitoring and in-depth specifications.

## Features

- **Dashboard**: A quick overview of your device's health, including RAM usage, storage status, and battery level.
- **Device Information**: Detailed specs about your device model, manufacturer, brand, and hardware identifiers.
- **System Details**: Information about Android version, security patch level, kernel version, and system uptime.
- **CPU Monitoring**: Real-time CPU frequency tracking and SOC details.
- **Battery Analytics**: Deep dive into battery health, capacity, voltage, temperature, and charging status.
- **Storage & RAM**: Visual representation of memory usage and internal storage availability.
- **Sensors**: List of all available hardware sensors on the device.
- **App Management**: Overview of installed applications.
- **Network & Display**: Insights into network connectivity and display properties.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Koin
- **Asynchronous Programming**: Kotlin Coroutines & Flow
- **Navigation**: Compose Navigation
- **Local Monitoring**: Android System APIs & Broadcast Receivers

## Performance Optimizations

- **Splash Screen API**: Utilizes the modern Android Splash Screen API to mask initial data loading.
- **Efficient Data Flow**: Optimized repository layer with flow conflation and caching to prevent UI lag during real-time updates.
- **Smooth UI**: Built with performance in mind, ensuring 60 FPS scrolling and smooth transitions.

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the app on an Android device (API 24+).

---
*Developed with focus on efficiency and user experience.*
