package com.isims.smartcampus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.isims.smartcampus.network.ApiService;
import com.isims.smartcampus.network.RetrofitClient;
import com.isims.smartcampus.network.UserPointsDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EcoPointsActivity extends AppCompatActivity {

    private EditText editUserId;
    private ProgressBar progressBar;
    private LinearLayout pointsCard;
    private TextView textUserName;
    private TextView textPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_points);

        editUserId = findViewById(R.id.edit_user_id);
        progressBar = findViewById(R.id.progress_bar);
        pointsCard = findViewById(R.id.points_card);
        textUserName = findViewById(R.id.text_user_name);
        textPoints = findViewById(R.id.text_points);

        Button btnCheck = findViewById(R.id.btn_check_points);
        btnCheck.setOnClickListener(v -> checkPoints());
    }

    private void checkPoints() {
        String userId = editUserId.getText().toString().trim();
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please enter your User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        pointsCard.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getUserPoints(userId).enqueue(new Callback<UserPointsDto>() {
            @Override
            public void onResponse(Call<UserPointsDto> call, Response<UserPointsDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    UserPointsDto dto = response.body();
                    textUserName.setText(dto.getName());
                    textPoints.setText(String.valueOf(dto.getTotalEcoPoints()));
                    pointsCard.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(EcoPointsActivity.this,
                            "User not found or server error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserPointsDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EcoPointsActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
