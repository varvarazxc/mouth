package com.example.inkwave.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.inkwave.R;
import com.example.inkwave.utils.SupabaseClient;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvAuthor, tvDescription, tvViews, tvLikes;
    private MaterialButton btnRead, btnLike, btnComment;

    private String bookId;
    private int currentLikes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        tvTitle = findViewById(R.id.tvBookTitle);
        tvAuthor = findViewById(R.id.tvBookAuthor);
        tvDescription = findViewById(R.id.tvBookDescription);
        tvViews = findViewById(R.id.tvBookViews);
        tvLikes = findViewById(R.id.tvBookLikes);
        btnRead = findViewById(R.id.btnRead);
        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);

        bookId = getIntent().getStringExtra("bookId");
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String description = getIntent().getStringExtra("description");
        int views = getIntent().getIntExtra("views", 0);
        currentLikes = getIntent().getIntExtra("likes", 0);

        tvTitle.setText(title != null ? title : "Название книги");
        tvAuthor.setText(author != null ? "✍️ " + author : "✍️ Автор");
        tvDescription.setText(description != null ? description : "Описание книги...");
        tvViews.setText("👁️ " + views);
        tvLikes.setText("❤️ " + currentLikes);

        btnRead.setOnClickListener(v -> {
            if (bookId == null) {
                Toast.makeText(this, "❌ Ошибка: ID книги не найден", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(BookDetailActivity.this, ReaderActivity.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        });

        btnLike.setOnClickListener(v -> likeBook());

        btnComment.setOnClickListener(v -> showCommentDialog());
    }

    private void likeBook() {
        try {
            JSONObject json = new JSONObject();
            json.put("likes", currentLikes + 1);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(SupabaseClient.SUPABASE_URL + "/rest/v1/books?id=eq." + bookId)
                    .patch(body)
                    .addHeader("apikey", SupabaseClient.API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(BookDetailActivity.this, "❌ Ошибка сети", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        currentLikes++;
                        runOnUiThread(() -> {
                            tvLikes.setText("❤️ " + currentLikes);
                            Toast.makeText(BookDetailActivity.this, "❤️ Лайк +1!", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(BookDetailActivity.this, "❌ Ошибка: " + response.code(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(BookDetailActivity.this, "❌ Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void showCommentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("💬 Добавить комментарий");

        final EditText input = new EditText(this);
        input.setHint("Введите комментарий...");
        builder.setView(input);

        builder.setPositiveButton("Отправить", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                sendComment(text);
            } else {
                Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void sendComment(String text) {
        try {
            JSONObject json = new JSONObject();
            json.put("book_id", Long.parseLong(bookId));
            json.put("user_id", "72eb8e21-c454-47e6-814c-b0c121ca1899");
            json.put("text", text);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(SupabaseClient.SUPABASE_URL + "/rest/v1/comments")
                    .post(body)
                    .addHeader("apikey", SupabaseClient.API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(BookDetailActivity.this, "❌ Ошибка", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(BookDetailActivity.this, "💬 Комментарий отправлен!", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}