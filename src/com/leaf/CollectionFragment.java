package com.leaf;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import uiworks.GalleryAdapter;
import uiworks.LeafAdapter;
import uiworks.PicAdapter;
import uiworks.PicPreview;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;

import database.Leaf;
import database.User;

public class CollectionFragment extends BaseFragment {

	// private PicAdapter imgAdapt;
	// private Gallery picGallery;
	private PicPreview picView;
	private ArrayList<Bitmap> imageBitmaps;
	private int selectedImage;
	private String selectedImagePath = null;
	ArrayList<Leaf> leaves;
	Menu barMenus;
	AsyncTask loadGalleryImages;
	private ListView gallerList;
	private GalleryAdapter galleryAdapter;

	// private List<File> filePaths;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.collectionfragment, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
		loadGalleryImages.cancel(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub

		menu.add(0, 0, Menu.NONE, "Edit").setIcon(R.drawable.ic_action_edit)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(1, 1, Menu.NONE, "Upload").setIcon(R.drawable.ic_action_upload)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(2, 2, Menu.NONE, "Get Info").setIcon(R.drawable.ic_action_about)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(3, 3, Menu.NONE, "Delete").setIcon(R.drawable.ic_action_delete)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		barMenus = menu;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case 0:
			if (selectedImagePath != null) {
				DrawRectFragment fragment = new DrawRectFragment();
				Bundle data = new Bundle();
				data.putString("path", selectedImagePath);
				System.out.println("PHOTO ID : " + dataSource.getPhotoID(leaves.get(selectedImage).getId()));
				data.putLong("id", leaves.get(selectedImage).getId());
				data.putInt("photo_id", dataSource.getPhotoID(leaves.get(selectedImage).getId()));
				fragment.setArguments(data);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(android.R.id.content, fragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
			return true;

		case 1:
			try {
				uploadPhoto(leaves.get(selectedImage));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		case 2:
			System.out.println("info clicked");
			getPhotoInfo();
			return true;
		case 3:
			deleteLeafFromGallery(selectedImage);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void getPhotoInfo() {

		new AsyncTask<Void, Void, JSONObject>() {
			ProgressDialog dialog = new ProgressDialog(getActivity());
			User user = LeafActivity.user;

			public void openWebURL(String inURL) {
				Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));

				startActivity(browse);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.setMessage("Image Details Loading..");
				dialog.show();
			}

			protected void onPostExecute(JSONObject json) {
				dialog.dismiss();
				if (json == null)
					return;

//				if (json.isNull("species")) {
//
//					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//					alertDialogBuilder.setTitle("Error");
//					alertDialogBuilder.setMessage("Please edit leaf location and explore at least once.")
//							.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog, int id) {
//									dialog.cancel();
//								}
//							});
//					AlertDialog alertDialog = alertDialogBuilder.create();
//					alertDialog.show();
//					return;
//				}

				try {

					JSONObject specJson = json.getJSONObject("data").getJSONObject("photo").getJSONObject("species");
					String botanicalName, name, nameTr;
					final String link;
					int specid;
					botanicalName = specJson.getString("botanical_name");
					name = specJson.getString("common_name");
					nameTr = specJson.getString("common_name_tr");
					link = specJson.getString("wiki_link");
					specid = specJson.getInt("species_id");
					System.out.println("SONUC :::" + botanicalName + name + nameTr + specid + link);

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

				// getActivity().onBackPressed();
			};

			@Override
			protected JSONObject doInBackground(Void... arg0) {

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(LeafActivity.SERVER_URL);
				JSONObject json = null;

				try {
					String photo_id = Integer.toString(dataSource.getPhotoID(leaves.get(selectedImage).getId()));
					// List<NameValuePair> nameValuePairs = new
					// ArrayList<NameValuePair>(2);
					// nameValuePairs.add(new BasicNameValuePair("method",
					// "get_photo_details"));
					// nameValuePairs.add(new BasicNameValuePair("photo_id",
					// Integer.toString(dataSource.getPhotoID(leaves
					// .get(selectedImage).getId()))));
					//
					// System.out.println("INFOO PHOTo  ID :" +
					// dataSource.getPhotoID(leaves.get(selectedImage).getId()));
					//
					// httppost.setEntity(new
					// UrlEncodedFormEntity(nameValuePairs));
					httppost = new HttpPost(LeafActivity.SERVER_URL + "?method=get_photo_details&photo_id=" + photo_id);
					HttpResponse response = httpclient.execute(httppost);

					String responceString = EntityUtils.toString(response.getEntity());
					responceString = responceString.substring(responceString.indexOf("{"), responceString.length());

					System.out.println("response : " + responceString);
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

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LoadGalleryImages(-1);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		imageBitmaps = new ArrayList<Bitmap>();
		picView = (PicPreview) getView().findViewById(R.id.picture);
		gallerList = (ListView) getView().findViewById(R.id.gallerlist);

		gallerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
				bmpOptions.inJustDecodeBounds = false;
				bmpOptions.inSampleSize = 1;
				selectedImagePath = leaves.get(position).getImagePath();
				Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, bmpOptions);
				picView.setImageBitmap(bm);
				picView.setRect(leaves.get(position).getDetail(), bm.getHeight(), bm.getWidth());
				selectedImage = position;

				System.out.println(" photo id : " + leaves.get(position).getPhotoId());

			}
		});

	}

	public void LoadGalleryImages(final int position) {

		loadGalleryImages = new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						System.out.println("COLECTION on pre executeee baþladý ");
						galleryAdapter = new GalleryAdapter(getActivity(), imageBitmaps);
						gallerList.setAdapter(galleryAdapter);
						BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
						bmpOptions.inJustDecodeBounds = false;
						bmpOptions.inSampleSize = 2;
						if (position == -1 && galleryAdapter.getCount() > 0) {
							selectedImage = galleryAdapter.getCount() - 1;
							selectedImagePath = leaves.get(galleryAdapter.getCount() - 1).getImagePath();
							Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, bmpOptions);
							picView.setImageBitmap(bm);
							gallerList.setSelection(galleryAdapter.getCount() - 1);
							galleryAdapter.notifyDataSetChanged();
						} else if (galleryAdapter.getCount() > 0) {
							selectedImage = position;
							selectedImagePath = leaves.get(position).getImagePath();
							Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, bmpOptions);
							picView.setImageBitmap(bm);
							gallerList.setSelection(position);
							galleryAdapter.notifyDataSetChanged();
						}

						((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
						System.out.println("COLECTION on pre executeee bittii");
					}

				});

			}

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				leaves = new ArrayList<Leaf>();
				imageBitmaps = new ArrayList<Bitmap>();
				System.gc();
				leaves = (ArrayList<Leaf>) dataSource.getLocalLeaf();
				System.out.println("leaves size : " + leaves.size());

				Bitmap pic;
				for (Leaf leaf : leaves) {
					BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
					bmpOptions.inJustDecodeBounds = false;
					bmpOptions.inSampleSize = 8;
					pic = BitmapFactory.decodeFile(leaf.getImagePath(), bmpOptions);
					if (pic == null) {
						dataSource.deleteLeaf(leaf.getImagePath());
					} else {
						imageBitmaps.add(pic);
					}
				}

				System.out.println("image bitmap size : " + imageBitmaps.size());
				return null;
			}

		}.execute();

	}

	public void uploadPhoto(final Leaf leaf) throws Exception {

		new AsyncTask<Void, Void, JSONObject>() {
			ProgressDialog dialog = new ProgressDialog(getActivity());
			String description;
			User user = LeafActivity.user;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				dialog.setMessage("Uploading photo");
				dialog.show();
				if (leaf.getDetail() != null)
					description = leaf.getDetail();
				else
					description = "none";

			}

			@Override
			protected void onPostExecute(JSONObject json) {
				// TODO Auto-generated method stub
				super.onPostExecute(json);
				dialog.dismiss();
				if (json == null) {
					Toast.makeText(getActivity(), "Error ocuured", 1000).show();
				}
				System.out.println(" json : " + json);
				try {
					if (json.get("is_succeeded").toString().contains("false")) {
						String message = json.get("message").toString();
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
						alertDialogBuilder.setTitle("Error");
						alertDialogBuilder.setMessage(message).setCancelable(false)
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					} else {

						json = json.getJSONObject("data");
						json = json.getJSONObject("details");
						String id = json.getString("photo_id");
						dataSource.setPhotoID(leaf.getImagePath(), Integer.parseInt(id));
						System.out.println("photoid ");

					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			protected JSONObject doInBackground(Void... arg0) {
				try {

					Bitmap bm = BitmapFactory.decodeFile(leaf.getImagePath());
					// while (bm.getHeight() > 1024)
					// bm = Bitmap.createScaledBitmap(bm, bm.getWidth() / 2,
					// bm.getHeight() / 2, false);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bm.compress(CompressFormat.JPEG, 100, bos);
					byte[] data = bos.toByteArray();

					HttpClient httpClient = new DefaultHttpClient();
					HttpPost postRequest = new HttpPost(LeafActivity.SERVER_URL);
					ByteArrayBody bab = new ByteArrayBody(data, "sample.jpg");
					MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					reqEntity.addPart("method", new StringBody("upload_photo"));
					reqEntity.addPart("user_id", new StringBody(Integer.toString(user.getUserId())));
					reqEntity.addPart("token", new StringBody(user.getToken()));
					reqEntity.addPart("latitude", new StringBody(leaf.getLatitude()));
					reqEntity.addPart("longitude", new StringBody(leaf.getLongitude()));
					reqEntity.addPart("description", new StringBody(description));
					reqEntity.addPart("photo", bab);
					postRequest.setEntity(reqEntity);
					HttpResponse response = httpClient.execute(postRequest);

					String responceString = EntityUtils.toString(response.getEntity());
					responceString = responceString.substring(responceString.indexOf("{"), responceString.length());
					JSONObject json = new JSONObject(responceString);

					return json;
				} catch (Exception e) {
					// handle exception here
					Log.e(e.getClass().getName(), e.getMessage());
				}
				return null;
			}

		}.execute();

	}

	private void deleteLeafFromGallery(final int position) {
		if (position < 0)
			return;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle("Confirmation");
		alertDialogBuilder.setMessage("Do you want to delete this leaf ?").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dataSource.deleteLeaf(leaves.get(position).getImagePath());
						LoadGalleryImages(position - 1);
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}

// private List<File> getListFiles(File parentDir) {
// ArrayList<File> inFiles = new ArrayList<File>();
// File[] files = parentDir.listFiles();
// for (File file : files) {
// if (file.isDirectory()) {
// inFiles.addAll(getListFiles(file));
// } else {
// if (file.getName().endsWith(".jpeg")) {
// inFiles.add(file);
// }
// }
// }
// return inFiles;
// }

// String path = Environment.getExternalStorageDirectory() +
// File.separator + getActivity().getPackageName();
//
// filePaths = getListFiles(new File(path));
// Bitmap pic;
// for (File ImagePath : filePaths) {
// BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
// bmpOptions.inJustDecodeBounds = false;
// bmpOptions.inSampleSize = 8;
// pic = BitmapFactory.decodeFile(ImagePath.getPath(), bmpOptions);
// imageBitmaps.add(pic);
// }
