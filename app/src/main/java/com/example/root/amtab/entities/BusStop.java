package com.example.root.amtab.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by root on 14/10/16.
 */

public class BusStop implements IBusStop, Parcelable{

    private String id;
    private String details;
    private double latitude;
    private double longitude;
    private int distance;
    private int progressive;
    private long time;
    private ArrayList<Line> lines;
    private boolean preferite = false;
    public BusStop(String id, String details, double latitude, double longitude)
    {
        this.id = id;
        this. details = details;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    protected BusStop(Parcel in) {
        id = in.readString();
        details = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        time = in.readLong();
        progressive = in.readInt();
        preferite = in.readByte() != 0;
        //info = in.createTypedArrayList(Ride.CREATOR);
    }

    public void setPreferite(boolean bool)
    {
        preferite = bool;
    }

    public boolean isPreferite()
    {
        return preferite;
    }
    public static final Creator<BusStop> CREATOR = new Creator<BusStop>() {
        @Override
        public BusStop createFromParcel(Parcel in) {
            return new BusStop(in);
        }

        @Override
        public BusStop[] newArray(int size) {
            return new BusStop[size];
        }
    };

    public void setProgressive( int progressive )
    {
        this.progressive = progressive;
    }
    public int getProgressive()
    {
        return progressive;
    }
    public void setTime(long time)
    {
        this.time = time;
    }
    public long getTime()
    {
        return time;
    }
    public int getDistance()
    {
        return distance;
    }
    public String getDistanceS()
    {
        return distance + " metri";
    }

    public void setDistance(int distance)
    {
        this.distance = distance;
    }

    @Override
    public BusStop clone() {
        BusStop b = new BusStop(id,details,latitude,longitude);
        if(preferite)
        {
            b.setPreferite(preferite);
        }
        return b;
    }

    public String getId()
    {
        return id;
    }
    public String getDetails()
    {
        return details;
    }
    public double getLatitude()
    {
        return latitude;
    }
    public double getLongitude()
    {
        return longitude;
    }
    public ArrayList<Line> getLines()
    {
        return lines;
    }
    public String getClearDetails()
    {
        return details.replaceAll("'"," ");
    }
    public void addLines(ArrayList<Line> lines)
    {
        this.lines = lines;
    }

    @Override
    public String toString()
    {
        String result = details;
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(details);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeLong(time);
        parcel.writeInt(progressive);
        parcel.writeByte( (byte) (preferite ? 1 : 0 ));
        //parcel.writeTypedList(info);
    }

    /**
     * Created by root on 29/10/16.
     */


}
