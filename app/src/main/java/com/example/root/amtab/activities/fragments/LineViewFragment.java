package com.example.root.amtab.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.root.amtab.R;
import com.example.root.amtab.activities.adapters.DataEvents;
import com.example.root.amtab.activities.adapters.LineRecyclerViewAdapter;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.database.OnProgressDownloadingListener;
import com.example.root.amtab.entities.Line;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class LineViewFragment extends Fragment implements OnProgressDownloadingListener, DataEvents{

    private static final String STATE_ITEMS = "STATE_ITEMS";
    private LineRecyclerViewAdapter mAdapter;
    private ProgressBar progressBar;
    private ArrayList<Line> items;
    private Thread linesLoader;

    public LineViewFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        CRUD crud = CRUD.getCRUD();
        if( savedInstanceState != null )
        {
            items = savedInstanceState.getParcelableArrayList(STATE_ITEMS);
            if( items.size() == 0 )
            {
                progressBar.setVisibility(View.VISIBLE);
                crud.addListenerForDownlaod(this);
            }
            else
            {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }else
        {
            items = new ArrayList<>();
            crud.addListenerForDownlaod(this);
        }
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        int color = ContextCompat.getColor(getContext(),R.color.primarySurfaceColor);
        progressBar.getIndeterminateDrawable().setColorFilter( color, android.graphics.PorterDuff.Mode.SRC_IN);
        mAdapter = new LineRecyclerViewAdapter(getActivity(), items );
        recyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(STATE_ITEMS,items);
        super.onSaveInstanceState(savedInstanceState);
    }
   
    @Override
    public void onPause()
    {
        super.onPause();
        if( linesLoader != null && linesLoader.getState() != Thread.State.TERMINATED )
        {
            items = new ArrayList<>();
            linesLoader.interrupt();
        }else if( linesLoader != null && linesLoader.getState() == Thread.State.TERMINATED )
        {
            linesLoader = null;
        }
    }
    @Override
    public void onEndLines() {
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
        if( linesLoader != null )
        {
            linesLoader.interrupt();
            items.clear();
        }
        linesLoader = crud.loadLinesOnAdapter(items,this);
    }

    @Override
    public void onStartDownloadLines(int number) {

    }

    @Override
    public void onEndBusStops() {

    }

    @Override
    public void onStartDownloadBusStops(int number) {

    }

    @Override
    public void onEndDailyService() {
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
        if( linesLoader != null )
        {
            linesLoader.interrupt();
        }
        items.clear();
        linesLoader = crud.loadLinesOnAdapter(items,this);
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

    @Override
    public void onDataChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(items.size() > 0 ) {
                    mAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
