package com.bashirli.locationsaver.DataCollect;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Data implements Serializable {
@PrimaryKey(autoGenerate = true)
    public int id;
@ColumnInfo(name = "name")
    public String name;
@ColumnInfo(name = "longtitude")
    public double longtitude;
@ColumnInfo(name="latitude")
public double latitude;

public Data(String name,double latitude,double longtitude){
    this.name=name;
    this.latitude=latitude;
    this.longtitude=longtitude;

}
}
