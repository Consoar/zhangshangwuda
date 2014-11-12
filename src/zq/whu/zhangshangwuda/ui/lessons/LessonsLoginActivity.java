package zq.whu.zhangshangwuda.ui.lessons;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import zq.whu.zhangshangwuda.base.SwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.db.CourseDataUtil;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.entity.Lessons;
import zq.whu.zhangshangwuda.tools.BosCrypto;
import zq.whu.zhangshangwuda.tools.Constants;
import zq.whu.zhangshangwuda.tools.GetScoreTools;
import zq.whu.zhangshangwuda.tools.LessonsSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class LessonsLoginActivity extends SwipeBackSherlockActivity 
{
	private final String UPDATE_SUCCESS = "0";
	
	private Button LoginButton = null;
	private EditText AccountView;
	private EditText PasswordView;
	public String Account;
	public String Password;
	private ProgressDialog pd = null;
	private static String updateURL = "http://115.29.17.73:8001/courses/data/update.json";
	private static String getURL = "http://115.29.17.73:8001/courses/data/get.json";
	private ImageView bottomImg;

	/**
	 * 在获取课表之前要首先调用这个函数来获取最新课表到服务器 || 可能最长要等待6s
	 * @return statusCode	info
	 * 			0			请求成功
	 * 			1			请求方式必须为POST		不会出现
	 * 			2			请求数据不完整			不会出现
	 * 			3			人工验证码错误，尝试重新提交
	 * 			4			学号/密码 错误
	 * 			5			未知错误
	 */
	private String LoginUpdate()
	{
		String strResult = null;
		String statusCode = null;
		boolean status = false;
		
		HttpClient UpdateClient = new DefaultHttpClient();
		String Account = AccountView.getText().toString();
		String Password = PasswordView.getText().toString();
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("id", Account));
		pairs.add(new BasicNameValuePair("pwd", Password));
		
		try 
		{
			HttpEntity httpEntity = new UrlEncodedFormEntity(pairs);
			HttpPost httpPost = new HttpPost(updateURL);
			httpPost.setEntity(httpEntity);
			HttpResponse httpResponse = UpdateClient.execute(httpPost);

			strResult = EntityUtils.toString(httpResponse.getEntity());
			
			JSONObject jsonObject = new JSONObject(strResult);
			status = jsonObject.getBoolean("success");
			if (status)
			{
				statusCode = UPDATE_SUCCESS;
			}
			else
			{
				statusCode = jsonObject.getString("info");
			}
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return statusCode;
	}
	
	/**
	 * 在调用完update后调用这个函数
	 * @return statuscode		info
	 * 			1				请求方式为post		不会出现
	 * 			2				请求数据不完整		不会出现
	 * 			3				需要先调用update / 未完成update
	 * 			4				密码错误 / 过期， 需要首先调用update
	 * 			other			课程信息，用JSONObject包装
	 */
	private String LoginGet()
	{
		String strResult = null;
		String infoString = null;
		boolean status = false;
		
		HttpClient httpClient = new DefaultHttpClient();
		String Account = AccountView.getText().toString();
		String Password = PasswordView.getText().toString();
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("id", Account));
		pairs.add(new BasicNameValuePair("pwd", Password));
		
		try 
		{
			HttpEntity httpEntity = new UrlEncodedFormEntity(pairs);
			HttpPost httpPost = new HttpPost(getURL);
			httpPost.setEntity(httpEntity);
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			strResult = EntityUtils.toString(httpResponse.getEntity());
			
			JSONObject jsonObject = new JSONObject(strResult);
			status = jsonObject.getBoolean("success");
			
			infoString = jsonObject.getString("info");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return infoString;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) 
		{
			bottomImg.setVisibility(View.GONE);
		} 
		else 
		{
			bottomImg.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.lessons_login);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		findViews();
		InitConfig();
		LessonsSharedPreferencesTool.setLessonsId(getApplicationContext(), 0);
		Account = AccountView.getText().toString();
		Password = PasswordView.getText().toString();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case android.R.id.home:
			finish();
			break;
		}
		return false;
	}

	class LoginButtonListener implements OnClickListener 
	{
		@Override
		public void onClick(View v) 
		{
			SaveConfig();
			Account = AccountView.getText().toString();
			Password = PasswordView.getText().toString();
			ToastUtil.showToast(LessonsLoginActivity.this, "正在获取课表请稍候~",10000);
			new Thread(new LogInThread()).start();
		}
	}
	
	public void findViews() 
	{
		AccountView = (EditText) findViewById(R.id.lessons_txtAccount_EditText);
		PasswordView = (EditText) findViewById(R.id.lessons_txtPassword_EditText);
		LoginButton = (Button) findViewById(R.id.lessons_cmdLogin_Button);
		LoginButton.setOnClickListener(new LoginButtonListener());
		bottomImg = (ImageView) findViewById(R.id.lessons_bottom);
		Configuration cf = this.getResources().getConfiguration(); // 获取设置的配置信息
		int ori = cf.orientation; // 获取屏幕方向
		if (ori == Configuration.ORIENTATION_LANDSCAPE) 
		{
			bottomImg.setVisibility(View.GONE);
			// 横屏
		} 
		else if (ori == Configuration.ORIENTATION_PORTRAIT) 
		{
			// 竖屏
			bottomImg.setVisibility(View.VISIBLE);
		}
	}

	private void SaveConfig() 
	{
		SharedPreferences.Editor localEditor = getSharedPreferences("User_Data", 0).edit();
		String str1 = AccountView.getText().toString();
		String str2 = PasswordView.getText().toString();
		try 
		{
			str1 = BosCrypto.encrypt(BosCrypto.Excalibur, str1);
			str2 = BosCrypto.encrypt(BosCrypto.Excalibur, str2);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		localEditor.putString("lessons_Account", str1).commit();
		if (isRememberMe()) 
		{
			localEditor.putString("lessons_Password", str2).commit();
		} 
		else 
		{
			ClearPassword();
		}
	}

	public void ClearPassword() 
	{
		LessonsLoginActivity.this.getSharedPreferences("User_Data", 0).edit()
				.putString("lessons_Password", "").commit();
	}

	public boolean isRememberMe() 
	{
		SharedPreferences Mysettings = LessonsLoginActivity.this
				.getSharedPreferences(Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("lessons_isRememberMe", true);
	}

	public void InitConfig() 
	{
		SharedPreferences localSharedPreferences = getSharedPreferences("User_Data", 0);
		EditText EditTextView1 = AccountView;
		String str1 = localSharedPreferences.getString("lessons_Account", "");
		String str2 = localSharedPreferences.getString("lessons_Password", "");
		try 
		{
			str1 = BosCrypto.decrypt(BosCrypto.Excalibur, str1);
			str2 = BosCrypto.decrypt(BosCrypto.Excalibur, str2);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		EditTextView1.setText(str1);
		if (isRememberMe()) 
		{
			EditText EditTextView2 = PasswordView;
			EditTextView2.setText(str2);
		}
	}

	Handler LogInHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			LoginButton.setEnabled(true);
			if (msg.arg1 == 0) 
			{
				LessonsSharedPreferencesTool.setLessonsHave(
						LessonsLoginActivity.this, true);
				Intent intent = new Intent("zq.whu.zhangshangwuda.lessonsShow");
				intent.putExtra("Type", "Login");
				sendBroadcast(intent);
				LessonsLoginActivity.this.finish();
			}
			if (msg.arg1 == 1) 
			{
				ToastUtil.showToast(LessonsLoginActivity.this, "少年~账号密码不对哦...");
			}
			if (msg.arg1 == 2) 
			{
				ToastUtil.showToast(LessonsLoginActivity.this, "额……验证码错了呢……");
			}
			if (msg.arg1 == 3) 
			{
				ToastUtil.showToast(LessonsLoginActivity.this,"由于不可抗拒的原因失败了...");
			}
		}
	};

	class LogInThread implements Runnable 
	{
		@Override
		public void run() 
		{
			Message msg = LogInHandler.obtainMessage();
			String statusCode = null;
			String lessons = null;
			int NUM = 0;
			
			while(true)
			{
				//LoadingBar
				statusCode = LoginUpdate();		//begin wait
				if (statusCode.equals("4"))
				{
					msg.what = 1;
					break;
				}
				else if (statusCode.equals("5"))
				{
					msg.what = 3;
					break;
				}
				else if (statusCode.equals("3"))
				{
					continue;
				}
				else if (statusCode.equals("0"))
				{
					//update succeed
					lessons = LoginGet();		//get lessons
					NUM++;
					if (NUM >= 3)				//the call number of LoginGet
					{
						msg.what = 3;
						break;
					}
					if (lessons.equals("3") || lessons.equals("4"))
					{
						continue;
					}
					else 						//get succeed
					{
						msg.what = 0;
						 List<Map<String, String>> list = new ArrayList<Map<String, String>>();
						 
						 String TermFirstDay = MobclickAgent.getConfigParams(LessonsLoginActivity.this, "term_firstday");
						 if (TermFirstDay != "")
						 {
							 LessonsSharedPreferencesTool.setTermFirstDay(LessonsLoginActivity.this, TermFirstDay);
						 }
						 
						 
						 
					}
				}
				else 
				{
					msg.what = 3;
					break;
				}
			}
			LogInHandler.sendMessage(msg);		
		}
	}
}
