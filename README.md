# Tsuki

Tsuki adalah aplikasi Android untuk pelacakan siklus menstruasi. Nama "Tsuki" berasal dari bahasa Jepang yang berarti "bulan", mencerminkan hubungan antara siklus lunar dan siklus menstruasi. Aplikasi ini dirancang dengan antarmuka yang bersih dan ramah pengguna, mencakup autentikasi pengguna, pelacakan siklus, kalender interaktif, log harian, dan konten edukasi.

---

## Fitur Utama

### Autentikasi
- **Sign Up / Sign In via Email** — Registrasi dan login dengan validasi error (email duplikat, password lemah, dll.)
- **Google Sign In** — Login menggunakan akun Google via Firebase Auth
- **Forgot Password** — Reset password melalui email link dari Firebase
- **Session Persistence** — App mengingat status login; user tidak perlu login ulang setiap buka app
- **Logout** — Sign out dari Firebase dan Google secara bersamaan

### Onboarding
- **Splash Screen** — Layar pembuka yang cek status login; redirect ke onboarding atau home
- **Onboarding 3 Langkah** — Carousel interaktif dengan ViewPager2, dots indicator, dan swipe gesture
- **Profile Setup** — Alur pengumpulan nama, tanggal lahir, panjang period, dan tanggal period terakhir

### Home
- **Greeting Dinamis** — Sapaan berubah sesuai waktu (Good Morning/Afternoon/Evening/Night)
- **Week Strip** — Strip hari yang bisa digeser; tap hari untuk melihat info siklus di tanggal tersebut
- **Cycle Card** — Menampilkan fase siklus saat ini (Period/Follicular/Ovulation/Luteal), hari ke-N dalam fase, dan sisa hari
- **Prediction Cards** — Prediksi Next Period, Ovulation, dan Fertile Window berdasarkan data siklus

### Kalender
- **Kalender Interaktif** — Grid kalender dengan warna berbeda per fase (period, fertile, ovulation, today)
- **Navigasi Bulan** — Spinner bulan/tahun dan tombol prev/next
- **Log Detail Card** — Tap tanggal yang sudah di-log untuk melihat detail flow, symptoms, dan mood
- **Edit Period Dates** — Bottom sheet untuk mengubah tanggal period dengan range selection

### Log Harian
- **Flow Intensity** — Pilih intensitas (Low/Normal/High) dengan single select
- **Symptoms** — Multi-select gejala (Cramps, Headaches, Bloating, dll.)
- **Mood** — Multi-select suasana hati (Happy, Calm, Sad, dll.)
- **Pre-fill Otomatis** — Saat buka tanggal yang sudah di-log, chip terisi otomatis dari Firestore
- **Save Dialog** — Dialog konfirmasi dengan animasi centang setelah log tersimpan

### Learn
- **Quote of the Day** — Quote berbeda setiap hari berdasarkan tanggal
- **Tab Filter** — Filter artikel per kategori (Period, Wellness, Fertility)
- **Article Detail** — Halaman detail artikel dengan hero image

### Profile
- **Data Profil** — Nama dan email dari Firebase/SharedPreferences
- **Preferences** — Toggle Reminder Notifications dan Daily Tips
- **Help Center** — Akses pusat bantuan
- **Logout** — Keluar dari akun

### Notifikasi
- **Period Reminder** — Notifikasi otomatis 3 hari sebelum, 1 hari sebelum, dan hari H period
- **Fertile Window Reminder** — Notifikasi saat fertile window dimulai
- **AlarmManager** — Notifikasi bekerja meski app ditutup

---

## Arsitektur & Teknologi

### Stack Utama

| Teknologi | Versi | Kegunaan |
|---|---|---|
| Java | 11 | Bahasa pemrograman utama |
| Android SDK | minSdk 24, targetSdk 36 | Platform target |
| Android Gradle Plugin | 8.13.2 | Sistem build |

### Firebase

| Layanan | Kegunaan |
|---|---|
| Firebase Authentication | Email/Password login, Google Sign In, password reset |
| Cloud Firestore | Penyimpanan data profil, siklus, dan log harian di cloud |

### UI Libraries

