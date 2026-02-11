package it.units.battleship.client.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import static it.units.battleship.client.http.HttpClient.JSON;
import static it.units.battleship.client.http.HttpClient.client;

public class JsonHttpClient<R, E> {
    final TypeToken<R> responseClass;
    final TypeToken<E> errorClass;
    final HashMap<String, String> headers = new HashMap<>();
    GsonBuilder gsonBuilder = new GsonBuilder();

    /**
     * Constructs a new JSON HTTP request with the specified response and error types.
     *
     * @param responseClass the class of the success response type
     * @param errorClass    the class of the error type
     */
    public JsonHttpClient(Class<R> responseClass, Class<E> errorClass) {
        gsonBuilder.serializeNulls();
        this.responseClass = TypeToken.get(responseClass);
        this.errorClass = TypeToken.get(errorClass);
    }

    /**
     * Constructs a new JSON HTTP request with the specified response and error TypeTokens.
     *
     * @param responseClass the TypeToken of the success response type
     * @param errorClass    the TypeToken of the error type
     */
    public JsonHttpClient(TypeToken<R> responseClass, TypeToken<E> errorClass) {
        gsonBuilder.serializeNulls();
        this.responseClass = responseClass;
        this.errorClass = errorClass;
    }

    /**
     * Constructs a new JSON HTTP request with TypeToken for response and Class for error.
     *
     * @param responseClass the TypeToken of the success response type
     * @param errorClass    the class of the error type
     */
    public JsonHttpClient(TypeToken<R> responseClass, Class<E> errorClass) {
        gsonBuilder.serializeNulls();
        this.responseClass = responseClass;
        this.errorClass = TypeToken.get(errorClass);
    }

    /**
     * Registers a custom type adapter for serialization/deserialization of a specific type.
     *
     * @param classz     the class to register the type adapter for
     * @param serializer the serializer/deserializer object
     */
    public void registerTypeAdapter(Class<?> classz, Object serializer) {
        gsonBuilder.registerTypeAdapter(classz, serializer);
    }

    /**
     * Adds an HTTP header to the request.
     *
     * @param key   the header name
     * @param value the header value
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Gets the configured Gson instance for serialization/deserialization.
     *
     * @return the Gson instance
     */
    Gson getGson() {
        return gsonBuilder.create();
    }

