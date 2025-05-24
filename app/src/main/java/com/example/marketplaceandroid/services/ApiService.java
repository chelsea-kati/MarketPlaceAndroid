package com.example.marketplaceandroid.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.example.marketplaceandroid.models.User;
import com.example.marketplaceandroid.utils.NetworkUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiService {

    private static final String TAG = "ApiService";

    // Modifie ici ton IP si tu testes sur un device physique
    private static final String BASE_URL = "http://172.20.10.14:8080/api/auth";

    private static final String PREF_NAME = "marketplace_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER = "user_data";

    private static ApiService instance;

    private final OkHttpClient httpClient;
    private final Gson gson;
    private final SharedPreferences sharedPreferences;
    private final ExecutorService executorService;

    private ApiService(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        gson = new Gson();
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        executorService = Executors.newFixedThreadPool(4);
    }

    public static synchronized ApiService getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService(context.getApplicationContext());
        }
        return instance;
    }

    // Interface callback
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public boolean isLoggedIn() {
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        return token != null && !token.isEmpty();
    }

    public User getCurrentUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void login(String email, String password, ApiCallback<User> callback) {
        if (!NetworkUtils.isNetworkAvailable()) {
            callback.onError("Pas de connexion internet");
            return;
        }
        executorService.execute(() -> {
            try {
                JsonObject loginData = new JsonObject();
                loginData.addProperty("email", email);
                loginData.addProperty("password", password);

                RequestBody body = RequestBody.create(
                        gson.toJson(loginData),
                        MediaType.get("application/json")
                );

                Request request = new Request.Builder()
                        .url(BASE_URL + "/auth/login")
                        .post(body)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    String responseBody = response.body().string();

                    if (response.isSuccessful()) {
                        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                        String token = jsonResponse.get("token").getAsString();
                        User user = gson.fromJson(jsonResponse.get("user"), User.class);
                        saveUserData(token, user);
                        callback.onSuccess(user);
                    } else {
                        String errorMessage = parseErrorMessage(responseBody);
                        callback.onError(errorMessage);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Erreur lors de la connexion", e);
                callback.onError("Erreur de connexion: " + e.getMessage());
            }
        });
    }

    public void register(User user, String password, ApiCallback<User> callback) {
        if (!NetworkUtils.isNetworkAvailable()) {
            callback.onError("Pas de connexion internet");
            return;
        }
        executorService.execute(() -> {
            try {
                JsonObject registerData = new JsonObject();
                registerData.addProperty("nom", user.getNom());
                registerData.addProperty("email", user.getEmail());
                registerData.addProperty("telephone", user.getTelephone());
                registerData.addProperty("adresse", user.getAdresse());
                registerData.addProperty("password", password);

                RequestBody body = RequestBody.create(
                        gson.toJson(registerData),
                        MediaType.get("application/json")
                );

                Request request = new Request.Builder()
                        .url(BASE_URL + "/auth/register")
                        .post(body)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    String responseBody = response.body().string();

                    if (response.isSuccessful()) {
                        User registeredUser = gson.fromJson(responseBody, User.class);
                        callback.onSuccess(registeredUser);
                    } else {
                        String errorMessage = parseErrorMessage(responseBody);
                        callback.onError(errorMessage);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Erreur lors de l'inscription", e);
                callback.onError("Erreur d'inscription: " + e.getMessage());
            }
        });
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER);
        editor.apply();
    }

    private void saveUserData(String token, User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }

    private String parseErrorMessage(String responseBody) {
        try {
            JsonObject errorJson = gson.fromJson(responseBody, JsonObject.class);
            if (errorJson.has("message")) {
                return errorJson.get("message").getAsString();
            } else if (errorJson.has("error")) {
                return errorJson.get("error").getAsString();
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du parsing de l'erreur", e);
        }
        return "Erreur inconnue";
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
}
