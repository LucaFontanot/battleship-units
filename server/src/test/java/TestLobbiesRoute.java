import com.google.gson.Gson;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.LobbiesResponseData;
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
        Request request = new Request.Builder()
                .url("http://localhost:7000/api/lobbies")
                .get()
                .build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            assertEquals(200, response.code());
            assertNotNull(response.body());
            LobbiesResponseData lobbiesResponseData = gson.fromJson(response.body().string(), LobbiesResponseData.class);
            assertNotNull(lobbiesResponseData);
            assertTrue(lobbiesResponseData.getCount() >= 0);
            assertNotNull(lobbiesResponseData.getResults());
        } catch (Exception e) {
            fail("Exception during testLobbiesRoute: " + e.getMessage());
        }
    }
}
