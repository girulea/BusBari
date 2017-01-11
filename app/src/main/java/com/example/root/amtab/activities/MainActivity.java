package com.example.root.amtab.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.root.amtab.R;
import com.example.root.amtab.Utility.CustomRunnable;
import com.example.root.amtab.activities.adapters.PageViewAdapter;
import com.example.root.amtab.activities.fragments.BusStopViewFragment;
import com.example.root.amtab.activities.fragments.LineViewFragment;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.database.OnProgressDownloadingListener;
import com.example.root.amtab.maps2.SelectionActivity;

public class MainActivity extends FragmentActivity implements OnProgressDownloadingListener {

    private static final String STATE_VIEWPAGER = "VIEWPAGER";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CRUD crud;
    private FloatingActionButton fab;
    private Activity activity;
    private ImageButton imageGetLocation;
    private TextView descriptionProgress;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        activity = this;
        setContentView(R.layout.activity_main);
        setupViewPager();

    }

    private void setupViewPager()
    {
        PageViewAdapter adapter = new PageViewAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter.addFragment(new BusStopViewFragment(),getString(R.string.fermate));
        adapter.addFragment(new LineViewFragment(),getString(R.string.linee));
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
    @Override
    public void onResume()
    {
        super.onResume();
        crud = CRUD.createCRUD(this);
        crud.addListenerForDownlaod(this);
        crud.run();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }
    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


    @Override
    public void onStart()
    {
        super.onStart();
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        imageGetLocation = (ImageButton) findViewById(R.id.imageGetLocation);
        progressBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
        if( progressBar != null ) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
            descriptionProgress = (TextView) findViewById(R.id.downloadDescription);
        }
        Thread thread = new Thread(new CustomRunnable(this,null) {
            @Override
            public void run() {
                Activity thisActivity = (Activity) object;
                if (ContextCompat.checkSelfPermission(thisActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                        fab.setVisibility(View.INVISIBLE);
                        imageGetLocation.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(thisActivity,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putParcelable(STATE_VIEWPAGER, viewPager.onSaveInstanceState());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null)
        {
            viewPager.onRestoreInstanceState( savedInstanceState.getParcelable(STATE_VIEWPAGER) );
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fab.setVisibility(View.VISIBLE);
                    imageGetLocation.setVisibility(View.VISIBLE);

                } else {

                    fab.setVisibility(View.INVISIBLE);
                    imageGetLocation.setVisibility(View.INVISIBLE);
                }
                return;
            }

        }
    }
    @Override
    public void onEndLines() {}

    @Override
    public void onStartDownloadLines(final int number)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(0);
                progressBar.setMax(number);
                descriptionProgress.setText(getString( R.string.downloadlinee ));
            }
        });
    }

    @Override
    public void onEndBusStops() {
    }

    @Override
    public void onStartDownloadBusStops(final int number) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(0);
                progressBar.setMax(number);
                descriptionProgress.setText(getString( R.string.downloadfermate));
            }
        });
    }


    @Override
    public void onEndDailyService() {

        if( fab != null ) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                    } else {
                        startActivity(intent);
                    }
                }
            });
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.downloadDescriptionContainer);
                linearLayout.removeAllViews();
            }
        });

    }

    @Override
    public void onStartDownloadDailyService(final int number) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(0);
                progressBar.setMax(number);
                descriptionProgress.setText( getString( R.string.downloadserviziogiornaliero ) );
            }
        });
    }

    @Override
    public void onStartParseDailyService(final int number) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(0);
                progressBar.setMax(number);
                descriptionProgress.setText( getString( R.string.elaborazioneserviziogiornaliero));
            }
        });
    }

    @Override
    public void onProgressDownload() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress( progressBar.getProgress() + 1 );
            }
        });
    }

    @Override
    public void onEndBusStopsOfLine() { }

    @Override
    public void onStartDownloadBusStopsOfLine(final int number)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(0);
                progressBar.setMax(number);
                descriptionProgress.setText( getString( R.string.downloadfermatedilinea));
            }
        });
    }


}
