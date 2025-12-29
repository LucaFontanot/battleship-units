# Specification: Implement GameController and IGameView

## Context
The project follows a strict MVC architecture. The Domain Models (`Ship`, `ShipType`, `Grid`, `FleetManager`) are already implemented in the `game` module. We need to implement the Controller and the View Interface to orchestrate the game logic without coupling to a concrete UI.

## Goal
Implement the `GameController` class and the `IGameView` interface in the `game` module.

## Requirements

### 1. IGameView Interface
*   **Location:** `game/src/main/java/battleship/view/IGameView.java`
*   **Purpose:** Define the contract for the View implementation.
*   **Methods:**
    *   `void onGameStart()`
    *   `void onUpdateGridState(int x, int y, CellStates state)`
    *   `void onShipSunk(String shipName)`
    *   `void onGameMessage(String message)`
    *   `void onGameOver(String winner)`

### 2. GameController Class
*   **Location:** `game/src/main/java/battleship/controller/GameController.java`
*   **Purpose:** Orchestrate the flow between Models and the View Interface.
*   **Dependencies:**
    *   Accepts `IGameView` and Model instances (e.g., `Grid`, `FleetManager`) via **Constructor Injection**.
*   **State Management:**
    *   Manage game states: `Setup`, `PlayerTurn`, `EnemyTurn`, `GameOver`.
*   **Game Logic:**
    *   **Start Game:** Initialize grids and trigger ship placement.
    *   **Attack Processing:**
        *   Expose method `processShot(int x, int y)` for the View to call.
        *   Query `Grid` and `FleetManager`.
        *   Determine Hit/Miss/Sunk.
        *   Update View via `onUpdateGridState` and `onShipSunk`.
    *   **Turn Swapping:** Handle switching between Player and Opponent turns.
    *   **Win Condition:** Check `FleetManager.isDefeated()` after every move and call `onGameOver` if true.

## Constraints
*   **No Concrete View Code:** Do not use `System.out.println`, Swing components, or any specific UI framework classes in the Controller or Interface.
*   **SOLID Principles:** Ensure strict adherence to Dependency Inversion (Controller depends on Interface) and Single Responsibility.
