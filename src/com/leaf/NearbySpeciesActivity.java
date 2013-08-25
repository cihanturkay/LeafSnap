package com.leaf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uiworks.CustomItemizedOverlay;
import uiworks.CustomOverlayItem;
import uiworks.MyOverlays;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class NearbySpeciesActivity extends MapActivity {

	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	CustomItemizedOverlay<CustomOverlayItem> itemizedOverlay;
	private MyLocationOverlay myLocationOverlay;

	// private MyOverlays itemizedoverlay;
	// private MyLocationOverlay myLocationOverlay;
	private List<Overlay> mapOverlays;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.nearbyspeciesfragment); // bind the layout to
														// the activity

		// Configure the Map
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		mapController = mapView.getController();
		mapController.setZoom(14); // Zoon 1 is world view

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new GeoUpdateHandler());

		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);

		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(myLocationOverlay.getMyLocation());

				System.out.println("geo_info lat :" + myLocationOverlay.getMyLocation().getLatitudeE6());
				System.out.println("geo_info lng :" + myLocationOverlay.getMyLocation().getLongitudeE6());
			}
		});

		// itemizedoverlay = new MyOverlays(this, drawable);
		// createMarker();

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			System.out.println("geo_info lat :" + lat);
			System.out.println("geo_info lng :" + lng);
			GeoPoint point = new GeoPoint(lat, lng);
			createMarker();
			mapController.animateTo(point); // mapController.setCenter(point);

		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	private void createMarker() {
		System.out.println("Create Marker");

		new AsyncTask<Void, Void, Void>() {
			ProgressDialog dialog = new ProgressDialog(NearbySpeciesActivity.this);
			CustomOverlayItem overlayItem;
			String user;
			String createDate;
			String url;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.setMessage("Map is loading...");
				dialog.show();
			}

			protected void onPostExecute(Void result) {
				mapOverlays.add(itemizedOverlay);
				dialog.dismiss();

			};

			@Override
			protected Void doInBackground(Void... arg0) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(LeafActivity.SERVER_URL);
				JSONObject json = null;
				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("method", "get_photos"));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					String responceString = EntityUtils.toString(response.getEntity());
					responceString = responceString.substring(responceString.indexOf("{"), responceString.length());
					try {

						json = new JSONObject(responceString);
						JSONArray jArray = json.getJSONArray("data");

						System.out.println("*****JARRAY*****" + jArray);
						for (int i = 0; i < jArray.length(); i++) {

							JSONObject json_data = jArray.getJSONObject(i);
							String id = json_data.getString("photo_id");
							String latitude = json_data.getString("latitude");
							String longitude = json_data.getString("longitude");

							double lati = Double.parseDouble(latitude);
							double longi = Double.parseDouble(longitude);

							if (longi != 0 || lati != 0) {

								GeoPoint geo = new GeoPoint((int) (lati * 1E6), (int) (longi * 1E6));
								OverlayItem item = new OverlayItem(geo, id, null);

								httppost = new HttpPost(LeafActivity.SERVER_URL + "?method=get_photo_details&photo_id=" + id);
								response = httpclient.execute(httppost);
								responceString = EntityUtils.toString(response.getEntity());
								responceString = responceString.substring(responceString.indexOf("{"),
										responceString.length());
								System.out.println("response string : " + responceString);

								json = new JSONObject(responceString);
								json = json.getJSONObject("data").getJSONObject("photo");
								System.out.println("json : " + json);
								user = json.getJSONObject("user").getString("user_name");
								createDate = json.getString("create_date");
								url = json.getString("url");
								System.out.println("user : " + user + "url : " + url);

								overlayItem = new CustomOverlayItem(geo, user, createDate, url);
								itemizedOverlay.addOverlay(overlayItem);

								System.out
										.println("photo id : " + id + " latitude : " + lati + " longitude : " + longi);
							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (ClientProtocolException e) {
				} catch (IOException e) {
				}
				return null;
			}

		}.execute();
	}

	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.point);
		itemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(drawable, mapView);
		createMarker();
	}

	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();

	}
}