package zq.whu.zhangshangwuda.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtils {

	public static SharedPreferences sp;

	public static void putString(Context context, String fileName, String key,
			String value) {
		sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(Context context, String fileName,
			String key, String defValue) {
		sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String s = sp.getString(key, defValue);
		return s;
	}

	public static void putBoolean(Context context, String fileName, String key,
			boolean value) {
		sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBoolean(Context context, String fileName,
			String key, boolean defaultValue) {
		sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

		boolean b = sp.getBoolean(key, defaultValue);
		return b;
	}

	public static void putInt(Context context, String fileName, String key,
			int value) {
		sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(Context context, String fileName, String key,
			int defaultValue) {
		sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		int i = sp.getInt(key, defaultValue);
		return i;
	}
}
