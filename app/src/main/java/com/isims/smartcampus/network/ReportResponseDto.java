package com.isims.smartcampus.network;

import com.google.gson.annotations.SerializedName;

public class ReportResponseDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("category")
    private String category;

    @SerializedName("ecoPoints")
    private Integer ecoPoints;

    @SerializedName("message")
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getEcoPoints() {
        return ecoPoints;
    }

    public void setEcoPoints(Integer ecoPoints) {
        this.ecoPoints = ecoPoints;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
