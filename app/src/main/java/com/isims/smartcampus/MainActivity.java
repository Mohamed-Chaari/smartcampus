package com.isims.smartcampus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("SmartCampusPrefs", Context.MODE_PRIVATE);
        String role = prefs.getString("role", null);
        String userId = prefs.getString("userId", null);

        if (role == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        TextView textGreeting = findViewById(R.id.text_greeting);
        textGreeting.setText("Hello, " + userId + " 👋");

        View cardReport = findViewById(R.id.card_report);
        View cardRelocation = findViewById(R.id.card_relocation);
        View cardEcoPoints = findViewById(R.id.card_eco_points);
        View cardAdminMonitor = findViewById(R.id.card_admin_monitor);
        View cardMaintenance = findViewById(R.id.card_maintenance);
        View fabNewReport = findViewById(R.id.fab_new_report);
        View btnLogout = findViewById(R.id.btn_logout);

        // Hide all dynamically first
        cardReport.setVisibility(View.GONE);
        cardRelocation.setVisibility(View.GONE);
        cardEcoPoints.setVisibility(View.GONE);
        cardAdminMonitor.setVisibility(View.GONE);
        cardMaintenance.setVisibility(View.GONE);
        fabNewReport.setVisibility(View.GONE);

        // Role based visibility
        switch (role) {
            case "STUDENT":
                cardReport.setVisibility(View.VISIBLE);
                cardEcoPoints.setVisibility(View.VISIBLE);
                fabNewReport.setVisibility(View.VISIBLE);
                break;
            case "PROFESSOR":
                cardReport.setVisibility(View.VISIBLE);
                cardRelocation.setVisibility(View.VISIBLE);
                fabNewReport.setVisibility(View.VISIBLE);
                break;
            case "ADMIN":
                cardAdminMonitor.setVisibility(View.VISIBLE);
                cardReport.setVisibility(View.VISIBLE);
                cardAdminMonitor.setOnClickListener(v -> startActivity(new Intent(this, AdminDashboardActivity.class)));
                break;
            case "MAINTENANCE":
                cardMaintenance.setVisibility(View.VISIBLE);
                cardMaintenance.setOnClickListener(v -> startActivity(new Intent(this, MaintenanceDashboardActivity.class)));
                break;
        }

        // Set click listeners for existing views
        cardReport.setOnClickListener(v -> startActivity(new Intent(this, ReportIssueActivity.class)));
        cardRelocation.setOnClickListener(v -> startActivity(new Intent(this, RelocationActivity.class)));
        cardEcoPoints.setOnClickListener(v -> startActivity(new Intent(this, EcoPointsActivity.class)));
        fabNewReport.setOnClickListener(v -> startActivity(new Intent(this, ReportIssueActivity.class)));

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
