package com.isims.smartcampus.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @Multipart
    @POST("api/reports/submit")
    Call<ReportResponseDto> submitReport(
            @Part MultipartBody.Part image,
            @Part("description") RequestBody description,
            @Part("studentId") RequestBody studentId,
            @Part("location") RequestBody location,
            @Part("priority") RequestBody priority,
            @Part("equipmentType") RequestBody equipmentType
    );

    @GET("api/reports/user/{userId}/points")
    Call<UserPointsDto> getUserPoints(@Path("userId") String userId);

    @GET("api/reports")
    Call<java.util.List<EcoIssueDto>> getAllReports();

    @PATCH("api/reports/{issueId}/status")
    Call<EcoIssueDto> updateReportStatus(@Path("issueId") Long issueId, @Query("status") String status);

    @GET("api/reports/stats")
    Call<StatsDto> getCampusStats();

    @GET("api/reports/leaderboard")
    Call<java.util.List<UserPointsDto>> getLeaderboard();

    @POST("api/relocation/request")
    Call<RelocationResponseDto> requestRelocation(@Body RelocationRequestBody body);

    @POST("api/auth/login")
    Call<LoginResponseDto> login(@Body LoginRequestBody body);
}
