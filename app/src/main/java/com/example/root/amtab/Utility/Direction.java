package com.example.root.amtab.Utility;

/**
 * Created by root on 14/10/16.
 */

// classe enum per la direzione di una linea/bus
public enum Direction {
    ANDATA,RITORNO,E;

    public static Direction getDircetionFromString(String d)
    {
        Direction result = Direction.E;
        d = d.toLowerCase();
        switch (d)
        {
            case "andata":;
            case "a" :result = Direction.ANDATA;
                break;
            case "ritorno":;
            case "r": result = Direction.RITORNO;
                break;
        }
        return result;
    }

    public static String[] directionsToString(Direction[] directions)
    {
        String[] result = new String[directions.length];
        for(int i = 0; i < result.length; i++ )
        {
            result[i] = directions[i].toString();
        }
        return result;
    }

    public static Direction[] getDirectionsFromString(String[] directions)
    {
        Direction[] result = new Direction[directions.length];
        for(int i = 0; i < result.length; i++ )
        {
            result[i] = getDircetionFromString( directions[i]);
        }
        return result;
    }
}
