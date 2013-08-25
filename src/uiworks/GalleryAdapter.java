package uiworks;

import java.util.ArrayList;
import java.util.List;

import uiworks.LeafAdapter.LeafHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leaf.R;

import database.Leaf;

public class GalleryAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Bitmap> imageBitmaps;

	public GalleryAdapter(Context context, ArrayList<Bitmap> imageBitmaps) {
		this.context = context;
		this.imageBitmaps = imageBitmaps;
	}

	public int getCount() {
		return imageBitmaps.size();
	}

	public Object getItem(int position) {
		return imageBitmaps.get(position);
	}

	/** Use the array index as a unique id. */
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		LeafHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.galleryitem, parent, false);

			holder = new LeafHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.galleritem);		
			row.setTag(holder);
		} else {
			holder = (LeafHolder) row.getTag();
		}

		
		holder.imgIcon.setImageBitmap(imageBitmaps.get(position));
		
		return row;
	}

	static class LeafHolder {
		ImageView imgIcon;
		
	}
}

