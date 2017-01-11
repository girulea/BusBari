package com.example.root.amtab.activities;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.root.amtab.R;
import com.example.root.amtab.Utility.Direction;
import com.example.root.amtab.activities.adapters.DataEvents;
import com.example.root.amtab.activities.adapters.TheoreticalPathRecyclerViewAdapter;
import com.example.root.amtab.activities.fragments.PathViewFragment;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.entities.BusStop;

import java.util.ArrayList;

/**
 * Created by root on 11/11/16.
 */

public class TheoreticalPathActivity extends AppCompatActivity implements DataEvents, AdapterView.OnItemSelectedListener {

    private static final String STATE_ITEMS = "STATEITEMS" ;
    private static final String STATE_LINEID = "STATELINEID";
    private static final String STATE_DIRECTIONS = "STATEDIRECTIONS" ;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<BusStop> items;
    private Direction[] directions;
    private String lineID;
    private TheoreticalPathRecyclerViewAdapter mAdapter;
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
        setContentView(R.layout.activity_theoreticalpathactivity);
        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PathViewFragment())
                    .commit();
        }
    }

    @Override
    protected  void onResume()
    {
        super.onResume();
        if( items == null )
        {
            CRUD crud = CRUD.getCRUD();
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            Intent intent = getIntent();
            lineID = intent.getStringExtra("LINEID");
            TextView idLineTextView = (TextView) findViewById(R.id.id_line);
            idLineTextView.setText(lineID);
            items = new ArrayList<>();
            mAdapter = new TheoreticalPathRecyclerViewAdapter(this, items);
            recyclerView.setAdapter(mAdapter);

            directions = crud.getLineDirections( lineID );
            Spinner spinner = (Spinner) findViewById(R.id.spinner2);
            ArrayAdapter<Direction> adapter = new ArrayAdapter<>( this,R.layout.spinner_item,R.id.textView,directions);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }
    }

    @Override
    public void onDataChanged()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(STATE_ITEMS,items);
        savedInstanceState.putString(STATE_LINEID,lineID);
        savedInstanceState.putStringArray(STATE_DIRECTIONS, Direction.directionsToString(directions ) );
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        items = savedInstanceState.getParcelableArrayList(STATE_ITEMS);
        lineID = savedInstanceState.getString(STATE_LINEID);
        directions = Direction.getDirectionsFromString(savedInstanceState.getStringArray(STATE_DIRECTIONS));
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        TextView textView = (TextView) view.findViewById(R.id.textView);
        Direction direction = Direction.getDircetionFromString(textView.getText().toString());
        CRUD crud = CRUD.getCRUD();
        items.clear();
        crud.loadTheoreticalPathOnAdapter(items, lineID, direction, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
