# Visa Checklist (Android)

Personal **document checklist + deadline reminders** for your visa application.  
It does **not** log into BLS, bypass security, scrape slots, or book appointments.

## Features

- Pre-filled Schengen-style document checklist (tap to mark done)
- Progress overview
- Appointment / deadline date picker
- Local morning reminder notification (9:00) on the chosen day
- Personal notes (saved on device)
- Reset checklist

## Download the APK (GitHub Actions)

This project builds a **debug APK** in GitHub Actions. You do not need Android Studio on this PC.

1. Push this folder to a GitHub repository (see below).
2. Open the repo → **Actions**.
3. Open the latest **Build APK** run (green check).
4. Under **Artifacts**, download **visa-checklist-debug-apk**.
5. Unzip it and copy `app-debug.apk` to your phone.
6. On Android: allow install from that source, then open the APK.

You can also start a build manually: **Actions → Build APK → Run workflow**.

The debug APK is for personal testing, not Play Store.

## Push to GitHub

If Git is available:

```bash
git init
git add .
git commit -m "Add visa checklist Android app"
git branch -M main
git remote add origin https://github.com/YOUR_USER/visa-checklist.git
git push -u origin main
```

Create the empty repo on GitHub first (no README), then replace `YOUR_USER`.

## Open in Android Studio (optional)

- [Android Studio](https://developer.android.com/studio) (Ladybug or newer)
- Android device or emulator (API 26+)

1. **File → Open** → this folder
2. Wait for Gradle sync
3. Press **Run**

## Official booking

Book appointments only through the official BLS / embassy channels for your destination. This app only helps you organize what you need beforehand.
