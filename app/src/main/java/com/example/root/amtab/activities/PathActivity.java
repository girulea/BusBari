package com.example.root.amtab.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.amtab.R;
import com.example.root.amtab.activities.adapters.DataEvents;
import com.example.root.amtab.activities.adapters.PathRecyclerViewAdapter;
import com.example.root.amtab.activities.fragments.PathViewFragment;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.entities.BUS;
import com.example.root.amtab.entities.BusStop;
import com.example.root.amtab.entities.Ride;

import java.util.ArrayList;

/**
 * Created by root on 03/11/16.
 */

public class PathActivity extends AppCompatActivity implements DataEvents {

    private PathRecyclerViewAdapter mAdapter;
    private ArrayList < BusStop > items;
    private Thread locationThread;
    private boolean firstChanged = true;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    BUS bus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Fade());
        }
        setContentView(R.layout.activity_pathactivity);
        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PathViewFragment())
                    .commit();
        }
    }

    @Override
    protected  void onStart()
    {
        super.onStart();
        CRUD crud = CRUD.getCRUD();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        Intent intent = getIntent();
        Ride ride = intent.getParcelableExtra("RIDE");
        TextView idLineTextView = (TextView) findViewById(R.id.id_line);
        idLineTextView.setText(ride.getLineID());
        items = ride.getBusStops();
        bus = new BUS(ride.getLineID(), ride.getID());
        locationThread = crud.getBusLocation(bus,this);
        mAdapter = new PathRecyclerViewAdapter( this, items );
        recyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationThread != null)
        {
            locationThread.interrupt();
        }
    }

    //METODO DELL'INTERFACCIA DATAEVENTS USATO PER NOTIFICARE CHE LA POSIZIONE DEL BUS È MUTATA
    @Override
    public void onDataChanged() {
        final Context context = getApplicationContext();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bus.getLongitude() != 0 && mAdapter.getBusPosition() != bus.getProgressive())
                {
                    int tmp = mAdapter.setBusPosition(bus.getProgressive());
                    if (tmp != -1)
                    {
                        mAdapter.notifyItemChanged(tmp);
                    }
                    mAdapter.notifyItemChanged(bus.getProgressive());
                    if (firstChanged)
                    {
                        recyclerView.scrollToPosition(bus.getProgressive());
                        firstChanged = false;
                    }
                }
                else if (bus.getLongitude() == 0)
                {
                    int tmp = mAdapter.setBusPosition(-1);
                    if (tmp != -1)
                    {
                        mAdapter.notifyItemChanged(tmp);
                    }
                    CharSequence text = getString(R.string.posizionedelpullmannondisponibile);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }
}