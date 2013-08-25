package com.leaf;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;

import database.ContentsDataSource;

public class BaseFragment extends SherlockFragment {
	protected ContentsDataSource dataSource;
	protected SharedPreferences userInfo;
	protected SharedPreferences.Editor preferencesEditor;
	Location location;
	LocationManager locationManager;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		userInfo = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
		preferencesEditor = userInfo.edit();
		dataSource = LeafActivity.getDatabase(getActivity());
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		dataSource = LeafActivity.getDatabase(getActivity());

	}
	
	
	// Define a listener that responds to location updates
	LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	      // Called when a new location is found by the network location provider.
	    	BaseFragment.this.location = location;
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };
}
