package com.leaf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import database.ContentsDataSource;
import database.User;
import com.actionbarsherlock.view.Window;


public class LeafActivity extends SherlockFragmentActivity {
	
	private static ContentsDataSource dataSource = null;
	public static String SERVER_URL = "http://192.168.90.1:8080/map/api/";
	public static User user;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);	
			SharedPreferences preferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
			user = new User();
			user.setUserName(preferences.getString("userName", null));
			user.setName(preferences.getString("name", null));
			user.setSurname(preferences.getString("surName", null));
			user.setEmail(preferences.getString("email", null));
			user.setPassword(preferences.getString("password", null));
			user.setToken(preferences.getString("token", null));
			user.setUserId(preferences.getInt("userId", -1));
		setupTabs(savedInstanceState);			
	}
	
	public static ContentsDataSource getDatabase (Context context){
		if (dataSource != null && dataSource.isOpen()){
			return dataSource;
		}
		else{
			dataSource = new ContentsDataSource(context);
			dataSource.open();
			return dataSource;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(user == null){
		SharedPreferences preferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
		user = new User();
		user.setUserName(preferences.getString("userName", null));
		user.setName(preferences.getString("name", null));
		user.setSurname(preferences.getString("surName", null));
		user.setEmail(preferences.getString("email", null));
		user.setPassword(preferences.getString("password", null));
		user.setToken(preferences.getString("token", null));
		}
		System.out.println("on resumeee Leaf Activity");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(dataSource != null)
			dataSource.close();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
	}

	public void setupTabs(Bundle savedInstanceState) {

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		ActionBar.Tab homeTab = actionBar.newTab();
		homeTab.setText("Home");
		homeTab.setTabListener(new MyTabListener<HomeFragment>(this, "home", HomeFragment.class, null));
		getSupportActionBar().addTab(homeTab);

		ActionBar.Tab catalogTab = actionBar.newTab();
		catalogTab.setText("Catalog");
		catalogTab.setTabListener(new MyTabListener<CatalogFragment>(this, "catalog", CatalogFragment.class, null));
		getSupportActionBar().addTab(catalogTab);

		ActionBar.Tab collectionTab = getSupportActionBar().newTab();
		collectionTab.setText("My Collection");
		collectionTab.setTabListener(new MyTabListener<CollectionFragment>(this, "mycollection",
				CollectionFragment.class, null));		
		getSupportActionBar().addTab(collectionTab);
		
		ActionBar.Tab snapItTab = actionBar.newTab();
		snapItTab.setText("Snap It");
		snapItTab.setTabListener(new MyTabListener<SnapItFragment>(this, "snapit", SnapItFragment.class, null));
		getSupportActionBar().addTab(snapItTab);
		
		ActionBar.Tab mapFragment = actionBar.newTab();
		mapFragment.setText("Map");
		mapFragment.setTabListener(new MyTabListener<MapFragment>(this, "map", MapFragment.class, null));
		getSupportActionBar().addTab(mapFragment);

		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
	}
}