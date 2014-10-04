package zq.whu.zhangshangwuda.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import zq.whu.zhangshangwuda.ui.news.NewsContentActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

public class SplashScreen extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 800; // 延迟0.8秒
	private boolean _touched = false;
	private Timer timer;
	private long startTime;
	private String href = null;
	private SharedPreferences Mysettings;
	private boolean firstRun = false;
	String img[] = { "splash/1.jpg", "splash/2.jpg", "splash/3.jpg",
			"splash/4.jpg", "splash/5.jpg", "splash/6.jpg", "splash/7.jpg" };

	private static final String SHAREDPREFERENCES_NAME = "my_pref";
	private static final String KEY_GUIDE_ACTIVITY = "guide_activity";

	private boolean isFirstEnter(Context context, String className) {
		if (context == null || className == null
				|| "".equalsIgnoreCase(className))
			return false;
		String mResultStr = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString(
				KEY_GUIDE_ACTIVITY, "");// 取得所有类名
		if (mResultStr.equalsIgnoreCase("false"))
			return false;
		else
			return true;
	}

	private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SWITCH_MAINACTIVITY:
				Intent mIntent = new Intent();
				mIntent.setClass(SplashScreen.this, MainActivity.class);
				SplashScreen.this.startActivity(mIntent);
				SplashScreen.this.finish();
				break;
			case SWITCH_GUIDACTIVITY:
				mIntent = new Intent();
				mIntent.setClass(SplashScreen.this, GuideActivity.class);
				SplashScreen.this.startActivity(mIntent);
				SplashScreen.this.finish();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		

		boolean mFirst = isFirstEnter(SplashScreen.this, SplashScreen.this
				.getClass().getName());
		if (mFirst)
			mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY, 2000);
		else
		{
			if (BuildConfig.DEBUG) {
				XGPushConfig.enableDebug(getApplicationContext(), true);
			} else
				XGPushConfig.enableDebug(getApplicationContext(), false);
			XGPushManager.registerPush(getApplicationContext());
			getWindow().setBackgroundDrawable(null);
			Mysettings = getSharedPreferences("User_Data", 0);
			timer = new Timer(true);
			startTime = System.currentTimeMillis();
			timer.schedule(task, 0, 1);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		XGPushClickedResult clickedResult = XGPushManager
				.onActivityStarted(this);
		if (clickedResult != null) {
			String customContent = clickedResult.getCustomContent();
			// System.out.println("customContent ==> "+customContent);
			if (customContent != null && customContent.length() != 0) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(customContent);
					href = jsonObject.getString("href");
				} catch (JSONException e) {
					e.printStackTrace();
					href = null;
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		XGPushManager.onActivityStoped(this);
	}

	private final TimerTask task = new TimerTask() {
		@Override
		public void run() {
			if (task.scheduledExecutionTime() - startTime > SPLASH_DISPLAY_LENGHT
					|| _touched) {
				Message message = new Message();
				message.arg1 = 0;
				timerHandler.sendMessage(message);
				timer.cancel();
				this.cancel();
			}

		}
	};

	private final Handler timerHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 0:
				Intent intent = new Intent();
				intent.setClass(SplashScreen.this, MainActivity.class);
				if (href != null) {
					intent.putExtra("href", href);
				}
				finish();
				startActivity(intent);
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		if (evt.getAction() == MotionEvent.ACTION_DOWN) {
			_touched = true;
		}
		return true;
	}

	private Bitmap getImageFromAssetsFile(String fileName) {
		Bitmap image = null;
		AssetManager am = getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}