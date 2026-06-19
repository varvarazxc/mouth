package com.example.inkwave.network;

import com.example.inkwave.models.Book;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SupabaseService {
    @GET("books?select=*")
    Call<List<Book>> getBooks(@Header("apikey") String apiKey);
}