package it.units.battleship.client.http;

public interface StringHttpResponse {
    void onSuccess(String response);

    void onFailure(String response);
}
