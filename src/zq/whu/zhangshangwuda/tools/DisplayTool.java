package zq.whu.zhangshangwuda.tools;

import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.news.NewsFragmentSupport;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;

public class DisplayTool {
	private static DisplayMetrics displayMetrics;

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** * 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**获取缩略图片链接*/
	public static String getSmallImageUrl(Context cx, String url) {
		int scale;
		StringBuffer sb = new StringBuffer(url);
		switch(NetworkUtils.getNetworkType(cx)) {
		case NetworkUtils.NETTYPE_WIFI:
			scale = 160;
			break;
		case NetworkUtils.NETTYPE_CMWAP:
		case NetworkUtils.NETTYPE_CMNET:
		default:
			scale = 80;
		}
		if(url.endsWith(".jpg")){
			sb.insert(url.indexOf(".jpg"), "-" + scale);
		}else if(url.endsWith(".gif")){
			sb.insert(url.indexOf(".gif"), "-" + scale);
		}else if(url.endsWith(".png")){
			sb.insert(url.indexOf(".png"), "-" + scale);
		}
		return sb.toString();
	}
	
	/**适配与屏幕匹配的图片链接*/
	public static String getMyImageUrl(String url) {
		int scale;
		StringBuffer sb = new StringBuffer(url);
		int densitydpi = MyApplication.getDensityDpi();
		if(densitydpi > 240)
			scale = 640;
		else if(densitydpi > 160)
			scale = 320;
		else if(densitydpi > 120)
			scale = 160;
		else
			scale = 80;
		if(url.endsWith(".jpg")){
			sb.insert(url.indexOf(".jpg"), "-" + scale);
		}else if(url.endsWith(".gif")){
			sb.insert(url.indexOf(".gif"), "-" + scale);
		}else if(url.endsWith(".png")){
			sb.insert(url.indexOf(".png"), "-" + scale);
		}
		return sb.toString();
	}
	
	/**将适配图片链接变回原始连接*/
	public static String getInitImageUrl(String url) {
		StringBuffer sb = new StringBuffer(url);
		if(url.contains("-80"))
			sb.delete(sb.indexOf("-80"), sb.indexOf("-80") + 3);
		else if(url.contains("-160"))
			sb.delete(sb.indexOf("-160"), sb.indexOf("-160") + 4);
		else if(url.contains("-320"))
			sb.delete(sb.indexOf("-320"), sb.indexOf("-320") + 4);
		else if(url.contains("-640"))
			sb.delete(sb.indexOf("-640"), sb.indexOf("-640") + 4);
		return sb.toString();
	}
	
	public DisplayMetrics getDisplayMetrics(Context cx) {
		if (displayMetrics != null) {
			return displayMetrics;
		} else {
			if (cx != null) {
				Display display = ((Activity) cx).getWindowManager()
						.getDefaultDisplay();
				DisplayMetrics metrics = new DisplayMetrics();
				display.getMetrics(metrics);
				displayMetrics = metrics;
				return metrics;
			} else {
				// default screen is 800x480
				DisplayMetrics metrics = new DisplayMetrics();
				metrics.widthPixels = 480;
				metrics.heightPixels = 800;
				return metrics;
			}
		}
	}
}
