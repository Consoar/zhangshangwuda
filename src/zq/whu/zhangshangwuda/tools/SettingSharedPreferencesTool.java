package zq.whu.zhangshangwuda.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingSharedPreferencesTool {
	public static boolean wifi_isRememberMe(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("wifi_isRememberMe", false);
	}

	public static boolean wifi_isAutoLogin(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("wifi_isAutoLogin", false);
	}

	public static boolean wifi_isCheckNetwork(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("wifi_isCheckNetwork", false);
	}

	public static boolean lessons_isRememberMe(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("lessons_isRememberMe", false);
	}

	public static boolean lessons_isShowNowLessons(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("lessons_isShowNowLessons", true);
	}

	public static boolean lessons_isWidgetShowNowLessons(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("lessons_isWidgetShowNowLessons", true);
	}

	public static String getStartTab(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getString("start_tab", "news");
	}
	
	public static String getRingerMode(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getString("ringer_mode", "silent");
	}
	
	public static String getRingerAfterMode(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getString("ringer_after_mode", "recover");
	}

	public static Boolean common_isViewPagerTX(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("common_isViewPagerTX", true);
	}
	
}
