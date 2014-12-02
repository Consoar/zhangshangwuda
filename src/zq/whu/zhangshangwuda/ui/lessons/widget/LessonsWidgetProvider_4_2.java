package zq.whu.zhangshangwuda.ui.lessons.widget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.tools.LessonsWidgetSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.SettingSharedPreferencesTool;
import zq.whu.zhangshangwuda.ui.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class LessonsWidgetProvider_4_2 extends AppWidgetProvider {
	private Timer timer = new Timer();
	private Context context;
	private AppWidgetManager appWidgetManager;
	private static List<Map<String, String>> lessonsList_4_2 = null;

	public void updateWidget(Context context, RemoteViews Courses) {
		final Intent nextpageIntent = new Intent(context,
				LessonsWidgetProvider_4_2.class);
		nextpageIntent
				.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_2.nextpage");
		final PendingIntent nextpagePendingIntent = PendingIntent.getBroadcast(
				context, 1, nextpageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Courses.setOnClickPendingIntent(R.id.lessons_widget_4_2_nextpage,
				nextpagePendingIntent);

		final Intent previousIntent = new Intent(context,
				LessonsWidgetProvider_4_2.class);
		previousIntent
				.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_2.previous");
		final PendingIntent previousPendingIntent = PendingIntent.getBroadcast(
				context, 2, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Courses.setOnClickPendingIntent(R.id.lessons_widget_4_2_previous,
				previousPendingIntent);

		final Intent nextIntent = new Intent(context,
				LessonsWidgetProvider_4_2.class);
		nextIntent
				.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_2.next");
		final PendingIntent nextPendingIntent = PendingIntent.getBroadcast(
				context, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Courses.setOnClickPendingIntent(R.id.lessons_widget_4_2_next,
				nextPendingIntent);

		final Intent todayIntent = new Intent(context,
				LessonsWidgetProvider_4_2.class);
		todayIntent
				.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_2.today");
		final PendingIntent todayPendingIntent = PendingIntent.getBroadcast(
				context, 4, todayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Courses.setOnClickPendingIntent(R.id.lessons_widget_4_2_today,
				todayPendingIntent);
		ComponentName componentName = new ComponentName(context,
				LessonsWidgetProvider_4_2.class);
		AppWidgetManager.getInstance(context).updateAppWidget(componentName,
				Courses);
	}

	public long getLessonsWidgetShowTime(Context context) {
		return LessonsWidgetSharedPreferencesTool
				.getLessonsWidgetShowTime_4_2(context);
	}

	public void setLessonsWidgetShowTime(Context context, Date anytime) {
		long temp = anytime.getTime();
		LessonsWidgetSharedPreferencesTool.setLessonsWidgetShowTime_4_2(
				context, temp);
	}

	public void getlessonsList(Context context, int k) {
		lessonsList_4_2 = LessonsDb.getInstance(context).getLessonsByDay(
				Integer.toString(k));
		lessonsList_4_2 = LessonsTool.sortLessonsByTime(lessonsList_4_2);
		for (int i = 0; i < lessonsList_4_2.size(); ++i) {
			String tstring = lessonsList_4_2.get(i).get("name");
			if (tstring.indexOf(" ") > 0)
				lessonsList_4_2.get(i).put("name",
						tstring.substring(0, tstring.indexOf(" ")));
			tstring = lessonsList_4_2.get(i).get("time");
			lessonsList_4_2.get(i).put("time",
					tstring.substring(0, tstring.indexOf("节") + 1));
		}
		int nowWeek = LessonsTool.getNowWeek(context);
		boolean isWidgetShowNowLessons = SettingSharedPreferencesTool
				.lessons_isWidgetShowNowLessons(context);
		if (isWidgetShowNowLessons)
			lessonsList_4_2 = LessonsTool.washLessonsByWeek(lessonsList_4_2,
					nowWeek);
	}

	public void setNextAlarm(Context context) {
		AlarmManager aManager = (AlarmManager) context
				.getSystemService(Service.ALARM_SERVICE);
		Calendar tcalendar = Calendar.getInstance();
		tcalendar.setTimeInMillis(System.currentTimeMillis());
		tcalendar.add(Calendar.DAY_OF_YEAR, +1);
		tcalendar.set(Calendar.HOUR_OF_DAY, 0);
		tcalendar.set(Calendar.MINUTE, 19);
		tcalendar.set(Calendar.SECOND, 19);
		Intent intent = new Intent(context, LessonsWidgetProvider_4_2.class);
		intent.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_2.today");
		PendingIntent work = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		aManager.cancel(work);
		aManager.set(AlarmManager.RTC, tcalendar.getTimeInMillis(), work);
		Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "设置定时更新成功");
	}

	public void cancelNextAlarm(Context context) {
		AlarmManager aManager = (AlarmManager) context
				.getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(context, LessonsWidgetProvider_4_2.class);
		intent.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_2.today");
		PendingIntent work = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		aManager.cancel(work);
		Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "取消定时更新成功");
	}

	public String getTitle(Context context, Date time) {
		int DayOfWeek;
		DayOfWeek = getDayOfWeek(time);
		String str2 = "";
		switch (DayOfWeek) {
		case 1:
			str2 = context.getString(R.string.Lessons_day_1);
			break;
		case 2:
			str2 = context.getString(R.string.Lessons_day_2);
			break;
		case 3:
			str2 = context.getString(R.string.Lessons_day_3);
			break;
		case 4:
			str2 = context.getString(R.string.Lessons_day_4);
			break;
		case 5:
			str2 = context.getString(R.string.Lessons_day_5);
			break;
		case 6:
			str2 = context.getString(R.string.Lessons_day_6);
			break;
		case 7:
			str2 = context.getString(R.string.Lessons_day_7);
			break;
		}
		String str1 = getMMDDTime(time);
		String title = str1 + " " + str2;
		return title;
	}

	public int getDayOfWeek(Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		int getDayOfWeeek = calendar.get(calendar.DAY_OF_WEEK);
		if (--getDayOfWeeek == 0) {
			getDayOfWeeek = 7;
		}
		return getDayOfWeeek;

	}

	public String getMMDDTime(Date time) {
		String str1 = new SimpleDateFormat(" MM月 dd 日").format(time);
		return str1;

	}

	public void addPage(int k, Context context) {
		LessonsWidgetSharedPreferencesTool.setLessonsWidgetPage_4_2(context, k);
		RemoteViews Courses = new RemoteViews(context.getPackageName(),
				R.layout.lessons_widget_4_2);
		Date anytime = new Date(getLessonsWidgetShowTime(context));
		Courses.setTextViewText(R.id.lessons_widget_4_2_title,
				getTitle(context, anytime));
		if (lessonsList_4_2.size() > 0) {
			int m = R.id.lessons_widget_4_2_linearLayout_body1;
			Courses.removeAllViews(R.id.lessons_widget_4_2_linearLayout_body1);
			Courses.removeAllViews(R.id.lessons_widget_4_2_linearLayout_body2);
			Courses.removeAllViews(R.id.lessons_widget_4_2_linearLayout_body3);
			for (int i = k * 3; (i < 3 * (k + 1) && i < lessonsList_4_2.size()); ++i) {
				RemoteViews Course = new RemoteViews(context.getPackageName(),
						R.layout.lessons_widget_4_2_item);
				Course.setTextViewText(R.id.lessons_widget_4_2_courseName, "");
				Course.setTextViewText(R.id.lessons_widget_4_2_time, "");
				Course.setTextViewText(R.id.lessons_widget_4_2_place, "");
				switch (i % 3) {
				case 0:
					m = R.id.lessons_widget_4_2_linearLayout_body1;
					break;
				case 1:
					m = R.id.lessons_widget_4_2_linearLayout_body2;
					break;
				case 2:
					m = R.id.lessons_widget_4_2_linearLayout_body3;
					break;
				}
				Course.setTextViewText(R.id.lessons_widget_4_2_courseName,
						lessonsList_4_2.get(i).get("name"));
				Course.setTextViewText(R.id.lessons_widget_4_2_time,
						lessonsList_4_2.get(i).get("time"));
				Course.setTextViewText(R.id.lessons_widget_4_2_place,
						lessonsList_4_2.get(i).get("place"));
				Courses.addView(m, Course);
			}
			// ComponentName componentName = new ComponentName(context,
			// LessonsWidgetProvider.class);
			// AppWidgetManager.getInstance(context).updateAppWidget(
			// componentName, Courses);
		} else {
			int m = R.id.lessons_widget_4_2_linearLayout_body1;
			Courses.removeAllViews(R.id.lessons_widget_4_2_linearLayout_body1);
			Courses.removeAllViews(R.id.lessons_widget_4_2_linearLayout_body2);
			Courses.removeAllViews(R.id.lessons_widget_4_2_linearLayout_body3);
			RemoteViews Course = new RemoteViews(context.getPackageName(),
					R.layout.lessons_widget_4_2_item);
			Course.setTextViewText(R.id.lessons_widget_4_2_courseName,
					"少年~今天没有课呢~");
			Course.setTextViewText(R.id.lessons_widget_4_2_time, "");
			Course.setTextViewText(R.id.lessons_widget_4_2_place, "");
			Courses.addView(m, Course);
			// ComponentName componentName = new ComponentName(context,
			// LessonsWidgetProvider.class);
			// AppWidgetManager.getInstance(context).updateAppWidget(
			// componentName, Courses);
		}
		updateWidget(context, Courses);
	}

	public void sendBroadcastToday(Context context) {
		LessonsWidgetSharedPreferencesTool.setLessonsWidgetPage_4_2(context, 0);
		final Intent todayIntent = new Intent(context,
				LessonsWidgetProvider_4_2.class);
		todayIntent
				.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_2.today");
		context.sendBroadcast(todayIntent);
		Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "发送广播回到今天");
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		// System.out.println("onEnabled");
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "onEnabled");
		sendBroadcastToday(context);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		// System.out.println("onDeleted");
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "onDeleted");
		cancelNextAlarm(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		// System.out.println("onUpdate");
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "onUpdate");
		sendBroadcastToday(context);
	}

	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		// System.out.println("onReceive");
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "onReceive");
		if (intent.getAction().equals(
				"zq.whu.zhangshangwuda.ui.lessons.widget_4_2.nextpage")) {
			Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "转到下一页");
			int k = LessonsWidgetSharedPreferencesTool
					.getLessonsWidgetPage_4_2(context);
			Date anytime = new Date(getLessonsWidgetShowTime(context));
			getlessonsList(context, getDayOfWeek(anytime));
			if ((k + 1) * 3 <= lessonsList_4_2.size() - 1) {
				addPage(++k, context);
			} else {
				k = 0;
				addPage(k, context);
			}
		}
		if (intent.getAction().equals(
				"zq.whu.zhangshangwuda.ui.lessons.widget_4_2.today")) {
			Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "转到今天");
			Date anytime = new Date();
			setLessonsWidgetShowTime(context, anytime);
			getlessonsList(context, getDayOfWeek(anytime));
			setNextAlarm(context);
			addPage(0, context);
		}

		if (intent.getAction().equals(
				"zq.whu.zhangshangwuda.ui.lessons.widget_4_2.next")) {
			Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "转到下一天");
			Date anytime = new Date(getLessonsWidgetShowTime(context));
			Calendar anycalendar = Calendar.getInstance();
			anycalendar.setTime(anytime);
			anycalendar.add(Calendar.DAY_OF_YEAR, 1);
			anytime = anycalendar.getTime();
			setLessonsWidgetShowTime(context, anytime);
			getlessonsList(context, getDayOfWeek(anytime));
			addPage(0, context);
		}
		if (intent.getAction().equals(
				"zq.whu.zhangshangwuda.ui.lessons.widget_4_2.previous")) {
			Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "转到上一天");
			Date anytime = new Date(getLessonsWidgetShowTime(context));
			Calendar anycalendar = Calendar.getInstance();
			anycalendar.setTime(anytime);
			anycalendar.add(Calendar.DAY_OF_YEAR, -1);
			anytime = anycalendar.getTime();
			setLessonsWidgetShowTime(context, anytime);
			getlessonsList(context, getDayOfWeek(anytime));
			addPage(0, context);
		}
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		// System.out.println("onDisabled");
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_2", "onDisabled");
		super.onDisabled(context);
	}

}