package com.example.root.amtab.maps2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.root.amtab.R;
import com.example.root.amtab.maps2.cluster.MyItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;


public class MapsLineActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleApiClient.ConnectionCallbacks, TaskListener {

    private GoogleMap mGoogleMap;
    private Context context;
    private String[] idLines;
    private String[] directions;
    private View mapView;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private ClusterManager<MyItem> mClusterManager;
    private int contPress;
    private Spinner spinner;
    private Spinner spinnerDirection;
    private static final String SPINNER_KEY = "spinnerLine";
    private static final String SPINNER_DIRECTION_KEY = "spinnerDirection";
    private TaskListener taskListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_line);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.bus_icon_white36dp);
        context = this;
        contPress = 0;

        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerDirection = (Spinner) findViewById(R.id.spinnerDirection);

        directions = new String[0];
        idLines = new String[0];


        taskListener = this;
        if (savedInstanceState != null) {
            LoadDataSpinnerTask loadDataSpinnerTask = new LoadDataSpinnerTask(this, spinner, spinnerDirection, savedInstanceState.getInt(SPINNER_KEY), savedInstanceState.getInt(SPINNER_DIRECTION_KEY), taskListener);
            loadDataSpinnerTask.execute();
        } else {
            LoadDataSpinnerTask loadDataSpinnerTask = new LoadDataSpinnerTask(this, spinner, spinnerDirection, 0, 0, taskListener);
            loadDataSpinnerTask.execute();
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (contPress > 1) {
                    mGoogleMap.clear();
                    drawMarkers(spinner.getSelectedItem().toString(), spinnerDirection.getSelectedItem().toString());
                } else {
                    contPress++;
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (contPress > 1) {
                    mGoogleMap.clear();
                    drawMarkers(spinner.getSelectedItem().toString(), spinnerDirection.getSelectedItem().toString());
                } else {
                    contPress++;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SPINNER_KEY, spinner.getSelectedItemPosition());
        outState.putInt(SPINNER_DIRECTION_KEY, spinnerDirection.getSelectedItemPosition());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        }

        //sposto il bottone della posizione corrente
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 20, 0);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.112505, 16.873593) , 10));

    }



     void drawMarkers(String line, String direction) {
         if (!line.equals("...")) {
             CurrentRideBusStop currentRideBusStop = new CurrentRideBusStop(context, direction, line, mClusterManager, mGoogleMap);
             currentRideBusStop.execute();
         }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPause() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        if (mapFragment != null) {
            mapFragment.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mapFragment != null) {
            mapFragment.onResume();
        }

        super.onResume();
    }

    @Override
    public void onLowMemory() {
        if (mapFragment != null) {
            mapFragment.onLowMemory();
        }
        super.onLowMemory();
    }

    @Override
    public void onFinished() {
        drawMarkers(spinner.getSelectedItem().toString(), spinnerDirection.getSelectedItem().toString());
    }
}
