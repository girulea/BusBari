package com.example.root.amtab.activities.adapters;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.amtab.R;
import com.example.root.amtab.Utility.Status;
import com.example.root.amtab.Utility.Util;
import com.example.root.amtab.entities.Ride;

import java.util.ArrayList;

/**
 * Created by root on 31/10/16.
 */

public class RideGridViewAdapter extends BaseAdapter {

    private ArrayList <Ride> rides;
    public RideGridViewAdapter(ArrayList<Ride> rides)
    {
        this.rides = rides;
    }

    @Override
    public int getCount() {
        return rides.size();
    }

    @Override
    public Object getItem(int i) {
        return rides.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Ride ride = rides.get(i);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ride_view, viewGroup,false);
        TextView title = (TextView) v.findViewById(R.id.rideIDView);
        title.setText( ( i + 1 + "" ));
        if( ride.getBusStops() != null && ride.getBusStops().size() > 0)
        {
            TextView timeView = (TextView) v.findViewById(R.id.startTimeRideView);
            long time = ride.getBusStops().get(0).getTime();
            String formatTime = Util.getShortDateFormat(time);
            timeView.setText(formatTime);
            LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.surfaceView);
            Status status = Util.getRideStatus(ride);
            int color = 0;
            switch (status.toString()) {
                case "NOT_STARTED":
                    color = ContextCompat.getColor(v.getContext(), R.color.primarySurfaceColor);
                    break;
                case "STARTED":
                    color = ContextCompat.getColor(v.getContext(), R.color.secondarySurfaceColor);
                    break;
                case "FINISHED":
                    color = ContextCompat.getColor(v.getContext(), R.color.tertiarySurfaceColor);
                    break;
            }
            linearLayout.setBackgroundColor(color);
        }

        return  v;
    }

}
