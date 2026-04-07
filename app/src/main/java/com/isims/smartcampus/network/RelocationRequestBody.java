package com.isims.smartcampus.network;

import com.google.gson.annotations.SerializedName;

public class RelocationRequestBody {

    @SerializedName("professorId")
    private final String professorId;

    @SerializedName("originalRoomId")
    private final Long originalRoomId;

    @SerializedName("reportedAttendance")
    private final Integer reportedAttendance;

    public RelocationRequestBody(String professorId, Long originalRoomId, Integer reportedAttendance) {
        this.professorId = professorId;
        this.originalRoomId = originalRoomId;
        this.reportedAttendance = reportedAttendance;
    }

    public String getProfessorId() { return professorId; }
    public Long getOriginalRoomId() { return originalRoomId; }
    public Integer getReportedAttendance() { return reportedAttendance; }
}
