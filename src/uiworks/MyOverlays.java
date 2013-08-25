package uiworks;

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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.leaf.LeafActivity;
import com.leaf.NearbySpeciesActivity;
import com.leaf.R;

public class MyOverlays extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private MapView map;
	private PopupPanel panel;
	private AsyncTask loadImageTask;
	private TextView textView;
	private ImageView image;

	public MyOverlays(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public MyOverlays(Drawable defaultMarker, Context context, MapView map) {
		super(boundCenterBottom(defaultMarker));
		this.mContext = context;
		this.map = map;
		panel = new PopupPanel(R.layout.popuppanel);
	}

	protected boolean onTap(int index) {
		OverlayItem item = getItem(index);
		GeoPoint geo = item.getPoint();
		Point pt = map.getProjection().toPixels(geo, null);

		View view = panel.getView();
		
		image = (ImageView) view.findViewById(R.id.popupimage);
		textView = (TextView) view.findViewById(R.id.popuptext);
		setImage(Integer.parseInt(item.getTitle()));
		panel.show(pt.y * 2 > map.getHeight());

		return (true);
		// OverlayItem overlayItem = mOverlays.get(index);
		// Builder builder = new AlertDialog.Builder(mContext);
		// builder.setTitle(overlayItem.getTitle());
		// builder.setMessage(overlayItem.getSnippet());
		// builder.setCancelable(true);
		// builder.setPositiveButton("Detail", new OkOnClickListener());
		// builder.setNegativeButton("Cancel", new CancelOnClickListener());
		// AlertDialog dialog = builder.create();
		// dialog.show();
		// return true;
	};

	private final class CancelOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {

		}
	}

	private final class OkOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {

		}
	}

	public class PopupPanel {
		View popup;
		boolean isVisible = false;

		PopupPanel(int layout) {
			ViewGroup parent = (ViewGroup) map.getParent();

			popup = LayoutInflater.from(mContext).inflate(layout, parent, false);

			popup.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					hide();
				}
			});
		}

		View getView() {
			return (popup);
		}

		void show(boolean alignTop) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			if (alignTop) {
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				lp.setMargins(0, 20, 0, 0);
			} else {
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				lp.setMargins(0, 0, 0, 60);
			}

			hide();

			((ViewGroup) map.getParent()).addView(popup, lp);
			isVisible = true;
		}

		void hide() {
			if (isVisible) {
				isVisible = false;
				((ViewGroup) popup.getParent()).removeView(popup);
			}
		}
	}
	
	private void setImage(final int id) {
		System.out.println("Create Marker");

		new AsyncTask<Void, Void, Void>() {
			String user;
			String createDate;
			String url;
			
			@Override
			protected void onPreExecute() {
				
			}

			protected void onPostExecute(Void result) {
				UrlImageViewHelper.setUrlDrawable(image, url, R.drawable.loading, null);
				textView.setText(user + " " +createDate + " tarihinde çekti.");
			};
			

			@Override
			protected Void doInBackground(Void... arg0) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(LeafActivity.SERVER_URL);
				JSONObject json = null;
				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("method", "get_photo_details"));
					nameValuePairs.add(new BasicNameValuePair("photo_id", Integer.toString(id)));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					String responceString = EntityUtils.toString(response.getEntity());
					responceString = responceString.substring(responceString.indexOf("{"), responceString.length());
					try {

					
						json = new JSONObject(responceString);
						json = json.getJSONObject("data").getJSONObject("photo");
						System.out.println("json : "+ json);
						user = json.getJSONObject("user").getString("user_name");
						createDate = json.getString("create_date");
						url = "http://turkaylar.com/i/photos/"+id+".jpg";							
						System.out.println("user : "+user +"url : "+url);
						
					} catch (JSONException e) {
						e.printStackTrace();
						}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute();
	}

	
}