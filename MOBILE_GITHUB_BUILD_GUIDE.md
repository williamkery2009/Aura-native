# Build Aura Music APK Using Only Your Redmi 13 5G

This project is prepared for cloud APK builds using GitHub Actions. You do not need Android Studio on your phone.

## What you need

- Your Redmi 13 5G
- A GitHub account
- Chrome or any mobile browser
- The `aura-native` project uploaded to a GitHub repository

## Step 1: Create a GitHub repository

1. Open GitHub in your mobile browser.
2. Tap **+**.
3. Tap **New repository**.
4. Repository name example:

```text
aura-music-native
```

5. Choose **Private** or **Public**.
6. Tap **Create repository**.

## Step 2: Upload the project

Upload the contents of the `aura-native` folder to the repository.

The repository root should contain files like:

```text
settings.gradle.kts
build.gradle.kts
gradle.properties
gradle/libs.versions.toml
app/
core/
core-ui/
core-design/
core-domain/
core-database/
core-media/
core-network/
feature-home/
feature-library/
feature-search/
feature-player/
.github/workflows/android-debug-build.yml
```

Important: do not upload the parent folder as an extra wrapper level. `settings.gradle.kts` must be at the repository root.

## Step 3: Run the cloud build

1. Open your repository on GitHub.
2. Tap the **Actions** tab.
3. Select **Build Aura Music Debug APK**.
4. Tap **Run workflow**.
5. Choose the branch, usually `main`.
6. Tap the green **Run workflow** button.

GitHub will build the APK in the cloud.

## Step 4: Download the APK on your phone

1. Wait until the build shows a green check mark.
2. Open the completed workflow run.
3. Scroll to **Artifacts**.
4. Download:

```text
aura-music-debug-apk
```

5. Extract the downloaded ZIP if needed.
6. Install the APK file inside it.

The APK name will look similar to:

```text
app-debug.apk
```

## Step 5: Allow installation

Android may ask you to allow installs from your browser or file manager.

Enable:

```text
Settings > Apps > Special app access > Install unknown apps
```

Then install the APK.

## Step 6: First launch

On first launch:

1. Open Aura Music.
2. Tap **SCAN DEVICE AUDIO**.
3. Grant audio permission.
4. Aura will import your local songs into its private Room database.

## Notes for Redmi 13 5G

Your Redmi 13 5G should be able to run and test the app normally. Heavy tasks such as compiling Kotlin, Compose, Hilt, Room, and Media3 happen in GitHub cloud servers, not on your phone.

## If the build fails

Open the failed GitHub Actions run and copy the red error log. Send it back here and I can fix the project files for the next build.
