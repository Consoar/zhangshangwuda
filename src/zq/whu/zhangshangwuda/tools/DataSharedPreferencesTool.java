package zq.whu.zhangshangwuda.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataSharedPreferencesTool {

	public static Boolean get_notifi_isShow(Context c){
		SharedPreferences Mysettings = c.getSharedPreferences("Data", 0);
		return Mysettings.getBoolean("notifi_isShow", false);
	}
	
	public static void set_notifi_isShow(Context c,boolean isShow){
		SharedPreferences Mysettings = c.getSharedPreferences("Data", 0);
		Editor editor = Mysettings.edit();
		editor.putBoolean("notifi_isShow", isShow);
		editor.commit();
	}

}