    /**
     * Enqueues an asynchronous HTTP request and handles the response via callback.
     *
     * @param callback the callback to invoke upon request completion
     * @param request  the HTTP request to execute
     */
    private void enqueue(JsonHttpResponse<R, E> callback, Request request) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(e.getMessage(), null, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        callback.onFailure("Response body is null", null, response);
                        return;
                    }
                    String responseString = responseBody.string();
                    if (response.isSuccessful()) {
                        R t = getGson().fromJson(responseString, responseClass);
                        callback.onSuccess(response, t);
                    } else {
                        if (errorClass.equals(TypeToken.get(Void.class))) {
                            callback.onFailure("API Error", null, response);
                            return;
                        }
                        E e = getGson().fromJson(responseString, errorClass);
                        callback.onFailure("API Error", e, response);
                    }
                }
            }
        });
    }

    /**
     * Executes an asynchronous HTTP GET request.
     *
     * @param url      the request URL
     * @param callback the callback to invoke upon request completion
     */
    public void get(String url, JsonHttpResponse<R, E> callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(Headers.of(headers))
                .build();
        enqueue((JsonHttpResponse<R, E>) callback, request);
    }

    /**
     * Executes an asynchronous HTTP POST request with JSON data in the body.
     *
     * @param <T>      the type of data to send
     * @param url      the request URL
     * @param data     the data to serialize and send in the body
     * @param callback the callback to invoke upon request completion
     */
    public <T> void post(String url, T data, JsonHttpResponse<R, E> callback) {
        RequestBody body = RequestBody.create(getGson().toJson(data), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(Headers.of(headers))
                .build();
        enqueue((JsonHttpResponse<R, E>) callback, request);
    }

    /**
     * Executes an asynchronous HTTP PUT request with JSON data in the body.
     *
     * @param <T>      the type of data to send
     * @param url      the request URL
     * @param data     the data to serialize and send in the body
     * @param callback the callback to invoke upon request completion
     */
    public <T> void put(String url, T data, JsonHttpResponse<R, E> callback) {
        RequestBody body = RequestBody.create(getGson().toJson(data), JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .headers(Headers.of(headers))
                .build();
        enqueue((JsonHttpResponse<R, E>) callback, request);
    }

    /**
     * Executes an asynchronous HTTP DELETE request.
     *
     * @param url      the request URL
     * @param callback the callback to invoke upon request completion
     */
    public void delete(String url, JsonHttpResponse<R, E> callback) {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .headers(Headers.of(headers))
                .build();
        enqueue((JsonHttpResponse<R, E>) callback, request);
    }

    /**
     * Executes an asynchronous HTTP PATCH request.
     *
     * @param url      the request URL
     * @param callback the callback to invoke upon request completion
     */
    public void patch(String url, JsonHttpResponse<R, E> callback) {
        Request request = new Request.Builder()
                .url(url)
                .patch(RequestBody.create(new byte[0], null))
                .headers(Headers.of(headers))
                .build();
        enqueue((JsonHttpResponse<R, E>) callback, request);
    }

    /**
     * Executes a synchronous HTTP GET request and returns the deserialized response.
     *
     * @param url the request URL
     * @return the deserialized response
     * @throws JsonHttpException if an error occurs during the request or if the response is an error
     */
    public R getSync(String url) throws JsonHttpException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(Headers.of(headers))
                .build();
        return parseSyncResponse(request);
    }

    /**
     * Executes a synchronous HTTP POST request with JSON data in the body and returns the deserialized response.
     *
     * @param url  the request URL
     * @param data the data to serialize and send in the body
     * @return the deserialized response
     * @throws JsonHttpException if an error occurs during the request or if the response is an error
     */
    public R postSync(String url, Object data) throws JsonHttpException {
        RequestBody body = RequestBody.create(getGson().toJson(data), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(Headers.of(headers))
                .build();
        return parseSyncResponse(request);
    }

    /**
     * Executes a synchronous HTTP PUT request with JSON data in the body and returns the deserialized response.
     *
     * @param url  the request URL
     * @param data the data to serialize and send in the body
     * @return the deserialized response
     * @throws JsonHttpException if an error occurs during the request or if the response is an error
     */
    public R putSync(String url, Object data) throws JsonHttpException {
        RequestBody body = RequestBody.create(getGson().toJson(data), JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .headers(Headers.of(headers))
                .build();
        return parseSyncResponse(request);
    }

    /**
     * Executes a synchronous HTTP DELETE request and returns the deserialized response.
     *
     * @param url the request URL
     * @return the deserialized response
     * @throws JsonHttpException if an error occurs during the request or if the response is an error
     */
    public R deleteSync(String url) throws JsonHttpException {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .headers(Headers.of(headers))
                .build();
        return parseSyncResponse(request);
    }

    /**
     * Executes a synchronous HTTP PATCH request and returns the deserialized response.
     *
     * @param url the request URL
     * @return the deserialized response
     * @throws JsonHttpException if an error occurs during the request or if the response is an error
     */
    public R patchSync(String url) throws JsonHttpException {
        Request request = new Request.Builder()
                .url(url)
                .patch(RequestBody.create(new byte[0], null))
                .headers(Headers.of(headers))
                .build();
        return parseSyncResponse(request);
    }

    /**
     * Parses the response from a synchronous request and returns the deserialized result.
     *
     * @param request the HTTP request to execute
     * @return the deserialized response
     * @throws JsonHttpException if an error occurs during the request or if the response is an error
     */
    private R parseSyncResponse(Request request) throws JsonHttpException {
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new JsonHttpException("Response body is null", null, response);
            }
            String responseString = response.body().string();
            if (response.isSuccessful()) {
                return getGson().fromJson(responseString, responseClass);
            } else {
                if (errorClass.equals(TypeToken.get(Void.class))) {
                    throw new JsonHttpException("API Error", null, response);
                }
                E e = getGson().fromJson(responseString, errorClass);
                throw new JsonHttpException("API Error", e, response);
            }
        } catch (Exception e) {
            throw new JsonHttpException(e.getMessage(), null, null);
        }
    }
}
