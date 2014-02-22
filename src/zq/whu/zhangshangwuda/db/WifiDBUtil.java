package zq.whu.zhangshangwuda.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WifiDBUtil extends SQLiteOpenHelper {

	private final static String DATABSE_NAME = "db_wifi.db";
	private final static int DATABASE_VERSION = 1;

	public WifiDBUtil(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABSE_NAME, null, DATABASE_VERSION);
	}

	public WifiDBUtil(Context context) {
		super(context, DATABSE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable_wifi(db);
	}

	private void createTable_wifi(SQLiteDatabase db) {
		String sql = "create table if not exists " + "wifi"
				+ "(id integer primary key autoincrement,"
				+ "username text not null, password text not null);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS  wifi");
		onCreate(db);
	}

}
