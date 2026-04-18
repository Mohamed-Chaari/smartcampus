package com.isims.smartcampus;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isims.smartcampus.network.ApiService;
import com.isims.smartcampus.network.EcoIssueDto;
import com.isims.smartcampus.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintenanceDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerIssues;
    private IssueAdapter issueAdapter;
    private List<EcoIssueDto> allIssues = new java.util.ArrayList<>();
    private com.google.android.material.chip.ChipGroup chipGroupFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_dashboard);

        recyclerIssues = findViewById(R.id.recycler_issues);
        recyclerIssues.setLayoutManager(new LinearLayoutManager(this));
        
        issueAdapter = new IssueAdapter(issueId -> markIdeaResolved(issueId));
        recyclerIssues.setAdapter(issueAdapter);
        
        chipGroupFilters = findViewById(R.id.chip_group_filters);
        chipGroupFilters.setOnCheckedChangeListener((group, checkedId) -> applyFilters());

        loadIssues();
    }

    private void loadIssues() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllReports().enqueue(new Callback<List<EcoIssueDto>>() {
            @Override
            public void onResponse(Call<List<EcoIssueDto>> call, Response<List<EcoIssueDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allIssues = response.body();
                    applyFilters();
                } else {
                    Toast.makeText(MaintenanceDashboardActivity.this, "Failed to load issues", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EcoIssueDto>> call, Throwable t) {
                Toast.makeText(MaintenanceDashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        int checkedId = chipGroupFilters.getCheckedChipId();
        List<EcoIssueDto> filtered = new java.util.ArrayList<>();

        for (EcoIssueDto issue : allIssues) {
            boolean matches = false;
            if (checkedId == R.id.chip_all) {
                matches = true;
            } else if (checkedId == R.id.chip_pending) {
                if ("PENDING".equals(issue.getStatus())) matches = true;
            } else if (checkedId == R.id.chip_critical) {
                if ("HIGH".equals(issue.getPriority()) || "CRITICAL".equals(issue.getPriority())) matches = true;
            } else {
                matches = true;
            }
            if (matches) filtered.add(issue);
        }
        issueAdapter.setIssues(filtered);
    }

    private void markIdeaResolved(Long issueId) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.updateReportStatus(issueId, "RESOLVED").enqueue(new Callback<EcoIssueDto>() {
            @Override
            public void onResponse(Call<EcoIssueDto> call, Response<EcoIssueDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MaintenanceDashboardActivity.this, "Issue Resolved!", Toast.LENGTH_SHORT).show();
                    loadIssues(); // Refresh list
                } else {
                    Toast.makeText(MaintenanceDashboardActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EcoIssueDto> call, Throwable t) {
                Toast.makeText(MaintenanceDashboardActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
