package com.example.root.amtab.database;

/**
 * Created by root on 06/11/16.
 */


    // CLASSE CONTENENTE LE STRINGHE PER LA CREAZIONE DELLE TABELLE SQLITE

    final class CREATION_STRING
    {
        final static String PRE = "CREATE TABLE ";
        final static String PRIMARY_KEY = " TEXT PRIMARY KEY";

        final static String CREATE_LINE = PRE + EntryContract.LineEntry.TABLE_NAME +
                " (" + EntryContract.LineEntry.COLUMN_NAME_ID +
                PRIMARY_KEY + ","+ EntryContract.LineEntry.COLUMN_NAME_DETAILS + " TEXT)";

        final static String CREATE_BUSSTOP = PRE + EntryContract.BusStopEntry.TABLE_NAME +
                " (" + EntryContract.BusStopEntry.COLUMN_NAME_ID +
                PRIMARY_KEY + ","+ EntryContract.BusStopEntry.COLUMN_NAME_DETAILS + " TEXT," +
                EntryContract.BusStopEntry.COLUMN_NAME_LATITUDE + " REAL," +
                EntryContract.BusStopEntry.COLUMN_NAME_LONGITUDE + " REAL,"+EntryContract.BusStopEntry.COLUMN_NAME_FAVORITE+" NUMERIC DEFAULT 0)";

        final static String CREATE_BUSSTOP_OF_LINES = PRE + EntryContract.BusStopOfLineEntry.TABLE_NAME +
                " (" + EntryContract.BusStopOfLineEntry.COLUMN_NAME_LINE_ID + " TEXT," +
                EntryContract.BusStopOfLineEntry.COLUMN_NAME_BUSSTOP_ID + " TEXT," +
                EntryContract.BusStopOfLineEntry.COLUMN_NAME_DIRECTION + " TEXT," +
                EntryContract.BusStopOfLineEntry.COLUMN_NAME_PROGRESSIVE + " INTEGER,"+
                " FOREIGN KEY (" + EntryContract.BusStopOfLineEntry.COLUMN_NAME_LINE_ID +
                ") REFERENCES " + EntryContract.LineEntry.TABLE_NAME + "(" + EntryContract.LineEntry.COLUMN_NAME_ID + "), FOREIGN KEY (" +
                EntryContract.BusStopOfLineEntry.COLUMN_NAME_BUSSTOP_ID +  ") REFERENCES " + EntryContract.BusStopEntry.TABLE_NAME + "(" +
                EntryContract.BusStopEntry.COLUMN_NAME_ID + "), " + " PRIMARY KEY (" + EntryContract.BusStopOfLineEntry.COLUMN_NAME_LINE_ID + "," + EntryContract.BusStopOfLineEntry.COLUMN_NAME_BUSSTOP_ID + "," + EntryContract.BusStopOfLineEntry.COLUMN_NAME_PROGRESSIVE + "," +
                EntryContract.BusStopOfLineEntry.COLUMN_NAME_DIRECTION + "))";
}
