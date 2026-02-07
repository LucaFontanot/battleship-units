package battleship.controller.http;

import lombok.Getter;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

@Getter
public class JsonHttpException extends Exception {

    final Object responseBody;
    final Response response;

    public JsonHttpException(String message, @Nullable Object responseBody, @Nullable Response response) {
        super(message);
        this.responseBody = responseBody;
        this.response = response;
    }
}
