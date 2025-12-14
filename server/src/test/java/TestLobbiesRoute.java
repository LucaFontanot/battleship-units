import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.LobbiesResponseData;
import it.units.battleship.data.LobbyCreateRequestData;
import it.units.battleship.data.LobbyData;
import it.units.battleship.data.socket.WebSocketAuthenticationRequest;
import it.units.battleship.data.socket.WebSocketMessage;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestLobbiesRoute {
    Thread webServerThread;
    WebServerApp webServerApp;
    Gson gson = new Gson();


    @BeforeEach
    @SneakyThrows
    public void setup() {
        webServerApp = new WebServerApp(7000);
        webServerThread = new Thread(webServerApp);
        webServerThread.start();
        Thread.sleep(100);
    }

    @AfterEach
    public void teardown() {
        webServerApp.close();
        webServerThread.interrupt();
    }

    @Test
    public void testLobbiesRoute() {
        String lobbyID = getLobby();
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
            assertEquals(lobbyID, lobbyData.getLobbyID());
            assertFalse(lobbyData.getLobbyID().isEmpty());
        } catch (Exception e) {
            fail("Exception during testLobbiesRoute: " + e.getMessage());
        }
    }

    String getLobby() {
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
            return lobbyData.getLobbyID();
        } catch (Exception e) {
            fail("Exception during testLobbyCreationRoute: " + e.getMessage());
        }
        return null;
    }

    @Test
    public void testLobbyCreationRoute() {
        getLobby();
    }


    @Test
    @SneakyThrows
    public void testWebsocketConnectionFailLobbyNotFound() {
        Request request = new Request.Builder()
                .url("ws://localhost:7000/api/lobbies")
                .build();
        AbstractWebsocket socket = new AbstractWebsocket() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket opened");
                assertEquals(101, response.code());
            }
        };
        WebSocket webSocket = new OkHttpClient().newWebSocket(request, socket);
        Thread.sleep(1000); // wait for connection to establish
        WebSocketAuthenticationRequest webSocketAuthenticationRequest = new WebSocketAuthenticationRequest("non-existent-lobby-id", "PlayerX");
        WebSocketMessage<WebSocketAuthenticationRequest> authMessage = new WebSocketMessage<>("authenticate", webSocketAuthenticationRequest);
        String authMessageJson = gson.toJson(authMessage);
        webSocket.send(authMessageJson);
        WebSocketMessage<Boolean> responseMessage = gson.fromJson(socket.waitForMessage(), new TypeToken<WebSocketMessage<Boolean>>() {
        }.getType());
        assertEquals("authenticate", responseMessage.getType());
        assertFalse(responseMessage.getData());
        WebSocketMessage<String> errorMessage = gson.fromJson(socket.waitForMessage(), new TypeToken<WebSocketMessage<String>>() {
        }.getType());
        assertEquals("error", errorMessage.getType());
        assertEquals("Lobby not found", errorMessage.getData());
        webSocket.close(1000, "Test complete");
    }

    @Test
    @SneakyThrows
    public void testWebsocketConnectionSuccess() {
        String lobbyID = getLobby();
        Request request = new Request.Builder()
                .url("ws://localhost:7000/api/lobbies")
                .build();
        AbstractWebsocket socket = new AbstractWebsocket() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket opened");
                assertEquals(101, response.code());
            }
        };
        WebSocket webSocket = new OkHttpClient().newWebSocket(request, socket);
        Thread.sleep(1000); // wait for connection to establish
        WebSocketAuthenticationRequest webSocketAuthenticationRequest = new WebSocketAuthenticationRequest(lobbyID, "Player2");
        WebSocketMessage<WebSocketAuthenticationRequest> authMessage = new WebSocketMessage<>("authenticate", webSocketAuthenticationRequest);
        String authMessageJson = gson.toJson(authMessage);
        webSocket.send(authMessageJson);
        WebSocketMessage<Boolean> responseMessage = gson.fromJson(socket.waitForMessage(), new TypeToken<WebSocketMessage<Boolean>>() {
        }.getType());
        assertEquals("authenticate", responseMessage.getType());
        assertTrue(responseMessage.getData());
        WebSocketMessage<LobbyData> lobbyUpdateMessage = gson.fromJson(socket.waitForMessage(), new TypeToken<WebSocketMessage<LobbyData>>() {
        }.getType());
        assertEquals("lobby", lobbyUpdateMessage.getType());
        webSocket.close(1000, "Test complete");
    }

    @Test
    @SneakyThrows
    public void testWebsocketConnectionFailLobbyFull() {
        String lobbyID = getLobby();
        Request request = new Request.Builder()
                .url("ws://localhost:7000/api/lobbies")
                .build();
        AbstractWebsocket socket1 = new AbstractWebsocket() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket 1 opened");
                assertEquals(101, response.code());
            }
        };
        WebSocket webSocket1 = new OkHttpClient().newWebSocket(request, socket1);
        Thread.sleep(500); // wait for connection to establish
        WebSocketAuthenticationRequest webSocketAuthenticationRequest1 = new WebSocketAuthenticationRequest(lobbyID, "Player1");
        WebSocketMessage<WebSocketAuthenticationRequest> authMessage1 = new WebSocketMessage<>("authenticate", webSocketAuthenticationRequest1);
        webSocket1.send(gson.toJson(authMessage1));
        WebSocketMessage<Boolean> responseMessage1 = gson.fromJson(socket1.waitForMessage(), new TypeToken<WebSocketMessage<Boolean>>() {
        }.getType());
        assertEquals("authenticate", responseMessage1.getType());
        assertTrue(responseMessage1.getData());
        WebSocketMessage<LobbyData> lobbyUpdateMessage1 = gson.fromJson(socket1.waitForMessage(), new TypeToken<WebSocketMessage<LobbyData>>() {
        }.getType());
        assertEquals("lobby", lobbyUpdateMessage1.getType());
        assertEquals(lobbyID, lobbyUpdateMessage1.getData().getLobbyID());
        assertEquals("Player1", lobbyUpdateMessage1.getData().getPlayerOne());
        AbstractWebsocket socket2 = new AbstractWebsocket() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket 2 opened");
                assertEquals(101, response.code());
            }
        };
        WebSocket webSocket2 = new OkHttpClient().newWebSocket(request, socket2);
        Thread.sleep(500);
        WebSocketAuthenticationRequest webSocketAuthenticationRequest2 = new WebSocketAuthenticationRequest(lobbyID, "Player2");
        WebSocketMessage<WebSocketAuthenticationRequest> authMessage2 = new WebSocketMessage<>("authenticate", webSocketAuthenticationRequest2);
        webSocket2.send(gson.toJson(authMessage2));
        WebSocketMessage<Boolean> responseMessage2 = gson.fromJson(socket2.waitForMessage(), new TypeToken<WebSocketMessage<Boolean>>() {
        }.getType());
        assertEquals("authenticate", responseMessage2.getType());
        assertTrue(responseMessage2.getData());
        WebSocketMessage<LobbyData> lobbyUpdateMessage2 = gson.fromJson(socket2.waitForMessage(), new TypeToken<WebSocketMessage<LobbyData>>() {
        }.getType());
        assertEquals("lobby", lobbyUpdateMessage2.getType());
        assertEquals(lobbyID, lobbyUpdateMessage2.getData().getLobbyID());
        assertEquals("Player1", lobbyUpdateMessage2.getData().getPlayerOne());
        assertEquals("Player2", lobbyUpdateMessage2.getData().getPlayerTwo());
        AbstractWebsocket socket3 = new AbstractWebsocket() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket 3 opened");
                assertEquals(101, response.code());
            }
        };
        WebSocket webSocket3 = new OkHttpClient().newWebSocket(request, socket3);
        Thread.sleep(500);
        WebSocketAuthenticationRequest webSocketAuthenticationRequest3 = new WebSocketAuthenticationRequest(lobbyID, "Player3");
        WebSocketMessage<WebSocketAuthenticationRequest> authMessage3 = new WebSocketMessage<>("authenticate", webSocketAuthenticationRequest3);
        webSocket3.send(gson.toJson(authMessage3));
        WebSocketMessage<Boolean> responseMessage3 = gson.fromJson(socket3.waitForMessage(), new TypeToken<WebSocketMessage<Boolean>>() {
        }.getType());
        assertEquals("authenticate", responseMessage3.getType());
        assertFalse(responseMessage3.getData());
        WebSocketMessage<String> errorMessage = gson.fromJson(socket3.waitForMessage(), new TypeToken<WebSocketMessage<String>>() {
        }.getType());
        assertEquals("error", errorMessage.getType());
        assertEquals("Lobby is full", errorMessage.getData());
        webSocket1.close(1000, "Test complete");
        webSocket2.close(1000, "Test complete");
        webSocket3.close(1000, "Test complete");
    }

    @Test
    @SneakyThrows
    public void testForwardMessage() {
        String lobbyID = getLobby();
        Request request = new Request.Builder()
                .url("ws://localhost:7000/api/lobbies")
                .build();
        AbstractWebsocket socket1 = new AbstractWebsocket() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket 1 opened");
                assertEquals(101, response.code());
            }
        };
        WebSocket webSocket1 = new OkHttpClient().newWebSocket(request, socket1);
        Thread.sleep(500); // wait for connection to establish
        WebSocketAuthenticationRequest webSocketAuthenticationRequest1 = new WebSocketAuthenticationRequest(lobbyID, "Player1");
        WebSocketMessage<WebSocketAuthenticationRequest> authMessage1 = new WebSocketMessage<>("authenticate", webSocketAuthenticationRequest1);
        webSocket1.send(gson.toJson(authMessage1));
        WebSocketMessage<Boolean> responseMessage1 = gson.fromJson(socket1.waitForMessage(), new TypeToken<WebSocketMessage<Boolean>>() {
        }.getType());
        assertEquals("authenticate", responseMessage1.getType());
        assertTrue(responseMessage1.getData());
        socket1.waitForMessage(); // consume lobby update message
        AbstractWebsocket socket2 = new AbstractWebsocket() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                System.out.println("WebSocket 2 opened");
                assertEquals(101, response.code());
            }
        };
        WebSocket webSocket2 = new OkHttpClient().newWebSocket(request, socket2);
        Thread.sleep(500);
        WebSocketAuthenticationRequest webSocketAuthenticationRequest2 = new WebSocketAuthenticationRequest(lobbyID, "Player2");
        WebSocketMessage<WebSocketAuthenticationRequest> authMessage2 = new WebSocketMessage<>("authenticate", webSocketAuthenticationRequest2);
        webSocket2.send(gson.toJson(authMessage2));
        WebSocketMessage<Boolean> responseMessage2 = gson.fromJson(socket2.waitForMessage(), new TypeToken<WebSocketMessage<Boolean>>() {
        }.getType());
        assertEquals("authenticate", responseMessage2.getType());
        assertTrue(responseMessage2.getData());
        socket2.waitForMessage(); // consume lobby update message
        WebSocketMessage<String> testMessage = new WebSocketMessage<>("test", "Hello from Player1");
        webSocket1.send(gson.toJson(testMessage));
        WebSocketMessage<String> receivedMessage = gson.fromJson(socket2.waitForMessage(), new TypeToken<WebSocketMessage<String>>() {
        }.getType());
        assertEquals("test", receivedMessage.getType());
        assertEquals("Hello from Player1", receivedMessage.getData());
        webSocket1.close(1000, "Test complete");
        webSocket2.close(1000, "Test complete");
    }
}
