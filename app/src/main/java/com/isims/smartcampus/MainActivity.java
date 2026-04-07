package com.isims.smartcampus;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.card_report).setOnClickListener(v -> {
            startActivity(new Intent(this, ReportIssueActivity.class));
        });

        findViewById(R.id.card_relocation).setOnClickListener(v -> {
            startActivity(new Intent(this, RelocationActivity.class));
        });

        findViewById(R.id.card_eco_points).setOnClickListener(v -> {
            startActivity(new Intent(this, EcoPointsActivity.class));
        });
    }
}