| Library | Versi | Kegunaan |
|---|---|---|
| AndroidX AppCompat | 1.7.1 | Base class Activity |
| Material Components | 1.13.0 | TextInputLayout, BottomSheet, dll. |
| ConstraintLayout | 2.2.1 | Layout utama |
| CardView | 1.0.0 | Kartu UI |
| ViewPager2 | 1.0.0 | Onboarding carousel |
| DotsIndicator | 4.3 | Indikator halaman onboarding |
| FlexboxLayout | 3.0.0 | Chip wrap otomatis di Log fragment |

### Penyimpanan Lokal

| Mekanisme | Kegunaan |
|---|---|
| SharedPreferences | Cache data siklus dan profil untuk akses offline |
| AlarmManager | Penjadwalan notifikasi lokal |

---

## Instalasi dan Menjalankan Proyek

### Prasyarat

- Android Studio Hedgehog atau yang lebih baru
- JDK 11
- Android SDK dengan `compileSdk 36` dan `minSdk 24`
- File `google-services.json` dari Firebase Console (taruh di folder `app/`)
- Perangkat fisik atau emulator Android (API level 24 ke atas)

### Setup Firebase

1. Buat project di [console.firebase.google.com](https://console.firebase.google.com)
2. Daftarkan app Android dengan package name `com.example.tsuki`
3. Enable **Email/Password** dan **Google** di Authentication > Sign-in method
4. Buat **Firestore Database** dengan production mode
5. Download `google-services.json` dan taruh di folder `app/`
6. Tambahkan SHA-1 fingerprint di Project Settings

### Langkah Instalasi

1. Clone repositori ini:
   ```bash
   git clone https://github.com/your-username/tsuki.git
   cd tsuki
   ```

2. Buka proyek di Android Studio melalui **File > Open**

3. Taruh `google-services.json` di folder `app/`

4. Sync Gradle: **File > Sync Project with Gradle Files**

### Menjalankan Aplikasi

Klik tombol **Run** (Shift+F10) dengan perangkat atau emulator yang sudah terhubung.

---

## Struktur Folder

```
Tsuki/
├── app/
│   ├── google-services.json              # Konfigurasi Firebase (tidak di-commit)
│   ├── build.gradle.kts
│   └── src/main/
│       ├── java/com/example/tsuki/
│       │   ├── SplashActivity.java
│       │   ├── OnboardingActivity.java   # Host ViewPager2 onboarding
│       │   ├── ChoiceActivity.java
│       │   ├── SignUpChoiceActivity.java
│       │   ├── SignInActivity.java
│       │   ├── ForgotPasswordActivity.java
│       │   ├── ProfileSetupActivity.java # Host fragment onboarding profil
│       │   ├── MainActivity.java         # Host bottom navigation
│       │   ├── HomeFragment.java
│       │   ├── CalendarFragment.java
│       │   ├── LogFragment.java
│       │   ├── LearnFragment.java
│       │   ├── ProfileFragment.java
│       │   ├── CycleCalculator.java      # Logika kalkulasi fase siklus
│       │   ├── FirestoreManager.java     # Helper operasi Firestore
│       │   ├── ReminderScheduler.java    # Penjadwalan notifikasi
│       │   ├── NotificationHelper.java
│       │   └── PeriodReminderReceiver.java
│       └── res/
│           ├── drawable/
│           ├── font/                     # Lato, Dancing Script
│           ├── layout/
│           ├── menu/
│           ├── anim/
│           └── values/
├── gradle/libs.versions.toml
└── build.gradle.kts
```

---

## Alur Aplikasi

```
Splash (cek login)
    ├── Sudah login → MainActivity (Home)
    └── Belum login → Onboarding → Choice
                                    ├── Sign Up → ProfileSetup → Loading → Notification → Home
                                    └── Sign In → Home
```

---

## Status Proyek

Aplikasi dalam tahap pengembangan aktif. Fitur inti yang sudah diimplementasikan:

- Autentikasi lengkap (Email + Google)
- Kalkulasi dan prediksi siklus menstruasi
- Kalender interaktif dengan visualisasi fase
- Log harian dengan sinkronisasi Firestore
- Notifikasi lokal terjadwal
- Konten edukasi dengan filter kategori

Fitur yang direncanakan:
- Sinkronisasi data siklus dari multiple log untuk meningkatkan akurasi prediksi
- Integrasi Apple dan Facebook Sign In
