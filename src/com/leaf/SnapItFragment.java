package com.leaf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import uiworks.Preview;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SnapItFragment extends BaseFragment {
	private Preview mPreview;
	private String INPUT_IMG_FILENAME;
	private String UserName;
	private Bitmap snappedImage = null;
	private ProgressDialog dialog;
	private String tempImagePath;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.snapitfragment, container, false);
		System.out.println("ON CREATE VÝEW SNAPIT");
		return view;
	}

	Menu barMenus;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub



		menu.add(2, 2, Menu.NONE, "Capture").setIcon(R.drawable.ic_action_photo)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(1, 3, Menu.NONE, "Try Again").setIcon(R.drawable.ic_action_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(1, 4, Menu.NONE, "Add Collection").setIcon(R.drawable.ic_action_done)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.setGroupVisible(1, false);
		barMenus = menu;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UserName = LeafActivity.user.getName();
		dialog = new ProgressDialog(getActivity());
		// ((LeafActivity) getActivity()).setEventListener(this);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		System.out.println("oncreateactivity#####");
		super.onActivityCreated(savedInstanceState);
		mPreview = (Preview) getView().findViewById(R.id.cameraview);

	}

	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(final byte[] imageData, final android.hardware.Camera camera) {
			if (imageData != null) {
				mPreview.camera.stopPreview();
				Intent mIntent = new Intent();
				getActivity().setResult(0, mIntent);
				snappedImage = getSnappedImage(imageData);
				saveImageToSD(snappedImage);
				barMenus.setGroupVisible(1, true);
				barMenus.setGroupVisible(2, false);
			}

		}
	};

	// Called when shutter is opened
	ShutterCallback shutterCallback = new ShutterCallback() { // <6>
		public void onShutter() {			
			Log.d("snap it", "onShutter'd");
		}
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() { // <7>
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub
			Log.d("snap it", "onPictureTaken - raw");
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case 2:
			System.out.println("2");		
			dialog.setMessage("Do not move! Capturing...");
			dialog.show();
			mPreview.camera.autoFocus(myAutoFocusCallback);
			return true;
		case 3:
			System.out.println("3");
			dataSource.deleteLeaf(tempImagePath);
			mPreview.camera.startPreview();
			barMenus.setGroupVisible(1, false);
			barMenus.setGroupVisible(2, true);
			return true;
		case 4:
			System.out.println("4");
			((LeafActivity) getActivity()).getSupportActionBar().selectTab(
					((LeafActivity) getActivity()).getSupportActionBar().getTabAt(2));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	   AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

		   @Override
		   public void onAutoFocus(boolean arg0, Camera arg1) {
		    // TODO Auto-generated method stub
			   mPreview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);	
		   }};

	public boolean saveImageToSD(final Bitmap bitmap) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				dialog.dismiss();
				bitmap.recycle();
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				File exst = Environment.getExternalStorageDirectory();
				File directory = new File(exst.getPath() + File.separator + "Leaves");
				if (!directory.exists())
					directory.mkdir();
				Date date = new Date();
				CharSequence formattedDate = DateFormat.format("yyyyMMdd-hhmmss", date.getTime());
				INPUT_IMG_FILENAME = UserName + formattedDate.toString();
				System.out.println("INPUT IMAGE :" + INPUT_IMG_FILENAME);

				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				String imagePath = directory.getPath() + File.separator + INPUT_IMG_FILENAME + ".jpeg";
				tempImagePath = imagePath;
				File f = new File(imagePath);
				//Location location = getBestLocation();
				try {
					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					if(dataSource == null)
						System.out.println("NULLLLLLLLLLL");
					
					/**
					 * Latitude, Longitude
						41.105889,29.022982
					 */
					if(location == null){
						System.out.println("location nulll");
						location.setLatitude(41.105889);
						location.setLongitude(29.022982);
					}
					else
						System.out.println("Location : latitude : "+location.getLatitude()+" longitude : "+ location.getLongitude());
					dataSource.addLeaf(imagePath, formattedDate.toString(), Double.toString(location.getLatitude()),
							Double.toString(location.getLongitude()), true, false);

				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute();

		return true;
	}

	private Bitmap getSnappedImage(byte[] imageData) {
		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
		bmpOptions.inJustDecodeBounds = true;
		int sampleSize = 2;
		
		bmpOptions.inSampleSize = sampleSize;
		bmpOptions.inJustDecodeBounds = false;

		Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length,bmpOptions);
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		
		Bitmap resizedBitmap = Bitmap
				.createBitmap(myImage, 0, 0, myImage.getWidth(), myImage.getHeight(), matrix, true);
		
		myImage.recycle();
