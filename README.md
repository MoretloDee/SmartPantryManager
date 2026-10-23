# Smart Pantry Manager
**Module:** Mobile App Development 700

An Android application built in Java that helps users reduce food waste by tracking pantry ingredients and suggesting recipes using a strict-matching algorithm.

## Database
This app uses **SQLite** (via `SQLiteOpenHelper`) for local, on-device persistent storage. This ensures the app works entirely offline and data persists between sessions.

## Features
* Pantry Item CRUD (Create, Read, Update, Delete)
* Strict-matching recipe suggestions
* Pre-seeded database with 15 recipes
* Settings screen with SharedPreferences
