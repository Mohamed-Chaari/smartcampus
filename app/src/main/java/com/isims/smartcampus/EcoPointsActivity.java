package com.isims.smartcampus;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.isims.smartcampus.network.ApiService;
import com.isims.smartcampus.network.RetrofitClient;
import com.isims.smartcampus.network.UserPointsDto;

import java.util.List;
import java.util.Locale;

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

        // Pre-fill user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SmartCampusPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", "");
        
        if (editUserId != null) {
            editUserId.setText(userId);
            if (!userId.isEmpty()) {
                checkPoints();
            }
        }

        Button btnCheck = findViewById(R.id.btn_check_points);
        if (btnCheck != null) {
            btnCheck.setOnClickListener(v -> checkPoints());
        }

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getLeaderboard().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<UserPointsDto>> call, @NonNull Response<List<UserPointsDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (containerLeaderboard != null) {
                        containerLeaderboard.removeAllViews();
                        List<UserPointsDto> leaderboard = response.body();
                        
                        int rank = 1;
                        for (UserPointsDto student : leaderboard) {
                            TextView tv = new TextView(EcoPointsActivity.this);
                            String name = student.getName() != null ? student.getName() : "Unknown";
                            String sid = student.getUserId() != null ? student.getUserId() : "N/A";
                            int pts = student.getTotalEcoPoints() != null ? student.getTotalEcoPoints() : 0;
                            
                            String entry = String.format(Locale.getDefault(), "%d. %s (%s) - %d Pts", 
                                    rank, name, sid, pts);
                            tv.setText(entry);
                            tv.setTextSize(16);
                            tv.setPadding(0, 8, 0, 8);
                            
                            // Highlight top 3
                            if (rank == 1) tv.setTextColor(Color.parseColor("#FFD700")); // Gold
                            else if (rank == 2) tv.setTextColor(Color.parseColor("#C0C0C0")); // Silver
                            else if (rank == 3) tv.setTextColor(Color.parseColor("#CD7F32")); // Bronze
                            else tv.setTextColor(Color.DKGRAY);
                            
                            containerLeaderboard.addView(tv);
                            rank++;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserPointsDto>> call, @NonNull Throwable t) {
                Toast.makeText(EcoPointsActivity.this, "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPoints() {
        if (editUserId == null) return;
        
        String userId = editUserId.getText().toString().trim();
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please enter your User ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (pointsCard != null) pointsCard.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getUserPoints(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserPointsDto> call, @NonNull Response<UserPointsDto> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    UserPointsDto dto = response.body();
                    if (textUserName != null) {
                        textUserName.setText(dto.getName() != null ? dto.getName() : "Unknown");
                    }
                    if (textPoints != null) {
                        int points = dto.getTotalEcoPoints() != null ? dto.getTotalEcoPoints() : 0;
                        textPoints.setText(String.valueOf(points));
                    }
                    if (pointsCard != null) pointsCard.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(EcoPointsActivity.this,
                            "User not found or server error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserPointsDto> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(EcoPointsActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
