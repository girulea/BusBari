package com.example.root.amtab.maps2;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.root.amtab.R;
import com.example.root.amtab.Utility.Direction;
import com.example.root.amtab.database.CRUD;
import com.example.root.amtab.entities.Line;

import java.util.ArrayList;

/**
 * Created by gian on 08/11/16.
 */

public class LoadDataSpinnerTask extends AsyncTask<String, Void, String> {
    private Activity activity;
    private CRUD crud;
    private String[] idLines;
    private String[] directions;
    private Spinner spinner;
    private Spinner spinnerDirection;
    private int posSpinner;
    private int posSpinnerDirection;
    private  final TaskListener taskListener;


    public LoadDataSpinnerTask(Activity activity, Spinner spinner, Spinner spinnerDirection, int posSpinner, int posSpinnerDirection, TaskListener taskListener) {
        this.activity = activity;
        this.spinner = spinner;
        this.spinnerDirection = spinnerDirection;
        this.posSpinner = posSpinner;
        this.posSpinnerDirection = posSpinnerDirection;
        this.taskListener = taskListener;
    }

    @Override
    protected String doInBackground(String... params) {
        crud = crud.getCRUD();
        initializeArray();
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        ArrayAdapter<String> adapterDirection = new ArrayAdapter<String>(activity, R.layout.spinner_item_maps, directions);
        adapterDirection.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDirection.setAdapter(adapterDirection);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spinner_item_maps, idLines);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (posSpinner != 0) {
            spinner.setSelection(posSpinner);
            spinnerDirection.setSelection(posSpinnerDirection);
            taskListener.onFinished();
        }



    }


    private void initializeArray() {
        idLines = getIdLine();
        directions = new String[2];
        directions[0] = Direction.ANDATA.toString();
        directions[1] = Direction.RITORNO.toString();
    }


    private String[] getIdLine() {
        ArrayList<Line> lines = crud.getLines();
        idLines = new String[lines.size() + 1];
        idLines[0] = "...";
        for (int i = 0; i < idLines.length - 1; i++) {
            idLines[i + 1] = lines.get(i).getId();
        }
        return idLines;
    }

}
