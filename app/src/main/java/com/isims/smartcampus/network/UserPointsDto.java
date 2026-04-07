package com.isims.smartcampus.network;

import com.google.gson.annotations.SerializedName;

public class UserPointsDto {

    @SerializedName("userId")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("totalEcoPoints")
    private Integer totalEcoPoints;

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public Integer getTotalEcoPoints() { return totalEcoPoints; }
}
