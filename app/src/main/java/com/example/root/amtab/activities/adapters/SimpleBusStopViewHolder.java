package com.example.root.amtab.activities.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.example.root.amtab.R;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.entities.BusStop;

public class SimpleBusStopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // each data item is just a string in this case
    protected View v;
    protected BusStop busStop;
    protected ImageButton iFavorite;
    protected ImageButton iNavigation;
    protected Activity activity;

    public SimpleBusStopViewHolder(View v, BusStop busStop) {
        super(v);
        this.v = v;
        this.busStop = busStop;
        iFavorite = (ImageButton) v.findViewById(R.id.favoriteButton);
        iFavorite.setOnClickListener(this);
        iNavigation = (ImageButton) v.findViewById(R.id.navigationButton);
        iNavigation.setOnClickListener(this);
    }

    protected void setActivity(Activity activity)
    {
        this.activity = activity;
    }
    protected void setObject(BusStop busStop)
    {
        this.busStop = busStop;
        if(busStop.isPreferite())
        {
            iFavorite.setImageDrawable(v.getContext().getDrawable(R.mipmap.favorite_white36dp));
        }else
        {
            iFavorite.setImageDrawable(v.getContext().getDrawable(R.mipmap.favorite_border_white36dp));
        }
    }

    @Override
    public void onClick(View view) {
        if( view.equals(iFavorite))
        {
            CRUD crud = CRUD.getCRUD();
            boolean favoriteState = busStop.isPreferite();
            boolean result = crud.saveFavorite(busStop.getId(),!busStop.isPreferite());
            if(result) {
                if (favoriteState)
                {
                    iFavorite.setImageDrawable(v.getContext().getDrawable(R.mipmap.favorite_border_white36dp));
                    busStop.setPreferite(false);
                }
                else if (!favoriteState)
                {
                    iFavorite.setImageDrawable(v.getContext().getDrawable(R.mipmap.favorite_white36dp));
                    busStop.setPreferite(true);
                }
            }else
            {

            }
        }else if( view.equals(iNavigation))
        {
            double lng = busStop.getLongitude();
            double lat = busStop.getLatitude();
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.startActivity(mapIntent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
            } else {
                activity.startActivity(mapIntent);
            }
            activity.startActivity(mapIntent);

        }
    }

}