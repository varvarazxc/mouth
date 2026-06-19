package com.example.inkwave.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.inkwave.R;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        try {
            TextView tvName = findViewById(R.id.tvName);
            TextView tvEmail = findViewById(R.id.tvEmail);
            MaterialButton btnLogout = findViewById(R.id.btnLogout);

            tvName.setText("Автор");
            tvEmail.setText("author@inkwave.com");

            btnLogout.setOnClickListener(v -> {
                Toast.makeText(this, "Выход выполнен", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}