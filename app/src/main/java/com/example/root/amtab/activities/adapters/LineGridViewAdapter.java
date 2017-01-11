package com.example.root.amtab.activities.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.root.amtab.R;
import com.example.root.amtab.activities.RidesActivity;
import com.example.root.amtab.entities.Line;

import java.util.ArrayList;

/**
 * Created by root on 31/10/16.
 */

public class LineGridViewAdapter extends BaseAdapter {

    private ArrayList <Line> lines;
    private Activity activity;
    public LineGridViewAdapter(ArrayList<Line> lines, Activity activity)
    {
        this.lines = lines;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return lines.size();
    }

    @Override
    public Object getItem(int i) {
        return lines.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        final Line l = lines.get(i);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.circle_line, null, false);
        TextView title = (TextView) v.findViewById(R.id.id_line);
        title.setText(l.getId());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RidesActivity.class);
                intent.putExtra("idLine", l.getId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                } else {
                    activity.startActivity(intent);
                }
            }
        });
        return  v;
    }

}
