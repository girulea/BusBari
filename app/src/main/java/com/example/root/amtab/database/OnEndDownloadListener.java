package com.example.root.amtab.database;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created by root on 01/11/16.
 */


//INTERFACCIA USATA COME LISTENER PER NOTIFICARE LA FINE DEL DOWNLOAD DA PARTE DI DAILYSERVICEDOWNLOADER
public interface OnEndDownloadListener {

    void finishedDownloading(ArrayList<ContentValues> contentValuesArrayList, String lineID);
}
