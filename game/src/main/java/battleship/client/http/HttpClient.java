package battleship.client.http;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class HttpClient {

    public static final MediaType JSON = MediaType.get("application/json");

    // Configure OkHttpClient with reasonable timeouts to prevent hanging requests
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .build();

}
