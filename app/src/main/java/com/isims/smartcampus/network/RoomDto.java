package com.isims.smartcampus.network;

public class RoomDto {
    private Long id;
    private String name;
    private String building;
    private Integer capacity;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBuilding() { return building; }
    public Integer getCapacity() { return capacity; }

    @Override
    public String toString() {
        return name + " (" + building + ") - Cap: " + capacity;
    }
}
