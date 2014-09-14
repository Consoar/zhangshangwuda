package zq.whu.zhangshangwuda.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import zq.whu.zhangshangwuda.ui.news.NewsContentActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

public class SplashScreen extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 1000; // 延迟1秒
	private boolean _touched = false;
	private Timer timer;
	private long startTime;
	private SharedPreferences Mysettings;
	private boolean firstRun = false;
	String img[] = { "splash/1.jpg", "splash/2.jpg", "splash/3.jpg",
			"splash/4.jpg", "splash/5.jpg", "splash/6.jpg", "splash/7.jpg" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		getWindow().setBackgroundDrawable(null);
		Mysettings = getSharedPreferences("User_Data", 0);
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(new Date());
		// int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		// ((ImageView) findViewById(R.id.splash_img))
		// .setImageBitmap(getImageFromAssetsFile(img[w - 1]));
		// if (w <= 0)
		// w = 7;
		timer = new Timer(true);
		startTime = System.currentTimeMillis();
		timer.schedule(task, 0, 1);

	}

	@Override
	protected void onResume() {
		super.onResume();
		XGPushClickedResult clickedResult = XGPushManager.onActivityStarted(this);
		if (clickedResult != null){
			String customContent = clickedResult.getCustomContent();
			if (customContent != null && customContent.length() != 0){
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(customContent);
					String href = jsonObject.getString("href");
					Intent intent = new Intent(this, NewsContentActivity.class);
					intent.putExtra("href", href);
					startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
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
				// overridePendingTransition(0, 0);
				// intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				// overridePendingTransition(0, 0);
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