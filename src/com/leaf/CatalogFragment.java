package com.leaf;

import java.util.ArrayList;

import uiworks.LeafAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

import database.Leaf;

public class CatalogFragment extends BaseFragment{
	
	private ArrayList<Leaf> leaves = new ArrayList<Leaf>();
	private ArrayList<Leaf> searchedLeaves = new ArrayList<Leaf>();
	private ListView catalogList;
	private LeafAdapter leafAdapter;
	private EditText searchBarText;
	private Editable searchedText;	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);		
	}
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	View view = inflater.inflate(R.layout.catalogfragment, container, false);  
    	Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.oak);
    	
    	for(int i = 0; i<=20;i++){
		Leaf leaf = new Leaf(0, "Oak", "Meþe", "no detail", "Eudicots", "Rosids", "Fagales", "Fagaceae", "Quercus", image, "longitude", "latitude");
    	leaves.add(leaf);
    	}
    	
    	
    	searchBarText = (EditText) view.findViewById(R.id.searchbar);
    	searchBarText.addTextChangedListener(textWatcher);
    	catalogList = (ListView) view.findViewById(R.id.cataloglist);
    	leafAdapter = new LeafAdapter(getActivity(), leaves);
    	catalogList.setAdapter(leafAdapter);    	

    	catalogList.setOnItemClickListener(itemClickListener);
    	
        return view;
    }
    
    private TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			searchedText = searchBarText.getText();
			searchedLeaves.clear();
			for (int i = 0; i < leaves.size(); i++)
	        {
				if(leaves.get(i).getTurkishName().toLowerCase().contains(searchedText.toString().toLowerCase())){
					searchedLeaves.add(leaves.get(i));
				}
				else if(leaves.get(i).getName().toLowerCase().contains(searchedText.toString().toLowerCase())){
					searchedLeaves.add(leaves.get(i));
				}
	        } 
			leafAdapter = new LeafAdapter(getActivity(), searchedLeaves);
			catalogList.setAdapter(leafAdapter);
		}
		
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}
	};
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {


		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			Leaf leaf = (Leaf) parent.getAdapter().getItem(position);
			System.out.println("leaf name"+leaf.getName());
			System.out.println("leaf turkish name"+leaf.getTurkishName());
			
			Bundle data = new Bundle();
			data.putString("id",Integer.toString(leaf.getId()));
			SherlockFragment fragment = new DetailFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();

			 // Replace whatever is in the fragment_container view with this fragment,
			 // and add the transaction to the back stack
			//transaction.detach(CatalogFragment.this);
			getFragmentManager().findFragmentById(getId()).onDetach();
			 transaction.replace(android.R.id.content, fragment);
			 transaction.addToBackStack(null);

			 // Commit the transaction
			 transaction.commit();		 
			
		}
	};
    
    
}
