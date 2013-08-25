package com.leaf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class DetailFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		
		System.out.println("Deatil fragment callledd");
		return inflater.inflate(R.layout.detailfragment, container, false); 
	}
	
}
