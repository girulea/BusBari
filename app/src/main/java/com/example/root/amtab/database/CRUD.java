package com.example.root.amtab.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.root.amtab.Utility.CustomRunnable;
import com.example.root.amtab.Utility.Direction;
import com.example.root.amtab.Utility.Util;
import com.example.root.amtab.activities.MainActivity;
import com.example.root.amtab.activities.adapters.DataEvents;
import com.example.root.amtab.database.EntryContract.BusStopEntry;
import com.example.root.amtab.database.EntryContract.DailyServiceEntry;
import com.example.root.amtab.database.EntryContract.LineEntry;
import com.example.root.amtab.entities.BUS;
import com.example.root.amtab.entities.BusStop;
import com.example.root.amtab.entities.Line;
import com.example.root.amtab.entities.Ride;
import com.example.root.amtab.maps2.cluster.MyItem;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 15/10/16.
 */
 // CLASSE CHE METTE A DISPOSIZIONE VARI METODI PER INTERFACCIARE LE VARI PARDI DEL SISTEMA CON I DATI PERSISTENTI.
public class CRUD{


    private static final String URL_DOWNLOAD_BUS = "http://bari.opendata.planetek.it/OrariBus/v2.1/OpenDataService.svc/REST/MezziLinea/";

    private static CRUD crud;
    private HashMap<String,BusStop> busstopsCache;
    private DBManager manager;

    // COSTRUTTORE PRIVATO PER AVERE UNA SOLA ISTANZA DELLA CLASSE
    private CRUD(MainActivity activityMain)
    {
        manager = new DBManager(activityMain);
        busstopsCache = new HashMap<>();
    }

    public static CRUD createCRUD(MainActivity activityMain)
    {
        if( crud == null ) {
            crud = new CRUD(activityMain);
        }
        return crud;
    }
    public static CRUD getCRUD()
    {
        return crud;
    }

