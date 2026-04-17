package com.isims.smartcampus.network;

public class StatsDto {
    private long totalAnomalies;
    private long pendingAnomalies;
    private long totalRelocations;

    public long getTotalAnomalies() { return totalAnomalies; }
    public long getPendingAnomalies() { return pendingAnomalies; }
    public long getTotalRelocations() { return totalRelocations; }
}
