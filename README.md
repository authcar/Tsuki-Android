# Tsuki

Tsuki adalah aplikasi Android untuk pelacakan siklus menstruasi. Nama "Tsuki" berasal dari bahasa Jepang yang berarti "bulan", mencerminkan hubungan antara siklus lunar dan siklus menstruasi. Aplikasi ini dirancang dengan antarmuka yang bersih dan ramah pengguna, saat ini mencakup alur onboarding dan pendaftaran akun.

---

## Fitur Utama

- **Splash Screen** — Layar pembuka bermerek yang secara otomatis berpindah ke onboarding setelah 2 detik.
- **Onboarding 3 Langkah** — Carousel interaktif yang memperkenalkan fitur-fitur utama aplikasi, dilengkapi indikator titik (dots indicator) dan navigasi geser.
- **Halaman Pilihan Akun** — Titik masuk untuk memilih antara Sign Up dan Sign In.
- **Pendaftaran via Email** — Formulir registrasi dengan kolom nama lengkap, email, dan kata sandi. Tombol daftar aktif secara dinamis hanya ketika semua kolom telah diisi.
- **Autentikasi Sosial** — Opsi masuk menggunakan akun Google, Apple, dan Facebook.
- **Pengumpulan Data Profil** — Alur pasca-pendaftaran untuk mengumpulkan nama pengguna dan tanggal lahir.
- **Desain Edge-to-Edge** — Tampilan layar penuh yang memanfaatkan seluruh area layar perangkat.

---

## Instalasi dan Menjalankan Proyek

### Prasyarat

- Android Studio Hedgehog atau yang lebih baru
- JDK 11
- Android SDK dengan `compileSdk 36` dan `minSdk 24`
- Perangkat fisik atau emulator Android (API level 24 ke atas)

### Langkah Instalasi

1. Clone repositori ini:
   ```bash
   git clone https://github.com/your-username/tsuki.git
   cd tsuki
   ```

2. Buka proyek di Android Studio melalui **File > Open**, lalu pilih folder root proyek.

3. Tunggu proses Gradle sync selesai secara otomatis.

### Menjalankan Aplikasi

**Melalui Android Studio:**
Klik tombol **Run** (Shift+F10) dengan perangkat atau emulator yang sudah terhubung.

**Melalui command line:**
```bash
# Build APK debug
./gradlew assembleDebug

# Install langsung ke perangkat yang terhubung
./gradlew installDebug

# Bersihkan hasil build sebelumnya
./gradlew clean
```

### Menjalankan Pengujian

```bash
# Unit test
./gradlew test

# Instrumented test (memerlukan perangkat/emulator yang terhubung)
./gradlew connectedAndroidTest
```

---

## Struktur Folder

```
Tsuki/
├── app/
│   ├── build.gradle.kts                  # Konfigurasi build level app
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/tsuki/   # Seluruh source Java
│       │   │   ├── SplashActivity.java
│       │   │   ├── Onboarding1Activity.java
│       │   │   ├── Onboarding2Activity.java
│       │   │   ├── Onboarding3Activity.java
│       │   │   ├── ChoiceActivity.java
│       │   │   ├── SignUpChoiceActivity.java
│       │   │   ├── NameActivity.java
│       │   │   └── BirthdayActivity.java
│       │   └── res/
│       │       ├── drawable/             # Background, ikon vektor, aset PNG
│       │       ├── font/                 # Lato, Dancing Script
│       │       ├── layout/               # Satu file XML per Activity
│       │       └── values/               # colors, dimens, strings, themes
│       ├── test/                         # Unit test (JUnit 4)
│       └── androidTest/                  # Instrumented test (Espresso)
├── gradle/
│   └── libs.versions.toml               # Versi dependensi terpusat
├── build.gradle.kts                     # Konfigurasi build level root
└── settings.gradle.kts
```

### Deskripsi Activity

| Activity | Peran |
|---|---|
| `SplashActivity` | Launcher; berpindah otomatis ke onboarding setelah 2 detik |
| `Onboarding1/2/3Activity` | Carousel onboarding 3 langkah |
| `ChoiceActivity` | Titik masuk Sign Up / Sign In |
| `SignUpChoiceActivity` | Formulir registrasi email dan autentikasi sosial |
| `NameActivity` | Pengumpulan nama pengguna (pasca pendaftaran) |
| `BirthdayActivity` | Pengumpulan tanggal lahir (pasca pendaftaran) |

---

## Teknologi yang Digunakan

| Teknologi / Library | Versi | Kegunaan |
|---|---|---|
| Java | 11 | Bahasa pemrograman utama |
| Android SDK | minSdk 24, targetSdk 36 | Platform target |
| Android Gradle Plugin | 8.13.2 | Sistem build |
| AndroidX AppCompat | 1.7.1 | Base class Activity dan dukungan fragment |
| Material Components | 1.13.0 | Komponen UI (TextInputLayout, tombol, dll.) |
| ConstraintLayout | 2.2.1 | Sistem layout utama |
| CardView | 1.0.0 | Elemen UI berbentuk kartu |
| ViewPager2 | 1.0.0 | Carousel layar onboarding |
| DotsIndicator | 4.3 | Indikator titik untuk ViewPager2 |
| AndroidX Activity | 1.13.0 | Dukungan Edge-to-Edge |
| JUnit 4 | 4.13.2 | Framework unit testing |
| Espresso | 3.7.0 | Framework instrumented testing |

---

## Status Proyek

Proyek ini sedang dalam tahap pengembangan awal. Alur onboarding dan pendaftaran akun telah diimplementasikan. Fitur pelacakan siklus menstruasi belum dikembangkan.