    // SERVE PER FAR ESEGUIRE I PRIMI CONTROLLI SUL DB DA PARTE DEL DBMANAGER
    public void run()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = manager.getWritableDatabase();
            }
        });
        thread.start();

    }

    // METODO CHE RESTITUISCE GLI ID DELLE CORSE DI UNA DETERMINATA LINEA E DIREZIONE
    public String[] getIDRides(String line_id, Direction direction)
    {
        String[] result = null;
        SQLiteDatabase db = manager.getReadableDatabase();
        String table_name = DailyServiceEntry.TABLE_NAME_PRE + Util.getDayOfWeek();
        String whereClause = DailyServiceEntry.COLUMN_NAME_LINE_ID + "=? and "+ DailyServiceEntry.COLUMN_NAME_DIRECTION + "=?";
        String[] selectClause = { DailyServiceEntry.COLUMN_NAME_ROAD_ID };

        Cursor resultSet = db.query( table_name, selectClause, whereClause,new String[]{line_id,direction.toString()}, DailyServiceEntry.COLUMN_NAME_ROAD_ID, null, null);
        if( resultSet.getCount() > 0 ) {
            result = new String[resultSet.getCount()];
            resultSet.moveToFirst();
            int index = 0;
            do {
                result[index++] = resultSet.getString(0);
            } while (resultSet.moveToNext());
        }
        resultSet.close();
        return result;
    }

    // RESTIUSCE LE DIREZIONI DI UNA LINEA
    public Direction[] getLineDirections(String lineID)
    {
        Direction[] directions = null;
        SQLiteDatabase db = manager.getReadableDatabase();
        String table_name = DailyServiceEntry.TABLE_NAME_PRE + Util.getDayOfWeek();
        String[] columns = { DailyServiceEntry.COLUMN_NAME_LINE_ID, DailyServiceEntry.COLUMN_NAME_DIRECTION };
        String selection = DailyServiceEntry.COLUMN_NAME_LINE_ID+"=?";
        String[] selectionArgs = new String[]{ lineID };
        String groupBY = DailyServiceEntry.COLUMN_NAME_DIRECTION;

        Cursor resultSet = db.query( table_name, columns, selection,selectionArgs, groupBY, null, null,null);
        if( resultSet.getCount() > 0 ) {
            directions = new Direction[resultSet.getCount()];
            resultSet.moveToFirst();
            int index = 0;
            do {
                directions[index++] = Direction.getDircetionFromString(resultSet.getString(1));
            } while (resultSet.moveToNext());
        }
        resultSet.close();
        return directions;
    }

    // CREA UN DIZIONARIO USANDO COME CHIAVE L'ID DELLA CORSA E COME VALORE UN OGGETTO DI TIPO RIDE
    private  HashMap<String, Ride> createDictionary(String[] ridesID, String lineID, String direction, ArrayList<Ride> items)
    {
        HashMap<String, Ride> result = new HashMap<>();
        for(String s : ridesID)
        {
            Ride d = new Ride(s,lineID,direction);
            items.add(d);
            result.put(s, d);
        }
        return  result;
    }
    // METODO USATO COME CACHE PER EVITARE DI DOVER INTERROGARE PIÙ VOLTE IL DB PER DATI CHE GIÀ SI È RICHIESTO
    private BusStop getBusStop(String busStopID)
    {
        BusStop result = null;
        if( !busstopsCache.containsKey(busStopID))
        {
            String[] whereArgument = new String[]{busStopID};
            String[] selection = new String[]{BusStopEntry.COLUMN_NAME_ID,
                    BusStopEntry.COLUMN_NAME_DETAILS,
                    BusStopEntry.COLUMN_NAME_LONGITUDE,
                    BusStopEntry.COLUMN_NAME_LATITUDE};
            SQLiteDatabase db = manager.getReadableDatabase();
            Cursor resultSet = db.query(BusStopEntry.TABLE_NAME, selection, BusStopEntry.COLUMN_NAME_ID + "=?", whereArgument, null, null, null);
            try {
                resultSet.moveToFirst();
                String descrition = resultSet.getString(1);
                float longitude = resultSet.getFloat(2);
                float latitude = resultSet.getFloat(3);
                result = new BusStop(busStopID, descrition, latitude, longitude);
                busstopsCache.put(busStopID, result);
            }catch (Exception e)
            {
                result = new BusStop(busStopID,"Errore",0,0);
                busstopsCache.put(busStopID, result);
            }
        }
        result = busstopsCache.get(busStopID).clone();
        return result;
    }


    //METODO CHE RESTIUSCE TUTTE LE LINEE
    public ArrayList<Line> getLines()
    {
        ArrayList<Line> lines = new ArrayList<>();
        try {
            SQLiteDatabase db = manager.getReadableDatabase();
            Cursor resultSet = db.query(LineEntry.TABLE_NAME, null, null, null, null, null, null);
            if (resultSet.getCount() > 0)
            {
                resultSet.moveToFirst();
                do {
                    lines.add(new Line(resultSet.getString(0), resultSet.getString(1)));
                } while (resultSet.moveToNext());
                resultSet.close();
            }
        }catch (Exception e){}
        return lines;
    }


    //METODO CHE AGGIUNGE LE LINEE ALL'ARRAYLIST IL QUALE È USATO DA UN ADPATER IN UNA VIEW, ALLA FINE DEL DOWNLOAD VIENE RICHIAMATO IL METODO DELL'INTERFACCIA DATAEVENTS PER NOTIFICARE CHE IL TASK SI È CONCLUSO CON SUCCESSO
    public Thread loadLinesOnAdapter(ArrayList<Line> items, DataEvents listener) {

        Thread thread = new Thread(new CustomRunnable(items, listener) {
            @Override
            public void run() {
                ArrayList<Line> items = (ArrayList<Line>) object;
                try {
                    SQLiteDatabase db = manager.getReadableDatabase();
                        Cursor resultSet = db.query(LineEntry.TABLE_NAME, null, null, null, null, null, null);
                        if (resultSet.getCount() > 0) {
                            resultSet.moveToFirst();
                            do {
                                items.add(new Line(resultSet.getString(0), resultSet.getString(1)));
                            } while (resultSet.moveToNext());
                            resultSet.close();
                            listener.onDataChanged();
                    }
                } catch (Exception e) {
                }

            }
        });
        thread.start();
        return thread;
    }

    //METODO CHE AGGIUNGE LE FERMATE IN BASE AD UNA POSIZIONE ALL'ARRAYLIST IL QUALE È USATO DA UN ADPATER IN UNA VIEW, ALLA FINE DEL DOWNLOAD VIENE RICHIAMATO IL METODO DELL'INTERFACCIA DATAEVENTS PER NOTIFICARE CHE IL TASK SI È CONCLUSO CON SUCCESSO
    public Thread loadBusStopsOnAdapter(ArrayList<BusStop> busstops, DataEvents adapter, final double longitude, final double latitude)
    {
        Thread thread = new Thread(
                new CustomRunnable( busstops, adapter) {
                    @Override
                    public void run() {
                        ArrayList<BusStop> list = (ArrayList<BusStop>) object;
                        try {
                            SQLiteDatabase db = manager.getReadableDatabase();
                            String query = "SELECT "+ BusStopEntry.COLUMN_NAME_ID + "," + BusStopEntry.COLUMN_NAME_DETAILS  + "," + BusStopEntry.COLUMN_NAME_LATITUDE  + "," + BusStopEntry.COLUMN_NAME_LONGITUDE + "," + BusStopEntry.COLUMN_NAME_FAVORITE + ",";
                            query += "  ( ABS ( " + BusStopEntry.COLUMN_NAME_LONGITUDE +"-"+longitude + ") + ABS( " + BusStopEntry.COLUMN_NAME_LATITUDE +"-"+latitude + ")  )  as dist FROM " + BusStopEntry.TABLE_NAME +" ORDER BY dist LIMIT 50";
                            Cursor resultSet = db.rawQuery(query, null);
                            if (resultSet.getCount() > 0) {
                                resultSet.moveToFirst();
                                do {
                                    String id = resultSet.getString(0);
                                    String detail = resultSet.getString(1);
                                    double latitude = Double.parseDouble(resultSet.getString(2));
                                    double longitude = Double.parseDouble(resultSet.getString(3));
                                    double dist = Double.parseDouble( resultSet.getString(5)) * 3960 * 3.141592;
                                    int bool = resultSet.getInt(4);
                                    boolean favorite = bool == 1; // conversione in boolean
                                    BusStop b = new BusStop( id, detail, latitude, longitude);
                                    b.setPreferite(favorite);
                                    b.setDistance( (int) dist );
                                    list.add(b);
                                } while (resultSet.moveToNext());
                                resultSet.close();
                            }
                            if (list != null) {
                                searchLinesOfBusstop(list);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if( listener != null && list.size()  > 0 ) {
                            listener.onDataChanged();
                        }
                    }
                });
        thread.start();
        return thread;
    }

     //METODO CHE AGGIUNGE LE CORSE DI UNA LINEA, CON DIREZIONE SPECIFICATA, ALL'ARRAYLIST IL QUALE È USATO DA UN ADPATER IN UNA VIEW, ALLA FINE DEL DOWNLOAD VIENE RICHIAMATO IL METODO DELL'INTERFACCIA DATAEVENTS PER NOTIFICARE CHE IL TASK SI È CONCLUSO CON SUCCESSO
    public void loadLineServiceOnAdapter(ArrayList < Ride > items, String idLine, Direction direction, final DataEvents listener) {

        if (direction == null) {
            direction = Direction.ANDATA;
        }
        SQLiteDatabase db = manager.getReadableDatabase();
        String[] ridesID = getIDRides(idLine, direction);
        HashMap < String, Ride > dictionary = createDictionary(ridesID, idLine, direction.toString(), items);

        String table_name = DailyServiceEntry.TABLE_NAME_PRE + Util.getDayOfWeek();
        String whereClause = DailyServiceEntry.COLUMN_NAME_LINE_ID + "=? and " + DailyServiceEntry.COLUMN_NAME_DIRECTION + "=?";
        String[] selectClause = {
                DailyServiceEntry.COLUMN_NAME_ROAD_ID,
                DailyServiceEntry.COLUMN_NAME_LINE_ID,
                DailyServiceEntry.COLUMN_NAME_BUSSTOP_ID,
                DailyServiceEntry.COLUMN_NAME_DIRECTION,
                DailyServiceEntry.COLUMN_NAME_PROGRESSIVE,
                DailyServiceEntry.COLUMN_NAME_TIME
        };

        final Cursor resultSet = db.query(table_name, selectClause, whereClause, new String[] { idLine, direction.toString() }, null, null, null);
        Thread thread = new Thread(new CustomRunnable(dictionary, listener) {
            @Override
            public void run() {
                HashMap < String, Ride > dictionary = (HashMap < String, Ride > ) object;
                if (resultSet.getCount() > 0) {
                    resultSet.moveToFirst();
                    do {
                        String ride_id = resultSet.getString(0);
                        String busstop_id = resultSet.getString(2);
                        int progressive = resultSet.getInt(4);
                        long time = resultSet.getLong(5);
                        BusStop b = getBusStop(busstop_id);  //VIENE USATO UN METODO PER IL CACHING PER RIDURRE GLI ACCESSI AL DB DATO CHE ALCUNI OGGETTI SONO STATI GIÀ RICHIESTI IN PRECEDENZA
                        b.setProgressive(progressive);
                        b.setTime(time);
                        dictionary.get(ride_id).addBusStop(b); //VIENE USATO IL DIZIONARIO PER EVITARE DI DOVER CICLARE OGNI VOLTA UN ARRAY PER TROVARE LA CORSA ALLA QUALE UNA FERMATA APPARTIENE
                    } while (resultSet.moveToNext());
                    resultSet.close();
                }
                listener.onDataChanged();
            }
        });
        thread.start();
    }

    // METODO CHE CARICA SU UNA FERMATA LE LINEE DALLA QUALE SARÀ SERVITO IN BASE AL SERVIZIO GIORNALIERO ATTAUALE
    private void searchLinesOfBusstop(ArrayList<BusStop> busstop)
    {
        SQLiteDatabase db = manager.getReadableDatabase();
        String day = Util.getDayOfWeek().toString();
        String table_name = DailyServiceEntry.TABLE_NAME_PRE + day;
        String prefixQuery = "Select " + LineEntry.TABLE_NAME + "." + LineEntry.COLUMN_NAME_ID + "," + LineEntry.TABLE_NAME + "." + LineEntry.COLUMN_NAME_DETAILS +
                " FROM " + table_name + " JOIN " + LineEntry.TABLE_NAME +
                " ON " + LineEntry.TABLE_NAME + "." + LineEntry.COLUMN_NAME_ID + " = " + table_name + "." +DailyServiceEntry.COLUMN_NAME_LINE_ID +
                " WHERE " + DailyServiceEntry.COLUMN_NAME_BUSSTOP_ID + " LIKE ";
        for( BusStop b : busstop)
        {
            String query = prefixQuery + "'" + b.getId() + "'"+" GROUP BY " + LineEntry.TABLE_NAME + "." + LineEntry.COLUMN_NAME_ID ;
            Cursor resultSet = db.rawQuery(query, null);
            if(resultSet.getCount() > 0 ) {
                resultSet.moveToFirst();
                ArrayList<Line> lines = new ArrayList<>();
                do {
                    lines.add(new Line( resultSet.getString(0),resultSet.getString(1)));
                } while (resultSet.moveToNext());
                b.addLines(lines);
            }
            resultSet.close();
        }

    }

    // METODO CHE CARICA LE FERMATE DEL PERCOSO TEORICO DI UNA LINEA, SU UN ADAPTER
    public void loadTheoreticalPathOnAdapter(ArrayList < BusStop > items, String idLine, Direction d, final DataEvents listener) {

        if( d == null )
        {
            d = Direction.ANDATA;
        }
        String direction = d.toString();
        String table_name = EntryContract.BusStopOfLineEntry.TABLE_NAME;
        String query =
                " SELECT " + EntryContract.BusStopOfLineEntry.COLUMN_NAME_LINE_ID + "," + EntryContract.BusStopOfLineEntry.COLUMN_NAME_BUSSTOP_ID + "," +
                EntryContract.BusStopOfLineEntry.COLUMN_NAME_DIRECTION + "," + EntryContract.BusStopOfLineEntry.COLUMN_NAME_PROGRESSIVE  +
                " FROM " + EntryContract.BusStopOfLineEntry.TABLE_NAME  +
                " WHERE " + EntryContract.BusStopOfLineEntry.COLUMN_NAME_LINE_ID + " LIKE '" + idLine + "'" + " AND " + EntryContract.BusStopOfLineEntry.COLUMN_NAME_DIRECTION +  " LIKE '" + d + "' ORDER BY " + EntryContract.BusStopOfLineEntry.COLUMN_NAME_PROGRESSIVE;
                //" ORDER BY " + EntryContract.BusStopOfLineEntry.COLUMN_NAME_PROGRESSIVE;
        SQLiteDatabase db = manager.getReadableDatabase();
        //final Cursor resultSet = db.query(table_name, selectClause, whereClause, new String[] { idLine, direction }, null, null, EntryContract.BusStopOfLineEntry.COLUMN_NAME_PROGRESSIVE,null);
        //final Cursor resultSet = db.query(table_name, selectClause, whereClause, new String[] { idLine, direction }, null, null, null);
        final Cursor resultSet = db.rawQuery(query,null);

        Thread thread = new Thread(new CustomRunnable(items, listener) {
            @Override
            public void run()
            {
                ArrayList<BusStop> items = (ArrayList<BusStop>) object;
                if (resultSet.getCount() > 0)
                {
                    resultSet.moveToFirst();
                    do {
                        String line_id = resultSet.getString(0);
                        String busstop_id = resultSet.getString(1);
                        String direction = resultSet.getString(2);
                        int progressive = resultSet.getInt(3);
                        BusStop b = getBusStop(busstop_id);  //VIENE USATO UN METODO PER IL CACHING PER RIDURRE GLI ACCESSI AL DB DATO CHE ALCUNI OGGETTI SONO STATI GIÀ RICHIESTI IN PRECEDENZA
                        b.setProgressive(progressive);
                        items.add(b);
                    } while (resultSet.moveToNext());
                    resultSet.close();
                }
                listener.onDataChanged();
            }
        });
        thread.start();
    }

    // METODO PER IL TRACKING DEL PULLMAN DI UNA DETERMINATA CORSA. IL LISTENER VIENE USATO PER NOTIFICARE LA POSIZIONE DEL PULLMAN ALLA VIEW CHE LO TRACCIA
    public Thread getBusLocation(BUS bus, DataEvents listener)
    {
        Thread thread = new Thread(
                new CustomRunnable(bus, listener) {
                    @Override
                    public void run()
                    {
                        BUS bus = (BUS) object;
                        String lineID = bus.getLineID() + "";
                        int index = lineID.indexOf('/');
                        if (index != -1)
                        {
                            lineID = lineID.replaceAll("/", "barrato");
                        }
                        boolean condiction = true;
                        while(  condiction )
                        {
                            try
                            {
                                JSONArray jsonArray = Util.readJSONFromURL(URL_DOWNLOAD_BUS + lineID);
                                boolean changed = false;
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String rideID = object.getString("IdCorsa");
                                    if (bus.getRideID().compareTo(rideID) == 0) {
                                        String nextBusStop = object.getString("IdProssimaFermata");
                                        int progressive = object.getInt("ProgressivoFermata")-1;
                                        JSONObject coordinatesObject = object.getJSONObject("UltimeCoordinateMezzo");
                                        double longitude = coordinatesObject.getDouble("Longitudine");
                                        double latitude = coordinatesObject.getDouble("Latitudine");
                                        String date = coordinatesObject.getString("DataOraAcquisizioneIt");
                                        long time = Util.getDateFromJSONString(date);
                                        bus.setNextBusStop(nextBusStop);
                                        bus.setProgressive(progressive);
                                        bus.setLongitude(longitude);
                                        bus.setLatitude(latitude);
                                        bus.setTime(time);
                                        listener.onDataChanged();
                                        changed = true;
                                        Thread.sleep(2000);
                                    }
                                }
                                if( changed == false )
                                {
                                    bus.setLatitude(0);
                                    bus.setLongitude(0);
                                    listener.onDataChanged();
                                    condiction = false;

                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        thread.start();
        return thread;
    }


    //METODO USATO PER SALVARE UNA FERMATA COME PREFERITA ( CAMBIANDO IL FLAG DI STATO ALL'INTERNO DELLA TABELLA DELLE FERMATE)
    public boolean saveFavorite(String busStopID, boolean isFavorite)
    {
        boolean result = false;
        try
        {
            if( !manager.isInTransaction() )
            {
                SQLiteDatabase db = manager.getWritableDatabase();
                int bool = isFavorite == true ? 1 : 0;
                ContentValues cv = new ContentValues();
                cv.put(BusStopEntry.COLUMN_NAME_FAVORITE, bool); //These Fields should be your String values of actual column names
                int update = db.update(BusStopEntry.TABLE_NAME, cv, BusStopEntry.COLUMN_NAME_ID + "=?", new String[]{busStopID});
                result = true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    public void addListenerForDownlaod(OnProgressDownloadingListener listener)
    {
        manager.addListener(listener);
    }

    public void removeListenerForDownload( OnProgressDownloadingListener listener )
    {
        manager.removeListener(listener);
    }
    public void insertMarkers(ClusterManager<MyItem> mClusterManager) {
        try {
            SQLiteDatabase db = manager.getReadableDatabase();
            Cursor resultSet = db.query(EntryContract.BusStopEntry.TABLE_NAME, null, null, null, null, null, null);
            if (resultSet.getCount() == 0) {
            } else {
                resultSet.moveToFirst();
                do {
                    double lat = Double.parseDouble(resultSet.getString(2));
                    double lng = Double.parseDouble(resultSet.getString(3));
                    LatLng latLng = new LatLng(lat, lng);
                    MyItem item = new MyItem(resultSet.getString(1), "", latLng);
                    mClusterManager.addItem(item);
                } while (resultSet.moveToNext());
                resultSet.close();
            }
        }catch (Exception e){
            Log.e("CRUD", "errore nell'inserimento dei markers " + e);
        }
    }

    //
    public ArrayList<Ride> getLineService(String idLine, Direction direction)
    {
        ArrayList<Ride> result = null;
        if (direction == null) {
            direction = Direction.ANDATA;
        }
        SQLiteDatabase db = manager.getReadableDatabase();
        String[] ridesID = getIDRides(idLine, direction);
        try {
            HashMap<String, Ride> dictionary = createDictionaryMaps(ridesID, idLine, direction.toString());

            String table_name = DailyServiceEntry.TABLE_NAME_PRE + Util.getDayOfWeek();
            String whereClause = DailyServiceEntry.COLUMN_NAME_LINE_ID + "=? and " + DailyServiceEntry.COLUMN_NAME_DIRECTION + "=?";
            String[] selectClause = {
                    DailyServiceEntry.COLUMN_NAME_ROAD_ID,
                    DailyServiceEntry.COLUMN_NAME_LINE_ID,
                    DailyServiceEntry.COLUMN_NAME_BUSSTOP_ID,
                    DailyServiceEntry.COLUMN_NAME_DIRECTION,
                    DailyServiceEntry.COLUMN_NAME_PROGRESSIVE,
                    DailyServiceEntry.COLUMN_NAME_TIME};

            Cursor resultSet = db.query(table_name, selectClause, whereClause, new String[]{idLine, direction.toString()}, null, null, null);
            if (resultSet.getCount() > 0) {
                resultSet.moveToFirst();
                do {
                    String ride_id = resultSet.getString(0);
                    //String line_id = resultSet.getString(1);
                    String busstop_id = resultSet.getString(2);
                    int progressive = resultSet.getInt(4);
                    long time = resultSet.getLong(5);
                    BusStop b = getBusStop(busstop_id);
                    b.setProgressive(progressive);
                    b.setTime(time);
                    dictionary.get(ride_id).addBusStop(b);
                } while (resultSet.moveToNext());
                resultSet.close();
            }
            result = new ArrayList<>(dictionary.values());
        } catch (Exception e) {
            Log.e("CRUD", "errore nel crud linea: " + idLine + " " + direction);
        }
        return result;
    }

    private  HashMap<String, Ride> createDictionaryMaps(String[] ridesID, String lineID, String direction)
    {
        HashMap<String, Ride> result = new HashMap<>();
        for(String s : ridesID)
        {
            Ride d = new Ride(s,lineID,direction);
            result.put(s, d);
        }
        return  result;
    }

}
