package com.example.inkwave.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inkwave.R;
import com.example.inkwave.adapters.BooksAdapter;
import com.example.inkwave.models.Book;
import com.example.inkwave.network.SupabaseService;
import com.example.inkwave.utils.SupabaseClient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Book> bookList = new ArrayList<>();
    private BooksAdapter adapter;
    private MaterialButton btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnProfile = findViewById(R.id.btnProfile);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BooksAdapter(bookList, book -> {
            Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
            intent.putExtra("bookId", book.getId().toString());
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthorName());
            intent.putExtra("description", book.getDescription());
            intent.putExtra("views", book.getViews());
            intent.putExtra("likes", book.getLikes());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadBooksFromSupabase();

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
    }

    private void loadBooksFromSupabase() {
        SupabaseService service = SupabaseClient.getClient().create(SupabaseService.class);
        Call<List<Book>> call = service.getBooks(SupabaseClient.API_KEY);

        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookList.clear();
                    bookList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Загружено: " + bookList.size() + " книг", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}