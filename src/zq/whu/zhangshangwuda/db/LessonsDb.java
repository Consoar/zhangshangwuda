package zq.whu.zhangshangwuda.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zq.whu.zhangshangwuda.entity.Lessons;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class LessonsDb extends LessonsDBUtil {

	private static final String TABLE_NAME = "lessons";

	private static LessonsDb service = null;

	public static LessonsDb getInstance(Context context) {
		if (service == null) {
			service = new LessonsDb(context);
		}
		return service;
	}

	public LessonsDb(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public LessonsDb(Context context) {
		super(context);
	}

	/**
	 * 插入一条数据
	 * 
	 * @param lessons
	 * @return
	 */
	public boolean insert(Lessons lessons) {
		try {
			ContentValues cv = new ContentValues();
			cv.put("id", lessons.getId());
			cv.put("name", lessons.getName());
			cv.put("day", lessons.getDay());
			cv.put("ste", lessons.getSte());
			cv.put("mjz", lessons.getMjz());
			cv.put("time", lessons.getTime());
			cv.put("place", lessons.getPlace());
			cv.put("teacher", lessons.getTeacher());
			cv.put("other", lessons.getOther());
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert(TABLE_NAME, null, cv);
			db.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 更新一条数据
	 * 
	 * @param lessons
	 */
	public void update(Lessons lessons) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("id", lessons.getId());
		cv.put("name", lessons.getName());
		cv.put("day", lessons.getDay());
		cv.put("ste", lessons.getSte());
		cv.put("mjz", lessons.getMjz());
		cv.put("time", lessons.getTime());
		cv.put("place", lessons.getPlace());
		cv.put("teacher", lessons.getTeacher());
		cv.put("other", lessons.getOther());
		db.update(TABLE_NAME, cv, "id=?", new String[] { lessons.getId()
				.toString() });
		db.close();
	}

	/**
	 * 由星期数获取课程
	 * 
	 * @param day
	 * @return
	 */
	public List<Map<String, String>> getLessonsByDay(String day) {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, "day = ?",
				new String[] { String.valueOf(day) }, null, null, null);
		try {
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", cursor.getString(cursor.getColumnIndex("id")));
				map.put("name", cursor.getString(cursor.getColumnIndex("name")));
				map.put("day", cursor.getString(cursor.getColumnIndex("day")));
				map.put("ste", cursor.getString(cursor.getColumnIndex("ste")));
				map.put("mjz", cursor.getString(cursor.getColumnIndex("mjz")));
				map.put("time", cursor.getString(cursor.getColumnIndex("time")));
				map.put("place",
						cursor.getString(cursor.getColumnIndex("place")));
				map.put("teacher",
						cursor.getString(cursor.getColumnIndex("teacher")));
				map.put("other",
						cursor.getString(cursor.getColumnIndex("other")));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
			db.close();
		}
		return list;
	}

	/**
	 * 由ID获取课程
	 * 
	 * @param Id
	 * @return
	 */
	public Map<String, String> getLessonsById(String id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, "id = ?",
				new String[] { String.valueOf(id) }, null, null, null);
		Map<String, String> map = new HashMap<String, String>();
		if (cursor.moveToFirst()) {
			map.put("id", cursor.getString(cursor.getColumnIndex("id")));
			map.put("name", cursor.getString(cursor.getColumnIndex("name")));
			map.put("day", cursor.getString(cursor.getColumnIndex("day")));
			map.put("ste", cursor.getString(cursor.getColumnIndex("ste")));
			map.put("mjz", cursor.getString(cursor.getColumnIndex("mjz")));
			map.put("time", cursor.getString(cursor.getColumnIndex("time")));
			map.put("place", cursor.getString(cursor.getColumnIndex("place")));
			map.put("teacher",
					cursor.getString(cursor.getColumnIndex("teacher")));
			map.put("other", cursor.getString(cursor.getColumnIndex("other")));
			cursor.close();
			db.close();
			return map;
		}
		if (cursor != null)
			cursor.close();
		cursor = null;
		db.close();
		return null;
	}

	/**
	 * 
	 * @return 数据库中的所有课程列表
	 */
	public List<Map<String, String>> getLocalLessonsList() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, //
				null, null, null, null, null, null);
		try {
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", cursor.getString(cursor.getColumnIndex("id")));
				map.put("name", cursor.getString(cursor.getColumnIndex("name")));
				map.put("day", cursor.getString(cursor.getColumnIndex("day")));
				map.put("ste", cursor.getString(cursor.getColumnIndex("ste")));
				map.put("mjz", cursor.getString(cursor.getColumnIndex("mjz")));
				map.put("time", cursor.getString(cursor.getColumnIndex("time")));
				map.put("place",
						cursor.getString(cursor.getColumnIndex("place")));
				map.put("teacher",
						cursor.getString(cursor.getColumnIndex("teacher")));
				map.put("other",
						cursor.getString(cursor.getColumnIndex("other")));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
			db.close();
		}
		return list;
	}

	/**
	 * 根据课程名删除课程
	 * 
	 * @param name
	 * @return
	 */
	public boolean deleteByName(String name) {
		if (this.getWritableDatabase().delete(TABLE_NAME, "name=?",
				new String[] { name }) != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据Id删除课程
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteById(String id) {
		if (this.getWritableDatabase().delete(TABLE_NAME, "id=?",
				new String[] { id }) != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除所有的数据
	 */
	public void deleteAll() {
		this.getWritableDatabase().delete(TABLE_NAME, null, null);
	}

}
