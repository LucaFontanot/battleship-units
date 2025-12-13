import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractWebsocket extends WebSocketListener {
    String lastMessage = "";
    CountDownLatch latch = null;

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        System.out.println("Received message: " + text);
        lastMessage = text;
        if (latch != null) {
            latch.countDown();
        }
    }

    public String waitForMessage() throws InterruptedException {
        if (lastMessage == null || lastMessage.isEmpty()) {
            latch = new CountDownLatch(1);
            latch.await();
            latch = null;
        }
        String message = lastMessage;
        lastMessage = "";
        return message;
    }


}
