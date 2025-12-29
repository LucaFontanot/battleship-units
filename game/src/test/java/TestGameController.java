import battleship.controller.GameController;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.view.GameView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestGameController {

    @Mock
    private GameView mockView;

    @Mock
    private Grid mockGrid;

    @Mock
    private FleetManager mockFleetManager;

    private GameController gameController;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        gameController = new GameController(mockGrid, mockFleetManager, mockView);
    }

    @Test
    void testInitialization(){
        assertNotNull(gameController, "Game controller was not initialized correctly.");
    }

}
