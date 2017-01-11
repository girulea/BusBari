package com.example.root.amtab.Utility;


import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.example.root.amtab.entities.Ride;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by root on 15/10/16.
 */

public class Util {

    public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    public static final String SHORT_DATE_FORMAT = "HH:mm";
    public static final String MINUTE_FORMAT = "mm";
    public static final String HOUR_FORMAT = "HH";
    private static long tStart;
    private static long tEnd;


    //metodo per la lettura di una stringa da un url e conversine in formato jsonarrray
    public static JSONArray readJSONFromURL(String urlString) {

        JSONArray jsonArray = null;
        String jsonString;
        while( jsonArray == null ) {
            try {
                jsonString = readFromUrl(urlString);
                jsonArray = new JSONArray(jsonString);
            } catch (Exception e) {

            }
        }
        return jsonArray;
    }

    // metodo per il download id una stringa da un url
    public static String readFromUrl(String urlString )
    {
        String result = "";
        try {
            HttpURLConnection urlConnection = null;

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // metodo che ritorno il giorno della settimana di ieri
    public static EDAY getYesterdayDateString() {
        Calendar cal = Calendar.getInstance(Locale.ITALIAN);
        cal.add(Calendar.DATE, -1);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        return getEDAY(day);
    }

    // metodo che restiusce il giorno della settimana di tipo enum
    public static EDAY getDayOfWeek()
    {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        EDAY result = getEDAY(day);
        return result;
    }

    private static EDAY getEDAY(int day)
    {
        EDAY result = null;
        switch(day)
        {
            case Calendar.MONDAY:
                result = EDAY.MONDAY;
                break;
            case Calendar.TUESDAY:
                result = EDAY.TUESDAY;
                break;
            case Calendar.WEDNESDAY:
                result = EDAY.WEDNESDAY;
                break;
            case Calendar.THURSDAY:
                result = EDAY.THURSDAY;
                break;
            case Calendar.FRIDAY:
                result = EDAY.FRIDAY;
                break;
            case Calendar.SATURDAY:
                result = EDAY.SATURDAY;
                break;
            case Calendar.SUNDAY:
                result = EDAY.SUNDAY;
                break;
        }
        return result;
    }

    //ordina le corse in base all'orario
    public static void sortRidesByTime(ArrayList<Ride> list)
    {
        Collections.sort(list, new Comparator<Ride>() {
            @Override
            public int compare(Ride o1, Ride o2) {
                return (int ) ( o1.getBusStops().get(0).getTime() - o2.getBusStops().get(0).getTime() );
            }
        });
    }

    // metodo che restituisce una data formattata da un stringa json secondo lo standard unix-timestamp
    public static long getDateFromJSONString(String d) throws ParseException {
        d = d.replace("/Date(","");
        int index = d.indexOf("+");
        if(index != -1)
        {
            d = d.substring(0,index);
        }
        long number = Long.parseLong(d);
        return number;
    }

    //metodo che restituisce lo stato di una corsa in base al tempo corrente
    public static Status getRideStatus(Ride ride)
    {
        Status sta = null;
        long time = ride.getBusStops().get(0).getTime();
        String formatTime = Util.getShortDateFormat(time);
        int hours = Integer.parseInt( formatTime.substring(0,2));
        int minutes = Integer.parseInt(formatTime.substring(3));
        if( checkIfTimeIsPast( hours, minutes))
        {
            sta = Status.STARTED;
            time = ride.getBusStops().get( ride.getBusStops().size() - 1 ).getTime();
            formatTime = Util.getShortDateFormat(time);
            hours = Integer.parseInt( formatTime.substring(0,2));
            minutes = Integer.parseInt(formatTime.substring(3));
            if( checkIfTimeIsPast(hours, minutes ))
            {
                sta = Status.FINISHED;
            }
        }else{
            sta = Status.NOT_STARTED;
        }
        return  sta;
    }

    private static boolean checkIfTimeIsPast( int hours, int minutes)
    {
        boolean result = false;
        int attualHours = Integer.parseInt(Util.getDateFormat(Util.HOUR_FORMAT));
        int attualMinutes = Integer.parseInt(Util.getDateFormat(Util.MINUTE_FORMAT));
        if( attualHours > hours  || ( attualHours == hours &&  attualMinutes > minutes ))
        {
            result = true;
        }
        return result;
    }

    //metodo che restiusce un oggetto calendar dal timestamp
    public static Calendar getDateFromTimestamp(long time) {
        Calendar cal = Calendar.getInstance(Locale.ITALIAN);
        cal.setTimeInMillis(time);
        return cal;
    }

    // metodo che restituisce l'orario attuale in base, secondo il tipo di formattazione passata
    @NonNull
    public static String getDateFormat(String format)
    {
        Calendar cal = Calendar.getInstance(Locale.ITALIAN);
        return DateFormat.format( format, cal).toString();
    }

    // meotodo che restitusce ora e minuti di una determinata istanza di tempo da un oggetto calendar
    @NonNull
    public static String getShortDateFormat(Calendar cal)
    {
        return DateFormat.format(SHORT_DATE_FORMAT, cal).toString();
    }
     //meotodo che restitusce ora e minuti di una determinata istanza di tempo dato uno timestamp
    @NonNull
    public static String getShortDateFormat(long timeMillis)
    {
        Calendar cal = getDateFromTimestamp(timeMillis);
        return getShortDateFormat(cal);
    }

    // timer usati durante il debug per verificare il tempo di esecuzione di alcuni task
    public static void startTimer()
    {
        tStart = System.currentTimeMillis();
    }
    public static double getElapsedTime()
    {
        tEnd = System.currentTimeMillis();
        return ( tEnd - tStart)  / 1000.0;
    }
}
