package battleship.controller.handlers;

import it.units.battleship.Coordinate;
import it.units.battleship.ShipType;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestLocalPlayerCommunication {

    @Test
    void computerTargetsAdjacentAfterHit() {
        int gridRows = 5;
        int gridCols = 5;
        LocalPlayerCommunication communication = new LocalPlayerCommunication(
                gridRows,
                gridCols,
                Map.of(ShipType.DESTROYER, 1),
                new FixedRandom()
        );
        ShotCaptureListener listener = new ShotCaptureListener();
        communication.addCommunicationEventsListener(listener);

        communication.sendMessage(GameMessageType.SHOT_REQUEST, new ShotRequestDTO(new Coordinate(4, 4)));
        assertNotNull(listener.lastShotRequest);
        Coordinate firstShot = listener.lastShotRequest.coord();
        assertEquals(new Coordinate(0, 0), firstShot);

        communication.sendMessage(
                GameMessageType.GRID_UPDATE,
                new GridUpdateDTO(true, "0".repeat(gridRows * gridCols), List.of())
        );

        communication.sendMessage(GameMessageType.SHOT_REQUEST, new ShotRequestDTO(new Coordinate(4, 3)));
        assertNotNull(listener.lastShotRequest);
        Coordinate secondShot = listener.lastShotRequest.coord();

        int distance = Math.abs(firstShot.row() - secondShot.row()) + Math.abs(firstShot.col() - secondShot.col());
        assertEquals(1, distance);
    }

    private static final class ShotCaptureListener implements CommunicationEvents {
        private ShotRequestDTO lastShotRequest;

        @Override
        public void onPlayerMessage(String playerName, String message) {
        }

        @Override
        public void onOpponentGridUpdate(GridUpdateDTO gridUpdateDTO) {
        }

        @Override
        public void onShotReceived(ShotRequestDTO shotRequestDTO) {
            this.lastShotRequest = shotRequestDTO;
        }

        @Override
        public void onGameSetupReceived(it.units.battleship.data.socket.payloads.GameConfigDTO gameConfigDTO) {
        }

        @Override
        public void onGameStatusReceived(it.units.battleship.data.socket.payloads.GameStatusDTO gameStatusDTO) {
        }
    }

    private static final class FixedRandom extends Random {
        @Override
        public int nextInt(int bound) {
            return 0;
        }
    }
}
