package uiworks;

import java.util.ArrayList;
import java.util.List;

import com.leaf.R;

import database.Leaf;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeafAdapter extends BaseAdapter {

	private Context context;
	private List<Leaf> Items = new ArrayList<Leaf>();

	public LeafAdapter(Context context, ArrayList<Leaf> items) {
		this.context = context;
		Items = items;
	}

	public int getCount() {
		return Items.size();
	}

	public Object getItem(int position) {
		return Items.get(position);
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
			row = inflater.inflate(R.layout.adapterrow, parent, false);

			holder = new LeafHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.list_image);
			holder.name = (TextView) row.findViewById(R.id.list_name);
			holder.nametr = (TextView) row.findViewById(R.id.list_nametr);
			row.setTag(holder);
		} else {
			holder = (LeafHolder) row.getTag();
		}

		Leaf leaf = Items.get(position);
		holder.imgIcon.setImageBitmap(leaf.getImage());
		holder.name.setText(leaf.getName());
		holder.nametr.setText(leaf.getTurkishName());

		return row;
	}

	static class LeafHolder {
		ImageView imgIcon;
		TextView nametr;
		TextView name;
	}
}
