package com.bashirli.locationsaver.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.bashirli.locationsaver.DataCollect.Data;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface DataDAO {
@Insert
    Completable insert(Data data);
@Delete
    Completable delete(Data data);
@Query("SELECT * FROM Data")
    Flowable<List<Data>> getAll();
}
