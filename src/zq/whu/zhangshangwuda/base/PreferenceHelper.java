package zq.whu.zhangshangwuda.base;

import zq.whu.zhangshangwuda.tools.Constants;
import zq.whu.zhangshangwuda.ui.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

	public static final String KEY_THEME = "theme";

	private static SharedPreferences.Editor mEditor = null;
	private static SharedPreferences mdPreferences = null;

	public PreferenceHelper(Context context) {
	}

	private static SharedPreferences.Editor getEditor(Context paramContext) {
		if (mEditor == null)
			mEditor = paramContext.getSharedPreferences(
					Constants.PREFS_NAME_USER_DATA, 0).edit();
		return mEditor;
	}

	private static SharedPreferences getSharedPreferences(Context paramContext) {
		if (mdPreferences == null)
			mdPreferences = paramContext.getSharedPreferences(
					Constants.PREFS_NAME_USER_DATA, 0);
		return mdPreferences;
	}

	public static int getTheme(Context context) {
		return PreferenceHelper.getSharedPreferences(context).getInt(KEY_THEME,
				R.style.MyLightTheme);
	}

	public static void setTheme(Context context, int theme) {
		getEditor(context).putInt(KEY_THEME, theme).commit();
	}

}
