## Plan: Beginner JavaFX Restaurant App

Build a simple restaurant UI in JavaFX (Java 21/JavaFX 21) matching the PDF’s intent while keeping code beginner-friendly.

### Steps
1. Align requirements: outline target screens/flows from the PDF (menus, cart, checkout), clarify must‑have features and scope.  
2. Define UI skeleton in FXML: primary layout, navigation, and placeholders for menu list, item detail, cart, and order summary views.  
3. Create controllers with clear methods (`showMenu()`, `addToCart()`, `checkout()`) and a minimal in-memory model for items/cart (no database).  
4. Wire navigation and state: load/swap FXML via `App.setRoot`, pass shared cart/state through a small `AppState` class or singleton.  
5. Add basic styling and accessibility: CSS for spacing/contrast, keyboard focus, and simple input validation.  
6. Smoke-test flows on Java 21/JavaFX 21: run `mvn clean javafx:run`, adjust for any beginner pitfalls.

### Further Considerations
1. Can you share the PDF’s key screens/requirements (menu structure, actions) since the file isn’t readable here?  
2. Prefer a single-window view switcher or multi-pane layout (menu + cart sidebar)?  
3. Should we include mock data only, or also a simple persistence option (e.g., JSON file)?
