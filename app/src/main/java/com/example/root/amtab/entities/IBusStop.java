package com.example.root.amtab.entities;

import java.util.ArrayList;

/**
 * Created by root on 18/10/16.
 */

 interface IBusStop {

     String getId();
     String getDetails();
     double getLatitude();
     double getLongitude();
     ArrayList<Line> getLines();
     String getClearDetails();
     boolean isPreferite();
    void setPreferite(boolean bool);
     String getDistanceS();
     BusStop clone();
}
