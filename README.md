

# AutoLarm

**A smarter alarm clock that works backward from when you need to arrive.**

AutoLarm is an Android alarm clock application that helps users plan their mornings by automatically calculating when they need to wake up based on their desired arrival time, commute duration, morning routine, and additional buffer time.

Rather than manually figuring out when to set an alarm, users can define their morning tasks, estimate how long each one takes, and let AutoLarm handle the scheduling.

> **Project Status:** Early development. Features described below are planned and are not yet implemented.

## Features

### Smart Alarm Scheduling

- **Arrival-based alarms:** Set a desired arrival time instead of manually calculating a wake-up time.
- **Automatic wake-up calculation:** Determine when to wake up based on commute time, selected tasks, and buffer time.
- **Multiple alarms:** Create, edit, enable, disable, and delete alarms independently.
- **Pre-alarms:** Configure additional alarms before the calculated wake-up time to help prepare for waking up.
- **Post-alarms:** Configure additional alarms after the get-up alarm as a safeguard against falling back asleep.
- **Customizable ringtones:** Choose different alarm sounds for pre-alarms, get-up alarms, and post-alarms.

### Morning Routine & Task Management

- Create, edit, and delete custom morning tasks.
- Assign an estimated duration to each task.
- Maintain a reusable library of tasks.
- Select which tasks apply to individual alarms.
- Automatically incorporate selected task durations into alarm calculations.
- Add customizable buffer time for unexpected delays.

### Travel Time Integration

- Specify departure and destination locations.
- Choose a transportation mode.
- Manually enter estimated travel duration.
- Optionally integrate with Google Maps Platform to estimate travel time automatically.

### Additional Functionality

- Local storage of alarms, tasks, and preferences.
- Alarm scheduling that functions when the application is closed, subject to Android system restrictions.
- Restoration of scheduled alarms following device restarts.
- Settings for alarm sounds, permissions, and preferences.
- Information and onboarding screen explaining how AutoLarm works.

## How It Works

AutoLarm calculates the required get-up time by subtracting the total preparation and travel duration from the user's desired arrival time.

**Formula:**

`Get-Up Time = Arrival Time - (Travel Time + Task Durations + Buffer Time)`

For example:

| Component | Time |
|---|---|
| Desired arrival | 9:00 AM |
| Commute | 24 minutes |
| Morning tasks | 68 minutes |
| Buffer | 10 minutes |
| **Calculated get-up time** | **7:18 AM** |

Users can optionally configure pre-alarms and post-alarms around this calculated get-up time.

## Technology Stack

AutoLarm is being developed as a native Android application using Kotlin and Jetpack Compose.

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Android Studio | Development environment |
| Jetpack Compose | Declarative user interface |
| Material 3 | UI components and theming |
| MVVM | Application architecture |
| Navigation Compose | Screen navigation |
| Room / SQLite | Persistent storage of alarms and tasks |
| DataStore | Application settings and preferences |
| AlarmManager | Scheduling precise Android alarms |
| BroadcastReceiver | Handling alarm events and device restarts |
| NotificationManager | Alarm notifications and notification channels |
| Google Routes API | Optional travel-time estimation |

**Minimum SDK:** API 26 (Android 8.0 Oreo)

## Architecture

AutoLarm follows an MVVM-inspired layered architecture designed to separate user interface components, application logic, and data management.

### UI Layer

Built using Jetpack Compose, the UI layer contains the application's screens and reusable interface components.

- **Alarms Screen:** Displays alarms, arrival information, selected tasks, and calculated wake-up times.
- **Tasks Screen:** Allows users to create and manage their task library.
- **Settings Screen:** Manages permissions, ringtones, and alarm preferences.
- **Information Screen:** Provides onboarding, tutorials, and application information.

Shared components include the top navigation bar, bottom navigation bar, alarm cards, task cards, and dialogs.

### ViewModel Layer

ViewModels manage UI state and coordinate interactions between screens and application logic.

- `AlarmsViewModel`
- `TasksViewModel`
- `SettingsViewModel`

### Domain Layer

Contains the application's core business logic, including:

- Calculating total preparation time.
- Determining the required wake-up time.
- Generating pre-alarm and post-alarm schedules.
- Managing alarm scheduling and cancellation.
- Validating tasks and alarm configurations.

### Data Layer

Repositories provide a consistent interface for accessing and updating application data.

- `AlarmRepository`
- `TaskRepository`
- `SettingsRepository`
- `MapsRepository` (optional)

Room provides local database persistence, while DataStore manages application preferences.

### Android System Integration

AutoLarm uses Android's native system APIs to schedule alarms, handle notifications, play ringtones, and restore alarms after device restarts.

Alarm scheduling is managed independently of the application's UI so that alarms can function when the application is not actively open, subject to Android's permission and background-execution restrictions.

## Planned Project Structure

The following structure represents the intended organization of the codebase and may evolve during development.

```text
com.kaylinvonbergen.autolarm/
│
├── MainActivity.kt
│
├── ui/
│   ├── screens/
│   │   ├── alarms/
│   │   ├── tasks/
│   │   ├── settings/
│   │   └── info/
│   ├── components/
│   ├── navigation/
│   └── theme/
│
├── viewmodel/
│   ├── AlarmsViewModel.kt
│   ├── TasksViewModel.kt
│   └── SettingsViewModel.kt
│
├── domain/
│   ├── model/
│   └── usecase/
│
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── entity/
│   │   └── AutoLarmDatabase.kt
│   └── repository/
│
├── alarm/
│   ├── AlarmScheduler.kt
│   ├── AlarmReceiver.kt
│   └── BootReceiver.kt
│
└── util/
```

## Getting Started

### Prerequisites

- Android Studio
- Android SDK 26 or higher
- Kotlin and Gradle (configured through Android Studio)
- Android emulator or physical Android device

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/YOUR_USERNAME/AutoLarm.git
   ```

2. Open the project in Android Studio.

3. Allow Gradle to synchronize and download the required dependencies.

4. Select an Android emulator or connected physical device.

5. Build and run the application.

The Google Maps integration, once implemented, will require additional API configuration.

## Permissions

AutoLarm may require the following Android permissions depending on the device version and enabled functionality:

- **Notifications:** Display alarm notifications.
- **Exact alarms:** Schedule precise alarm events where required.
- **Full-screen intents:** Display ringing alarm interfaces where permitted.
- **Boot completed:** Restore alarms after device restarts.
- **Location (optional):** Support location-based travel-time functionality if enabled.

Permissions will be requested only as needed for the relevant functionality.

## Design

AutoLarm uses a dark interface with a minimal visual style.

| Element | Specification |
|---|---|
| Primary background | `#242836` |
| Navigation bars | `#181B25` |
| Cards and containers | White, 15% opacity |
| Borders | 1px white |
| Primary text | White |
| Display font | Khand |

The interface is designed around two primary views: **Alarms** and **Tasks**, with additional Settings and Information screens.

## Future Considerations

Potential enhancements beyond the initial release include:

- Recurring alarms for selected weekdays.
- Dynamic commute estimates based on traffic conditions.
- Automatic wake-up time adjustments when travel conditions change.
- Task ordering and routine templates.
- Additional alarm customization.
- Accessibility improvements and alternative themes.

---

**AutoLarm** — Wake up when you need to, not when you think you should.
