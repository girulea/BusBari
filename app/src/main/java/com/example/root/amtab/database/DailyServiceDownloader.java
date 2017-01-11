package com.example.root.amtab.database;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.root.amtab.Utility.Direction;
import com.example.root.amtab.Utility.Util;
import com.example.root.amtab.entities.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by root on 01/11/16.
 */

    //CLASSE PER IL DOWNLOAD E IL PARSING DEL SERVIZIO GIORNALIERO DI UNA LINEA SPECIFICATA
    public class DailyServiceDownloader extends AsyncTask<Object, Object, Void>
    {
        private static final String URL_DOWNLOAD_DAILY_SERVICE = "http://bari.opendata.planetek.it/OrariBus/v2.1/OpenDataService.svc/REST/ServizioGiornaliero/";
        private JSONArray jsonArray;
        private ArrayList<ContentValues> contentList;
        private Line line;
        private OnEndDownloadListener listener;
        public DailyServiceDownloader(Line line, OnEndDownloadListener listener)
        {
            this.line = line;
            this.listener = listener;
            contentList = new ArrayList<>();
        }
        @Override
        protected Void doInBackground(Object... objects) {

            try {
                download();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v)
        {
            listener.finishedDownloading( contentList , line.getId() );
        }

        private void download(  ) throws JSONException {
            String lineID = line.getId();
            int index = lineID.indexOf('/');
            if (index != -1) {
                lineID = lineID.replaceAll("/", "barrato");
            }
            String url = URL_DOWNLOAD_DAILY_SERVICE + lineID;
            jsonArray = Util.readJSONFromURL(url);
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                ContentValues contentValues = new ContentValues();
                contentValues.put(EntryContract.DailyServiceEntry.COLUMN_NAME_ROAD_ID, object.getString("IdCorsa"));
                contentValues.put(EntryContract.DailyServiceEntry.COLUMN_NAME_BUSSTOP_ID, object.getString("IdFermata"));
                contentValues.put(EntryContract.DailyServiceEntry.COLUMN_NAME_LINE_ID, line.getId());
                contentValues.put(EntryContract.DailyServiceEntry.COLUMN_NAME_DIRECTION, Direction.getDircetionFromString(object.getString("Direzione")).toString());
                contentValues.put(EntryContract.DailyServiceEntry.COLUMN_NAME_PROGRESSIVE, object.getInt("Progressivo"));
                String time = object.getString("Orario");
                long orario = 0;
                try {
                    orario = Util.getDateFromJSONString(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                contentValues.put(EntryContract.DailyServiceEntry.COLUMN_NAME_TIME, orario);
                contentList.add(contentValues);
            }
        }
}
