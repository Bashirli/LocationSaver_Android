package com.bashirli.locationsaver.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bashirli.locationsaver.DataCollect.Data;

@Database(entities = {Data.class},version = 1)
public abstract class DataDB extends RoomDatabase {
public abstract DataDAO dataDAO();
}
