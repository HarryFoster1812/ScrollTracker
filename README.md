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

## 🧾 Permissions
| Permission | Why it's needed |
| ---------- | --------------- | 
| Accessibility Service |	To monitor scroll events in other apps |
| Ignore Battery Optimizations | To prevent the system from killing the app |

## 🧪 Future Improvements / Roadmap
Here are some ideas to expand the project:
- Detailed App Breakdown (Show a line graph of a specfific app over time)
- Migrate to SQLite
- Add more themes
- Add text and style the persistant notification

## 🛠️ Tech Stack
- Language: Java
- Charts: MPAndroidChart
- Persistence: Jackson (JSON serialization)
- Permissions: Android Accessibility API, Battery Optimization API

## 🐞 Known Issues
- Scroll tracking accuracy can vary on apps using custom views.
- Scroll data is not tracked when Accessibility Service is turned off (Not really an issue more of a limitaion).
- Certain Android OEMs (e.g., Xiaomi, Huawei) may still kill background services even with battery optimizations disabled and a foreground service.
- Certain apps do not send ScrollEvents which makes calculating scoll distance to unreliable and inaccurate

