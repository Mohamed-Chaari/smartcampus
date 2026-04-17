package com.isims.smartcampus;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.isims.smartcampus.network.ApiService;
import com.isims.smartcampus.network.RetrofitClient;
import com.isims.smartcampus.network.StatsDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView textTotalAnomalies;
    private TextView textPendingAnomalies;
    private TextView textTotalRelocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        textTotalAnomalies = findViewById(R.id.text_total_anomalies);
        textPendingAnomalies = findViewById(R.id.text_pending_anomalies);
        textTotalRelocations = findViewById(R.id.text_total_relocations);

        loadStats();
    }

    private void loadStats() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getCampusStats().enqueue(new Callback<StatsDto>() {
            @Override
            public void onResponse(Call<StatsDto> call, Response<StatsDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatsDto stats = response.body();
                    textTotalAnomalies.setText(String.valueOf(stats.getTotalAnomalies()));
                    textPendingAnomalies.setText(String.valueOf(stats.getPendingAnomalies()));
                    textTotalRelocations.setText(String.valueOf(stats.getTotalRelocations()));
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Failed to load stats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatsDto> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
