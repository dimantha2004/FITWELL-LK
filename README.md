# FitWell LK

**Offline-first personal fitness and wellness tracker** – CI6330 Mobile Application Development, Coursework 2 (Health and Fitness).

FitWell LK lets a user plan workouts, log completed sessions, track daily water intake, keep a wellness journal (weight, sleep, mood) and set fitness goals whose progress is calculated automatically from what they log. Every feature works without an internet connection: data is saved in SQLite on the phone first and synchronised with a Spring Boot + MySQL backend when a connection is available.

| | |
|---|---|
| Mobile | Flutter 3.47 · Dart · Material 3 · Riverpod 3 · GoRouter · Drift (SQLite) · Dio · fl_chart · connectivity_plus |
| Backend | Java 21 · Spring Boot 3.5 · Spring Web / Data JPA / Security / Validation · JWT (JJWT) · Flyway |
| Database | MySQL 8 |

## Features

| Area | What it does |
|---|---|
| Authentication | Register, login, logout, JWT session stored in secure storage, session-expiry handling |
| Dashboard | Personalised greeting, today's activity ring, hydration ring, latest wellness entry, weekly activity chart, active goals, recent activity feed, quick actions |
| Workout plans | Create / view / edit / delete plans with a dynamic exercise list; search by text; filter by difficulty |
| Workout tracker | Log completed workouts (type, duration, calories, date/time, notes, optional plan); edit, swipe-to-delete; weekly summary |
| Hydration | Quick-add buttons, custom drinks, edit/delete entries, configurable daily goal, animated water bottle, 14-day history with goal line |
| Wellness journal | Weight, sleep, mood (1–5) and notes; edit/delete; weight/sleep/mood trend charts; clear "not medical advice" notice |
| Goals | Session, active-minute, hydration-day, weight and custom goals; automatic progress; weekly and monthly progress views |
| Sync | Outbox of pending operations, automatic retry, conflict handling (last write wins), sync status badge and details screen |

## Repository layout

```
fitwell_app/       Flutter application
  lib/core/          config, theme, enums, validators, formatters
  lib/data/          local (Drift DB + outbox), remote (Dio client), repositories, sync engine
  lib/domain/        pure statistics & goal-progress calculations
  lib/state/         Riverpod providers and controllers (auth, sync, feature read models)
  lib/ui/            router, screens, reusable/custom widgets
  test/              unit, sync-engine and widget tests
fitwell-backend/   Spring Boot REST API
  src/main/java/lk/fitwell/api/   auth, user, security, common, plan, session, hydration, wellness, goal
  src/main/resources/db/migration Flyway SQL migration (schema)
  src/test/                       MockMvc integration tests (H2 in MySQL mode)
database/          Standalone MySQL schema script
docs/              API smoke test script, test evidence, report, diagrams
```

## Running the backend

Requirements: JDK 21, Maven 3.9+, MySQL 8 running on `localhost:3306`.

1. Copy `fitwell-backend/config/application-local.properties.example` to `fitwell-backend/config/application-local.properties` and set your MySQL password and a JWT secret (`openssl rand -base64 48`). This file is gitignored.
2. Start the API from the `fitwell-backend` folder:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```
   Flyway creates the `fitwell_lk` database tables on first start. Alternatively provide `DB_USERNAME`, `DB_PASSWORD` and `JWT_SECRET` as environment variables.
3. Check it: `curl http://localhost:8080/api/health`
4. Run the tests: `mvn test` (13 integration tests, in-memory H2) and, with the server running, `bash ../docs/api-smoke-test.sh` (21 live checks against MySQL).

## Running the app

```bash
cd fitwell_app
flutter pub get
dart run build_runner build      # generates the Drift database code
flutter run                       # Android emulator: backend at http://10.0.2.2:8080/api
```

On a physical phone, point the app at your computer's LAN IP:

```bash
flutter run --dart-define=API_BASE_URL=http://192.168.1.10:8080/api
flutter build apk --release --dart-define=API_BASE_URL=http://192.168.1.10:8080/api
```

Tests: `flutter analyze` and `flutter test` (24 tests).

## Helper files

- `docs/FitWell_LK.postman_collection.json` - Postman collection for every endpoint (login stores the token automatically).
- `docs/api-smoke-test.sh` - 21 live HTTP checks against a running server.
- `docs/seed_demo_data.py` - creates `demo@fitwell.lk` / `Demo1234` with three weeks of history through the API (used for the demo and screenshots).
- `docs/evidence/` - raw outputs of every test run, MySQL query results and test screenshots.
- `docs/report/src/` and `docs/presentation/` - scripts that generate the report (DOCX + PDF), diagrams and slides.

## REST API

All endpoints except `/api/auth/**` and `/api/health` require `Authorization: Bearer <jwt>`.

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Create account, returns JWT |
| POST | `/api/auth/login` | Sign in, returns JWT |
| GET / PUT | `/api/users/me` | Profile and daily water goal |
| GET | `/api/plans?search=&difficulty=` | List / search plans |
| GET / POST / PUT / DELETE | `/api/plans`, `/api/plans/{id}` | Workout plan CRUD |
| GET / POST / PUT / DELETE | `/api/sessions[?from=&to=]`, `/api/sessions/{id}` | Workout session CRUD |
| GET / POST / PUT / DELETE | `/api/hydration[?from=&to=]`, `/api/hydration/{id}` | Hydration CRUD |
| GET / POST / PUT / DELETE | `/api/wellness`, `/api/wellness/{id}` | Wellness journal CRUD |
| GET / POST / PUT / DELETE | `/api/goals`, `/api/goals/{id}` | Goal CRUD |
| GET | `/api/health` | Reachability check |

Sync semantics: ids are UUIDs generated on the phone; `POST` with an existing id returns the stored record (no duplicate); `PUT` is an upsert; an update whose `updatedAt` is older than the stored one returns `409` with the server copy; `DELETE` is idempotent.

## Disclaimer

The wellness features are for personal tracking only and do not provide medical advice or diagnosis.
