package com.flowz.locationtracker.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "places")
public class MyPlace {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "user_Latitude")
    private Double startLatitude;

    @ColumnInfo(name = "user_Longitude")
    private Double startLongitude;

    @ColumnInfo(name = "user_stopLatitude")
    private Double stopLatitude;

    @ColumnInfo(name = "user_stopLongitude")
    private Double stopLongitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getStopLatitude() {
        return stopLatitude;
    }

    public void setStopLatitude(Double stopLatitude) {
        this.stopLatitude = stopLatitude;
    }

    public Double getStopLongitude() {
        return stopLongitude;
    }

    public void setStopLongitude(Double stopLongitude) {
        this.stopLongitude = stopLongitude;
    }
}
