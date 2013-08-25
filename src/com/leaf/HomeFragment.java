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

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import database.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class HomeFragment extends BaseFragment {

	private Button startSignupButton;
	private Button loginButton;
	private EditText userName;
	private EditText password;
	private Animation shakeAnim;
	User user;
	CheckBox checkBox;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.homefragment, container, false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		user = LeafActivity.user;
		
		if(user.getToken() != null){
			loginButton.setEnabled(false);
			startSignupButton.setEnabled(false);
			checkBox.setChecked(true);
		}else if(user.getName()!=null){
			System.out.println("username : "+user.getName() );
			System.out.println("password : "+user.getPassword());
			userName.setText(user.getUserName());
			password.setText(user.getPassword());
			loginSystem(userName.getText().toString(), password.getText().toString());	
			loginButton.setEnabled(false);
			
		}else{
			Toast.makeText(getActivity(), "You have to create account firstly", Toast.LENGTH_SHORT).show();
			startSignupButton.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startSignupButton.setAnimation(shakeAnim);
				}
			}, Toast.LENGTH_SHORT);
			checkBox.setChecked(false);
		}

	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		((SherlockFragmentActivity)getActivity()).setSupportProgressBarIndeterminateVisibility(false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		userName = (EditText) getActivity().findViewById(R.id.Username);
		password = (EditText) getActivity().findViewById(R.id.Password);
		startSignupButton = (Button) getView().findViewById(R.id.gotoSignupButton);
		loginButton = (Button) getView().findViewById(R.id.LoginButton);	
		shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
		startSignupButton.setOnClickListener(buttonClickListener);
		loginButton.setOnClickListener(buttonClickListener);
		checkBox = (CheckBox)getView().findViewById(R.id.checkBox1);
	}

	private OnClickListener buttonClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if (view.getId() == R.id.LoginButton) {
				loginSystem(userName.getText().toString(),password.getText().toString());
				
			} else if (view.getId() == R.id.gotoSignupButton) {
				view.clearAnimation();
				SherlockFragment fragment = new SignUpFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_out_up, R.anim.fade_out);
				transaction.replace(android.R.id.content, fragment);
				transaction.addToBackStack(null);
				transaction.commit();

				// Fragment fragment =
				// getActivity().getSupportFragmentManager().findFragmentByTag("signup");
				// FragmentTransaction transaction =
				// getActivity().getSupportFragmentManager().beginTransaction();
				// if(fragment==null){
				// fragment = new SignUpFragment();
				// transaction.add(android.R.id.content,fragment, "signup");
				// System.out.println("############nulllllllllll");
				// }
				// transaction.detach(getActivity().getSupportFragmentManager().findFragmentById(getId()));
				// transaction.attach(fragment);
				// // transaction.replace(android.R.id.content, fragment);
				// transaction.commit();
			}

		}

		
	};
	
	private void loginSystem(final String userName, final String password) {
		new AsyncTask<Void, Void, JSONObject>(){
			ProgressDialog dialog = new ProgressDialog(getActivity());
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
	    		 dialog.setMessage("Attempt to log in..");
	    		 dialog.show();
			}
			
			@Override
			protected void onPostExecute(JSONObject result) {
				super.onPostExecute(result);	
				dialog.dismiss();
				checkBox.setChecked(true);
				boolean isSucceeded = false;
				String message = null; 
				try {
					isSucceeded = result.getBoolean("is_succeeded");
					message = result.getString("message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(isSucceeded){
					String token = null;
					int userId = -1;
					try {
						 token = result.getJSONObject("data").getJSONObject("token").getString("key");
						String id = result.getJSONObject("data").getJSONObject("token").getJSONObject("owner").getString("user_id");
						userId = Integer.parseInt(id);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					preferencesEditor.putString("token", token);
					preferencesEditor.putInt("userId", userId);
					user.setToken(token);
					user.setUserId(userId);
					System.out.println("Token : " + token + " userId : "+ user.getUserId());
				}
				else{
					Toast.makeText(getActivity(),"Failed !", Toast.LENGTH_SHORT).show();
				}
				
			}

			@Override
			protected JSONObject doInBackground(Void... arg0) {
				 HttpClient httpclient = new DefaultHttpClient();
				 HttpPost httppost = new HttpPost(LeafActivity.SERVER_URL);
				 JSONObject json = null;
				    try {
				        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
				        nameValuePairs.add(new BasicNameValuePair("method", "get_token"));
				        nameValuePairs.add(new BasicNameValuePair("user_name", userName));
				        nameValuePairs.add(new BasicNameValuePair("password", password));					        
				      
				        
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				        HttpResponse response = httpclient.execute(httppost);
				        String responceString = EntityUtils.toString(response.getEntity());
				        responceString = responceString.substring(responceString.indexOf("{"), responceString.length());
						try {
							json = new JSONObject(responceString);
					        System.out.println("Is succeeded : "+json.get("is_succeeded").toString());
					        System.out.println("Message : "+json.get("message").toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
				    } catch (ClientProtocolException e) {
				    } catch (IOException e) {
				    }
				    System.out.println("Home JSON :" + json);
				return json;
			}
			
		}.execute();
	   
	}
}
