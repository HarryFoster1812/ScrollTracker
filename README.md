# 📱 ScrollTracker
ScrollTracker is an Android application that tracks scroll activity across apps using an Accessibility Service, and visualises the data using charts. It helps users understand their app usage patterns over time.

## ✨ Features

### 📊 Visual Analytics
- Daily: Bar chart showing total scroll distance per app.
- Weekly & Monthly: Line charts aggregating total scroll activity.

### 🧠 Persistent Data Storage
- Data is serialized to internal storage using Jackson.
- Automatically loads saved data on app start.

### 🔐 Permission Handling UI
- Initial setup screen guides users to enable:
- Accessibility Service
- Battery optimization exemption
