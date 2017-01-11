package com.example.root.amtab.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.root.amtab.Utility.Util;
import com.example.root.amtab.entities.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by root on 01/11/16.
 */

    //CLASSE DI GESTIONE DEL DOWNLOAD E DELL'AGGIORNAMENTO DEL DB
    public  class ManageDBTask extends AsyncTask<Object, Object, Void> implements OnEndDownloadListener
    {
        //URI PER ACCEDERE AL SERVIZIO REST

        private static final String URL_DOWNLOAD_LINE = "http://bari.opendata.planetek.it/OrariBus/v2.1/OpenDataService.svc/REST/rete/Linee";
        private static final String URL_DOWNLOAD_BUSSTOP = "http://bari.opendata.planetek.it/OrariBus/v2.1/OpenDataService.svc/REST/rete/Fermate/";
        private static final String URL_DOWNLOAD_BUSSTOP_LINES= "http://bari.opendata.planetek.it/OrariBus/v2.1/OpenDataService.svc/REST/rete/FermateLineaTeoriche/";


        private HashMap<String,ArrayList<ContentValues>> contentValuesHashMap; //DIZIONARIO DI CONTENTVALUE GENERATI DA DAILYSERVICEDOWNLOADER
        private SQLiteDatabase db;
        private DBManager manager;
        private int countLine;
        private boolean onlyDailyService;

        public ManageDBTask(SQLiteDatabase database, DBManager manager, boolean onlyDailyService)
        {
            db = database;
            this.manager = manager;
            contentValuesHashMap = new HashMap<>();
            this.onlyDailyService = onlyDailyService;  //FLAG CHE INDICA SE SI HA NECESSITÀ DI SCARICARE TUTTO O SOLO IL SERVIZIO GIORNALIERO
            countLine = 0;
        }

        protected Void doInBackground(Object... obj) {
            try
            {
                updateAll();
            }catch (Exception e)
            {e.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(Void v){}

        private void updateAll() throws JSONException
        {
            if( !onlyDailyService )
            {
                updateBusStops();
                manager.onEndBusStops();
                updateLines();
                manager.onEndLines();
                updateBusStopsOfLines();
                manager.onEndBusStopsOfLine();
                parseDailyService();
            }
            else
            {
                downloadDailyService();
                parseDailyService();
            }
        }

        //METODO RICHIAMATO SOLO NEL CASO IN CUI LE FERMATE E LE LINEE SIANO GIÀ PRESENTI NEL DB. SELEZIONA TUTTE LE LINEE DEL DB E NE AVVIA IL DOWNLOAD DEL SERVIZIO GIORNALIERO
        private void downloadDailyService()
        {
            try {
                Cursor resultSet = db.query(EntryContract.LineEntry.TABLE_NAME, null, null, null, null, null, null);
                manager.onStartDownloadDailyService( resultSet.getCount() );
                if (resultSet.getCount() > 0) {
                    resultSet.moveToFirst();
                    do {
                        countLine++;
                        Line line = new Line(resultSet.getString(0), resultSet.getString(1));
                        new DailyServiceDownloader( line, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);  // AVVIA IL DOWNLOAD E IL PARSING DEL SERVIZIO GIORNALIERO DELLA LINEA
                    } while (resultSet.moveToNext());
                    resultSet.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        private void updateLines() throws JSONException
        {
            String ID = "IdLinea";
            String DETAIL = "DescrizioneLinea";
            JSONArray array = Util.readJSONFromURL(URL_DOWNLOAD_LINE);
            //Line[] lines = Util.convertJSONArrayToLinesArray(array);
            manager.onStartDownloadLines(array.length());
            db.beginTransaction();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                String lineID =  object.getString(ID);
                contentValues.put(EntryContract.LineEntry.COLUMN_NAME_ID, lineID);
                String detail = object.getString(DETAIL);
                contentValues.put(EntryContract.LineEntry.COLUMN_NAME_DETAILS, detail );
                db.insert(EntryContract.LineEntry.TABLE_NAME,null,contentValues);
                manager.onProgressDownload();
                countLine++;
                new DailyServiceDownloader( new Line(lineID, detail ), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }
        private void updateBusStops() throws JSONException {
            String ID = "IdFermata";
            String DETAIL = "DescrizioneFermata";
            String LONGITUDE = "Longitudine";
            String LATITUDE = "Latitudine";
            JSONArray array = Util.readJSONFromURL(URL_DOWNLOAD_BUSSTOP);
            manager.onStartDownloadBusStops(array.length());
            db.beginTransaction();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                JSONObject position = object.getJSONObject("PosizioneFermata");
                ContentValues contentValues = new ContentValues();
                contentValues.put(EntryContract.BusStopEntry.COLUMN_NAME_ID, object.getString(ID));
                contentValues.put(EntryContract.BusStopEntry.COLUMN_NAME_DETAILS, object.getString(DETAIL));
                contentValues.put(EntryContract.BusStopEntry.COLUMN_NAME_LONGITUDE, position.getDouble(LONGITUDE));
                contentValues.put(EntryContract.BusStopEntry.COLUMN_NAME_LATITUDE, position.getString(LATITUDE));
                db.insert(EntryContract.BusStopEntry.TABLE_NAME,null,contentValues);
                manager.onProgressDownload();
            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }
        private void updateBusStopsOfLines() throws JSONException {
            Cursor resultSet = db.query(EntryContract.LineEntry.TABLE_NAME, null,null,null,null,null,null);
            manager.onStartDownloadBusStopsOfLine( resultSet.getCount()*2 );
            resultSet.moveToFirst();
            do
            {
                String l = resultSet.getString(0);
                String tmp = "";
                int index =  l.indexOf('/');
                if( index != -1 )
                {
                    tmp = l.replaceAll("/","barrato");
                }else{
                    tmp = l;
                }
                JSONArray array = Util.readJSONFromURL( URL_DOWNLOAD_BUSSTOP_LINES + tmp );
                db.beginTransaction();
                for(int j = 0; j < array.length(); j++)
                {
                    JSONObject object = array.getJSONObject(j);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put( EntryContract.BusStopOfLineEntry.COLUMN_NAME_DIRECTION,object.getString("Direzione"));
                    contentValues.put( EntryContract.BusStopOfLineEntry.COLUMN_NAME_LINE_ID,l);
                    contentValues.put(EntryContract.BusStopOfLineEntry.COLUMN_NAME_BUSSTOP_ID,object.getString("IdFermata"));
                    contentValues.put(EntryContract.BusStopOfLineEntry.COLUMN_NAME_PROGRESSIVE,object.getInt("ProgressivoTeorico"));
                    db.insert(EntryContract.BusStopOfLineEntry.TABLE_NAME,null,contentValues);
                }
                manager.onProgressDownload();
                db.setTransactionSuccessful();
                db.endTransaction();
            }while (resultSet.moveToNext());
            resultSet.close();
        }

        private void parseDailyService()  {
            String day = Util.getDayOfWeek().toString();
            String table_name = EntryContract.DailyServiceEntry.TABLE_NAME_PRE + day;
            boolean result = false;
            try {
                do {
                    Thread.sleep(200);
                }while (contentValuesHashMap.keySet().size() != countLine);

                Set<String> keySet = contentValuesHashMap.keySet();
                manager.onStartParseDailyService(keySet.size());
                for( String s : keySet )
                {
                    ArrayList<ContentValues> contentValuesArrayList = contentValuesHashMap.get(s);
                    insert( contentValuesArrayList, table_name);
                    manager.onProgressDownload();
                }
                result = true;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                result = false;
            }
            catch (JSONException e1) {
                e1.printStackTrace();
                result = false;
            }
            if(result) {
                contentValuesHashMap.clear();
                manager.onEndDailyService();
            }
        }

        private void insert(ArrayList<ContentValues> list, String tablename) throws JSONException {
            db.beginTransaction();
            for(int j = 0; j < list.size(); j++)
             {
                ContentValues contentValues = list.get(j);
                db.insert(tablename,null,contentValues);
             }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        @Override
		public void finishedDownloading(ArrayList<ContentValues> contentList, String lineID) {
            manager.onProgressDownload();
            contentValuesHashMap.put(lineID, contentList);
		}


    }
