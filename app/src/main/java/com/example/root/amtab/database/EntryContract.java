package com.example.root.amtab.database;

import android.provider.BaseColumns;

/**
 * Created by root on 29/10/16.
 */

public final class EntryContract {

    private EntryContract() {}

    /* Inner class that defines the table contents */
    public static class BusStopEntry implements BaseColumns {
        public static final String TABLE_NAME = "FERMATE";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_DETAILS = "DETTAGLI";
        public static final String COLUMN_NAME_LONGITUDE = "LONGITUDINE";
        public static final String COLUMN_NAME_LATITUDE = "LATITUDINE";
        public static final String COLUMN_NAME_FAVORITE = "PREFERITO";

    }

    public static class LineEntry implements BaseColumns {
        public static final String TABLE_NAME = "LINEE";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_DETAILS = "DETTAGLI";
    }

    public static class BusStopOfLineEntry implements BaseColumns {
        public static final String TABLE_NAME = "FERMATE_DI_LINEA";
        public static final String COLUMN_NAME_LINE_ID = "ID_LINEA";
        public static final String COLUMN_NAME_BUSSTOP_ID = "ID_FERMATA";
        public static final String COLUMN_NAME_PROGRESSIVE = "PROGRESSIVO";
        public static final String COLUMN_NAME_DIRECTION = "DIREZIONE";
    }

    public static class DailyServiceEntry implements BaseColumns{
        public static final String TABLE_NAME_PRE = "SERVIZIO_GIORNALIERO_";
        public static final String COLUMN_NAME_LINE_ID = "ID_LINEA";
        public static final String COLUMN_NAME_BUSSTOP_ID = "ID_FERMATA";
        public static final String COLUMN_NAME_ROAD_ID = "ID_CORSA";
        public static final String COLUMN_NAME_TIME = "ORARIO";
        public static final String COLUMN_NAME_PROGRESSIVE = "PROGRESSIVO";
        public static final String COLUMN_NAME_DIRECTION = "DIREZIONE";
    }

}
