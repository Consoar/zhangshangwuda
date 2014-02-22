package zq.whu.zhangshangwuda.tools;

import android.content.Context;
import android.content.SharedPreferences;

public class LessonsSharedPreferencesTool {
	public static boolean getLessonsHave(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences("User_Data", 0);
		return Mysettings.getBoolean("Lessons_Have", false);
	}

	public static void setLessonsHave(Context c, boolean x) {
		SharedPreferences.Editor localEditor = c.getSharedPreferences(
				"User_Data", 0).edit();
		localEditor.putBoolean("Lessons_Have", x).commit();
	}

	public static String getTermFirstDay(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences("User_Data", 0);
		return Mysettings.getString("Lessons_TermFirstDay", "2012-8-31");
	}

	public static void setTermFirstDay(Context c, String x) {
		SharedPreferences.Editor localEditor = c.getSharedPreferences(
				"User_Data", 0).edit();
		localEditor.putString("Lessons_TermFirstDay", x).commit();
	}

	public static Integer getLessonsId(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences("User_Data", 0);
		return Mysettings.getInt("Lessons_Id", 0);
	}

	public static void setLessonsId(Context c, Integer x) {
		SharedPreferences.Editor localEditor = c.getSharedPreferences(
				"User_Data", 0).edit();
		localEditor.putInt("Lessons_Id", x).commit();
	}
}
