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
import org.json.JSONException;
import org.json.JSONObject;

import uiworks.RectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import database.Leaf;
import database.User;

public class DrawRectFragment extends BaseFragment {

	private RectView rectView;
	private String cameImagepath;
	private long cameImageID;
	private int photoId;
	private float bitmapHeight, bitmapWidth;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		System.out.println("drawrect fragment callledd");
		return inflater.inflate(R.layout.drawrectfragment, container, false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub

		menu.add(1, 1, Menu.NONE, "Explore").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(2, 2, Menu.NONE, "Back").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case 1:
			updateLeaf();
			return true;
		case 2:

			getActivity().onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = this.getArguments();
		cameImagepath = bundle.getString("path");
		cameImageID = bundle.getLong("id");
		photoId = bundle.getInt("photo_id");
		rectView = (RectView) getView().findViewById(R.id.editview);
		Bitmap bitmap = BitmapFactory.decodeFile(cameImagepath);
		bitmapHeight = bitmap.getHeight();
		bitmapWidth = bitmap.getWidth();
		System.out.println("came bitmap height : " + bitmap.getHeight());
		rectView.setImageBitmap(bitmap);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	public boolean updateLeaf() {

		new AsyncTask<Void, Void, JSONObject>() {
			ProgressDialog dialog = new ProgressDialog(getActivity());
			User user = LeafActivity.user;

			public void openWebURL( String inURL ) {
			    Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );

			    startActivity( browse );
			}
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.setMessage("Image Processing..");
				dialog.show();
			}

			protected void onPostExecute(JSONObject json) {
				dialog.dismiss();
				Leaf resultLeaf = new Leaf();
				
				
				if(json == null)
					return;				
				
				try {
					
					JSONObject specJson = json.getJSONObject("data").getJSONObject("species");
					String botanicalName,name,nameTr;
					final String link;
					int specid;
					botanicalName = specJson.getString("botanical_name");
					name = specJson.getString("common_name");
					nameTr = specJson.getString("common_name_tr");
					link = specJson.getString("wiki_link");
					specid = specJson.getInt("species_id");
					System.out.println("SONUC :::"+ botanicalName + name + nameTr + specid + link);
					 					
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
					alertDialogBuilder.setTitle(botanicalName);
					alertDialogBuilder.setMessage(name + " - " + nameTr).setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							}).setNegativeButton("WIKI", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									openWebURL(link);
								}
							});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//getActivity().onBackPressed();
			};

			@Override
			protected JSONObject doInBackground(Void... arg0) {
			
				String coordinatesDetail;
				coordinatesDetail = rectView.getRectCordinates(bitmapHeight, bitmapWidth);
				System.out.println("cordinate details : " + coordinatesDetail);
				dataSource.updateLeaf(cameImageID, coordinatesDetail);
				String[] corArray = coordinatesDetail.split(","); // x,y,width,height
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(LeafActivity.SERVER_URL);
				JSONObject json = null;
				
				System.out.println("photo id : " + photoId + "  x : "+corArray[0]);
				
				
				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
					nameValuePairs.add(new BasicNameValuePair("method", "segment_photo"));
					nameValuePairs.add(new BasicNameValuePair("user_id", Integer.toString(user.getUserId())));
					nameValuePairs.add(new BasicNameValuePair("token", user.getToken()));
					nameValuePairs.add(new BasicNameValuePair("photo_id", Integer.toString(photoId)));
					nameValuePairs.add(new BasicNameValuePair("x", corArray[0]));
					nameValuePairs.add(new BasicNameValuePair("y", corArray[1]));
					nameValuePairs.add(new BasicNameValuePair("width", corArray[2]));
					nameValuePairs.add(new BasicNameValuePair("height", corArray[3]));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					
					HttpResponse response = httpclient.execute(httppost);
					
					String responceString = EntityUtils.toString(response.getEntity());
					responceString = responceString.substring(responceString.indexOf("{"), responceString.length());
					
					System.out.println("response : "+responceString);
					
					json = new JSONObject(responceString);
					return json;

				} catch (ClientProtocolException e) {
				} catch (IOException e) {
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

		}.execute();

		return true;
	}
}
