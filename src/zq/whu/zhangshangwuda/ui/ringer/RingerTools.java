package zq.whu.zhangshangwuda.ui.ringer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.ui.MainActivity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import zq.whu.zhangshangwuda.ui.R;

public class RingerTools 
{
	private AlarmManager mAlarmManager;
	private AudioManager mAudioManager;
	private NotificationManager mNotificationManager;
	private Context context;
	
	public RingerTools(Context ctx)
	{
		this.context = ctx;
	}
	
	public void initAlarmManager()
	{
		if (mAlarmManager == null)
		{
			this.mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		}
	}
	
	public void initAudioManager()
	{
		if (mAudioManager == null)
		{
			this.mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		}
	}
	
	public void initNotificationManager()
	{
		if (mNotificationManager == null)
		{
			this.mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		}
	}
	
	public void setAfterTimeNoSilent(int hour, int min)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.HOUR, hour);
		calendar.add(Calendar.MINUTE, min);
		
		Intent i = new Intent(context, OffSilentReceiver.class);
		i.putExtra("isAfter", "yes");
		PendingIntent sender = PendingIntent.getBroadcast(context, 22, i, PendingIntent.FLAG_CANCEL_CURRENT);
		
		mAlarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
	}
	
//	public void cancelAfterTimeNoSilent()
//	{
//		Intent i = new Intent(context, OffSilentReceiver.class);
//		i.putExtra("isAfter", "yes");
//		PendingIntent sender = PendingIntent.getBroadcast(context, 22, i, PendingIntent.FLAG_CANCEL_CURRENT);
//		cleanNotification(0);
//		setSilent(false);
//		mAlarmManager.cancel(sender);
//	}
	
	public ArrayList<TimeOfLessons> getTimes()
	{
		ArrayList<TimeOfLessons> times = new ArrayList<TimeOfLessons>();
		
		List<Map<String, String>> mp = LessonsDb.getInstance(context).getLocalLessonsList();
        
        for (int i = 0; i < mp.size(); i++)
        {
        	Map<String, String> li = mp.get(i);
        	String tstring = li.get("time");
        	
        	int tstart = Integer.parseInt(tstring.substring(0, tstring.indexOf("-")));
        	int tend = Integer.parseInt(tstring.substring(tstring.indexOf("-") + 1));
        	
        	times.add(new TimeOfLessons(tstart, tend, Integer.parseInt(li.get("day"))));
        }
		return times;
	}
	
	public void setTimeOfSilent(boolean mu)
	{
		Intent intent_off = new Intent(context, OffSilentReceiver.class);
		intent_off.putExtra("isAfter", "no");
		Intent intent_on = new Intent(context, OnSilentReceiver.class);
		
		Calendar NOW_TIME = Calendar.getInstance();
		NOW_TIME.setTimeInMillis(System.currentTimeMillis());
		
		if (mu)
		{	
			showNotification(false, 1);
			ArrayList<TimeOfLessons> times = getTimes();
			
			for (TimeOfLessons tl: times)
			{
				Calendar tl_time = Calendar.getInstance();
				tl_time.setTimeInMillis(System.currentTimeMillis());
				tl_time.set(Calendar.DAY_OF_WEEK, tl.getDay());
				tl_time.set(Calendar.HOUR_OF_DAY, tl.getStartHour());
				tl_time.set(Calendar.MINUTE, tl.getStartMin());
				
				if (tl_time.getTimeInMillis() < NOW_TIME.getTimeInMillis())
				{
					tl_time.add(Calendar.WEEK_OF_MONTH, 1);
				}
				
				mAlarmManager.setRepeating(AlarmManager.RTC, tl_time.getTimeInMillis(), (7*24*60*60*1000),
						PendingIntent.getBroadcast(context, 23, intent_on, PendingIntent.FLAG_CANCEL_CURRENT));
			}
			
			for (TimeOfLessons tl: times)
			{
				Calendar tl_time = Calendar.getInstance();
				tl_time.setTimeInMillis(System.currentTimeMillis());
				tl_time.set(Calendar.DAY_OF_WEEK, tl.getDay());
				tl_time.set(Calendar.HOUR_OF_DAY, tl.getEndHour());
				tl_time.set(Calendar.MINUTE, tl.getEndMin());
				
				if (tl_time.getTimeInMillis() < NOW_TIME.getTimeInMillis())
				{
					tl_time.add(Calendar.WEEK_OF_MONTH, 1);
				}
				
				mAlarmManager.setRepeating(AlarmManager.RTC, tl_time.getTimeInMillis(), (7*24*60*60*1000),
						PendingIntent.getBroadcast(context, 23, intent_off, PendingIntent.FLAG_CANCEL_CURRENT));
			}
		}
		else
		{
			mAlarmManager.cancel(PendingIntent.getBroadcast(context, 23, intent_off, PendingIntent.FLAG_CANCEL_CURRENT));
			mAlarmManager.cancel(PendingIntent.getBroadcast(context, 23, intent_on, PendingIntent.FLAG_CANCEL_CURRENT));
			cleanNotification(1);
		}
	}
	
	/**
	 * 是否静音
	 * @param mu
	 */
	public void setSilent(boolean mu)
	{
		if (mu)
		{
			mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}
		else
		{
			mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
	}
	
	/**
	 * 显示notification 以及 是否静音(mu)
	 * @param mu
	 */
	public void showNotification(boolean mu, int id)
	{
		Notification notification;
		CharSequence contentTitle = "自动静音";
		CharSequence contentText = null;
		if (mu)
		{
			contentText = "自动静音状态：开启";
			notification = new Notification(R.drawable.ringer_notification_silent, "定时静音", System.currentTimeMillis());
		}
		else
		{
			contentText = "自动静音状态：关闭";
			notification = new Notification(R.drawable.ringer_notification_unsilent, "定时静音", System.currentTimeMillis());
		}
		
		if (id == 0)
		{
			contentTitle = "定时静音";
			contentText = " ";
		}
			
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags = Notification.FLAG_NO_CLEAR;
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		mNotificationManager.notify(id, notification);
	}
	
	public void cleanNotification(int id)
	{
		mNotificationManager.cancel(id);
	}
}
