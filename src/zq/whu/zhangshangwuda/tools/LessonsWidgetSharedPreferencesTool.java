package zq.whu.zhangshangwuda.tools;

import android.content.Context;
import android.content.SharedPreferences;

public class LessonsWidgetSharedPreferencesTool {
	public static int getLessonsWidgetPage_4_2(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences("User_Data", 0);
		return Mysettings.getInt("Lessons_Widget_Page_4_2", 0);
	}

	public static void setLessonsWidgetPage_4_2(Context c, int x) {
		SharedPreferences.Editor localEditor = c.getSharedPreferences(
				"User_Data", 0).edit();
		localEditor.putInt("Lessons_Widget_Page_4_2", x).commit();
	}

	public static int getLessonsWidgetPage_4_1(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences("User_Data", 0);
		return Mysettings.getInt("Lessons_Widget_Page_4_1", 0);
	}

	public static void setLessonsWidgetPage_4_1(Context c, int x) {
		SharedPreferences.Editor localEditor = c.getSharedPreferences(
				"User_Data", 0).edit();
		localEditor.putInt("Lessons_Widget_Page_4_1", x).commit();
	}

	public static long getLessonsWidgetShowTime_4_1(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences("User_Data", 0);
		return Mysettings.getLong("Lessons_Widget_ShowTime_4_1", 0);
	}

	public static void setLessonsWidgetShowTime_4_1(Context c, long x) {
		SharedPreferences.Editor localEditor = c.getSharedPreferences(
				"User_Data", 0).edit();
		localEditor.putLong("Lessons_Widget_ShowTime_4_1", x).commit();
	}

	public static long getLessonsWidgetShowTime_4_2(Context c) {
		SharedPreferences Mysettings = c.getSharedPreferences("User_Data", 0);
		return Mysettings.getLong("Lessons_Widget_ShowTime_4_2", 0);
	}

	public static void setLessonsWidgetShowTime_4_2(Context c, long x) {
		SharedPreferences.Editor localEditor = c.getSharedPreferences(
				"User_Data", 0).edit();
		localEditor.putLong("Lessons_Widget_ShowTime_4_2", x).commit();
	}
}
