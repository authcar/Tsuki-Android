# Project Structure

## Root Layout
```
Tsuki/
├── app/                        # Single app module
│   ├── build.gradle.kts        # App-level build config
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/tsuki/   # All Java source files
│       │   └── res/                      # All resources
│       ├── test/               # Unit tests
│       └── androidTest/        # Instrumented tests
├── gradle/libs.versions.toml   # Centralized dependency versions
└── build.gradle.kts            # Root build config
```

## Package: `com.example.tsuki`
All Activity classes live in a single flat package — no sub-packages yet.

| File | Role |
|---|---|
| `SplashActivity` | Launcher; auto-navigates to onboarding after 2s |
| `Onboarding1/2/3Activity` | 3-step onboarding carousel |
| `ChoiceActivity` | Sign Up / Sign In entry point |
| `SignUpChoiceActivity` | Email registration form + social auth |
| `NameActivity` | Collect user name (post sign-up) |
| `BirthdayActivity` | Collect date of birth (post sign-up) |

## Resources (`app/src/main/res/`)

### `layout/`
One XML layout file per Activity, named `activity_<screen_name>.xml`.

### `drawable/`
- `bg_*.xml` – shape/gradient backgrounds for UI elements
- `ic_*.xml` – vector icons
- `progress*.xml` – onboarding progress indicators
- `*.png` – raster assets (illustrations, logo)

### `values/`
| File | Contents |
|---|---|
| `colors.xml` | Brand palette: `primary_pink`, `navy`, `pink_bg`, `gray`, `white`, `black` |
| `dimens.xml` | Spacing scale (`spacing_xs/sm/md/lg/xl`), typography sizes, card/button dimensions |
| `strings.xml` | App name and string resources |
| `themes.xml` | `Theme.Tsuki` extending `Material3.DayNight.NoActionBar` |

### `font/`
- `lato.ttf`, `lato_bold.ttf`, `lato_light.ttf` – primary body font
- `dancing_script_medium.ttf` – decorative/display font

## Conventions

### Activities
- Always call `EdgeToEdge.enable(this)` in `onCreate` before `setContentView`
- Always apply window insets via `ViewCompat.setOnApplyWindowInsetsListener` on the root view (`@+id/main`)
- Use `AppCompatActivity` as the base class
- Navigate with explicit `Intent`; call `finish()` when going back

### Layouts
- Root container is always `ConstraintLayout` with `android:id="@+id/main"`
- Use `@dimen/` references for all spacing, sizing, and text sizes — no hardcoded dp/sp values
- Use `@color/` references — no hardcoded hex colors in layouts
- Use `@font/lato` (or `lato_bold`) for all text; `dancing_script_medium` for display/branding only
- Buttons use `AppCompatButton` with `android:textAllCaps="false"`
- Input fields use `TextInputLayout` (Material3 `OutlinedBox` style) wrapping `TextInputEditText`
- The standard screen chrome is: pink gradient top half (`bg_pink_gradient_rounded`) + white card (`bg_white_card`) floating over it + circular back button (`bg_circle_white` + `ic_back`)

### Drawables
- Shape backgrounds are defined as XML drawables, not inline in layouts
- Disabled button state uses a separate drawable (`bg_signup_button_disabled`)