//		Bitmap image = Bitmap.createScaledBitmap(resizedBitmap, targetWidth, targetHeight, false);
//		resizedBitmap.recycle();
		return resizedBitmap;
	}

//	private void doCrop(Uri mImageCaptureUri) {
//
//		Intent intent = new Intent("com.android.camera.action.CROP");
//		intent.setType("image/*");
//		List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
//		int size = list.size();
//		if (size == 0) {
//			Toast.makeText(getActivity(), "Can not find image crop app", Toast.LENGTH_SHORT).show();
//			return;
//		} else {
//			intent.setData(mImageCaptureUri);
//			intent.putExtra("scale", true);
//			intent.putExtra("return-data", true);
//
//			Intent i = new Intent(intent);
//			ResolveInfo res = list.get(0);
//			i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//			startActivityForResult(i, CROP_FROM_CAMERA);
//
//		}
//	}

//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode != Activity.RESULT_OK)
//			return;
//
//		switch (requestCode) {
//
//		case CROP_FROM_CAMERA:
//			Bundle extras = data.getExtras();
//			if (extras != null) {
//				isCutted = true;
//				Bitmap photo = extras.getParcelable("data");
//				dataSource.deleteLeaf(tempImagePath);
//				saveImageToSD(photo);
//				System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&");
//			}
//			break;
//		}
//
//	}
	
	



//	
//
//	/**
//	 * try to get the 'best' location selected from all providers
//	 */
//	private Location getBestLocation() {
//		Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
//		Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
//
//		// if we have only one location available, the choice is easy
//		if (gpslocation == null) {
//
//			Log.d("Location", "No GPS Location available.");
//			return networkLocation;
//		}
//		if (networkLocation == null) {
//			Log.d("Location", "No Network Location available");
//			return gpslocation;
//		}
//
//		// a locationupdate is considered 'old' if its older than the configured
//		// update interval. this means, we didn't get a
//		// update from this provider since the last check
//		long old = System.currentTimeMillis() - 1200000;
//		boolean gpsIsOld = (gpslocation.getTime() < old);
//		boolean networkIsOld = (networkLocation.getTime() < old);
//
//		// gps is current and available, gps is better than network
//		if (!gpsIsOld) {
//			Log.d("Location", "Returning current GPS Location");
//			return gpslocation;
//		}
//
//		// gps is old, we can't trust it. use network location
//		if (!networkIsOld) {
//			Log.d("Location", "GPS is old, Network is current, returning network");
//			return networkLocation;
//		}
//
//		// both are old return the newer of those two
//		if (gpslocation.getTime() > networkLocation.getTime()) {
//			Log.d("Location", "Both are old, returning gps(newer)");
//			return gpslocation;
//		} else {
//			Log.d("Location", "Both are old, returning network(newer)");
//			return networkLocation;
//		}
//	}
//
//	/**
//	 * get the last known location from a specific provider (network/gps)
//	 */
//	private Location getLocationByProvider(String provider) {
//		Location location = null;
//		LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(
//				Context.LOCATION_SERVICE);
//
//		try {
//			if (locationManager.isProviderEnabled(provider)) {
//
//				location = locationManager.getLastKnownLocation(provider);
//
//			}
//		} catch (IllegalArgumentException e) {
//			Log.d("Location", "Cannot acces Provider " + provider);
//		}
//		return location;
//	}
}