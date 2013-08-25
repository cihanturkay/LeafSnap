package database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContentsDataSource {
	
	private SQLiteDatabase database = null;
	private MySQLiteHelper dbHelper;

	public ContentsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);

	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();		
	}
	
	public boolean isOpen(){
		return database.isOpen();
	}
	

	public void close() {
		dbHelper.close();
	}
	
	public boolean addLeaf(String imagePath, String time, String latitude, String longitude, boolean isLocal, boolean isUploaded){
		int boolLocal = (isLocal) ? 1:0;
		int boolUploaded = (isUploaded) ? 1:0;
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_IMAGE, imagePath);
		values.put(MySQLiteHelper.COLUMN_TIME, time);
		values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
		values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
		values.put(MySQLiteHelper.COLUMN_ISLOCAL, boolLocal);
		values.put(MySQLiteHelper.COLUMN_ISUPLOADED, boolUploaded);
		values.put(MySQLiteHelper.COLUMN_PHOTO_ID, -1);
		database.insert(MySQLiteHelper.TABLE_CONTENTS, null, values);
		return true;
	}
	
	public void deleteLeaf(String imagePath){
		File previousImage = new File(imagePath);
		previousImage.delete();
		database.delete(MySQLiteHelper.TABLE_CONTENTS, MySQLiteHelper.COLUMN_IMAGE+ "=?",
		          new String[] {imagePath});
	}
	
	public void updateLeaf(long id,String detail){
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_DETAIL, detail);
		//values.put(MySQLiteHelper.COLUMN_IMAGE, path);
		database.update(MySQLiteHelper.TABLE_CONTENTS, values,
				MySQLiteHelper.COLUMN_ID + " ='" + id + "'", null);
	}
	
	public void setPhotoID(String path, int photoId){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PHOTO_ID, photoId);
		database.update(MySQLiteHelper.TABLE_CONTENTS, values,
				MySQLiteHelper.COLUMN_IMAGE + " ='" + path + "'", null);
	}
	
	public void updateUploadedType(long id,boolean isUploaded) {
		int bool;
		if(isUploaded)
			bool=1;
		else
			bool=0;

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ISUPLOADED, bool);
		database.update(MySQLiteHelper.TABLE_CONTENTS, values,
				MySQLiteHelper.COLUMN_ID + " ='" + id + "'", null);
	}
	
	public int getPhotoID(int id){
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTENTS, new String[] {

				MySQLiteHelper.COLUMN_PHOTO_ID},

				MySQLiteHelper.COLUMN_ID + "=?", new String[] {Integer.toString(id) }, null, null, null);
		
		if (cursor != null) {
			cursor.moveToFirst();
		}	
		
		return cursor.getInt(0);
			
	}
	
	public List<Leaf> getLocalLeaf() {

		List<Leaf> leaves = new ArrayList<Leaf>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTENTS, new String[] {

		MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_IMAGE, MySQLiteHelper.COLUMN_LATITUDE,
				MySQLiteHelper.COLUMN_LONGITUDE, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_NAMETR,
				MySQLiteHelper.COLUMN_ISUPLOADED,MySQLiteHelper.COLUMN_TIME,MySQLiteHelper.COLUMN_DETAIL},

		MySQLiteHelper.COLUMN_ISLOCAL + "=?", new String[] { "1" }, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		while (!cursor.isAfterLast()) {
			Leaf leaf = new Leaf();
			leaf.setId(cursor.getInt(0));
			leaf.setImagePath(cursor.getString(1));
			leaf.setLatitude(cursor.getString(2));
			leaf.setLongitude(cursor.getString(3));
			leaf.setName(cursor.getString(4));
			leaf.setTurkishName(cursor.getString(5));			
			boolean value = cursor.getInt(6) > 0;
			leaf.setUploaded(value);
			leaf.setTime(cursor.getString(7));
			leaf.setDetail(cursor.getString(8));
			leaves.add(leaf);
			cursor.moveToNext();
			System.out.println("getting the leaf path:" + leaf.getImagePath());
		}
		// Make sure to close the cursor
		cursor.close();
		return leaves;

	}

//	private Leaf cursorToContent(Cursor cursor) {
//		Leaf leaf = new Leaf();
//		content.setId(cursor.getLong(0));
//		content.setTitle(cursor.getString(1));
//		content.setContent(cursor.getString(2));
//		boolean value = cursor.getInt(3) > 0;
//		content.setFavorite(value);
//		content.setPath(cursor.getString(4));
//		return leaf;
//	}
}