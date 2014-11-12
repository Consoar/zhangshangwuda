package zq.whu.zhangshangwuda.ui.lessons.widget;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.tools.LessonsWidgetSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.SettingSharedPreferencesTool;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.lessons.LessonsDayActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class LessonsWidgetProvider_4_1 extends AppWidgetProvider {
	private Context context;
	private AppWidgetManager appWidgetManager;
	private static List<Map<String, String>> lessonsList_4_1 = null;

	public void updateWidget(Context context, RemoteViews Courses) {
		final Intent nextpageIntent = new Intent(context,
				LessonsWidgetProvider_4_1.class);
		nextpageIntent
				.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_1.nextpage");
		final PendingIntent nextpagePendingIntent = PendingIntent.getBroadcast(
				context, 1, nextpageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Courses.setOnClickPendingIntent(R.id.lessons_widget_4_1_nextpage,
				nextpagePendingIntent);

		final Intent zqIntent = new Intent(context, LessonsDayActivity.class);
		Date anytime = new Date(getLessonsWidgetShowTime(context));
		zqIntent.putExtra("day", getDayOfWeek(anytime));
		final PendingIntent zqPendingIntent = PendingIntent.getActivity(
				context, 0, zqIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Courses.setOnClickPendingIntent(R.id.lessons_widget_4_1_zq,
				zqPendingIntent);

		final Intent todayIntent = new Intent(context,
				LessonsWidgetProvider_4_1.class);
		todayIntent
				.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_1.today");
		final PendingIntent todayPendingIntent = PendingIntent.getBroadcast(
				context, 2, todayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Courses.setOnClickPendingIntent(R.id.lessons_widget_4_1_back,
				todayPendingIntent);

		// final Intent previousIntent = new Intent(context,
		// LessonsWidgetProvider_4_1.class);
		// previousIntent
		// .setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_1.previous");
		// final PendingIntent previousPendingIntent =
		// PendingIntent.getBroadcast(
		// context, 2, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Courses.setOnClickPendingIntent(R.id.lessons_widget_previous,
		// previousPendingIntent);

		// final Intent nextIntent = new Intent(context,
		// LessonsWidgetProvider_4_1.class);
		// nextIntent.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_1.next");
		// final PendingIntent nextPendingIntent = PendingIntent.getBroadcast(
		// context, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Courses.setOnClickPendingIntent(R.id.lessons_widget_next,
		// nextPendingIntent);
		//
		// final Intent todayIntent = new Intent(context,
		// LessonsWidgetProvider_4_1.class);
		// todayIntent.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_1.today");
		// final PendingIntent todayPendingIntent = PendingIntent.getBroadcast(
		// context, 4, todayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Courses.setOnClickPendingIntent(R.id.lessons_widget_today,
		// todayPendingIntent);

		ComponentName componentName = new ComponentName(context,
				LessonsWidgetProvider_4_1.class);
		AppWidgetManager.getInstance(context).updateAppWidget(componentName,
				Courses);
	}

	public long getLessonsWidgetShowTime(Context context) {
		return LessonsWidgetSharedPreferencesTool
				.getLessonsWidgetShowTime_4_1(context);
	}

	public void setLessonsWidgetShowTime(Context context, Date anytime) {
		long temp = anytime.getTime();
		LessonsWidgetSharedPreferencesTool.setLessonsWidgetShowTime_4_1(
				context, temp);
	}

	public void getlessonsList(Context context, int k) {
		lessonsList_4_1 = LessonsDb.getInstance(context).getLessonsByDay(
				Integer.toString(k));
		for (int i = 0; i < lessonsList_4_1.size(); ++i) {
			String tstring = lessonsList_4_1.get(i).get("name");
			if (tstring.indexOf(" ") > 0)
				lessonsList_4_1.get(i).put("name",
						tstring.substring(0, tstring.indexOf(" ")));
			tstring = lessonsList_4_1.get(i).get("time");
			lessonsList_4_1.get(i).put("time",
					tstring.substring(0, tstring.indexOf("节") + 1));
		}
		int nowWeek = LessonsTool.getNowWeek(context);
		boolean isWidgetShowNowLessons = SettingSharedPreferencesTool
				.lessons_isWidgetShowNowLessons(context);
		if (isWidgetShowNowLessons)
			lessonsList_4_1 = LessonsTool.washLessonsByWeek(lessonsList_4_1,
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
		Intent intent = new Intent(context, LessonsWidgetProvider_4_1.class);
		intent.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_1.today");
		PendingIntent work = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		aManager.cancel(work);
		aManager.set(AlarmManager.RTC, tcalendar.getTimeInMillis(), work);
		Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "设置定时更新成功");
	}

	public void cancelNextAlarm(Context context) {
		AlarmManager aManager = (AlarmManager) context
				.getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(context, LessonsWidgetProvider_4_1.class);
		intent.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_1.today");
		PendingIntent work = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		aManager.cancel(work);
		Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "取消定时更新成功");
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

	public void addPage(int k, Context context) {
		// System.out.println("addPage " + k);
		LessonsWidgetSharedPreferencesTool.setLessonsWidgetPage_4_1(context, k);
		RemoteViews Courses = new RemoteViews(context.getPackageName(),
				R.layout.lessons_widget_4_1);
		if (k != 0) {
			Courses.setViewVisibility(R.id.lessons_widget_4_1_back,
					View.VISIBLE);
			Courses.setViewVisibility(R.id.lessons_widget_4_1_zq, View.GONE);
		} else {
			Courses.setViewVisibility(R.id.lessons_widget_4_1_back, View.GONE);
			Courses.setViewVisibility(R.id.lessons_widget_4_1_zq, View.VISIBLE);
		}
		if (lessonsList_4_1.size() > 0) {
			String content = "";
			content = content + lessonsList_4_1.get(k).get("name") + '\n';
			content = content + lessonsList_4_1.get(k).get("time") + '\n';
			content = content + lessonsList_4_1.get(k).get("place");
			Courses.setTextViewText(R.id.lessons_widget_4_1_title, content);
			// ComponentName componentName = new ComponentName(context,
			// LessonsWidgetProvider.class);
			// AppWidgetManager.getInstance(context).updateAppWidget(
			// componentName, Courses);
		} else {
			Courses.setTextViewText(R.id.lessons_widget_4_1_title, "少年~今天没有课呢~");
			// ComponentName componentName = new ComponentName(context,
			// LessonsWidgetProvider.class);
			// AppWidgetManager.getInstance(context).updateAppWidget(
			// componentName, Courses);
		}
		updateWidget(context, Courses);
	}

	public void sendBroadcastToday(Context context) {
		LessonsWidgetSharedPreferencesTool.setLessonsWidgetPage_4_1(context, 0);
		final Intent todayIntent = new Intent(context,
				LessonsWidgetProvider_4_1.class);
		todayIntent
				.setAction("zq.whu.zhangshangwuda.ui.lessons.widget_4_1.today");
		context.sendBroadcast(todayIntent);
		Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "发送广播回到今天");
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		// System.out.println("onEnabled");
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "onEnabled");
		sendBroadcastToday(context);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		// System.out.println("onDeleted");
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "onDeleted");
		cancelNextAlarm(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		// System.out.println("onUpdate");
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "onUpdate");
		sendBroadcastToday(context);
	}

	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		// System.out.println("onReceive");
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "onReceive");
		if (intent.getAction().equals(
				"zq.whu.zhangshangwuda.ui.lessons.widget_4_1.nextpage")) {
			Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "转到下一页");
			int k = LessonsWidgetSharedPreferencesTool
					.getLessonsWidgetPage_4_1(context);
			Date anytime = new Date(getLessonsWidgetShowTime(context));
			getlessonsList(context, getDayOfWeek(anytime));
			if (k + 1 < lessonsList_4_1.size()) {
				addPage(++k, context);
			} else {
				k = 0;
				if (k + 1 == lessonsList_4_1.size())
					return;
				addPage(k, context);
			}
		}
		if (intent.getAction().equals(
				"zq.whu.zhangshangwuda.ui.lessons.widget_4_1.today")) {
			Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "转到今天");
			Date anytime = new Date();
			setLessonsWidgetShowTime(context, anytime);
			getlessonsList(context, getDayOfWeek(anytime));
			setNextAlarm(context);
			addPage(0, context);
		}

		if (intent.getAction().equals(
				"zq.whu.zhangshangwuda.ui.lessons.widget_4_1.next")) {
			Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "转到下一天");
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
				"zq.whu.zhangshangwuda.ui.lessons.widget_4_1.previous")) {
			Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "转到上一天");
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
		// Log.v("zq.whu.zhangshangwuda.ui.lessons.widget_4_1", "onDisabled");
		super.onDisabled(context);
	}

}