# Tech Stack

## Language & Platform
- **Language**: Java (not Kotlin)
- **Platform**: Android (minSdk 24, targetSdk 36, compileSdk 36)
- **Build system**: Gradle with Kotlin DSL (`build.gradle.kts`)
- **Version catalog**: `gradle/libs.versions.toml`

## Key Libraries
| Library | Version | Purpose |
|---|---|---|
| AndroidX AppCompat | 1.7.1 | Base activity/fragment support |
| Material Components | 1.13.0 | UI components (TextInputLayout, buttons, etc.) |
| ConstraintLayout | 2.2.1 | Primary layout system |
| CardView | 1.0.0 | Card UI elements |
| ViewPager2 | 1.0.0 | Onboarding swipe screens |
| DotsIndicator | 4.3 | Pager dot indicators (`com.tbuonomo:dotsindicator`) |
| Activity | 1.13.0 | EdgeToEdge support |

## Testing
- **Unit tests**: JUnit 4 (`app/src/test/`)
- **Instrumented tests**: Espresso (`app/src/androidTest/`)

## Common Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Install debug APK on connected device
./gradlew installDebug
```

## AGP Version
Android Gradle Plugin: `8.13.2`
