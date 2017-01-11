package com.example.root.amtab.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.root.amtab.R;
import com.example.root.amtab.Utility.Direction;
import com.example.root.amtab.Utility.Util;
import com.example.root.amtab.activities.adapters.DataEvents;
import com.example.root.amtab.activities.adapters.RideGridViewAdapter;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.entities.Ride;

import java.util.ArrayList;

/**
 * Created by root on 28/10/16.
 */

public class RidesActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener, DataEvents{

    private static final String STATE_ITEMS = "STATEITEMS";
    private static final String STATE_LINEID = "LINEID";
    private static final String STATE_DIRECTIONS = "DIRECTIONS";
    RideGridViewAdapter mAdapter;
    ArrayList<Ride> items;
    String id_line;
    GridView gridView;
    private Direction[] directions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Slide());
            getWindow().setExitTransition(new Slide());
        }
        setContentView(R.layout.activity_ridesactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRides);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if( items ==  null )
        {
            Intent intent = getIntent();
            CRUD crud = CRUD.getCRUD();
            id_line = intent.getStringExtra("idLine");
            items = new ArrayList<>();
            TextView idLineTextView = (TextView) findViewById(R.id.id_line);
            idLineTextView.setText(id_line);
            //spinner
            directions = crud.getLineDirections( id_line );
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<Direction> adapter = new ArrayAdapter<>( this,R.layout.spinner_item,R.id.textView,directions);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }

    }
    private void loadData(Direction direction)
    {
        CRUD crud = CRUD.getCRUD();
        crud.loadLineServiceOnAdapter( items, id_line, direction,this);
        mAdapter = new RideGridViewAdapter(items);
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Ride ride = (Ride) mAdapter.getItem(i);
                Intent intent = new Intent(view.getContext(), PathActivity.class);
                intent.putExtra("RIDE",ride);
                view.getContext().startActivity(intent);
            }
        });
        gridView.setAdapter(mAdapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        TextView textView = (TextView) view.findViewById(R.id.textView);
        Direction choice = Direction.getDircetionFromString(textView.getText().toString());
        loadData(choice);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDataChanged() {
        Util.sortRidesByTime(items);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(STATE_ITEMS,items);
        savedInstanceState.putString(STATE_LINEID,id_line);
        savedInstanceState.putStringArray(STATE_DIRECTIONS, Direction.directionsToString(directions ) );
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        items = savedInstanceState.getParcelableArrayList(STATE_ITEMS);
        id_line = savedInstanceState.getString(STATE_LINEID);
        directions = Direction.getDirectionsFromString(savedInstanceState.getStringArray(STATE_DIRECTIONS));
    }


}

