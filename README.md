NewEdgeDemo app

**Task -:**
To create Android application built with Jetpack Compose that provides real-time
English grammar checking, error highlighting, and text correction using Api.

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

**Limitations**
1. Camera feature will not work
2. Voice feature will not work

