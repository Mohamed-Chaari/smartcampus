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
import com.isims.smartcampus.network.RelocationRequestBody;
import com.isims.smartcampus.network.RelocationResponseDto;
import com.isims.smartcampus.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelocationActivity extends AppCompatActivity {

    private EditText editProfessorId;
    private EditText editRoomId;
    private EditText editAttendance;
    private ProgressBar progressBar;
    private LinearLayout resultCard;
    private TextView textResultMessage;
    private TextView textHvacStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relocation);

        editProfessorId = findViewById(R.id.edit_professor_id);
        editRoomId = findViewById(R.id.edit_room_id);
        editAttendance = findViewById(R.id.edit_attendance);
        progressBar = findViewById(R.id.progress_bar);
        resultCard = findViewById(R.id.result_card);
        textResultMessage = findViewById(R.id.text_result_message);
        textHvacStatus = findViewById(R.id.text_hvac_status);

        // Security check and auto-fill
        android.content.SharedPreferences prefs = getSharedPreferences("SmartCampusPrefs", android.content.Context.MODE_PRIVATE);
        String role = prefs.getString("role", "");
        String userId = prefs.getString("userId", "");

        if (!"PROFESSOR".equals(role)) {
            Toast.makeText(this, "Access Denied: Only Professors can request relocation.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        editProfessorId.setText(userId);
        editProfessorId.setEnabled(false); // Prevent changing ID

        Button btnRequest = findViewById(R.id.btn_request_relocation);
        btnRequest.setOnClickListener(v -> requestRelocation());
    }

    private void requestRelocation() {
        String professorId = editProfessorId.getText().toString().trim();
        String roomIdStr = editRoomId.getText().toString().trim();
        String attendanceStr = editAttendance.getText().toString().trim();

        if (professorId.isEmpty() || roomIdStr.isEmpty() || attendanceStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        long roomId;
        int attendance;
        try {
            roomId = Long.parseLong(roomIdStr);
            attendance = Integer.parseInt(attendanceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Room ID and attendance must be numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        resultCard.setVisibility(View.GONE);

        RelocationRequestBody body = new RelocationRequestBody(professorId, roomId, attendance);
        ApiService apiService = RetrofitClient.getApiService();
        apiService.requestRelocation(body).enqueue(new Callback<RelocationResponseDto>() {
            @Override
            public void onResponse(Call<RelocationResponseDto> call, Response<RelocationResponseDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    RelocationResponseDto res = response.body();
                    textResultMessage.setText(res.getMessage());
                    if (Boolean.TRUE.equals(res.getHvacShutdownRecommended())) {
                        textHvacStatus.setText("⚡ HVAC & lighting shutdown recommended for original room.");
                        textHvacStatus.setVisibility(View.VISIBLE);
                    } else {
                        textHvacStatus.setVisibility(View.GONE);
                    }
                    resultCard.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(RelocationActivity.this,
                            "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RelocationResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RelocationActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
