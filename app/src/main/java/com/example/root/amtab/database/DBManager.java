package com.example.root.amtab.database;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.root.amtab.Utility.Util;

import java.util.ArrayList;

import static com.example.root.amtab.database.CREATION_STRING.CREATE_BUSSTOP;
import static com.example.root.amtab.database.CREATION_STRING.CREATE_BUSSTOP_OF_LINES;
import static com.example.root.amtab.database.CREATION_STRING.CREATE_LINE;
import static com.example.root.amtab.database.CREATION_STRING.PRE;


//CLASSE DI GESTIONE DELLA CREAZIONE DEL DB E DEL CONTROLLO SUI DATI
public class DBManager extends SQLiteOpenHelper implements  OnProgressDownloadingListener {

    final static String DOWNLOAD_NUMBER_RIDES = "http://bari.opendata.planetek.it/OrariBus/v2.1/OpenDataService.svc/REST/NumCorseGiorno";
    final static String TABLE_AS_FLAG = "SUCCESS"; //Se la tabella esiste significa che il popolomento del db è andato a buon fine ( FERMATE E LINEE )
    final static String TABLE_AS_FLAG_DAILYSERVICE  = "SUCCESSDAILYSERVICE";
    public static final String DB_NAME = "mydb";
    private SQLiteDatabase writableDB;
    private SQLiteDatabase readableDB;
    private boolean outcomeDailyService = false;
    private boolean outcomeLinesAndBusStops = false;
    static int version = 1;
    private Context context;
    private ArrayList<OnProgressDownloadingListener> listeners;
    private int accessNumber = 0;
    private boolean inTransaction;
    public DBManager(Activity activity) {
        super(activity.getApplicationContext(), DB_NAME, null, version);
        this.context = activity.getApplicationContext();
        listeners = new ArrayList<>();
    }
    
    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(CREATE_LINE);
        database.execSQL(CREATE_BUSSTOP);
        database.execSQL(CREATE_BUSSTOP_OF_LINES);
        String CREATE_DAILY_SERVICE = createDailyService();
        database.execSQL(CREATE_DAILY_SERVICE);
        accessNumber++;
        //TasksDBManager.setManager(this);
        writableDB = database;
        inTransaction = true;
        new ManageDBTask( database, this, false ).execute();
    }
    @Override
    public void onOpen(SQLiteDatabase database )
    {
        if( accessNumber == 0 ) {
            writableDB = database;
            if(!checkTable(TABLE_AS_FLAG, database)) //SE LA TABELLA SUCCESS NON ESISTE SIGNIFICA CHE IL PRECEDENTE DOWNLOAD NON È ANADATO A BUON FINE
            {
                invalidateTable(database,EntryContract.BusStopEntry.TABLE_NAME);    // CANCELLO LE TABELLE PER ELIMINARE I DATI PARZIALI
                invalidateTable(database,EntryContract.LineEntry.TABLE_NAME);       // CANCELLO LE TABELLE PER ELIMINARE I DATI PARZIALI
                invalidateTable(database,EntryContract.BusStopOfLineEntry.TABLE_NAME);// CANCELLO LE TABELLE PER ELIMINARE I DATI PARZIALI
                String day = Util.getDayOfWeek().toString();
                invalidateTable(database,EntryContract.DailyServiceEntry.TABLE_NAME_PRE + day ); // CANCELLO LE TABELLE PER ELIMINARE I DATI PARZIALI
                database.execSQL(CREATE_LINE); //CREAZIONE DELLA TABELLA LINEE
                database.execSQL(CREATE_BUSSTOP);   //CREAZIONE DELLA TABELLA FERMATE
                database.execSQL(CREATE_BUSSTOP_OF_LINES); //CREAZIONE DELLA TABELLA FERMATE DI LINEA
                String CREATE_DAILY_SERVICE = createDailyService();
                database.execSQL(CREATE_DAILY_SERVICE); //CREAZIONE DELLA TABELLA SERVIZIO GIORNALIERO
                inTransaction = true;
                new ManageDBTask( database, this, false ).execute();
            }
            else
            {
                outcomeLinesAndBusStops = true;
                String day = Util.getDayOfWeek().toString();
                String table_name = EntryContract.DailyServiceEntry.TABLE_NAME_PRE + day;
                int attualHours = Integer.parseInt(Util.getDateFormat(Util.HOUR_FORMAT));
                if ( ( !checkTable(table_name, database) || !checkTable(TABLE_AS_FLAG_DAILYSERVICE,database) ) && attualHours > 5 )
                {
                    invalidateTable(database, TABLE_AS_FLAG_DAILYSERVICE);
                    invalidateTable( database, table_name );
                    invalidateTable(database, EntryContract.DailyServiceEntry.TABLE_NAME_PRE + Util.getYesterdayDateString() );
                    String creationString = createDailyService();
                    database.execSQL(creationString);
                    inTransaction = true;
                    new ManageDBTask(database, this, true).execute();
                }
                else
                {
                    outcomeDailyService = true;
                    onEndDailyService();
                }
            }
        }
        accessNumber++;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // metodo pensato per convalidare i dati dopo il download. Attualmente inutilizzabile poichè il servizio rest restituisce un valore con un margine di errore di + o - 100. Fino ad un probabile bug fix, useremo un workaround
    private boolean checkDailyService(SQLiteDatabase database)
    {
        boolean result = false;
        try
        {
            String table_name = EntryContract.DailyServiceEntry.TABLE_NAME_PRE + Util.getDayOfWeek();
            String[] selection = new String[]{ EntryContract.DailyServiceEntry.COLUMN_NAME_ROAD_ID };
            Cursor resultSet = database.query(table_name,selection,null,null,EntryContract.DailyServiceEntry.COLUMN_NAME_ROAD_ID, null,null,null);
            int currentRow = resultSet.getCount();
            resultSet.close();
            int nRow = Integer.parseInt( Util.readFromUrl( DOWNLOAD_NUMBER_RIDES ).replace("\n","") );
            if( currentRow == nRow )
            {
                result = true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    // FUNZIONE PER VERIFICARE CHE UNA TEBELLA ALL'INTERNO DEL DB ESISTA
    private boolean checkTable(String name, SQLiteDatabase db )
    {
        boolean result = true;
        try{
            Cursor resultSet = db.query( name ,null, null, null, null, null, null );
            resultSet.close();
        }catch (Exception e)
        {
            result = false;
        }
        return result;
    }

    // METODO PER ELIMINARE UNA TABELLA DAL DB
    public void invalidateTable(SQLiteDatabase database, String tableName)
    {
        try{
            database.execSQL("DROP TABLE IF EXISTS " + tableName);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private String createDailyService()
    {
        String day = Util.getDayOfWeek().toString();
        String table_name = EntryContract.DailyServiceEntry.TABLE_NAME_PRE + day;
        String create_service = PRE + table_name + " (" + EntryContract.DailyServiceEntry.COLUMN_NAME_ROAD_ID + " TEXT," +
                EntryContract.DailyServiceEntry.COLUMN_NAME_LINE_ID + " TEXT,"+EntryContract.DailyServiceEntry.COLUMN_NAME_BUSSTOP_ID +" TEXT,"+
                EntryContract.DailyServiceEntry.COLUMN_NAME_DIRECTION + " TEXT,"+  EntryContract.DailyServiceEntry.COLUMN_NAME_PROGRESSIVE + " INTEGER,"+
                EntryContract.DailyServiceEntry.COLUMN_NAME_TIME + " INTEGER,"+
                " FOREIGN KEY (" + EntryContract.DailyServiceEntry.COLUMN_NAME_LINE_ID +
                ") REFERENCES " + EntryContract.LineEntry.TABLE_NAME + "(" + EntryContract.LineEntry.COLUMN_NAME_ID + "),"+
                " FOREIGN KEY (" + EntryContract.DailyServiceEntry.COLUMN_NAME_BUSSTOP_ID +
                ") REFERENCES " + EntryContract.BusStopEntry.TABLE_NAME + "(" + EntryContract.BusStopEntry.COLUMN_NAME_ID + "), " +
                " PRIMARY KEY (" + EntryContract.DailyServiceEntry.COLUMN_NAME_ROAD_ID + "," + EntryContract.DailyServiceEntry.COLUMN_NAME_PROGRESSIVE + "))";
        return create_service;
    }


    //ESEGUITO OVVERRIDE PER EVITARE DI APRIRE TROPPE CONNESSIONE AL DB
    @Override
    public SQLiteDatabase getWritableDatabase()
    {
        if(writableDB == null )
        {
            writableDB = super.getWritableDatabase();
        }
        return writableDB;
    }
    @Override
    public SQLiteDatabase getReadableDatabase()
    {
        if(readableDB == null )
        {
            readableDB = super.getReadableDatabase();
        }
        return readableDB;
    }

    public void addListener(OnProgressDownloadingListener listener)
    {
        if( !listeners.contains(listener))
        {
            listeners.add(listener);
            if (outcomeDailyService)
            {
                listener.onEndDailyService();
            }
            else if(outcomeLinesAndBusStops)
            {
                listener.onEndLines();
                listener.onEndBusStops();
                listener.onEndBusStopsOfLine();
            }

        }
    }
    public void removeListener(OnProgressDownloadingListener listener)
    {
        if( listeners.contains(listener))
        {
            listeners.remove(listener);
        }
    }
    @Override
    public void onEndLines()
    {
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onEndLines();
        }
    }

    @Override
    public void onStartDownloadLines(int number)
    {
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onStartDownloadLines(number);
        }
    }

    public void onEndBusStops()
    {
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onEndBusStops();
        }
    }

    @Override
    public void onStartDownloadBusStops(int number)
    {
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onStartDownloadBusStops(number);
        }
    }
    @Override
    public void onEndBusStopsOfLine()
    {
        outcomeLinesAndBusStops = true;
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_AS_FLAG +"( NOTHING TEXT )");
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onEndBusStopsOfLine();
        }

    }

    @Override
    public void onStartDownloadBusStopsOfLine(int number)
    {
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onStartDownloadBusStopsOfLine(number);
        }
    }

    @Override
    public void onProgressDownload()
    {
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onProgressDownload();
        }
    }

    @Override
    public void onEndDailyService()
    {
        outcomeDailyService = true;
        inTransaction = false;
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_AS_FLAG_DAILYSERVICE +"( NOTHING TEXT )");
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onEndDailyService();
        }
        listeners.clear();
    }

    @Override
    public void onStartDownloadDailyService(int number)
    {
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onStartDownloadDailyService(number);
        }
    }

    @Override
    public void onStartParseDailyService(int number) {
        for( OnProgressDownloadingListener listener : listeners )
        {
            listener.onStartParseDailyService(number);
        }
    }

    // METODO PER VERIFICARE CHE NON CI SIANO UNA TRANSAZIONE IN ATTO
    public boolean isInTransaction()
    {
        return  inTransaction;
    }
}