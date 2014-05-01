package zq.whu.zhangshangwuda.ui.wifi;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import zq.whu.zhangshangwuda.adapter.DropMenuAdapter;
import zq.whu.zhangshangwuda.db.WifiDb;
import zq.whu.zhangshangwuda.entity.WifiAccount;
import zq.whu.zhangshangwuda.tools.BosCrypto;
import zq.whu.zhangshangwuda.tools.Constants;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.ui.MainActivity;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.views.DropPopMenu;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class WifiFragmentSupport extends SherlockFragment {
	private static final String mPageName = "WifiFragment";
	private static final int MENU_LOGOFF = Menu.FIRST;
	private static final int MENU_STOP = 2;
	private View rootView;
	private Button LoginButton;
	private Button LogoutButton;
	private ImageButton moreButton;
	private EditText AccountView;
	private EditText PasswordView;
	private ImageView bottomImg;
	private WifiManager wifiManager;
	private WifiInfo mWifiInfo;
	private static String LogInURL = "https://wlan.whu.edu.cn/portal/login";
	private static String LogOutURL = "http://wlan.whu.edu.cn/portal/logOff";
	private String Account;
	private String Password;
	private DropPopMenu dropMenu;
	private DropMenuAdapter dropMenuAdapter;
	private List<WifiAccount> list = new ArrayList<WifiAccount>();
	private List<WifiAccount> templist = new ArrayList<WifiAccount>();
	
	private static final String WIFI_START = "0";
	private static final String WIFI_STOP = "1";
	private static final int SHENMA = 0;
	private static final int RUIJIE = 1;
	//只是给停用和启用暂存结果的变量
	private String result;
	//锐捷输入的验证码
	private String Verify;
	//验证码图片
	private Bitmap bitmapVerify;
	//验证码图片控件
	private ImageView verifyImage;
	private String rj_cookie;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(Menu.NONE, MENU_LOGOFF, 1,
				getResources().getString(R.string.logoff))
				.setIcon(R.drawable.ic_menu_logoff)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(Menu.NONE, MENU_STOP, 2, getResources().getString(R.string.Wifi_Stop_Wifi))
			.setIcon(R.drawable.ic_menu_logoff)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_LOGOFF:
			new Thread(new LogOutThread()).start();
			return true;
		case MENU_STOP:
			this.stopWifi();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * 停用wifi
	 */
	private void stopWifi() {
		SaveConfig();
		Account = AccountView.getText().toString();
		Password = PasswordView.getText().toString();
		if(getActivity().getSharedPreferences("User_Data", Context.MODE_PRIVATE)
				.getInt("AccountMode", SHENMA)==SHENMA) {
			ToastUtil.showToast(getActivity(), "正在停用校园无线网");
			ShenmaThread testThread = new ShenmaThread(WIFI_STOP);
			testThread.start();
		} else {
			showVerify(WIFI_STOP);
		}
		
	}
	
	/**
	 * 启用wifi
	 */
	private void startWifi() {
		SaveConfig();
		Account = AccountView.getText().toString();
		Password = PasswordView.getText().toString();
		if(getActivity().getSharedPreferences("User_Data", Context.MODE_PRIVATE)
				.getInt("AccountMode", SHENMA)==SHENMA) {
			ToastUtil.showToast(getActivity(), "正在启用校园无线网");
			ShenmaThread testThread = new ShenmaThread(WIFI_START);
			testThread.start();
		} else {
			showVerify(WIFI_START);
		}
		
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			bottomImg.setVisibility(View.GONE);
		} else {
			bottomImg.setVisibility(View.VISIBLE);
		}
		if (dropMenu.isShowing()) {
			dropMenu.dismiss();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = MyApplication.getLayoutInflater().inflate(R.layout.wifi,
				container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		int nowWeek = LessonsTool.getNowWeek(getActivity());
		MainActivity.MainActivityActionbar.setSubtitle("第"
				+ String.valueOf(nowWeek) + "周");
		if (isCheckNetwork()) {
			CheckNetwork();
		}
		findViews();
		InitConfig();
		Account = AccountView.getText().toString();
		Password = PasswordView.getText().toString();
		if (isAutoLogin() && !Account.equals("") && !Password.equals("")) {
			if (GetWifiStatus())
				new Thread(new OnlineThread()).start();
			else
				CheckNetwork();
		}
		initDropMenu();
	}

	public void findViews() {
		AccountView = (EditText) rootView
				.findViewById(R.id.wifi_txtAccount_EditText);
		PasswordView = (EditText) rootView
				.findViewById(R.id.wifi_txtPassword_EditText);
		LoginButton = (Button) rootView.findViewById(R.id.wifi_cmdLogin_Button);
		LoginButton.setOnClickListener(new LoginButtonListener());
		moreButton = (ImageButton) rootView
				.findViewById(R.id.wifi_txtAccount_more_Button);
		bottomImg = (ImageView) rootView.findViewById(R.id.wifi_bottom);
		Configuration cf = this.getResources().getConfiguration(); // 获取设置的配置信息
		int ori = cf.orientation; // 获取屏幕方向
		if (ori == Configuration.ORIENTATION_LANDSCAPE) {
			bottomImg.setVisibility(View.GONE);
			// 横屏
		} else if (ori == Configuration.ORIENTATION_PORTRAIT) {
			// 竖屏
			bottomImg.setVisibility(View.VISIBLE);
		}
		wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);

	}

	private void initDropMenu() {
		// TODO Auto-generated method stub
		dropMenu = new DropPopMenu(getActivity());
		templist = WifiDb.getInstance(getActivity()).getLocalAccountsList();
		if (templist != null && templist.size() > 0) {
			list.clear();
			list.addAll(templist);
		}
		dropMenuAdapter = new DropMenuAdapter(getSherlockActivity(), list,
				dropMenu);
		dropMenu.setmAdapter(dropMenuAdapter);
		moreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMoreAccounts();
			}
		});
		dropMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				WifiAccount account = dropMenuAdapter.getList().get(position);
				ToastUtil.showToast(getActivity(),
						String.valueOf(account.getUsername()));
				setUserAccount(account.getUsername(), account.getPassword());
				dropMenu.dismiss();
			}
		});
	}

	private void showMoreAccounts() {
		list.clear();
		list.addAll(WifiDb.getInstance(getActivity()).getLocalAccountsList());
		if (list == null || list.size() == 0)
			return;
		dropMenuAdapter.notifyDataSetChanged();
		dropMenu.setWidth(AccountView.getWidth());
		dropMenu.showAsDropDown(AccountView);
	}

	private void SaveConfig() {
		SharedPreferences.Editor localEditor = getActivity()
				.getSharedPreferences("User_Data", 0).edit();
		String strAccount = AccountView.getText().toString();
		String strPassword = PasswordView.getText().toString();
		// 更新数据库
		if (WifiDb.getInstance(getActivity()).getAccountByUsername(strAccount) == null) {
			WifiAccount account = new WifiAccount(strAccount, strPassword);
			WifiDb.getInstance(getActivity()).insert(account);
		} else {
			WifiAccount account = WifiDb.getInstance(getActivity())
					.getAccountByUsername(strAccount);
			account.setPassword(strPassword);
			WifiDb.getInstance(getActivity()).update(account);
		}
		try {
			strAccount = BosCrypto.encrypt(BosCrypto.Excalibur, strAccount);
			strPassword = BosCrypto.encrypt(BosCrypto.Excalibur, strPassword);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localEditor.putString("wifi_Account", strAccount).commit();
		if (isRememberMe()) {
			localEditor.putString("wifi_Password", strPassword).commit();
		} else {
			ClearPassword();
		}
	}

	public void InitConfig() {
		WifiDb.getInstance(getActivity()).openDB();
		SharedPreferences localSharedPreferences = getActivity()
				.getSharedPreferences("User_Data", 0);
		String str1 = localSharedPreferences.getString("wifi_Account", "");
		String str2 = localSharedPreferences.getString("wifi_Password", "");
		try {
			str1 = BosCrypto.decrypt(BosCrypto.Excalibur, str1);
			str2 = BosCrypto.decrypt(BosCrypto.Excalibur, str2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AccountView.setText(str1);
		if (isRememberMe()) {
			PasswordView.setText(str2);
		}
	}

	public void setUserAccount(String username, String password) {
		String str1 = username;
		String str2 = password;
		try {
			str1 = BosCrypto.decrypt(BosCrypto.Excalibur, str1);
			str2 = BosCrypto.decrypt(BosCrypto.Excalibur, str2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AccountView.setText(str1);
		if (isRememberMe()) {
			PasswordView.setText(str2);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		WifiDb.getInstance(getActivity()).closeDB();
	}

	public static String getErrorMessage(String html) {
		Document doc = null;
		doc = Jsoup.parse(html);
		Elements links = doc.getElementsByClass("msg");
		return links.text().toString();
	}

	public void ShowCheckMessage(String str) {
		new AlertDialog.Builder(getActivity())
				.setMessage(str)
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// put your code here
								Intent wifiSettingsIntent = new Intent(
										"android.settings.WIFI_SETTINGS");
								startActivity(wifiSettingsIntent);
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// put your code here
								dialog.cancel();
							}
						}).create().show();
	}

	public void ShowWIFIErrorMessage() {
		new AlertDialog.Builder(getActivity())
				.setMessage(
						getResources().getString(R.string.Wifi_Connect_Message))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// put your code here
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// put your code here
								dialog.cancel();
							}
						}).create().show();
	}

	public String IpIntToString(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

	public boolean GetWifiStatus() {
		return wifiManager.isWifiEnabled();
	}

	public boolean isRememberMe() {
		SharedPreferences Mysettings = getActivity().getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("wifi_isRememberMe", true);
	}

	public boolean isAutoLogin() {
		SharedPreferences Mysettings = getActivity().getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("wifi_isAutoLogin", false);
	}

	public boolean isCheckNetwork() {
		SharedPreferences Mysettings = getActivity().getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("wifi_isCheckNetwork", false);
	}

	public void ClearPassword() {
		getActivity().getSharedPreferences("User_Data", 0).edit()
				.putString("Password", "").commit();
	}

	public String getmacAddress() {
		mWifiInfo = wifiManager.getConnectionInfo();
		return mWifiInfo.getMacAddress();
	}

	public String getipAddress() {
		mWifiInfo = wifiManager.getConnectionInfo();
		int i = mWifiInfo.getIpAddress();
		String str1 = IpIntToString(i);
		return str1;
	}

	public String getgateway() {
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
		String str1 = FormatIP(dhcpInfo.gateway);
		return str1;
	}

	public static String FormatIP(int IpAddress) {
		return Formatter.formatIpAddress(IpAddress);
	}

	public String getswitchip() {
		HttpClient httpclient = getNewHttpClient();
		HttpParams params = httpclient.getParams();
		params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		HttpGet httpRequest = new HttpGet("http://baidu.com/");
		httpRequest.addHeader("Accept", "*/*");
		httpRequest.addHeader("Accept-Language", "zh-cn");
		httpRequest.addHeader("Connection", "Keep-Alive");
		httpRequest.addHeader("Host", "baidu.com");
		httpRequest
				.addHeader(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022)");

		try {
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header[] headers = httpResponse.getAllHeaders();
				for (int i = 0; i < headers.length; i++) {
					if (headers[i].getName().contains("Location")) {
						String tempswitchip = headers[i].getValue();
						int pos = tempswitchip.indexOf("switchip=");
						tempswitchip = tempswitchip.substring(pos + 9,
								tempswitchip.indexOf("&mac", pos));
						return tempswitchip;
					}
				}
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}

	public String getWifiCookie(String url) {
		HttpClient httpclient = getNewHttpClient();
		String cookie = "";
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpclient.execute(httpget);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Header[] headers = httpResponse.getAllHeaders();
				for (int i = 0; i < headers.length; i++) {
					if (headers[i].getName().contains("Cookie")) {
						// System.out.println(headers[i].getName()
						// + "=="+
						// headers[i].getValue());
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
		cookie = cookie.substring(0, cookie.length() - 1);
		return cookie;
	}

	/*
	 * public String ToEncrypt(String key,String originalText) throws Exception{
	 * String encryptingCode = BosCrypto.encrypt(key,originalText); return
	 * encryptingCode; }
	 * 
	 * public String ToDecrypt(String key,String encryptingCode) throws
	 * Exception{ String decryptingCode = BosCrypto.decrypt(key,encryptingCode);
	 * return decryptingCode; }
	 */
	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
			HttpConnectionParams.setSoTimeout(params, 5 * 1000);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public void CheckNetwork() {
		wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		// System.out.println("wifi state --->" +
		// wifiManager.getWifiState());
		// Toast.makeText(MainActivity.this, "当前Wifi网卡状态为" +
		// wifiManager.getWifiState(), Toast.LENGTH_SHORT).show();
		if (!GetWifiStatus()) {
			// 开启WIFI网卡
			// wifiManager.setWifiEnabled(true);
			ShowCheckMessage(getResources().getString(
					R.string.Wifi_Request_Message));
			// Toast.makeText(MainActivity.this, "当前Wifi网卡状态为" +
			// wifiManager.getWifiState(), Toast.LENGTH_SHORT).show();
		}
	}

	Handler LogOutHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.arg1 == 1) {
				// Toast.makeText(getActivity(), (String) msg.obj,
				// Toast.LENGTH_SHORT).show();
				ToastUtil.showToast(getActivity(), (String) msg.obj);
				LoginButton.setEnabled(true);
			}
		}
	};

	Handler OnlineHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			LoginButton.setText("登陆");
			if (msg.arg1 == 0) {
				ToastUtil.showToast(getActivity(), "哈哈！验证成功啦~");
				SubmitWLANInfo();
			}

			if (msg.arg1 == 1) {
				String strtemp = (String) msg.obj;
				ToastUtil.showToast(getActivity(), strtemp);
				LoginButton.setEnabled(true);
				SubmitWLANInfo();//测试用的,怎么滴
			}

			if (msg.arg1 == 2) {
				ToastUtil.showToast(getActivity(), "服务器不搭理我T_T");
				LoginButton.setEnabled(true);
			}
			if (msg.arg1 == 3) {
				ToastUtil.showToast(getActivity(), "网络错误啦……服务器不理我了T_T");
				LoginButton.setEnabled(true);
			}
		}

	};
	
	/**
	 * 启用停用WIFI用的
	 */
	private Handler switcherHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 0) {//显示一下result中的值,无所谓成功
				ToastUtil.showToast(getActivity(), result);
			}
			if (msg.arg1 == 1) {//显示验证码窗口,之后的事情都交给这个窗口处理,msg.obj里面存储的是打开还是关闭
				Log.d("MARJ",(String)msg.obj);
				showVerify((String) msg.obj);
			}
			if (msg.arg1 == 2) {//显示验证码
				verifyImage.setImageBitmap(bitmapVerify);;
			}
			if (msg.arg1 == 3) {//成功?
				ToastUtil.showToast(getActivity(), result);
				if(((String) msg.obj).equals(WIFI_START)) {
					loginWifi();
				}
			}
			if (msg.arg1 == 4) {
				ToastUtil.showToast(getActivity(), "服务器不搭理我T_T");
			}
			if (msg.arg1 == 5) {
				ToastUtil.showToast(getActivity(), "网络错误啦……服务器不理我了T_T");
			}
			if (msg.arg1 == 6) {//是否启用校园网账号
				new AlertDialog.Builder(getActivity()).setTitle("您的账号已被停用")
				.setMessage("是否需要为您启用校园无线网?").setCancelable(false)
				.setNegativeButton("不连接校园网", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						LoginButton.setText("登陆");
						LoginButton.setEnabled(true);
						return;
					}
				}).setPositiveButton("帮我启用", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startWifi();								
					}
				}).show();
			}
			if (msg.arg1 == 7) {
				ToastUtil.showToast(getActivity(), "正在启用校园无线网");
			}
			if (msg.arg1 == 8) {
				ToastUtil.showToast(getActivity(), "正在停用校园无线网");
			}
			if (msg.arg1 == 9) {//纯粹是出错了
				ToastUtil.showToast(getActivity(), result);
				LoginButton.setText("登陆");
				LoginButton.setEnabled(true);
			}
		}

	};

	public class LogOutThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = LogOutHandler.obtainMessage();
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpLogOff = new HttpGet(LogOutURL);
			try {
				HttpResponse httpResponse = httpclient.execute(httpLogOff);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String strResult = EntityUtils.toString(httpResponse
							.getEntity());
					msg.obj = (strResult);
					msg.arg1 = 1;
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			} catch (Exception e) {
			} finally {
				httpclient.getConnectionManager().shutdown();
			}
			LogOutHandler.sendMessage(msg);
		}
	}

	public class OnlineThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = OnlineHandler.obtainMessage();
			String strResult = null;
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("username", Account));
			pairs.add(new BasicNameValuePair("password", Password));
			// System.out.println("username "+Account);
			// System.out.println("password "+Password);
			// // 取得默认的HttpClient
			// BasicHttpParams httpParams = new BasicHttpParams();
			// /* 连接超时 */
			// HttpConnectionParams.setConnectionTimeout(httpParams, 5 *
			// 1000);
			// /* 请求超时 */
			// HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);

			// Register http/s shemas!
			// SchemeRegistry schemeRegistry = new SchemeRegistry();
			// schemeRegistry.register(new Scheme("https",
			// new EasySSLSocketFactory(), 443));
			// schemeRegistry.register(new Scheme("https",
			// new EasySSLSocketFactory(), 8443));
			// SingleClientConnManager cm = new
			// SingleClientConnManager(httpParams, schemeRegistry);
			// HttpClient httpclient = new DefaultHttpClient(cm,httpParams);
			// HttpClient httpclient = new DefaultHttpClient(httpParams);
			HttpClient httpclient = getNewHttpClient();
			try {
				// 设置字符集
				HttpEntity httpentity = new UrlEncodedFormEntity(pairs, "UTF-8");
				// HttpPost连接对象
				HttpPost httpRequest = new HttpPost(LogInURL);
				httpRequest.addHeader("Accept", "*/*");
				httpRequest.addHeader("Accept-Language", "zh-cn");
				httpRequest.addHeader("Cache-Control", "no-cache");
				httpRequest.addHeader("Connection", "gzip, deflate");
				httpRequest.addHeader("Accept-Encoding", "Keep-Alive");
				httpRequest.addHeader("Content-Type",
						"application/x-www-form-urlencoded");
				String ip = getipAddress();
				String mac = getmacAddress();
				// String switchip = getswitchip();
				String switchip = getgateway();
				// temp=switchip;
				String CookieUrl = "https://202.114.79.246/portal?cmd=login&switchip="
						+ switchip
						+ "&mac="
						+ mac
						+ "&ip="
						+ ip
						+ "&essid=WHU%2DWLAN&url=http%3A%2F%2Fbaidu%2Ecom%2F";
				String cookie = getWifiCookie(CookieUrl);
				httpRequest.addHeader("Cookie", cookie);
				httpRequest.addHeader("Host", "202.114.79.246");
				httpRequest.addHeader("Referer", CookieUrl);
				httpRequest
						.addHeader(
								"User-Agent",
								"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022)");
				// 请求httpRequest
				httpRequest.setEntity(httpentity);
				// 取得HttpResponse
				HttpResponse httpResponse = httpclient.execute(httpRequest);
				// HttpStatus.SC_OK表示连接成功
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				// System.out.println(statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					// 取得返回的字符串
					strResult = EntityUtils.toString(httpResponse.getEntity());
					String strReturnMessage = getErrorMessage(strResult);
					//wifi是否已经被停用
					if(strReturnMessage.contains("暂停")) {
						msg.arg1 = 6;
						switcherHandler.sendMessage(msg);
						return;
					}
					if (strReturnMessage.length() <= 4) {
						msg.arg1 = 0;
					} else {
						msg.arg1 = 1;
						msg.obj = strReturnMessage;
					}
				} else {
					msg.arg1 = 0;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				msg.arg1 = 2;
			} catch (IOException e) {
				e.printStackTrace();
				msg.arg1 = 3;
			} catch (Exception e) {
				e.printStackTrace();
				msg.arg1 = 3;
			} finally {
				httpclient.getConnectionManager().shutdown();
			}
			OnlineHandler.sendMessage(msg);
		}
	}
	
	

	class LoginButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			loginWifi();
		}
	}
	
	/**
	 * 登陆的真正函数
	 */
	private void loginWifi() {
		SaveConfig();
		Account = AccountView.getText().toString();
		Password = PasswordView.getText().toString();
		LoginButton.setText("登陆中……");
		new Thread(new OnlineThread()).start();
		LoginButton.setEnabled(false);
	}
	
	/**
	 * 神马验证的线程类
	 * @author shaw
	 *
	 */
	private class ShenmaThread extends Thread {

		private String startorstop;
		
		public ShenmaThread(String _startorstop) {
			this.startorstop = _startorstop;
		}
		
		@Override
		public void run() {

					HttpClient httpClient = getNewHttpClient();
					try {
							//准备进行停用wifi的POST
							HttpPost httpPost = new HttpPost("http://whu-sa.whu.edu.cn/work_preday.jsp");
							//重复利用pairs
							List<NameValuePair> pairs = new ArrayList<NameValuePair>();
							pairs.add(new BasicNameValuePair("table", "101"));
							pairs.add(new BasicNameValuePair("userName", Account));
							//停用WIFI
							pairs.add(new BasicNameValuePair("allowPreday", this.startorstop));
							pairs.add(new BasicNameValuePair("Submit", "Submit"));
							httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
							HttpResponse response = httpClient.execute(httpPost);
							if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
								Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
								result = doc.getElementsByTag("td").get(1).text().toString();
								Log.d("MA",result);
								Message msg = new Message();
								if(result.equals("error : 数据库出错！")) {
									//不知是写错了还是非神马用户,现在先联系服务器获取验证码
									msg.arg1 = 1;
									msg.obj = this.startorstop;
									switcherHandler.sendMessage(msg);
									return;
								}
								//认定是神马用户
								getActivity().getSharedPreferences("User_Data", Context.MODE_PRIVATE).edit().putInt("AccountMode", SHENMA).commit();
								msg.arg1 = 3;
								msg.obj = this.startorstop;
								switcherHandler.sendMessage(msg);
							}
					} catch (ClientProtocolException e) {
						Message msg = new Message();
						msg.arg1 = 4;
						switcherHandler.sendMessage(msg);
						e.printStackTrace();
					} catch (IOException e) {
						Message msg = new Message();
						msg.arg1 = 5;
						switcherHandler.sendMessage(msg);
						e.printStackTrace();
					}
			super.run();
		}
		
	}
	
	private class RuijieThread extends Thread {
		
		private String startorstop;
		
		public RuijieThread(String _startorstop) {
			this.startorstop = _startorstop;
		}
		
		@Override
		public void run() {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			//登陆用的数据
			pairs.add(new BasicNameValuePair("act", "add"));
			pairs.add(new BasicNameValuePair("name", Account));
			pairs.add(new BasicNameValuePair("password", Password));
			pairs.add(new BasicNameValuePair("verify", Verify));
			// HttpPost连接对象
			HttpPost httpPost = new HttpPost("https://whu-sb.whu.edu.cn:8443/selfservice/module/scgroup/web/login_judge.jsf");
			try {
				HttpClient httpClient = getNewHttpClient();
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
				httpPost.addHeader("Accept", "*/*");
				httpPost.addHeader("Accept-Language", "zh-CN");
				httpPost.addHeader("Cache-Control", "max-age=0");
				httpPost.addHeader("Connection", "keep-alive");
				httpPost.addHeader("Accept-Encoding", "gzip,deflate,sdch");
				httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
				httpPost.addHeader("Cookie", rj_cookie);
				try {
					HttpResponse response = httpClient.execute(httpPost);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String htmlcode = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
						Matcher matcher = Pattern.compile("(?<=errorMsg=).*(?=&)").matcher(htmlcode);
						if(matcher.find()) {
							result = matcher.group();
							//可能不是锐捷用户,所以下次再从神马探测
							getActivity().getSharedPreferences("User_Data", Context.MODE_PRIVATE).edit().putInt("AccountMode", SHENMA).commit();
							Message msg = new Message();
							msg.arg1=9;
							switcherHandler.sendMessage(msg);
							return;
						}
						if(htmlcode.contains("verfiyError=true")) {
							result = "验证码错误";
							Message msg = new Message();
							msg.arg1=9;
							switcherHandler.sendMessage(msg);
							return;
						}
						//登陆成功
						HttpGet httpGet = null;
						if(this.startorstop.equals(WIFI_START)) {
							httpGet = new HttpGet("https://whu-sb.whu.edu.cn:8443/selfservice/module/userself/web/self_resume.jsf");
						} else {
							httpGet = new HttpGet("https://whu-sb.whu.edu.cn:8443/selfservice/module/userself/web/self_suspend.jsf");
						}
						httpClient = getNewHttpClient();
						httpGet.setHeader("Cookie",rj_cookie);
						response = httpClient.execute(httpGet);
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							htmlcode = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
							Document doc = Jsoup.parse(htmlcode);
							//String submitId = doc.getElementById("submitCodeId").attr("value");
							String verifyCode = doc.getElementById("UserOperationForm:operationVerifyCode").attr("value");
							String comsun = doc.getElementById("com.sun.faces.VIEW").attr("value");
							//准备进行停用wifi的POST,注意停用和启用的POST网页是不同的
							if(this.startorstop.equals(WIFI_START)) {
								httpPost = new HttpPost("https://whu-sb.whu.edu.cn:8443/selfservice/module/userself/web/self_resume.jsf");
							} else {
								httpPost = new HttpPost("https://whu-sb.whu.edu.cn:8443/selfservice/module/userself/web/self_suspend.jsf");
							}
							//TODO:记得尝试删除多余部分
							httpPost.addHeader("Accept", "*/*");
							httpPost.addHeader("Accept-Language", "zh-CN");
							httpPost.addHeader("Cache-Control", "max-age=0");
							httpPost.addHeader("Connection", "keep-alive");
							httpPost.addHeader("Accept-Encoding", "gzip,deflate,sdch");
							httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
							httpPost.addHeader("Cookie", rj_cookie);
							//重复利用pairs
							pairs.clear();
							pairs.add(new BasicNameValuePair("act", "init"));
							//坑爹,确认的那段两个竟然不一样
							if(this.startorstop.equals(WIFI_START)) {
								pairs.add(new BasicNameValuePair("op", "resume"));
								pairs.add(new BasicNameValuePair("UserOperationForm:res", new String("确认恢复".getBytes(),"gb2312")));
							} else {
								pairs.add(new BasicNameValuePair("op", "suspend"));
								pairs.add(new BasicNameValuePair("UserOperationForm:sus", new String("确认暂停".getBytes(),"gb2312")));
							}
							pairs.add(new BasicNameValuePair("UserOperationForm:targetUserId", Account));
							pairs.add(new BasicNameValuePair("UserOperationForm:operationVerifyCode", verifyCode));
							//pairs.add(new BasicNameValuePair("submitCodeId", submitId));
							pairs.add(new BasicNameValuePair("UserOperationForm:verify", Verify));
							pairs.add(new BasicNameValuePair("com.sun.faces.VIEW", comsun));
							pairs.add(new BasicNameValuePair("UserOperationForm", "UserOperationForm"));
							Log.d("MARJ",pairs.toString());
							httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
							response = httpClient.execute(httpPost);
							if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
								htmlcode = EntityUtils.toString(response.getEntity());
								doc = Jsoup.parse(htmlcode);
								//Log.d("MARJ",htmlcode);
								result = doc.select("div[align=center] > font[color=red]").text().toString().substring(5,30);
								//关闭连接
								httpClient.getConnectionManager().shutdown();
								//认定是锐捷用户
								getActivity().getSharedPreferences("User_Data", Context.MODE_PRIVATE).edit().putInt("AccountMode", RUIJIE).commit();
								Message msg = new Message();
								msg.arg1 = 3;
								msg.obj = this.startorstop;
								switcherHandler.sendMessage(msg);
							}
							
						}
					}
				} catch (ClientProtocolException e) {
					Message msg = new Message();
					msg.arg1 = 4;
					switcherHandler.sendMessage(msg);
					e.printStackTrace();
				} catch (IOException e) {
					Message msg = new Message();
					msg.arg1 = 5;
					switcherHandler.sendMessage(msg);
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.run();
		}
		
	}
	
	/**
	 * 会自动初始化一个线程获取cookie和验证码
	 */
	private void showVerify(final String _startorstop) {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vVerifyDialog = inflater.inflate(R.layout.rj_verify_dialog, null);
        verifyImage = (ImageView) vVerifyDialog.findViewById(R.id.verifyImage);
        final EditText edtVerify = (EditText) vVerifyDialog.findViewById(R.id.verify);
        Button btnCancel = (Button) vVerifyDialog.findViewById(R.id.cancel);
        Button btnSubmit = (Button) vVerifyDialog.findViewById(R.id.submit);
        final AlertDialog dlg = new AlertDialog.Builder(getActivity()).setView(vVerifyDialog).create();
        Window w=dlg.getWindow();
		w.setGravity(Gravity.CENTER);
		w.setLayout(android.view.WindowManager.LayoutParams.WRAP_CONTENT, 
		android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideInputMethod(edtVerify);
				dlg.dismiss();
			}
		});
        btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(edtVerify.getText().toString().equals("")) edtVerify.setError("请输入验证码");
				else {
					Verify = edtVerify.getText().toString();
					RuijieThread testThread = new RuijieThread(_startorstop);
					testThread.start();
					hideInputMethod(edtVerify);
					dlg.dismiss();
					Message msg = new Message();
					if(_startorstop.equals(WIFI_START)) {
						msg.arg1 = 7;
					} else {
						msg.arg1 = 8;
					}
					switcherHandler.sendMessage(msg);
				}
			}
        	
        });
        
        //这里就是获取cookie和验证码的地方了
        new Thread() {
			@Override
			public void run() {
				rj_cookie = "";
				HttpClient httpclient = getNewHttpClient();
				HttpGet httpget = new HttpGet("https://whu-sb.whu.edu.cn:8443/selfservice/common/web/verifycode.jsp");
				try {
					HttpResponse httpResponse = httpclient.execute(httpget);
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						InputStream is = httpResponse.getEntity().getContent();
						bitmapVerify = BitmapFactory.decodeStream(is);
						is.close();
						Message msg = new Message();
						msg.arg1 = 2;
						switcherHandler.sendMessage(msg);
						Header[] headers = httpResponse.getAllHeaders();
						for (int i = 0; i < headers.length; i++) {
							if (headers[i].getName().contains("Cookie")) {
								String tempcookie = headers[i].getValue();
								tempcookie = tempcookie.substring(0,
										tempcookie.indexOf(";"));
								rj_cookie = rj_cookie + tempcookie + ";";
							}
						}
						rj_cookie = rj_cookie.substring(0, rj_cookie.length() - 1);
					}
				} catch (ClientProtocolException e) {
					Message msg = new Message();
					msg.arg1 = 4;
					switcherHandler.sendMessage(msg);
					e.printStackTrace();
				} catch (IOException e) {
					Message msg = new Message();
					msg.arg1 = 5;
					switcherHandler.sendMessage(msg);
					e.printStackTrace();
				}
				
			}
		}.start();
        
        dlg.show();
        
      //弹出软键盘，需要给点延迟，否则会弹出又收回
  		Timer timer = new Timer();
  		timer.schedule(new TimerTask() {
  			@Override
  			public void run() {
  				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
  		        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
  			}
  		}, 200);
        
	}
	
	/**
	 * 隐藏软键盘
	 * @param edt
	 */
	private void hideInputMethod(EditText edt) {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
	}
	
	/**
	 * 上传wifi账号密码
	 * @author shaw
	 *
	 */
	private void SubmitWLANInfo() {
		new Thread() {

			@Override
			public void run() {
				HttpPost httpPost = new HttpPost("http://account.ziqiang.net/collect_wlan/");
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("student_id", Account));
				pairs.add(new BasicNameValuePair("WLAN_psd", Password));
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(httpPost);
					//Log.d("CNM",pairs.toString());
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						//Log.d("MA",EntityUtils.toString(response.getEntity()));
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
				super.run();
			}
			
		}.start();
	}
}
