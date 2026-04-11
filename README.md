NewEdgeDemo app

**Task -:**
To create Android application built with Jetpack Compose that provides real-time
English grammar checking, error highlighting, and text correction using Api.
The Primary focus to this demo app to help user to fix their spelling mistakes in their sentenses.

**Task provided to Implement the UI as per**
https://www.figma.com/proto/Jw060Qq7f1zaSWZeKSczfS/CS---Correct-Spelling?node-id=2136-4835&viewport=-1552%2C-565%2C0.23&t=I3MoFGsf7ER2AqA6-0&scaling=scale-down&content-scaling=fixed&starting-point-node-id=2136%3A4835&show-proto-sidebar=1

**Task Implemented**
1. UI implemented as per Figma
2. Icons are taken from material designs **(Reason Figma was not enabled to extract icons, colors, text)**
3. Clean Architecture + MVVM: Separation of concerns between Data, Domain, and UI layers.
4. Jetpack Compose: A modern, reactive UI with dual-state card transitions.
5. Hilt DI: Robust dependency management for your repositories and use cases.
6. **Admob added (real time api is also added, require to uncomment)**

**Note -:** **Admob working requires app verification so it may not display**

**Add on completed**
1. Retrofit & Coroutines: Efficient, asynchronous networking using the LanguageTool API.
   BASE_URL = "https://api.languagetool.org/v2/
2. Implemented Free api integration for user experience **(This may vary with api result)**
3. Validations like orientation, Internet availability
4. Followed Structure, readability, scalability, best practices

**Prerequisites**
To build and run this application, ensure you have the following installed:
1. Android Studio: Jellyfish | 2023.3.1 or newer.
2. JDK: Java 17 or higher.
3. Android SDK: API Level 24 (Android 7.0) or higher. (compiled on Android 36)
4. Internet Connection: Required for API calls to the grammar engine and loading ads.

**How to Import, Build, and Run the Project**

**Step 1: Import the Project**
1. Open Android Studio.
2. On the Welcome screen, click Open (or go to File > Open if a project is already open).
3. Navigate to your project folder: C:/Users/verma/StudioProjects/NewEdgeDemo.
4. Select the top-level folder (the one containing the build.gradle file) and click OK.
5. Wait for the Gradle Sync to finish. You will see a progress bar at the bottom right. Do not interrupt this.

**Step 2: Set up the Emulator (Virtual Device)**
1. In the top right corner of Android Studio, click the Device Manager icon (looks like a small phone).
2. Click Create Device.
3. Choose a device (e.g., Pixel 7) and click Next.
4. Select a System Image (e.g., API 34 or UpsideDownCake). If it has a "Download" link next to it, click it first.
5. Click Next, then Finish.

**Step 3: Run on Emulator**
1. In the top toolbar, ensure "app" is selected in the dropdown.
2. Select your newly created emulator from the device dropdown.
3. Click the Run button (Green Play Icon ▶).

**Step 4: Run on a Physical Phone**
1. On your phone, go to Settings > About Phone.
2. Tap Build Number 7 times until it says "You are now a developer."
3. Go to Settings > System > Developer Options and enable USB Debugging.
4. Plug your phone into your PC via USB.
5. Accept the "Allow USB Debugging?" prompt on your phone screen.
6. Select your phone in the Android Studio device dropdown and click Run.

**Troubleshooting Build Errors:**
• Missing SDK: If you see an error about a missing "Android SDK Platform," click the link in the error log to download it.
• JDK Mismatch: Ensure you are using the Java version required by your project (usually Java 17 for modern Compose projects). Go to File > Settings > Build, Execution, Deployment > Build Tools > Gradle and check the Gradle JDK.   

**Limitations**
1. Camera feature will not work
2. Voice feature will not work
3. #648AFF color code of icon background is not matching with Figma, need UX to verify it

