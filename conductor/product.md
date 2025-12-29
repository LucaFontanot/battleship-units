# Initial Concept
To proof the corrects usage of SOLID principles etc for an university exam

# Product Guide

## Target Users
- Students or developers interested in learning about multi-module Java projects.
- Academic evaluators looking for clean code, SOLID principles application, and robust architectural design.

## Key Features
- **Classic Battleship Gameplay:** Implementation of the standard game logic, including a 10x10 grid and traditional ship types (Carrier, Battleship, Destroyer, Submarine, Patrol Boat).
- **Real-time Multiplayer:** Support for head-to-head matches using WebSockets for low-latency communication between the client and server.
- **SOLID Architecture:** A codebase structured specifically to demonstrate Object-Oriented design patterns and SOLID principles (Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion).
- **Multi-module Structure:** A clean separation of concerns across different modules:
    - `common`: Shared data models, utilities, and protocols.
    - `server`: A Javalin-based backend for lobby management and game orchestration.
    - `game`: A Swing-based GUI client for the player interface.

## Primary Objective
The primary goal of this project is to serve as a high-quality demonstration of software engineering best practices, specifically the application of SOLID principles and clean architecture in a Java-based client-server application, suitable for academic evaluation or an university exam.
