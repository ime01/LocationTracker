package com.flowz.locationtracker.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "places")
public class MyPlace {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "user_Latitude")
    private Double Latitude;

    @ColumnInfo(name = "user_Longitude")
    private Double Longitude;

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

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
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
