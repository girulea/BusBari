package com.example.root.amtab.entities;

/**
 * Created by root on 04/11/16.
 */

public class BUS {

    String lineID;
    String rideID;
    double longitude;
    double latitude;
    long time;
    int progressive;
    String nextBusStop;

    public BUS( String lineID, String rideID )
    {
        this.lineID = lineID;
        this.rideID = rideID;
    }

    public String getLineID()
    {
        return lineID;
    }
    public String getRideID()
    {
        return rideID;
    }
    public double getLongitude()
    {
        return longitude;
    }
    public double getLatitude()
    {
        return latitude;
    }
    public long getTime()
    {
        return time;
    }
    public int getProgressive()
    {
        return progressive;
    }
    public String getNExtBusStop()
    {
        return nextBusStop;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }
    public void setLongitude( double longitude )
    {
        this.longitude = longitude;
    }
    public void setProgressive(int progressive)
    {
        this.progressive = progressive;
    }
    public void setTime( long time )
    {
        this.time = time;
    }
    public void setNextBusStop(String nextBusStop )
    {
        this.nextBusStop = nextBusStop;
    }

    @Override
    public String toString()
    {
        String result= "latitude "+ latitude+" longitude "+longitude + " progressivo "+progressive;
        return result;
    }
}
