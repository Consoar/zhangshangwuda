package zq.whu.zhangshangwuda.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LessonsDBUtil extends SQLiteOpenHelper {

	private final static String DATABSE_NAME = "db_lessons.db";
	private final static int DATABASE_VERSION = 2;

	public LessonsDBUtil(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABSE_NAME, null, DATABASE_VERSION);
	}

	public LessonsDBUtil(Context context) {
		super(context, DATABSE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable_lessons(db);
	}

	private void createTable_lessons(SQLiteDatabase db) {
		String sql = "CREATE TABLE lessons(id TEXT," + "name TEXT,"
				+ "day TEXT," + "ste TEXT," + "mjz TEXT," + "time TEXT,"
				+ "place TEXT," + "teacher TEXT," + "other TEXT)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS  lessons");
		onCreate(db);
	}

}
