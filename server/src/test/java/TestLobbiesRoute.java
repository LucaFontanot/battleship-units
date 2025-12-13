import com.google.gson.Gson;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.LobbiesResponseData;
import it.units.battleship.data.LobbyCreateRequestData;
import it.units.battleship.data.LobbyData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestLobbiesRoute {
    Thread webServerThread;
    Gson gson = new Gson();

    @BeforeEach
    public void setup() {
        WebServerApp webServerApp = new WebServerApp(7000);
        webServerThread = new Thread(webServerApp);
        webServerThread.start();
    }

    @AfterEach
    public void teardown() {
        webServerThread.interrupt();
    }

    @Test
    public void testLobbiesRoute() {
        testLobbyCreationRoute();
        Request request = new Request.Builder()
                .url("http://localhost:7000/api/lobbies")
                .get()
                .build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            assertEquals(200, response.code());
            assertNotNull(response.body());
            LobbiesResponseData lobbiesResponseData = gson.fromJson(response.body().string(), LobbiesResponseData.class);
            assertNotNull(lobbiesResponseData);
            assertEquals(1, lobbiesResponseData.getCount());
            assertNotNull(lobbiesResponseData.getResults());
            assertEquals(1, lobbiesResponseData.getResults().size());
            LobbyData lobbyData = lobbiesResponseData.getResults().get(0);
            assertEquals("Test Lobby", lobbyData.getLobbyName());
            assertEquals("Player1", lobbyData.getPlayerOne());
            assertNotNull(lobbyData.getLobbyID());
            assertFalse(lobbyData.getLobbyID().isEmpty());
        } catch (Exception e) {
            fail("Exception during testLobbiesRoute: " + e.getMessage());
        }
    }

    @Test
    public void testLobbyCreationRoute() {
        Request request = new Request.Builder()
                .url("http://localhost:7000/api/lobbies")
                .post(okhttp3.RequestBody.create(
                        gson.toJson(new LobbyCreateRequestData("Test Lobby", "Player1")),
                        okhttp3.MediaType.parse("application/json")))
                .build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            assertEquals(201, response.code());
            assertNotNull(response.body());
            LobbyData lobbyData = gson.fromJson(response.body().string(), it.units.battleship.data.LobbyData.class);
            assertNotNull(lobbyData);
            assertEquals("Test Lobby", lobbyData.getLobbyName());
            assertEquals("Player1", lobbyData.getPlayerOne());
            System.out.println("Created Lobby ID: " + lobbyData.getLobbyID());
        } catch (Exception e) {
            fail("Exception during testLobbyCreationRoute: " + e.getMessage());
        }
    }
}
