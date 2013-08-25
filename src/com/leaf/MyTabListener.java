package com.leaf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;

public class MyTabListener<T extends Fragment>  implements ActionBar.TabListener {
	private final FragmentActivity mActivity;
	private final String mTag;
	private final Class<T> mClass;
	private final Bundle mArgs;
	private Fragment mFragment;

	public MyTabListener(FragmentActivity activity, String tag, Class<T> clz,
			Bundle args) {
		mActivity = activity;
		mTag = tag;
		mClass = clz;
		mArgs = args;
		FragmentTransaction ft = mActivity.getSupportFragmentManager()
				.beginTransaction();

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state. If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(
				mTag);
		if (mFragment != null && !mFragment.isDetached()) {
			ft.detach(mFragment);
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {		

		ft = mActivity.getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		if (mFragment == null) {
			mFragment = Fragment
					.instantiate(mActivity, mClass.getName(), mArgs);
			ft.add(android.R.id.content, mFragment, mTag);
			ft.commit();
		} else {
			ft.attach(mFragment);
			ft.commit();
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	       if (mFragment != null) {
	            //this segment removes the back history of everything in the tab you are leaving so when you click on the tab again you go back to a fresh start
	            FragmentManager man = mActivity.getSupportFragmentManager();
	            if(man.getBackStackEntryCount()>0) //this check is required to prevent null point exceptions when clicking off of a tab with no history
	                man.popBackStack(man.getBackStackEntryAt(0).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE); //this pops the stack back to index 0 so you can then detach and then later attach your initial fragment
	            //also it should be noted that if you do popbackstackimmediate here instead of just popbackstack you will see a flash as the gui changes back to the first fragment when the code executes
	            //end
	            ft.detach(mFragment);
	        }


	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
}