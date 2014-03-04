package zq.whu.zhangshangwuda.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zq.whu.zhangshangwuda.entity.WifiAccount;
import zq.whu.zhangshangwuda.entity.Lessons;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class WifiDb extends WifiDBUtil {

	private static final String TABLE_NAME = "wifi";

	private static WifiDb service = null;
	private SQLiteDatabase db;
	public static WifiDb getInstance(Context context) {
		if (service == null) {
			service = new WifiDb(context);
			
		}
		return service;
	}

	public WifiDb(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		openDB();
	}

	public WifiDb(Context context) {
		super(context);
		openDB();
	}
	
	public void openDB() {
		// TODO Auto-generated method stub
		db=this.getWritableDatabase();
	}
	public void closeDB(){
		if (db != null)
			db.close();
	}
	/**
	 * 插入一条数据
	 * 
	 * @param WifiAccount
	 * @return
	 */
	public boolean insert(WifiAccount account) {
		try {
			ContentValues cv = new ContentValues();
			cv.put("username", account.getUsername());
			cv.put("password", account.getPassword());
			db.insert(TABLE_NAME, null, cv);
		} catch (Exception e) {
			return false;
		} finally {
		}
		return true;
	}

	/**
	 * 更新一条数据
	 * 
	 * @param lessons
	 */
	public void update(WifiAccount account) {
		try {
			ContentValues cv = new ContentValues();
			cv.put("id", account.getId());
			cv.put("username", account.getUsername());
			cv.put("password", account.getPassword());
			db.update(TABLE_NAME, cv, "id=?",
					new String[] { String.valueOf(account.getId()) });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * 由ID获取课程
	 * 
	 * @param Id
	 * @return
	 */
	public WifiAccount getAccountById(String id) {
		Cursor cursor = db.query(TABLE_NAME, null, "id = ?",
				new String[] { String.valueOf(id) }, null, null, null);
		WifiAccount account = null;
		try {
			if (cursor.moveToFirst()) {
				account = new WifiAccount();
				account.setId(cursor.getInt(cursor.getColumnIndex("id")));
				account.setUsername(cursor.getString(cursor
						.getColumnIndex("username")));
				account.setPassword(cursor.getString(cursor
						.getColumnIndex("password")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return account;
	}

	/**
	 * 由username获取WIFI账户
	 * 
	 * @param username
	 * @return
	 */
	public WifiAccount getAccountByUsername(String username) {
		Cursor cursor = db.query(TABLE_NAME, null, "username = ?",
				new String[] { String.valueOf(username) }, null, null, null);
		WifiAccount account = null;
		try {
			if (cursor.moveToFirst()) {
				account = new WifiAccount();
				account.setId(cursor.getInt(cursor.getColumnIndex("id")));
				account.setUsername(cursor.getString(cursor
						.getColumnIndex("username")));
				account.setPassword(cursor.getString(cursor
						.getColumnIndex("password")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return account;
	}

	/**
	 * 
	 * @return 数据库中的所有WIFI账户信息
	 */
	public List<WifiAccount> getLocalAccountsList() {
		ArrayList<WifiAccount> list = new ArrayList<WifiAccount>();
		Cursor cursor = db.query(TABLE_NAME, //
				null, null, null, null, null, null);
		try {
			while (cursor.moveToNext()) {
				WifiAccount account = new WifiAccount();
				account.setId(cursor.getInt(cursor.getColumnIndex("id")));
				account.setUsername(cursor.getString(cursor
						.getColumnIndex("username")));
				account.setPassword(cursor.getString(cursor
						.getColumnIndex("password")));
				list.add(account);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return list;
	}

	/**
	 * 根据用户名删除账户
	 * 
	 * @param name
	 * @return
	 */
	public boolean deleteByName(String name) {
		if (db.delete(TABLE_NAME, "username=?",
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
		if (db.delete(TABLE_NAME, "id=?",
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
		db.delete(TABLE_NAME, null, null);
	}

}
