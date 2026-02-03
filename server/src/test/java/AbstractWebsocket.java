import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.*;

public abstract class AbstractWebsocket extends WebSocketListener {
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        System.out.println("Received message: " + text);
        messageQueue.offer(text);
    }

    public String waitForMessage() throws InterruptedException {
        String message = messageQueue.poll(5, TimeUnit.SECONDS);
        if (message == null){
            throw new RuntimeException("Timeout waiting for WebSocket message");
        }
        return message;
    }


}
