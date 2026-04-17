package com.isims.smartcampus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.isims.smartcampus.network.ApiService;
import com.isims.smartcampus.network.LoginRequestBody;
import com.isims.smartcampus.network.LoginResponseDto;
import com.isims.smartcampus.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUserId;
    private TextInputEditText etPassword;
    private AutoCompleteTextView spinnerRole;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if already logged in
        SharedPreferences prefs = getSharedPreferences("SmartCampusPrefs", Context.MODE_PRIVATE);
        if (prefs.contains("userId")) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        etUserId = findViewById(R.id.et_user_id);
        etPassword = findViewById(R.id.et_password);
        spinnerRole = findViewById(R.id.spinner_role);
        btnLogin = findViewById(R.id.btn_login);

        // Setup Dropdown Options
        String[] roles = new String[]{"Student", "Professor", "Admin", "Maintenance"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        spinnerRole.setAdapter(adapter);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String userId = etUserId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String selectedRole = spinnerRole.getText().toString().trim();

        if (userId.isEmpty() || password.isEmpty() || selectedRole.isEmpty()) {
            Toast.makeText(this, "Please enter all fields and select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.login(new LoginRequestBody(userId, password)).enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseDto loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        
                        // Validate selected role matches server role
                        String serverRole = loginResponse.getRole();
                        if (!serverRole.equalsIgnoreCase(selectedRole)) {
                            Toast.makeText(LoginActivity.this, "Account type does not match selected role", Toast.LENGTH_LONG).show();
                            return;
                        }

                        saveSession(loginResponse.getUserId(), serverRole);
                        navigateToMain();
                    } else {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSession(String userId, String role) {
        SharedPreferences prefs = getSharedPreferences("SmartCampusPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.putString("role", role);
        editor.apply();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
