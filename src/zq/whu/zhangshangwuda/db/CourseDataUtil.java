package zq.whu.zhangshangwuda.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class CourseScoreDataBaseHelper extends SQLiteOpenHelper
{
	final String CREATE_TABLE_SQL =
		"create table course_score(_id integer primary key autoincrement,id TEXT,name TEXT,"
		+ "type TEXT,credit TEXT,teacher TEXT,academy TEXT,major TEXT,score TEXT)";
	public CourseScoreDataBaseHelper(Context context, String name, int version)
	{
		super(context, name, null, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_TABLE_SQL);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db
		, int oldVersion, int newVersion)
	{
		System.out.println("--------onUpdate Called--------"+ oldVersion + "--->" + newVersion);
	}
}

public class CourseDataUtil{
	CourseScoreDataBaseHelper dbHelper;
	SQLiteDatabase db;
	public CourseDataUtil(Context context){
		dbHelper = new CourseScoreDataBaseHelper(context, "CourseScore.db3", 1);
		db=dbHelper.getReadableDatabase();
		
	}
	public List<Map<String, String>> getAllCourseScoreData(){
		List<Map<String, String>> courseInfo=new ArrayList<Map<String, String>>();
		Cursor cursor = db.rawQuery("select * from course_score ",null);
		Map<String, String> course;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
		{
			course=new HashMap<String, String>();
			course.put("_id", cursor.getString(cursor.getColumnIndex("_id")));
			course.put("id", cursor.getString(cursor.getColumnIndex("id")));
			course.put("name", cursor.getString(cursor.getColumnIndex("name")));
			course.put("type", cursor.getString(cursor.getColumnIndex("type")));
			course.put("credit", cursor.getString(cursor.getColumnIndex("credit")));
			course.put("teacher", cursor.getString(cursor.getColumnIndex("teacher")));
			course.put("academy", cursor.getString(cursor.getColumnIndex("academy")));
			course.put("major", cursor.getString(cursor.getColumnIndex("major")));
			course.put("score", cursor.getString(cursor.getColumnIndex("score")));
			courseInfo.add(course);			
		}
		cursor.close();
		return courseInfo;
	}
	
	public void addCourseScoreData(List<Map<String, String>> courseInfo){
		
		db.beginTransaction();
		try{
			for(Map<String, String> course:courseInfo){
				String[] strings=new String[8];
				strings[0]=course.get("id");
				strings[1]=course.get("name");
				strings[2]=course.get("type");
				strings[3]=course.get("credit");
				strings[4]=course.get("teacher");
				strings[5]=course.get("academy");
				strings[6]=course.get("major");
				strings[7]=course.get("score");
				db.execSQL("insert into course_score values(null,?,?,?,?,?,?,?,?)",strings);
			}
			db.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();
		}
		db.endTransaction();
	}
	
	public Map<String, String> getLessonsBy_Id(String _id){
		Cursor cursor = db.query("course_score", null, "_id = ?",
				new String[] { String.valueOf(_id) }, null, null, null);
		Map<String, String> map = new HashMap<String, String>();
		if (cursor.moveToFirst()) {
			map.put("_id", cursor.getString(cursor.getColumnIndex("_id")));
			map.put("id", cursor.getString(cursor.getColumnIndex("id")));
			map.put("name", cursor.getString(cursor.getColumnIndex("name")));
			map.put("type", cursor.getString(cursor.getColumnIndex("type")));
			map.put("credit", cursor.getString(cursor.getColumnIndex("credit")));
			map.put("teacher", cursor.getString(cursor.getColumnIndex("teacher")));
			map.put("academy", cursor.getString(cursor.getColumnIndex("academy")));
			map.put("major", cursor.getString(cursor.getColumnIndex("major")));
			map.put("score", cursor.getString(cursor.getColumnIndex("score")));
			cursor.close();
			db.close();
			return map;
		}
		if (cursor != null)
			cursor.close();
		cursor = null;
		return map;
	}
	
	public void deleteAllCourseScoreData(){
		db.beginTransaction();
		try{
			db.execSQL("delete from course_score");
			db.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();;
		}		
		db.endTransaction();
	}
	
	public void close(){
		if (dbHelper != null)
		{
			dbHelper.close();
		}
	}
}
