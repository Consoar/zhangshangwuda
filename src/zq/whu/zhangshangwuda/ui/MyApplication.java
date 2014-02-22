package zq.whu.zhangshangwuda.ui;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import zq.whu.zhangshangwuda.tools.Constants;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.WindowManager;

public final class MyApplication extends Application {

	private static final String TAG = MyApplication.class.getSimpleName();
	private static MyApplication myapplication = null;
	private static Activity activity;
	private static LayoutInflater layoutInflater;
	private static String innerCachePath; // 手机内部存储缓存(由于手机内部存储有限，考虑废弃)
	private static String sdcardCachePath; // sdcard外部存储缓存;

	private static int smallAvatarSize;
	private static int normalAvatarSize;
	private static int displayWidth;
	private static int displayHeight;
	private static int densityDpi;
	private static float density;
	public ImageLoader mImageLoader = null;
	private int lessonsWeek;

	public int getLessonsWeek() {
		return lessonsWeek;
	}

	public void setLessonsWeek(int lessonsWeek) {
		this.lessonsWeek = lessonsWeek;
	}

	public static LayoutInflater getLayoutInflater() {
		if (layoutInflater == null) {
			layoutInflater = getActivity().getLayoutInflater();
		}
		return layoutInflater;
	}

	public static void setLayoutInflater(LayoutInflater layoutInflater) {
		MyApplication.layoutInflater = layoutInflater;
	}

	public static MyApplication getInstance() {
		return myapplication;
	}

	public static int getDisplayWidth() {
		return displayWidth;
	}

	public static int getDisplayHeight() {
		return displayHeight;
	}

	public static int getDensityDpi() {
		return densityDpi;
	}

	public static float getDensity() {
		return density;
	}

	public static String getSdcardCachePath() {
		return sdcardCachePath;
	}

	public static String getInnerCachePath() {
		return innerCachePath;
	}

	public static int getSmallAvatarSize() {
		return smallAvatarSize;
	}

	public static int getNormalAvatarSize() {
		return normalAvatarSize;
	}

	public static Activity getActivity() {
		return activity;
	}

	public static void setActivity(Activity activity) {
		MyApplication.activity = activity;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		myapplication = this;
		//prefs = getSharedPreferences(Constants.PREFS_NAME_APP_SETTING,
		//		MODE_PRIVATE);
		//lessonsWeek = LessonsTool.getNowWeek(this);
		initImageLoader();
		initAvatarSize();
	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();
		File cacheDir = StorageUtils.getCacheDirectory(this);
		File cacheImg = new File(cacheDir, "IMG");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.discCache(new UnlimitedDiscCache(cacheImg))
				.discCacheSize(30 * 1024 * 1024)
				// .writeDebugLogs()
				.build();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(config);
	}

	private void initAvatarSize() {
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		// 要获取屏幕的宽和高等参数，首先需要声明一个DisplayMetrics对象，屏幕的宽高等属性存放在这个对象中
		DisplayMetrics DM = new DisplayMetrics();
		// 获取窗口管理器,获取当前的窗口,调用getDefaultDisplay()后，其将关于屏幕的一些信息写进DM对象中,最后通过getMetrics(DM)获取
		windowManager.getDefaultDisplay().getMetrics(DM);
		displayWidth = DM.widthPixels;
		displayHeight = DM.heightPixels;
		// 使用display.getOrientation() 判断横竖屏不准确
		if (displayWidth > displayHeight) {
			displayWidth = DM.heightPixels;
			displayHeight = DM.widthPixels;
		}
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		density = metrics.density;
		densityDpi = metrics.densityDpi;
		if (densityDpi <= DisplayMetrics.DENSITY_LOW) {
		} else if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
		} else if (densityDpi <= DisplayMetrics.DENSITY_HIGH
				&& displayWidth <= Constants.DISPLAY_HDPI_WIDTH) {
		} else {
		}
		if (BuildConfig.DEBUG) {
			Log.v("Display Width: ", " " + displayWidth);
			Log.v("Display Height: ", " " + displayHeight);
			Log.v("Display Density: ", " " + densityDpi);
			Log.d(TAG, "initAvatarSize Finish : " + System.currentTimeMillis()
					/ 1000);
		}
	}

	private void initCachePath() {
		innerCachePath = getCacheDir().getAbsolutePath();
		File sdcardPath = android.os.Environment.getExternalStorageDirectory();
		final String cacheDir = "/Android/data/" + getPackageName() + "/cache/";
		sdcardCachePath = sdcardPath.getAbsolutePath() + File.separator
				+ cacheDir;
	}
}
