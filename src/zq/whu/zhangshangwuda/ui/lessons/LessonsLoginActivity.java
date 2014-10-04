package zq.whu.zhangshangwuda.ui.lessons;

import java.io.IOException;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import zq.whu.zhangshangwuda.base.SwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.db.CourseDataUtil;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.entity.Lessons;
import zq.whu.zhangshangwuda.tools.BosCrypto;
import zq.whu.zhangshangwuda.tools.Constants;
import zq.whu.zhangshangwuda.tools.LessonsSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import zq.whu.zhangshangwuda.tools.GetScoreTools;
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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class LessonsLoginActivity extends SwipeBackSherlockActivity {
	private Button LoginButton = null;
	private EditText AccountView;
	private EditText PasswordView;
	public String Account;
	public String Password;
	private ProgressDialog pd = null;
	private EditText YZMView;
	private ImageView YZMimg;
	private static Bitmap YZMbm = null;
	private static String yzmURL = "http://202.114.74.198/GenImg";
	private static String BaseURL = "http://202.114.74.198/servlet/Login";
	private static String QueryStuScore ="http://202.114.74.198/stu/query_score.jsp";
	
	
	private static String MasterCookie;
	private ImageView bottomImg;

	public Bitmap getYZM(String url, String cookie) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Cookie", cookie);
		try {
			HttpResponse httpResponse = httpclient.execute(httpget);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				// 提取Cookie
				Header[] headers = httpResponse.getAllHeaders();
				cookie = "";
				for (int i = 0; i < headers.length; i++) {
					if (headers[i].getName().contains("Cookie")) {
						String tempcookie = headers[i].getValue();
						tempcookie = tempcookie.substring(0,
								tempcookie.indexOf(";"));
						cookie = cookie + tempcookie + ";";
					}
				}
				MasterCookie = cookie.substring(0, cookie.length() - 1);
				return bitmap;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}

	class refreshYZMThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			refreshYZM();
		}

	}

	public void refreshYZM() {
		MasterCookie = getFirstCookie("http://202.114.74.198/");
		YZMbm = getYZM(yzmURL, MasterCookie);
		YZMhandler.sendEmptyMessage(0);
	}

	Handler YZMhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			YZMimg.setImageBitmap(YZMbm);
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			bottomImg.setVisibility(View.GONE);
		} else {
			bottomImg.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.lessons_login);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		findViews();
		InitConfig();
		LessonsSharedPreferencesTool.setLessonsId(getApplicationContext(), 0);
		Account = AccountView.getText().toString();
		Password = PasswordView.getText().toString();
		new Thread(new refreshYZMThread()).start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return false;
	}

	class LoginButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SaveConfig();
			Account = AccountView.getText().toString();
			Password = PasswordView.getText().toString();
			// pd = ProgressDialog.show(
			// LessonsLoginActivity.this,
			// null,
			// LessonsLoginActivity.this.getResources().getString(
			// R.string.Loading_Tip), true, true);// 显示ProgressBar
			if (YZMbm != null) {
				LoginButton.setEnabled(false);
				ToastUtil.showToast(LessonsLoginActivity.this, "正在获取课表请稍候~",10000);
				new Thread(new LogInThread()).start();
			} else {
				ToastUtil.showToast(LessonsLoginActivity.this, "要先连上网哦~");
				new Thread(new refreshYZMThread()).start();
			}
			
		}
	}

	class YZMimgListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new Thread(new refreshYZMThread()).start();
		}
	}

	public String getFirstCookie(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		String cookie = null;
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpclient.execute(httpget);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Header[] headers = httpResponse.getAllHeaders();
				for (int i = 0; i < headers.length; i++) {
					if (headers[i].getName().contains("Cookie")) {
						String tempcookie = headers[i].getValue();
						tempcookie = tempcookie.substring(0,
								tempcookie.indexOf(";"));
						cookie = cookie + tempcookie + ";";
					}
				}
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		if (cookie != null)
			cookie = cookie.substring(0, cookie.length() - 1);
		return cookie;
	}

	public static String getErrorMessage(String html) {
		Document doc = null;
		doc = Jsoup.parse(html);
		Elements links = doc.getElementsByClass("TR_TITLE");
		return links.text().toString();
	}

	public String getLessonsHtml(String url, String cookie) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Cookie", cookie);
		httpget.addHeader("Accept-Language", "zh-cn");
		try {
			HttpResponse httpResponse = httpclient.execute(httpget);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				return strResult;
			} else {

			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	
	public void addLessonsScoreToDb(String url, String cookie){
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get=new HttpGet(QueryStuScore);
		get.setHeader( "Cookie" , cookie);
		String content=null;
		try {
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() == 200){
				HttpEntity httpEntity = response.getEntity();
				content = EntityUtils.toString(httpEntity, "GBK");
			}			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			get.abort(); 
			httpClient.getConnectionManager().shutdown();
		}
        GetScoreTools courses=new GetScoreTools(content);
        courses.parse();
        CourseDataUtil courseDataUtil=new CourseDataUtil(this);
        courseDataUtil.deleteAllCourseScoreData();		            
        courseDataUtil.addCourseScoreData(courses.getList());
        courseDataUtil.close();
        courseDataUtil=null;        
	}
	
	private String LogIn(String cookie) {
		// 取得默认的HttpClient
		String strResult = null;
		HttpClient httpclient = new DefaultHttpClient();
		String Account = AccountView.getText().toString();
		String Password = PasswordView.getText().toString();
		String ImgStr = YZMView.getText().toString();
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("who", "student"));
		pairs.add(new BasicNameValuePair("id", Account));
		pairs.add(new BasicNameValuePair("pwd", Password));
		pairs.add(new BasicNameValuePair("yzm", ImgStr));
		pairs.add(new BasicNameValuePair("submit", "%D5%FD%B3%A3%B5%C7%C2%BC"));
		try {
			// 设置字符集
			HttpEntity httpentity = new UrlEncodedFormEntity(pairs);
			// HttpPost连接对象
			HttpPost httpRequest = new HttpPost(BaseURL);
			httpRequest.addHeader("Cookie", cookie);
			// 请求httpRequest
			httpRequest.setEntity(httpentity);
			// 取得HttpResponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			// HttpStatus.SC_OK表示连接成功
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = getErrorMessage(strResult);
			} else {
				strResult = null;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			strResult = null;
		} catch (IOException e) {
			e.printStackTrace();
			strResult = null;
		} catch (Exception e) {
			e.printStackTrace();
			strResult = null;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return strResult;
	}

	public void findViews() {
		AccountView = (EditText) findViewById(R.id.lessons_txtAccount_EditText);
		PasswordView = (EditText) findViewById(R.id.lessons_txtPassword_EditText);
		LoginButton = (Button) findViewById(R.id.lessons_cmdLogin_Button);
		LoginButton.setOnClickListener(new LoginButtonListener());
		YZMView = (EditText) findViewById(R.id.lessons_YZM_EditText);
		YZMimg = (ImageView) findViewById(R.id.lessons_YZMIMG_ImageView);
		YZMimg.setOnClickListener(new YZMimgListener());
		bottomImg = (ImageView) findViewById(R.id.lessons_bottom);
		Configuration cf = this.getResources().getConfiguration(); // 获取设置的配置信息
		int ori = cf.orientation; // 获取屏幕方向
		if (ori == Configuration.ORIENTATION_LANDSCAPE) {
			bottomImg.setVisibility(View.GONE);
			// 横屏
		} else if (ori == Configuration.ORIENTATION_PORTRAIT) {
			// 竖屏
			bottomImg.setVisibility(View.VISIBLE);
		}
	}

	private void SaveConfig() {
		SharedPreferences.Editor localEditor = getSharedPreferences(
				"User_Data", 0).edit();
		String str1 = AccountView.getText().toString();
		String str2 = PasswordView.getText().toString();
		try {
			str1 = BosCrypto.encrypt(BosCrypto.Excalibur, str1);
			str2 = BosCrypto.encrypt(BosCrypto.Excalibur, str2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localEditor.putString("lessons_Account", str1).commit();
		if (isRememberMe()) {
			localEditor.putString("lessons_Password", str2).commit();
		} else {
			ClearPassword();
		}
	}

	public void ClearPassword() {
		LessonsLoginActivity.this.getSharedPreferences("User_Data", 0).edit()
				.putString("lessons_Password", "").commit();
	}

	public boolean isRememberMe() {
		SharedPreferences Mysettings = LessonsLoginActivity.this
				.getSharedPreferences(Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("lessons_isRememberMe", true);
	}

	public void InitConfig() {
		SharedPreferences localSharedPreferences = getSharedPreferences(
				"User_Data", 0);
		EditText EditTextView1 = AccountView;
		String str1 = localSharedPreferences.getString("lessons_Account", "");
		String str2 = localSharedPreferences.getString("lessons_Password", "");
		try {
			str1 = BosCrypto.decrypt(BosCrypto.Excalibur, str1);
			str2 = BosCrypto.decrypt(BosCrypto.Excalibur, str2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EditTextView1.setText(str1);
		if (isRememberMe()) {
			EditText EditTextView2 = PasswordView;
			EditTextView2.setText(str2);
		}
	}

	Handler LogInHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			LoginButton.setEnabled(true);
			if (msg.arg1 == 0) {
				LessonsSharedPreferencesTool.setLessonsHave(
						LessonsLoginActivity.this, true);
				Intent intent = new Intent("zq.whu.zhangshangwuda.lessonsShow");
				intent.putExtra("Type", "Login");
				sendBroadcast(intent);
				LessonsLoginActivity.this.finish();
			}
			if (msg.arg1 == 1) {
				ToastUtil.showToast(LessonsLoginActivity.this, "少年~账号密码不对哦...");
			}
			if (msg.arg1 == 2) {
				ToastUtil.showToast(LessonsLoginActivity.this, "额……验证码错了呢……");
			}
			if (msg.arg1 == 3) {
				ToastUtil.showToast(LessonsLoginActivity.this,
						"由于不可抗拒的原因失败了...");
			}
		}
	};

	class LogInThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = LogInHandler.obtainMessage();
			String MasterCooike = MasterCookie;
			String StateString;
			StateString = LogIn(MasterCooike);
			if (StateString == null) {
				msg.arg1 = 3;
			} else if (StateString.indexOf("密码") > 0) {
				msg.arg1 = 1;
			} else if (StateString.indexOf("验证码") > 0) {
				msg.arg1 = 2;
			} else {
				msg.arg1 = 0;
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				String TermFirstDay = MobclickAgent.getConfigParams(
						LessonsLoginActivity.this, "term_firstday");
				if (TermFirstDay != "")
					LessonsSharedPreferencesTool.setTermFirstDay(
							LessonsLoginActivity.this, TermFirstDay);
				String LessonsURL = MobclickAgent.getConfigParams(
						LessonsLoginActivity.this, "lessons_url_2");
				String ServantCookie = "studentid=" + Account + ";"
						+ MasterCooike + ";" + "studentid=" + Account;
				// System.out.println(LessonsURL);
				// LessonsURL="http://202.114.74.199/stu/query_stu_lesson.jsp?year=2012&term=%CF%C2&submit=%CF%D4+%CA%BE";
				String Html_lessons = getLessonsHtml(LessonsURL, ServantCookie);
				list = LessonsTool.getLessonsList(getApplicationContext(),
						Html_lessons);
				int size = list.size();
				if (size != 0)
					LessonsDb.getInstance(LessonsLoginActivity.this)
							.deleteAll();
				Lessons tlessons = new Lessons();
				for (int i = 0; i <= size - 1; ++i) {
					Map<String, String> tmap = new HashMap<String, String>();
					tmap = list.get(i);
					tlessons.setId(tmap.get("id"));
					tlessons.setName(tmap.get("name"));
					tlessons.setDay(tmap.get("day"));
					tlessons.setSte(tmap.get("ste"));
					tlessons.setMjz(tmap.get("mjz"));
					tlessons.setTime(tmap.get("time"));
					tlessons.setPlace(tmap.get("place"));
					tlessons.setTeacher(tmap.get("teacher"));
					tlessons.setOther(tmap.get("other"));
					LessonsDb.getInstance(LessonsLoginActivity.this).insert(
							tlessons);
					
					addLessonsScoreToDb(QueryStuScore,MasterCookie);
				}
			}
			LogInHandler.sendMessage(msg);
		}
	}
}
