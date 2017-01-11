package com.example.root.amtab.maps2.cluster;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by gian on 25/10/16.
 */

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private final String title;
    private final String snippet;


    public MyItem( String title, String snippet, LatLng mPosition) {
        this.mPosition = mPosition;
        this.title = title;
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }
}
