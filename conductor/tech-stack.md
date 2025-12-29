# Technology Stack

## Core Language & Runtime
- **Language:** Java 21+ (as indicated by modern library usage)
- **Build System:** Gradle (Multi-module structure)

## Backend (Server Module)
- **Framework:** Javalin (version 6.7.0) for web server and WebSocket management.
- **WebSocket Support:** Javalin's built-in WebSocket handlers for real-time game state synchronization.

## Frontend (Game Module)
- **UI Framework:** Java Swing.
- **Styling/Look & Feel:** FlatLaf (version 3.6.2) for a modern, clean interface.
- **Theme Detection:** jSystemThemeDetector for automatic light/dark mode support.
- **UI Design:** IntelliJ IDEA Forms (`forms_rt`).

## Shared (Common Module)
- **Data Serialization:** GSON (version 2.13.2) for JSON processing between client and server.
- **Logging:** SLF4J with Log4j2 implementation.
- **Utilities:** 
    - Lombok for reducing boilerplate code.
    - Apache Commons CLI for command-line argument parsing.

## Testing & Quality
- **Test Framework:** JUnit 5 (JUnit Jupiter).
- **Mocking/HTTP Testing:** OkHttp for server-side route testing.
