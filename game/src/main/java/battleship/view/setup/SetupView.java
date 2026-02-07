package battleship.view.setup;

import battleship.controller.actions.GridInteractionObserver;
import battleship.controller.setup.SetupGridHandler;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public interface SetupView {
    /**
     * Retrieves the currently selected orientation that is used for ship placement
     * on the player's grid during the setup phase of the Battleship game.
     *
     * @return the selected {@link Orientation}, which indicates the direction
     * (e.g., horizontal or vertical) for the ship being placed.
     */
    Orientation getSelectedOrientation();

    /**
     * Retrieves the type of ship currently selected by the player.
     * This method is typically used during the setup phase of the game
     * to determine which ship type the player intends to place on the grid.
     *
     * @return the currently selected {@code ShipType}, or {@code null} if no ship type is selected
     */
    ShipType getSelectedShipType();

    /**
     * Displays a preview of the ship placement during the setup phase in the Battleship game.
     * This method visually highlights the specified coordinates on the player's grid and indicates
     * whether the placement of the ship is valid or not. The preview reflects the ship's orientation,
     * type, and position based on the provided parameters.
     *
     * @param coord     the set of coordinates on the grid where the ship is being placed
     * @param validShip a boolean indicating whether the current placement of the ship is valid
     * @param ship      the ship being placed, containing its type, coordinates, and orientation
     */
    void showPlacementPreview(LinkedHashSet<Coordinate> coord, boolean validShip, Ship ship);

        /**
        * Plays an error sound to provide auditory feedback to the player when an invalid action occurs,
        * such as attempting to place a ship in an invalid location or selecting an unavailable ship type
        * during the setup phase of the Battleship game.
        */
    void playerErrorSound();

    void open();

    void setObserver(SetupGridHandler observer);

    void updateShipButtons(Map<ShipType, Integer> placedShip,
                           Map<ShipType, Integer> fleetConfiguration);

    void updateSetupGrid(String gridSerialized, List<Ship> fleetToRender);
}
