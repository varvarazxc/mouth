package com.example.inkwave.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.inkwave.R;
import com.example.inkwave.utils.SupabaseClient;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class ReaderActivity extends AppCompatActivity {

    private TextView tvChapterTitle, tvChapterContent;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvChapterContent = findViewById(R.id.tvChapterContent);

        bookId = getIntent().getStringExtra("bookId");

        if (bookId == null) {
            Toast.makeText(this, "❌ Ошибка: ID книги не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadFirstChapter();
    }

    private void loadFirstChapter() {
        try {
            Request request = new Request.Builder()
                    .url(SupabaseClient.SUPABASE_URL + "/rest/v1/chapters?book_id=eq." + bookId + "&order=order_number.asc&limit=1")
                    .addHeader("apikey", SupabaseClient.API_KEY)
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(ReaderActivity.this, "❌ Ошибка загрузки", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String json = response.body().string();
                            JSONArray chapters = new JSONArray(json);
                            if (chapters.length() > 0) {
                                JSONObject chapter = chapters.getJSONObject(0);
                                String title = chapter.getString("title");
                                String content = chapter.getString("content");
                                runOnUiThread(() -> {
                                    tvChapterTitle.setText(title);
                                    tvChapterContent.setText(content);
                                });
                            } else {
                                runOnUiThread(() -> {
                                    tvChapterTitle.setText("Нет глав");
                                    tvChapterContent.setText("У этой книги пока нет глав.");
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}