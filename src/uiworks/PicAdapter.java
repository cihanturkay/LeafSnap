package uiworks;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Base Adapter subclass creates Gallery view
 * - provides method for adding new images from user selection
 * - provides method to return bitmaps from array
 *
 */
public class PicAdapter extends BaseAdapter {
	
    int defaultItemBackground;    
    private Context galleryContext;
    private ArrayList<Bitmap> imageBitmaps;

    public PicAdapter(Context c, int styleAttribute[], int defaultBacgroungd,ArrayList<Bitmap> imageBitmaps) {
    	galleryContext = c;       
        this.imageBitmaps = imageBitmaps;
        TypedArray styleAttrs = galleryContext.obtainStyledAttributes(styleAttribute);
        
        defaultItemBackground = styleAttrs.getResourceId(defaultBacgroungd, 0);
        styleAttrs.recycle();
    }

    public int getCount() {
        return imageBitmaps.size();
    }

    public Object getItem(int position) {
        return imageBitmaps.get(position);
    }
    
    public long getItemId(int position) {
        return position;
    }
    
    public void addPic(Bitmap newPic)
    {
    	imageBitmaps.add(newPic);
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			System.out.println("getViewwww");
        ImageView imageView = new ImageView(galleryContext);
        imageView.setImageBitmap(imageBitmaps.get(position));
        imageView.setLayoutParams(new Gallery.LayoutParams(300, 200));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setBackgroundResource(defaultItemBackground);
        return imageView;
		}
		return convertView;
	}
}