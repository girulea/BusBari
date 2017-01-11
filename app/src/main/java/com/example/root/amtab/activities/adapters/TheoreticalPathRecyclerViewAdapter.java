package com.example.root.amtab.activities.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.root.amtab.R;
import com.example.root.amtab.entities.BusStop;

import java.util.ArrayList;

/**
 * Created by root on 15/10/16.
 */

public class TheoreticalPathRecyclerViewAdapter extends RecyclerView.Adapter<SimpleBusStopViewHolder> {

    private ArrayList<BusStop> listObject;
    private Activity activity;
    public TheoreticalPathRecyclerViewAdapter(Activity activity, ArrayList<BusStop> listObject)
    {
        this.activity = activity;
        this.listObject = listObject;
    }

    @Override
    public SimpleBusStopViewHolder onCreateViewHolder(ViewGroup parent, int position)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.busstop_view, parent, false);
        BusStop busStop = listObject.get(position);
        String listTitle = busStop.getDetails();
        TextView listTitleTextView = (TextView) view.findViewById(R.id.listTitle);
        ImageButton iButton = (ImageButton) view.findViewById(R.id.favoriteButton);
        iButton.setFocusable(false);
        TextView distanceView = (TextView) view.findViewById(R.id.textViewDistance);
        distanceView.setText(busStop.getDistanceS());
        ImageButton iButton2 = (ImageButton) view.findViewById(R.id.navigationButton);
        iButton2.setFocusable(false);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        TextView timeTextView = (TextView) view.findViewById(R.id.nextTimeView);
        timeTextView.setText( activity.getString(R.string.progressivo_teorico) + ( position + 1 ) );
        SimpleBusStopViewHolder holder = new SimpleBusStopViewHolder(view,busStop);
        return holder;
    }

    @Override
    public void onBindViewHolder(SimpleBusStopViewHolder holder, int position) {
        BusStop busStop = listObject.get(position);
        String listTile = busStop.getDetails();
        String distance = busStop.getDistanceS();
        TextView textView = (TextView) holder.v.findViewById(R.id.listTitle);
        textView.setText(listTile);
        TextView textView1 = (TextView) holder.v.findViewById(R.id.textViewDistance);
        textView1.setText(distance);
        TextView timeTextView = (TextView) holder.v.findViewById(R.id.nextTimeView);
        timeTextView.setText( activity.getString(R.string.progressivo_teorico) + ( position + 1 ) );
        holder.setObject(busStop);
        holder.setActivity(activity);
    }

    @Override
    public int getItemCount() {
        return this.listObject.size();
    }



}