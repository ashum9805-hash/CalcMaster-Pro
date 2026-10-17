# CalcMaster Pro

CalcMaster Pro is an Android calculator app written in Kotlin that provides advanced on-device math solving, fraction support, unit conversions, and a local history of calculations. It's structured as a standard Android app module and builds with Gradle (Kotlin DSL).

## Key features

- Rich math engine with solvers and models (MathEngine, AdvancedSolvers, MathKnowledge)
- Fraction helpers and math models for precise rational arithmetic
- Unit conversion utilities
- Local persistence for calculation history using Room-style database (AppDatabase, CalculationDao, CalculationEntity)

## Stack

- Language: Kotlin
- Runtime: Android (Gradle Kotlin DSL)
- Build: Gradle (build.gradle.kts at root and app module)

## Project structure (high level)

```
app/
  src/main/
    AndroidManifest.xml
    java/com/example/
      MainActivity.kt        # app entrypoint / UI host
      data/                  # Room DB, DAO, entity
      math/                  # MathEngine, AdvancedSolvers, MathKnowledge
      ui/                    # UI components
      units/                 # Unit conversion logic
    res/                    # layouts, strings, drawables
build.gradle.kts           # root Gradle build
settings.gradle.kts
gradle.properties
metadata.json
```

## Build & run

Prerequisites: Android SDK, Java JDK, Android Studio or command-line SDK tools.

From the repository root (recommended to open in Android Studio):

```bash
# build the debug APK
./gradlew assembleDebug
# install to a connected device/emulator
./gradlew installDebug
```

Or open the project in Android Studio and run on an emulator/device.

Environment variables that may be needed: ANDROID_HOME or ANDROID_SDK_ROOT pointing to your SDK installation.

## Tests

The project contains androidTest/ and test/ directories under app/src. To run unit tests:

```bash
./gradlew test
```

To run instrumentation tests on a connected device/emulator:

```bash
./gradlew connectedAndroidTest
```

## Suggestions / Next improvements

- Add a CI workflow (.github/workflows/android.yml) that runs unit tests and builds the debug APK on push/PR.
- Add focused unit tests for the math layer (app/src/test/java/com/example/math) to cover edge cases in MathEngine and AdvancedSolvers.
- Add an ARCHITECTURE.md describing separation between MathEngine, AdvancedSolvers, and UI responsibilities.
- Add example screenshots and a short Feature list in the README (assets/screenshots/).

## Contributing

Contributions are welcome. Open an issue or a pull request with a short description of the change.

## License

Specify a license file if you want an explicit license (e.g., MIT, Apache-2.0).

---

If you'd like, I can open a PR with this README, add a CI workflow, and draft unit tests for MathEngine. Which should I do next?