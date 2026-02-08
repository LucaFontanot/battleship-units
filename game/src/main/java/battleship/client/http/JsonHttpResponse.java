package battleship.client.http;

import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

public interface JsonHttpResponse <T, E> {
    void onSuccess(Response response, T data);
    void onFailure(@Nullable String error, E response, @Nullable Response httpResponse);
}
