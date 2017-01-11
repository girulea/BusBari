package com.example.root.amtab.activities.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.amtab.R;
import com.example.root.amtab.Utility.Util;
import com.example.root.amtab.entities.BusStop;

import java.util.ArrayList;

/**
 * Created by root on 15/10/16.
 */

public class PathRecyclerViewAdapter extends RecyclerView.Adapter<SimpleBusStopViewHolder> implements View.OnClickListener {

    private ArrayList<BusStop> listObject;
    private int busPosition = -1;
    private Activity activity;
    public PathRecyclerViewAdapter(Activity activity, ArrayList<BusStop> listObject) {
        this.activity = activity;
        this.listObject = listObject;
    }

    @Override
    public SimpleBusStopViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.busstop_view_on_line, parent, false);
        BusStop busStop = listObject.get(viewType);
        String listTitle = busStop.getDetails();
        TextView listTitleTextView = (TextView) view.findViewById(R.id.listTitle);
        LinearLayout imageView = (LinearLayout) view.findViewById(R.id.imageView);
        View image;
        TextView timeTextView = (TextView) view.findViewById(R.id.nextTimeView);
        imageView.removeAllViews();
        if( busPosition == viewType )
        {
            image = LayoutInflater.from(parent.getContext()).inflate( R.layout.bus_indicator2, null, false);
            timeTextView.setText( activity.getString( R.string.in_arrivo ) );
        }
        else
        {
            image = LayoutInflater.from(parent.getContext()).inflate( R.layout.bus_indicator, null, false);
            String formatTime = Util.getShortDateFormat( busStop.getTime());
            timeTextView.setText( formatTime );
        }
        imageView.addView(image);
        ImageButton iButton = (ImageButton) view.findViewById(R.id.favoriteButton);
        iButton.setFocusable(false);
        TextView distanceView = (TextView) view.findViewById(R.id.textViewDistance);
        distanceView.setText(busStop.getDistanceS());
        ImageButton iButton2 = (ImageButton) view.findViewById(R.id.navigationButton);
        iButton2.setFocusable(false);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        SimpleBusStopViewHolder holder = new SimpleBusStopViewHolder(view,busStop);
        return holder;
    }

    @Override
    public void onBindViewHolder(SimpleBusStopViewHolder holder, int position) {
        BusStop busStop = listObject.get(position);
        LinearLayout imageView = (LinearLayout) holder.v.findViewById(R.id.imageView);
        View image;
        TextView timeTextView = (TextView) holder.v.findViewById(R.id.nextTimeView);
        imageView.removeAllViews();
        if( busPosition == position )
        {
            image = LayoutInflater.from( holder.v.getContext()).inflate( R.layout.bus_indicator2, null, false);
            timeTextView.setText( activity.getString( R.string.in_arrivo ) );
        }
        else
        {
            image = LayoutInflater.from(holder.v.getContext()).inflate( R.layout.bus_indicator, null, false);
            String formatTime = Util.getShortDateFormat( busStop.getTime());
            timeTextView.setText( formatTime );
        }
        imageView.addView(image);
        String listTile = busStop.getDetails();
        String distance = busStop.getDistanceS();
        TextView textView = (TextView) holder.v.findViewById(R.id.listTitle);
        textView.setText(listTile);
        TextView textView1 = (TextView) holder.v.findViewById(R.id.textViewDistance);
        textView1.setText(distance);
        holder.setObject(busStop);
        holder.setActivity(activity);
    }

    @Override
    public int getItemCount() {
        return this.listObject.size();
    }


    @Override
    public void onClick(View view) {

    }

    public int getBusPosition()
    {
        return busPosition;
    }
    public int setBusPosition( int progressive )
    {
        int tmp = busPosition;
        busPosition = progressive;
        return tmp;
    }
}