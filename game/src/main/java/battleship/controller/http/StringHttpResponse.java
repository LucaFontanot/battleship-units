package battleship.controller.http;

public interface StringHttpResponse {
    void onSuccess(String response);
    void onFailure(String response);
}
