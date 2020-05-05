package com.flowz.locationtracker.room;

import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.RoomDatabase;



@Database(entities = {MyPlace.class}, version = 3)
public abstract class MyAppDataBase extends RoomDatabase {

    public abstract MyDAO myDAO();
}
