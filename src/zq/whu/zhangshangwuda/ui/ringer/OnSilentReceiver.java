package zq.whu.zhangshangwuda.ui.ringer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OnSilentReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		RingerTools rt = new RingerTools(context);
		rt.initAudioManager();
		rt.initNotificationManager();
		rt.setSilent(true);
		rt.showNotification(true, 1);
		//Toast.makeText(context, "开启静音", Toast.LENGTH_SHORT).show();
	}
}
