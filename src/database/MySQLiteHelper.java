package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_CONTENTS = "Leaves";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_NAMETR = "nametr";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DETAIL = "detail";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_LATITUDE ="latitude";
	public static final String COLUMN_LONGITUDE ="longitude";
	public static final String COLUMN_ISLOCAL = "isLocal";
	public static final String COLUMN_ISUPLOADED = "isuploaded";
	public static final String COLUMN_PHOTO_ID = "photoid";

	public static final String DATABASE_NAME = "content.db";
	public static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_CONTENTS + "( "
			+ COLUMN_ID	+ " integer primary key autoincrement, "
			+ COLUMN_IMAGE + " text not null, "
			+ COLUMN_NAMETR + " text, "
			+ COLUMN_NAME + " text, "
			+ COLUMN_DETAIL + " text, "
			+ COLUMN_TIME + " text, "
			+ COLUMN_LATITUDE + " text not null, "
			+ COLUMN_LONGITUDE + " text not null, "
			+ COLUMN_ISLOCAL + " integer, "
			+ COLUMN_ISUPLOADED + " integer, "
			+ COLUMN_PHOTO_ID + " integer );"
			;

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);
		onCreate(db);
	}

}