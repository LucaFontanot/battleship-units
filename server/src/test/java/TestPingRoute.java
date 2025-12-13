import com.google.gson.Gson;
import it.units.battleship.BuildConstants;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.PingResponseData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPingRoute {
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
    public void testPingRoute() {
        Request request = new Request.Builder()
                .url("http://localhost:7000/api/ping")
                .get()
                .build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            assertEquals(200, response.code());
            assertNotNull(response.body());
            String responseBody = response.body().string();
            PingResponseData pingResponseData = gson.fromJson(responseBody, PingResponseData.class);
            assertTrue(Math.abs(pingResponseData.getServerTime() - System.currentTimeMillis()) < 100);
            assertEquals(BuildConstants.VERSION, pingResponseData.getServerVersion());
        } catch (Exception e) {
            fail("Exception during testPingRoute: " + e.getMessage());
        }
    }
}
