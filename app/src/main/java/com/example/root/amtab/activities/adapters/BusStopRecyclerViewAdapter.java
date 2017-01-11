package com.example.root.amtab.activities.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.amtab.R;
import com.example.root.amtab.entities.BusStop;

import java.util.ArrayList;

/**
 * Created by root on 15/10/16.
 */

public class BusStopRecyclerViewAdapter extends RecyclerView.Adapter<BusStopViewHolder>  {

    private Context context;
    private ArrayList<BusStop> expandableListObject;
    private int LAYOUT;
    private Activity activity;
    public BusStopRecyclerViewAdapter(Activity activity, ArrayList<BusStop> expandableListObject, int LAYOUT) {
        this.activity = activity;
        this.expandableListObject = expandableListObject;
        this.LAYOUT = LAYOUT;
    }

    @Override
    public BusStopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(LAYOUT, parent, false);
        BusStop busStop = expandableListObject.get(viewType);
        String listTitle = busStop.getDetails();
        TextView listTitleTextView = (TextView) view.findViewById(R.id.listTitle);
        TextView distanceView = (TextView) view.findViewById(R.id.textViewDistance);
        distanceView.setText(busStop.getDistanceS());
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        if( busStop.getLines() != null )
        {
            TextView timeTextView = (TextView) view.findViewById(R.id.nextTimeView);
            timeTextView.setText( busStop.getLines().size() + activity.getString(R.string.linee));
        }
        BusStopViewHolder holder = new BusStopViewHolder(view,busStop);
        return holder;
    }

    @Override
    public void onBindViewHolder(BusStopViewHolder holder, int position) {
        BusStop busStop = expandableListObject.get(position);
        String listTile = busStop.getDetails();
        String distance = busStop.getDistanceS();
        TextView textView = (TextView) holder.v.findViewById(R.id.listTitle);
        textView.setText(listTile);
        TextView textView1 = (TextView) holder.v.findViewById(R.id.textViewDistance);
        textView1.setText(distance);
        TextView timeTextView = (TextView) holder.v.findViewById(R.id.nextTimeView);
        if( busStop.getLines() != null )
        {
            timeTextView.setText(busStop.getLines().size() + activity.getString( R.string.linee ));
        }
        else
        {
            timeTextView.setText(activity.getString(R.string.nessuna_linea_disponibile));
        }
        if(holder.expanded)
        {
            holder.collapse();
        }
        holder.setActivity(activity);
        holder.setObject(busStop);
    }

    @Override
    public int getItemCount() {
        return this.expandableListObject.size();
    }



}