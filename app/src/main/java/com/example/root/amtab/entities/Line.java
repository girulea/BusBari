package com.example.root.amtab.entities;

/**
 * Created by root on 14/10/16.
 */

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Line implements ILine, Parcelable{
    private String id;
    private String details;

    public Line(String id, String details)
    {
        this.id = id;
        this.details = details;
    }
    public Line(JSONObject object)
    {
        getIdFromJSONOject(object);
        getDetailsFromJSONOject(object);
    }

    protected Line(Parcel in) {
        id = in.readString();
        details = in.readString();
    }

    public static final Creator<Line> CREATOR = new Creator<Line>() {
        @Override
        public Line createFromParcel(Parcel in) {
            return new Line(in);
        }

        @Override
        public Line[] newArray(int size) {
            return new Line[size];
        }
    };

    public String getId()
    {
        return id;
    }
    public String getDetails()
    {
        return details;
    }
    public String getClearDetails()
    {
        return details.replaceAll("'","& QUOTE &");
    }

    @Override
    public String toString()
    {
        String result = "Linea: "+id;
        return result;
    }


    private void getIdFromJSONOject(JSONObject object)
    {
        try{
            id = object.getString("IdLinea");
        }catch(Exception e)
        {}
    }
    private void getDetailsFromJSONOject(JSONObject object)
    {
        try{
            details = object.getString("DescrizioneLinea");
        }catch(Exception e)
        {}
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(details);
    }
}
