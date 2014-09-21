package zq.whu.zhangshangwuda.ui.ringer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class OffSilentReceiver extends BroadcastReceiver
{
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		preferences = context.getSharedPreferences("Data", Context.MODE_WORLD_READABLE);
		editor = preferences.edit();
		
		editor.putLong("set_time", 0);
		editor.commit();
		
		RingerTools rt = new RingerTools(context);
		rt.initAudioManager();
		rt.initNotificationManager();
		rt.setSilent(false);
		
		if (intent.getStringExtra("isAfter").equals("yes"))
		{
			rt.cleanNotification(0);
		}
		else
		{
			rt.showNotification(false, 1);
		}
		//Toast.makeText(context, "关闭静音", Toast.LENGTH_SHORT).show();
	}
}
