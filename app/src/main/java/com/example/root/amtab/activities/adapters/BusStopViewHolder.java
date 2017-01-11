package com.example.root.amtab.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.root.amtab.R;
import com.example.root.amtab.entities.BusStop;
import com.example.root.amtab.entities.Line;

import java.util.ArrayList;

public class BusStopViewHolder  extends SimpleBusStopViewHolder implements View.OnClickListener{

    protected boolean expanded=false;
    protected LinearLayout viewToExpand;
    private LinearLayout viewContainer;
    public BusStopViewHolder(View v, BusStop busStop) {
        super(v,busStop);
        viewToExpand = (LinearLayout) v.findViewById(R.id.viewToExpand);
        viewContainer = (LinearLayout) v.findViewById(R.id.busstop_view_container);
        viewContainer.setOnClickListener(this);
    }
    protected void collapse()
    {
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.busstop_view_container);
        ll.removeView(v.findViewById(R.id.container_lines));
        expanded = false;
    }

    protected void expand()
    {
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.busstop_view_container);
        ArrayList<Line> lines = busStop.getLines();
        if( lines != null )
        {
            LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.busstop_view_line, null, false);
            GridView linesContainer = (GridView) view.findViewById(R.id.lineList_container);
            LineGridViewAdapter lineGridView = new LineGridViewAdapter( lines, activity );
            linesContainer.setAdapter(lineGridView);
            ll.addView(view);
        }else
        {
            CharSequence text = "Nessuna linea disponibile per la fermata selezionata";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(v.getContext(), text, duration);
            toast.show();
        }
        expanded = true;
    }
    @Override
    public void onClick(View view) {

        if( view.equals( viewContainer)) {
            if (expanded) {
                collapse();
            } else {
                expand();
            }
        }else{
            super.onClick(view);
        }
    }
}