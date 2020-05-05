package com.flowz.locationtracker.room;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addPlace(MyPlace myPlace);

    @Query("select * from places")
    public List<MyPlace> getPlaces();
}
