package battleship.controller.game.network;

import battleship.model.game.Grid;
import battleship.serializer.GameDataMapper;
import it.units.battleship.Coordinate;
import it.units.battleship.Defaults;
import it.units.battleship.GridMapper;
import it.units.battleship.data.LobbyData;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;

public class SinglePlayerClient extends AbstractPlayerCommunication{
    private final String playerName;
    private final LobbyData lobbyData;

    public SinglePlayerClient(LobbyData data, String playerName) {
        this.playerName = playerName;
        this.lobbyData = data;
    }

    Grid hostGrid;
    Grid computerGrid;

    @Override
    public <T> void sendMessage(GameMessageType type, T payload) {
        switch (type){
            case GAME_SETUP -> {
                generateAndSendGameSetup();
            }
            case GRID_UPDATE -> {
                GridUpdateDTO gridUpdateDTO = (GridUpdateDTO) payload;
                hostGrid = new Grid(GridMapper.deserialize(gridUpdateDTO.gridSerialized(), Defaults.GRID_COLS, Defaults.GRID_ROWS));
            }
            case SHOT_REQUEST -> {
                ShotRequestDTO shotRequestDTO = (ShotRequestDTO) payload;
                Coordinate shotCoord = GameDataMapper.toCoordinate(shotRequestDTO);
                handleIncomingShot(shotCoord);
            }
            case TURN_CHANGE -> {
                generateAndSendShot();
            }
        }
    }

    void generateAndSendGameSetup(){
        // Generate game setup as the computer player and send it to the controller

        //Generate random grid for computer player
    }

    void handleIncomingShot(Coordinate shotCoord){
        // Process incoming shot from the controller, update the computer grid and send back the result
    }

    void generateAndSendShot(){
        // Generate a shot coordinate for the computer player and send it to the controller
    }
}
