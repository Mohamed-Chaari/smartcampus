package com.isims.smartcampus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.isims.smartcampus.network.ApiService;
import com.isims.smartcampus.network.ReportResponseDto;
import com.isims.smartcampus.network.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportIssueActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private EditText editDescription;
    private ProgressBar progressBar;

    private String currentPhotoPath;
    private Uri photoURI;
    private File photoFile;

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            isSuccess -> {
                if (isSuccess) {
                    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    imagePreview.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        imagePreview = findViewById(R.id.image_preview);
        editDescription = findViewById(R.id.edit_description);
        progressBar = findViewById(R.id.progress_bar);

        Button btnCaptureImage = findViewById(R.id.btn_capture_image);
        Button btnSubmitReport = findViewById(R.id.btn_submit_report);

        btnCaptureImage.setOnClickListener(v -> checkCameraPermissionAndLaunch());

        btnSubmitReport.setOnClickListener(v -> submitReport());
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(photoURI);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void submitReport() {
        String description = editDescription.getText().toString().trim();

        if (photoFile == null || !photoFile.exists()) {
            Toast.makeText(this, "Please capture an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), photoFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", photoFile.getName(), reqFile);

        RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody studentIdPart = RequestBody.create(MediaType.parse("text/plain"), "STU-001");

        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getApiService();
        Call<ReportResponseDto> call = apiService.submitReport(imagePart, descPart, studentIdPart);
        call.enqueue(new Callback<ReportResponseDto>() {
            @Override
            public void onResponse(Call<ReportResponseDto> call, Response<ReportResponseDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ReportResponseDto res = response.body();
                    String msg = "Success! Category: " + res.getCategory() +
                            "\nPoints: " + res.getEcoPoints();
                    Toast.makeText(ReportIssueActivity.this, msg, Toast.LENGTH_LONG).show();

                    photoFile = null;
                    currentPhotoPath = null;
                    imagePreview.setImageDrawable(null);
                    editDescription.setText("");
                } else {
                    Toast.makeText(ReportIssueActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReportResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReportIssueActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
