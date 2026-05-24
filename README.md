# NCRAdarsh Android App — Setup Guide

## ✅ Features Included
| Feature | Status |
|---|---|
| Swipe Down to Refresh | ✅ |
| Back Button Navigation | ✅ |
| Exit Dialog (Confirm before close) | ✅ |
| Screen Rotation Support | ✅ |
| Splash Screen (2.5 sec) | ✅ |
| Loading Progress Bar | ✅ |
| Download Button Support | ✅ |
| No Internet Screen (Hindi) | ✅ |
| Font Size 85% | ✅ |
| Cookie Support | ✅ |
| External Links in Browser | ✅ |

---

## 📱 Android Studio Mein Build Kaise Karein

### Step 1 — Android Studio Install Karein
Download: https://developer.android.com/studio

### Step 2 — Project Open Karein
- Android Studio open karein
- **"Open"** click karein
- `NCRAdarsh` folder select karein
- Gradle sync hone dein (2-3 minute)

### Step 3 — Logo/Icon Add Karein (IMPORTANT!)
Splash screen aur app icon ke liye apni uploaded image use karein:

1. Android Studio mein **res → mipmap-hdpi** folder pe right-click
2. **"Show in Explorer/Finder"** click karein
3. Apni NCRAdarsh logo image ko copy karein:
   - `ic_launcher.png` naam se **mipmap-hdpi** (72x72px)
   - `ic_launcher.png` naam se **mipmap-mdpi** (48x48px)
   - `ic_launcher.png` naam se **mipmap-xhdpi** (96x96px)
   - `ic_launcher.png` naam se **mipmap-xxhdpi** (144x144px)
   - `ic_launcher.png` naam se **mipmap-xxxhdpi** (192x192px)

**Easy Shortcut:** Android Studio mein:
- Right-click on `res` folder
- **New → Image Asset**
- Apni logo image select karein, automatically sab sizes ban jayengi!

### Step 4 — APK Build Karein
1. **Build → Build Bundle(s)/APK(s) → Build APK(s)**
2. APK mil jayega: `app/build/outputs/apk/debug/app-debug.apk`

### Step 5 — Phone Mein Install Karein
- APK file phone mein copy karein
- **Settings → Security → Unknown Sources** enable karein
- APK file open karein aur install karein

---

## 🎨 Splash Screen Customize Karna
`res/layout/activity_splash.xml` mein:
- Background color change karna ho to `android:background="#000000"` modify karein
- Tagline text change karna ho to `android:text="Breaking News • Live Updates"` edit karein
- Duration change karna ho to `SplashActivity.java` mein `SPLASH_DURATION = 2500` (milliseconds) badlein

## 🌐 Website URL Change Karna
`MainActivity.java` mein line find karein:
```java
private static final String WEBSITE_URL = "https://ncradarsh.com";
```
Yahan apna URL change kar saktey hain.

---

## 🚀 Advanced Features (Already Included)
- **Cookie persistence** — user login sessions save rehti hain
- **External URL handling** — dusri websites browser mein khulti hain
- **Download Manager integration** — files Downloads folder mein jaati hain
- **WebView state preservation** — screen rotate hone par page reload nahi hota
- **Graceful error handling** — page load error par no-internet screen dikhti hai

---

## ❓ Common Issues

**Gradle sync fail ho raha hai?**
→ File → Invalidate Caches → Restart

**App install nahi ho raha?**  
→ Phone Settings mein "Install Unknown Apps" allow karein

**Logo nahi dikh raha splash mein?**  
→ Image Asset tool se sahi sizes mein icon generate karein
