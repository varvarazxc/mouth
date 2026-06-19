package com.example.inkwave.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.inkwave.R;
import com.example.inkwave.utils.SupabaseClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен быть минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Регистрация...");

        SupabaseClient.signUp(email, password, name, new SupabaseClient.AuthCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "✅ Регистрация успешна!", Toast.LENGTH_LONG).show();
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Зарегистрироваться");
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "❌ Ошибка: " + error, Toast.LENGTH_LONG).show();
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Зарегистрироваться");
                });
            }
        });
    }

    public void goToLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}