package com.isims.smartcampus.network;

import com.google.gson.annotations.SerializedName;

public class RelocationResponseDto {

    @SerializedName("requestId")
    private Long requestId;

    @SerializedName("originalRoomName")
    private String originalRoomName;

    @SerializedName("reportedAttendance")
    private Integer reportedAttendance;

    @SerializedName("suggestedRoomName")
    private String suggestedRoomName;

    @SerializedName("suggestedRoomBuilding")
    private String suggestedRoomBuilding;

    @SerializedName("suggestedRoomCapacity")
    private Integer suggestedRoomCapacity;

    @SerializedName("hvacShutdownRecommended")
    private Boolean hvacShutdownRecommended;

    @SerializedName("message")
    private String message;

    public Long getRequestId() { return requestId; }
    public String getOriginalRoomName() { return originalRoomName; }
    public Integer getReportedAttendance() { return reportedAttendance; }
    public String getSuggestedRoomName() { return suggestedRoomName; }
    public String getSuggestedRoomBuilding() { return suggestedRoomBuilding; }
    public Integer getSuggestedRoomCapacity() { return suggestedRoomCapacity; }
    public Boolean getHvacShutdownRecommended() { return hvacShutdownRecommended; }
    public String getMessage() { return message; }
}
