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

import database.User;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpFragment extends BaseFragment {
	private Button signUpButton;
	private EditText userNameView,passwordView,emailView,nameView,surnameView;
	private String userName,password,email,name,surName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		System.out.println("Signup fragment callledd");
		return inflater.inflate(R.layout.signupfragment, container, false); 
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		userNameView = (EditText) getActivity().findViewById(R.id.username_sign);
		passwordView = (EditText)  getActivity().findViewById(R.id.password_sign);
		emailView = (EditText)  getActivity().findViewById(R.id.email_sign);
		nameView = (EditText)  getActivity().findViewById(R.id.name_sign);
		surnameView = (EditText)  getActivity().findViewById(R.id.surname_sign);
		signUpButton = (Button) getView().findViewById(R.id.SignupButton);
		signUpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				userName = userNameView.getText().toString();
				password = passwordView.getText().toString();
				email = emailView.getText().toString();
				name = nameView.getText().toString();
				surName = surnameView.getText().toString();
				postData();
					
			}
		});
	}
	
	public void postData () {
		new AsyncTask<Void, Void, JSONObject>(){
			ProgressDialog dialog = new ProgressDialog(getActivity());
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
	    		 dialog.setMessage("Connecting");
	    		 dialog.show();
			}
			
			@Override
			protected void onPostExecute(JSONObject result) {
				super.onPostExecute(result);
				dialog.dismiss();	
				boolean isSucceeded = false;
				String message = null; 
				try {
					isSucceeded = result.getBoolean("is_succeeded");
					message = result.getString("message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(isSucceeded){
					signUpButton.setEnabled(false);
					Toast.makeText(getActivity(), "You have signed up succesfully", Toast.LENGTH_LONG).show();
					preferencesEditor.putString("name", name);
					preferencesEditor.putString("userName", userName);
					preferencesEditor.putString("password", password);
					preferencesEditor.putString("email", email);
					preferencesEditor.putString("surName", surName);
					preferencesEditor.commit();
					User user = LeafActivity.user;
					user.setEmail(email);
					user.setName(name);
					user.setPassword(password);
					user.setSurname(surName);
					user.setUserName(userName);					
					
					userNameView.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							getActivity().onBackPressed();
						}
					}, Toast.LENGTH_LONG);
				
				}
				else{
					Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
					userNameView.setText("");
					emailView.setText("");
					passwordView.setText("");
				}
				
			}

			@Override
			protected JSONObject doInBackground(Void... arg0) {
				 HttpClient httpclient = new DefaultHttpClient();
				 HttpPost httppost = new HttpPost(LeafActivity.SERVER_URL);
				 JSONObject json = null;
				    try {
				        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
				        nameValuePairs.add(new BasicNameValuePair("method", "sign_up"));
				        nameValuePairs.add(new BasicNameValuePair("user_name", userName));
				        nameValuePairs.add(new BasicNameValuePair("password", password));
				        nameValuePairs.add(new BasicNameValuePair("email", email));
				        nameValuePairs.add(new BasicNameValuePair("name", name));
				        nameValuePairs.add(new BasicNameValuePair("surname", surName));
				        
				        System.out.println("noluyo laannnnnn");
				        
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
				return json;
			}
			
		}.execute();
	   
	} 
//    private static String convertStreamToString(InputStream is) {
//        /*
//         * To convert the InputStream to String we use the BufferedReader.readLine()
//         * method. We iterate until the BufferedReader return null which means
//         * there's no more data to read. Each line will appended to a StringBuilder
//         * and returned as String.
//         */
//        BufferedReader reader = null;
//		try {
//			reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//        StringBuilder sb = new StringBuilder();
// 
//        String line = null;
//        try {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println("String : "+ sb.toString());
//        return sb.toString();
//    }
	
}
