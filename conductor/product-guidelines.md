# Product Guidelines

## Development Philosophy
- **Pragmatic and Concise:** The codebase should prioritize readability and directness. We value code that is easy to follow and maintain.
- **Selective Documentation:** Avoid over-commenting. Use comments to explain the "why" behind complex logic or specific architectural choices, rather than the "what".
- **Focus on SOLID:** While being pragmatic, the core of the implementation must demonstrate a clear understanding and application of SOLID principles to meet the project's educational and evaluative goals.

## User Experience & Interface (Swing Client)
- **Functional and Direct:** The user interface should be straightforward and intuitive.
- **Information Clarity:** Prioritize clear display of the game grids, ship statuses, and match information.
- **Ease of Navigation:** Ensure the flow from the welcome screen to lobby selection and into the game is seamless and requires minimal cognitive load.

## Architecture and Module Interaction
- **Pragmatic Flexibility:** We maintain a multi-module structure (`common`, `server`, `game`) to separate concerns.
- **Dependency Flow:** Generally, dependencies should flow towards the `common` module. While strict boundaries are the goal, we allow for pragmatic exceptions if they are clearly justified and documented to avoid over-engineering.
- **Communication:** Interaction between the client and server should be mediated by shared models and protocols defined in the `common` module, favoring simplicity in communication.
