package com.example.root.amtab.database;

/**
 * Created by root on 29/10/16.
 */

public interface OnProgressDownloadingListener
{
    void onEndLines();
    void onStartDownloadLines(int number);

    void onEndBusStops();
    void onStartDownloadBusStops(int number);

    void onEndDailyService();
    void onStartDownloadDailyService(int number);
    void onStartParseDailyService(int number);

    void onEndBusStopsOfLine();
    void onStartDownloadBusStopsOfLine(int number);

    void onProgressDownload();

}
