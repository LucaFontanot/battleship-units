# Plan: Implement GameController and IGameView

## Phase 1: Interface Definition
- [ ] Task: Define IGameView Interface
    - Create `IGameView.java` in `game/src/main/java/battleship/view/` with the specified methods (`onGameStart`, `onUpdateGridState`, etc.).
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Interface Definition' (Protocol in workflow.md)

## Phase 2: Controller Implementation (Core Logic)
- [ ] Task: Scaffold GameController and Tests
    - Create `GameController` class with Constructor Injection for `IGameView` and Models.
    - Create `TestGameController` in `game/src/test/java/battleship/controller/` to mock `IGameView` and test initialization.
- [ ] Task: Implement Game Start Logic
    - **Test:** Write test for `startGame()` verifying it initializes grids and calls `view.onGameStart()`.
    - **Implement:** Add `startGame()` logic to `GameController`.
- [ ] Task: Implement Attack Processing (Hit/Miss)
    - **Test:** Write tests for `processShot(x, y)`:
        - Verify `Grid` is queried.
        - Verify `view.onUpdateGridState` is called with correct state.
    - **Implement:** Add `processShot` logic handling Hit/Miss scenarios.
- [ ] Task: Implement Ship Sunk Logic
    - **Test:** Write test where a shot sinks a ship, verifying `view.onShipSunk` is called.
    - **Implement:** Update `processShot` to check `FleetManager` for sunk ships.
- [ ] Task: Implement Turn Swapping and Win Condition
    - **Test:** Write tests for turn switching and `onGameOver` when fleet is defeated.
    - **Implement:** Add state management for turns and win checks after shots.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Controller Implementation' (Protocol in workflow.md)
