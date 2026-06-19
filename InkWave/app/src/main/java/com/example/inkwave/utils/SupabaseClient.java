package com.example.inkwave.utils;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class SupabaseClient {
    public static final String SUPABASE_URL = "https://gnuopbwpvpwpaxafkfcx.supabase.co";
    public static final String API_KEY = "sb_publishable_h3eKNRH-lO4IV3Ebeg9AYg_FSwRbVsz";

    private static final OkHttpClient client = new OkHttpClient();

    public interface AuthCallback {
        void onSuccess();
        void onError(String error);
    }

    // ====== РЕГИСТРАЦИЯ ======
    public static void signUp(String email, String password, String name, AuthCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            json.put("data", new JSONObject().put("name", name));

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/auth/v1/signup")
                    .post(body)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject user = new JSONObject(responseBody);
                            String userId = user.getString("id");
                            createProfile(userId, email, name, callback);
                        } catch (Exception e) {
                            callback.onError("Ошибка создания профиля: " + e.getMessage());
                        }
                    } else {
                        try {
                            JSONObject error = new JSONObject(responseBody);
                            String msg = error.optString("msg", "Неизвестная ошибка");
                            callback.onError(msg);
                        } catch (Exception e) {
                            callback.onError("Ошибка: " + response.code());
                        }
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // ====== ВХОД ======
    public static void signIn(String email, String password, AuthCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/auth/v1/token?grant_type=password")
                    .post(body)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        try {
                            JSONObject error = new JSONObject(responseBody);
                            String msg = error.optString("msg", "Неверный email или пароль");
                            callback.onError(msg);
                        } catch (Exception e) {
                            callback.onError("Ошибка входа");
                        }
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // ====== СОЗДАНИЕ ПРОФИЛЯ ======
    private static void createProfile(String userId, String email, String name, AuthCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", userId);
            json.put("email", email);
            json.put("name", name);
            json.put("role", "author");

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/profiles")
                    .post(body)
                    .addHeader("apikey", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Ошибка создания профиля: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // ====== ДЛЯ RETROFIT ======
    private static retrofit2.Retrofit retrofit = null;

    public static retrofit2.Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(SUPABASE_URL + "/rest/v1/")
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}