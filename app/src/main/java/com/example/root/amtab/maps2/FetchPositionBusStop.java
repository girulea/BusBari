package com.example.root.amtab.maps2;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.root.amtab.Utility.Direction;
import com.example.root.amtab.Utility.Util;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.entities.BUS;
import com.example.root.amtab.entities.BusStop;
import com.example.root.amtab.maps2.cluster.MyItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by gian on 07/11/16.
 */

public class FetchPositionBusStop extends AsyncTask<String, Void, String> {
    private final String LOG_TAG = FetchPositionBusStop.class.getSimpleName();
    private Context context;
    private CRUD crud;
    private String line;
    private String direction;
    private ArrayList<BusStop> currentBusStop;
    private ClusterManager<MyItem> mClusterManager;
    private GoogleMap mGoogleMap;
    private ArrayList<BUS> activeBus;
    private String idActiveRoute;

    public FetchPositionBusStop(Context context, CRUD crud, String direction, String line, ClusterManager<MyItem> mClusterManager, GoogleMap mGoogleMap, String idCorsa ) {
        this.context = context;
        this.crud = crud;
        this.direction = direction;
        this.line = line;
        this.mClusterManager = mClusterManager;
        this.mGoogleMap = mGoogleMap;
        this.idActiveRoute = idCorsa;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;


        try {
            int index = line.indexOf('/');
            if (index != -1) {
                line = line.replaceAll("/", "barrato");
            }
            Uri.Builder builtUri = getURI(line);

            URL url = new URL(builtUri.toString());

            //Log.d(LOG_TAG, "url " + url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            jsonStr = buffer.toString();
            //Log.d(LOG_TAG, "json " + jsonStr);

        } catch (Exception e) {
            Log.e(LOG_TAG, "errore :( " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            activeBus = getDataFromJson(jsonStr, direction);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.d(LOG_TAG, "linea " + activeBus.toString());
        return  null;
    }

    private Uri.Builder getURI( String idlinea) {
        final String SCHEME = "http";
        final String AUTHORITY = "bari.opendata.planetek.it";
        final String ORARIBUS = "OrariBus";
        final String VERSION = "v2.1";
        final String OPEN_DATA_SERVICE = "OpenDataService.svc";
        final String REST = "REST";
        final String MEZZI_LINEA = "MezziLinea";


        Uri.Builder builtUri = new Uri.Builder();
        builtUri.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(ORARIBUS)
                .appendPath(VERSION)
                .appendPath(OPEN_DATA_SERVICE)
                .appendPath(REST)
                .appendPath(MEZZI_LINEA)
                .appendPath(idlinea);

        builtUri.build();

        return builtUri;
    }

    private ArrayList<BUS> getDataFromJson( String jsonStr, String direzione) throws JSONException {
        final String DIREZIONE_LINEA = "DirezioneLinea";
        final String ID_CORSA = "IdCorsa";
        final String ID_LINEA = "IdLinea";
        final String ID_PROSSIMA_FERMATA = "IdProssimaFermata";
        final String PROGRESSIVO_FERMATA = "ProgressivoFermata";
        final String ULTIME_COORDINATE = "UltimeCoordinateMezzo";
        final String DATA_ACQUISIZIONE = "DataOraAcquisizioneIt";
        final String LATITUDINE = "Latitudine";
        final String LONGITUDINE = "Longitudine";
        final String VELOCITA = "VelocitaKmh";
        ArrayList<BUS> list = null;

        if (jsonStr != null) {
            list = new ArrayList<BUS>();
            int index = line.indexOf('/');
            if (index != -1) {
                line = line.replaceAll("/", "barrato");
            }
            int j = 0;
            JSONArray arrayFermate = new JSONArray(jsonStr);
            for (int i = 0; i < arrayFermate.length(); i++) {
                JSONObject current = arrayFermate.getJSONObject(i);
                JSONObject position = current.getJSONObject(ULTIME_COORDINATE);
                String currentDiretion = Direction.getDircetionFromString(current.getString(DIREZIONE_LINEA)).toString();
                if (currentDiretion.equals(direzione) && current.getString(ID_CORSA).equals(idActiveRoute)) {
                    list.add(new BUS(current.getString(ID_LINEA), current.getString(ID_CORSA)));
                    //Log.d(LOG_TAG, i + " " + position.getDouble(LATITUDINE) + " " + position.getDouble(LONGITUDINE)  );
                    list.get(j).setLatitude(position.getDouble(LATITUDINE));
                    list.get(j).setLongitude(position.getDouble(LONGITUDINE));
                    list.get(j).setProgressive(current.getInt(PROGRESSIVO_FERMATA));
                    try {
                        list.get(j).setTime(Util.getDateFromJSONString(position.getString(DATA_ACQUISIZIONE)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    list.get(j).setNextBusStop(current.getString(ID_PROSSIMA_FERMATA));
                    j++;
                }
            }
        }
        return list;
    }



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


        if (activeBus.size() > 0) {
            for (BUS bus : activeBus) {
                LatLng latLng = new LatLng(bus.getLatitude(), bus.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        .title("Bus in corsa")
                        .snippet("Prossima fermata: " + bus.getProgressive() + ". Aggiornato alle: " + Util.getShortDateFormat(bus.getTime()));
                Marker marker = mGoogleMap.addMarker(markerOptions);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                marker.showInfoWindow();
            }
        } else {
            Toast.makeText(context, "Posizione bus non disponibile per questa linea", Toast.LENGTH_LONG).show();

        }

    }
}
