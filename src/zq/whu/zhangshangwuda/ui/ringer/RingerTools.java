package zq.whu.zhangshangwuda.ui.ringer;
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
	public void showNotification(boolean mu)
	{
		Notification notification = new Notification(R.drawable.icon, "定时静音", System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags = Notification.FLAG_NO_CLEAR;
		CharSequence contentTitle = "定时静音";
		CharSequence contentText = null;
		if (mu)
			contentText = "静音";
		else
			contentText = "铃声";
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentTitle, contentIntent);
		
		mNotificationManager.notify(0, notification);
	}
	
	public void cleanNotification()
	{
		mNotificationManager.cancel(0);
	}
}
