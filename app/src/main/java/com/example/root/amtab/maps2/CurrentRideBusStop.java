package com.example.root.amtab.maps2;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.root.amtab.Utility.Direction;
import com.example.root.amtab.Utility.Util;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.entities.BusStop;
import com.example.root.amtab.entities.Ride;
import com.example.root.amtab.maps2.cluster.CustomClusterRenderer;
import com.example.root.amtab.maps2.cluster.MyItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gian on 07/11/16.
 */

public class CurrentRideBusStop extends AsyncTask<Void, Void, ArrayList<BusStop>> {
    private Context context;
    private CRUD crud;
    private String line;
    private String direction;
    private ArrayList<BusStop> currentBusStop;
    private ClusterManager<MyItem> mClusterManager;
    private GoogleMap mGoogleMap;
    private String idCorsa;

    public CurrentRideBusStop(Context context, String direction, String line, ClusterManager<MyItem> mClusterManager, GoogleMap mGoogleMap ) {
        this.context = context;
        this.direction = direction;
        this.line = line;
        this.mClusterManager = mClusterManager;
        this.mGoogleMap = mGoogleMap;
    }


    @Override
    protected ArrayList<BusStop> doInBackground(Void... params) {
        crud = CRUD.getCRUD();
        currentBusStop = getCurrentRideBusStop();
        return currentBusStop;
    }

    @Override
    protected void onPostExecute(ArrayList<BusStop> currentBusStop) {
        super.onPostExecute(currentBusStop);
        mClusterManager = new ClusterManager<MyItem>(context, mGoogleMap);
        CustomClusterRenderer renderer = new CustomClusterRenderer(context, mGoogleMap, mClusterManager);
        mClusterManager.setRenderer(renderer);

        mGoogleMap.setOnCameraIdleListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);


        if (currentBusStop != null) {
            for (BusStop busStop : currentBusStop) {
                LatLng latLng = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                String orario = Util.getShortDateFormat(busStop.getTime());
                MyItem item = new MyItem(busStop.getDetails(), "Fermata numero: " + busStop.getProgressive() + ". Orario di passaggio : " + orario, latLng);
                mClusterManager.addItem(item);
            }
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentBusStop.get(0).getLatitude(), currentBusStop.get(0).getLongitude()), 15));
        } else {
            Toast.makeText(context, "Servizio giornaliero non disponibile o per questa linea o per questa direzione", Toast.LENGTH_LONG).show();
        }

        FetchPositionBusStop fetchPositionBusStop = new FetchPositionBusStop(context, crud, direction, line, mClusterManager, mGoogleMap, idCorsa);
        fetchPositionBusStop.execute();
    }


    public ArrayList<BusStop> getCurrentRideBusStop() {
        ArrayList<Ride> rides = crud.getLineService(line, Direction.getDircetionFromString(direction));

        if (rides != null) {
            Util.sortRidesByTime(rides);
            for (Ride ride : rides) {
                ArrayList<BusStop> busStops = ride.getBusStops();
                if (compareTime(busStops.get(busStops.size() - 1).getTime())) {
                    idCorsa = ride.getID();
                    return busStops;
                }
            }
        }
        return null;
    }



    private boolean compareTime(long time) {
        Date timeFromServer = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        //Date actualTime = new Date();
        Date actualTime = new Date();
        int compareToNow = timeFromServer.compareTo(actualTime);

        if (compareToNow >= 0) {
            return true;
        } else {
            return false;
        }
    }


}
