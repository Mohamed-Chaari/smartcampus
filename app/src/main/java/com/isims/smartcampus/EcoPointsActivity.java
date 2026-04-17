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

    private LinearLayout containerLeaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_points);

        editUserId = findViewById(R.id.edit_user_id);
        progressBar = findViewById(R.id.progress_bar);
        pointsCard = findViewById(R.id.points_card);
        textUserName = findViewById(R.id.text_user_name);
        textPoints = findViewById(R.id.text_points);
        containerLeaderboard = findViewById(R.id.container_leaderboard);

        // Pre-fill user ID
        android.content.SharedPreferences prefs = getSharedPreferences("SmartCampusPrefs", android.content.Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "");
        editUserId.setText(userId);

        Button btnCheck = findViewById(R.id.btn_check_points);
        btnCheck.setOnClickListener(v -> checkPoints());

        // Automatically load points and leaderboard
        if (!userId.isEmpty()) {
            checkPoints();
        }
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getLeaderboard().enqueue(new Callback<java.util.List<UserPointsDto>>() {
            @Override
            public void onResponse(Call<java.util.List<UserPointsDto>> call, Response<java.util.List<UserPointsDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    containerLeaderboard.removeAllViews();
                    java.util.List<UserPointsDto> leaderboard = response.body();
                    
                    int rank = 1;
                    for (UserPointsDto student : leaderboard) {
                        TextView tv = new TextView(EcoPointsActivity.this);
                        tv.setText(rank + ". " + student.getName() + " (" + student.getUserId() + ") - " + student.getTotalEcoPoints() + " Pts");
                        tv.setTextSize(16);
                        tv.setPadding(0, 8, 0, 8);
                        
                        // Highlight top 3
                        if (rank == 1) tv.setTextColor(android.graphics.Color.parseColor("#FFD700")); // Gold
                        else if (rank == 2) tv.setTextColor(android.graphics.Color.parseColor("#C0C0C0")); // Silver
                        else if (rank == 3) tv.setTextColor(android.graphics.Color.parseColor("#CD7F32")); // Bronze
                        else tv.setTextColor(android.graphics.Color.DKGRAY);
                        
                        containerLeaderboard.addView(tv);
                        rank++;
                    }
                }
            }

            @Override
            public void onFailure(Call<java.util.List<UserPointsDto>> call, Throwable t) {
                Toast.makeText(EcoPointsActivity.this, "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
            }
        });
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
