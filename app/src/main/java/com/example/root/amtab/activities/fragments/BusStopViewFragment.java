package com.example.root.amtab.activities.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.root.amtab.R;
import com.example.root.amtab.activities.adapters.BusStopRecyclerViewAdapter;
import com.example.root.amtab.activities.adapters.DataEvents;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.database.OnProgressDownloadingListener;
import com.example.root.amtab.entities.BusStop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class BusStopViewFragment extends Fragment implements OnProgressDownloadingListener, DataEvents, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,  GoogleApiClient.OnConnectionFailedListener {

    static final String STATE_ITEMS = "STATE_ITEMS";
    private BusStopRecyclerViewAdapter mAdapter;
    private ProgressBar progressBar;
    private ArrayList<BusStop>  items;
    private ImageButton imageGetLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean outcomeDailyService;
    private Thread busStopsLoader;
    public BusStopViewFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        imageGetLocation = (ImageButton) getActivity().findViewById(R.id.imageGetLocation);
        if( savedInstanceState != null )
        {
            items = savedInstanceState.getParcelableArrayList(STATE_ITEMS);
        }
        else
        {
            items = new ArrayList<>();
        }
        int color = ContextCompat.getColor(getContext(),R.color.secondarySurfaceColor);
        progressBar.getIndeterminateDrawable().setColorFilter( color, android.graphics.PorterDuff.Mode.SRC_IN);
        mAdapter = new BusStopRecyclerViewAdapter(getActivity(), items, R.layout.busstop_view );
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        return rootView;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if( busStopsLoader != null && busStopsLoader.getState() != Thread.State.TERMINATED )
        {
            items = new ArrayList<>();
            busStopsLoader.interrupt();
        }else if( busStopsLoader != null && busStopsLoader.getState() == Thread.State.TERMINATED )
        {
            busStopsLoader = null;
        }else if( !outcomeDailyService )
        {
            items = new ArrayList<>();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        checkComponents();
    }

    private void checkComponents()
    {
        CRUD crud = CRUD.getCRUD();
        if( items == null )
        {
            items = new ArrayList<>();
            progressBar.setVisibility(View.VISIBLE);
            crud.addListenerForDownlaod(this);
        }
        else if( items.size() == 0 )
        {
            progressBar.setVisibility(View.VISIBLE);
            crud.addListenerForDownlaod(this);
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
            imageGetLocation.setOnClickListener(this);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(STATE_ITEMS,items);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onDataChanged() {
        if( mGoogleApiClient != null )
        {
            mGoogleApiClient = null;
        }
        imageGetLocation.setOnClickListener(this);
        Activity activity = getActivity();
        if(activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onEndLines() {

    }

    @Override
    public void onStartDownloadLines(int number) {

    }

    @Override
    public void onEndBusStops() {
        updateView();
    }

    @Override
    public void onStartDownloadBusStops(int number) {

    }

    @Override
    public void onEndDailyService() {

        outcomeDailyService = true;
        updateView();
    }

    @Override
    public void onStartDownloadDailyService(int number) {

    }

    @Override
    public void onStartParseDailyService(int number) {

    }

    @Override
    public void onEndBusStopsOfLine() {

    }

    @Override
    public void onStartDownloadBusStopsOfLine(int number) {

    }

    @Override
    public void onProgressDownload() {

    }

    private void updateView()
    {
        Activity activity = getActivity();
        if( activity != null ) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }
        CRUD crud = CRUD.getCRUD();
        if (busStopsLoader != null) {
            busStopsLoader.interrupt();
        }
        items.clear();
        busStopsLoader = crud.loadBusStopsOnAdapter(items, this, 16.870059, 41.118545);
    }

    @Override
    public void onClick(View view)
    {
        if(mGoogleApiClient == null )
        {
            if( busStopsLoader != null )
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                busStopsLoader.interrupt();
                items.clear();
            }
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if( location != null )
            {
                double lng = location.getLongitude();
                double lat =  location.getLatitude();
                CRUD crud = CRUD.getCRUD();
                items.clear();
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);
                busStopsLoader = crud.loadBusStopsOnAdapter(items,this,lng,lat);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
