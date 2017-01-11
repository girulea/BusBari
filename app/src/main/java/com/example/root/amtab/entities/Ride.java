package com.example.root.amtab.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by root on 18/10/16.
 */

public class Ride implements IRide, Parcelable {

    private String id_line;
    private String id;
    private ArrayList<BusStop> busStops;
    private String direction;
    public Ride(String id, String id_line, String direction)
    {
        this.id = id;
        this.id_line = id_line;
        this.direction = direction;
        busStops = new ArrayList<>();
    }


    protected Ride(Parcel in) {
        id_line = in.readString();
        id = in.readString();
        busStops = in.createTypedArrayList(BusStop.CREATOR);
        direction = in.readString();
    }

    public static final Creator<Ride> CREATOR = new Creator<Ride>() {
        @Override
        public Ride createFromParcel(Parcel in) {
            return new Ride(in);
        }

        @Override
        public Ride[] newArray(int size) {
            return new Ride[size];
        }
    };

    public void addBusStop(BusStop busStop)
    {
        busStops.add(busStop);
    }
    
    public ArrayList<BusStop> getBusStops()
    {
        return busStops;
    }





    public String getLineID()
    {
        return id_line;
    }
    public String getID()
    {
        return id;
    }
    public String getDirection()
    {
        return direction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id_line);
        parcel.writeString(id);
        parcel.writeTypedList(busStops);
        parcel.writeString(direction);
    }
}
