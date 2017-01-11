package com.example.root.amtab.activities.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.root.amtab.R;
import com.example.root.amtab.activities.TheoreticalPathActivity;
import com.example.root.amtab.entities.Line;

import java.util.ArrayList;

/**
 * Created by root on 15/10/16.
 */

public class LineRecyclerViewAdapter extends RecyclerView.Adapter<LineViewHolder> {

    private Activity activity;
    private ArrayList<Line> listObject;
    private LinearLayout viewContainer;
    public LineRecyclerViewAdapter(Activity activity, ArrayList<Line> listObject) {
        this.activity = activity;
        this.listObject = listObject;
    }




    @Override
    public LineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_view, parent, false);
        Line line = listObject.get(viewType);
        String listTitle = line.getDetails();
        TextView listTitleTextView = (TextView) view.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        TextView idTextView = (TextView) view.findViewById(R.id.id_details);
        idTextView.setText(line.getId());
        LineViewHolder holder = new LineViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LineViewHolder holder, int position) {
        final Line line = listObject.get(position);
        String listTile = line.getDetails();
        viewContainer = (LinearLayout) holder.v.findViewById(R.id.lineViewContainer);
        viewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(view.getContext(), TheoreticalPathActivity.class);
                intent.putExtra("LINEID", line.getId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                }
                else
                {
                    activity.startActivity(intent);
                }
            }
        });
        TextView textViewTitle = (TextView) holder.v.findViewById(R.id.listTitle);
        textViewTitle.setText(listTile);
        TextView textViewId = (TextView) holder.v.findViewById(R.id.id_details);
        textViewId.setText(line.getId());

    }

    @Override
    public int getItemCount() {
        return this.listObject.size();
    }

}