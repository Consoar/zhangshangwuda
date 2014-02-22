package zq.whu.zhangshangwuda.views.toast;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtil {

	private static AppMsg mToast;

	public static void showToast(Activity mContext, String text, int duration) {

		mToast = AppMsg.makeText(mContext, text, AppMsg.STYLE_INFO);
		mToast.setDuration(duration);
		mToast.show();
	}

	public static void showToast(Activity mContext, String text) {

		mToast = AppMsg.makeText(mContext, text, AppMsg.STYLE_INFO);
		mToast.setDuration(1000);
		mToast.show();
	}

	public static void showToast(Activity mContext, int resId, int duration) {
		showToast(mContext, mContext.getResources().getString(resId), duration);
	}

	public static void showToast(Activity mContext, int resId) {
		showToast(mContext, mContext.getResources().getString(resId), 1000);
	}
}