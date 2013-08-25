package com.leaf;

import android.app.Activity;

public class MapFragment extends ActivityHostFragment {
    
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return NearbySpeciesActivity.class;
    }
}