package com.example.root.amtab.Utility;

import com.example.root.amtab.activities.adapters.DataEvents;

/**
 * Created by root on 03/11/16.
 */


// classe di supporto per eseguire task ( con parametri ) in parallelo, con un listener per avvisare della fine di un task
public class CustomRunnable implements  Runnable {

    public Object object;
    public DataEvents listener;
    public  CustomRunnable(Object object, DataEvents listener)
    {
        this.object = object;
       this.listener = listener;
    }

    @Override
    public void run() {}
}
