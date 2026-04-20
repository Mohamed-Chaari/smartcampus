package com.isims.smartcampus.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @Multipart
    @POST("api/reports/submit")
    Call<ReportResponseDto> submitReport(
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part description,
            @Part MultipartBody.Part studentId,
            @Part MultipartBody.Part location,
            @Part MultipartBody.Part priority,
            @Part MultipartBody.Part equipmentType
    );

    @GET("api/reports/user/{userId}/points")
    Call<UserPointsDto> getUserPoints(@Path("userId") String userId);

    @POST("api/relocation/request")
    Call<RelocationResponseDto> requestRelocation(@Body RelocationRequestBody body);

    @POST("api/auth/login")
    Call<LoginResponseDto> login(@Body LoginRequestBody body);
}
