package zq.whu.zhangshangwuda.ui.wifi;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

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
import zq.whu.zhangshangwuda.ui.AboutActivity;
import zq.whu.zhangshangwuda.ui.HelpActivity;
import zq.whu.zhangshangwuda.ui.MainActivityTAB;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.SettingActivity;
import zq.whu.zhangshangwuda.views.DropPopMenu;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	private final int MENU_GROUP = 1;
	private static final int MENU_LOGOFF = Menu.FIRST;
	private final int MENU_SETTING = Menu.FIRST + 1;
	private final int MENU_HELP = Menu.FIRST + 2;
	private final int MENU_FEEDBACK = Menu.FIRST + 3;
	private final int MENU_ABOUT = Menu.FIRST + 4;
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		menu.add(MENU_GROUP, MENU_LOGOFF, MENU_LOGOFF, getResources().getString(R.string.logoff));
		menu.add(MENU_GROUP, MENU_SETTING, MENU_SETTING, getResources().getString(R.string.LeftMenu_Setting));
		menu.add(MENU_GROUP, MENU_HELP, MENU_HELP, getResources().getString(R.string.LeftMenu_Help));
		menu.add(MENU_GROUP, MENU_FEEDBACK, MENU_FEEDBACK, getResources().getString(R.string.LeftMenu_FeedBack)); 
		menu.add(MENU_GROUP, MENU_ABOUT, MENU_ABOUT, getResources().getString(R.string.LeftMenu_About)); 
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case MENU_LOGOFF:
			new Thread(new LogOutThread()).start();
			return true;
		case MENU_SETTING:
			intent.setClass(getActivity(),SettingActivity.class);
			startActivity(intent);
			return true;
		case MENU_HELP:
			intent.setClass(getActivity(),HelpActivity.class);
			startActivity(intent);
			return true;
		case MENU_FEEDBACK:
			MainActivityTAB.agent.startFeedbackActivity();
			return true;
		case MENU_ABOUT:
			intent.setClass(getActivity(),AboutActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
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
		MainActivityTAB.MainActivityActionBar.setSubtitle("第"
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
		String str1 = AccountView.getText().toString();
		String str2 = PasswordView.getText().toString();
		try {
			str1 = BosCrypto.encrypt(BosCrypto.Excalibur, str1);
			str2 = BosCrypto.encrypt(BosCrypto.Excalibur, str2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localEditor.putString("wifi_Account", str1).commit();
		if (isRememberMe()) {
			localEditor.putString("wifi_Password", str2).commit();
		} else {
			ClearPassword();
		}
	}

	public void InitConfig() {
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
			}
		}
	};

	Handler OnlineHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			LoginButton.setText("登陆");
			if (msg.arg1 == 0) {
				ToastUtil.showToast(getActivity(), "哈哈！验证成功啦~");
			}

			if (msg.arg1 == 1) {
				String strtemp = (String) msg.obj;
				ToastUtil.showToast(getActivity(), strtemp);
				LoginButton.setEnabled(true);
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
			// 更新数据库
			if (WifiDb.getInstance(getActivity()).getAccountByUsername(Account) == null) {
				WifiAccount account = new WifiAccount(Account, Password);
				WifiDb.getInstance(getActivity()).insert(account);
			} else {
				WifiAccount account = WifiDb.getInstance(getActivity())
						.getAccountByUsername(Account);
				account.setPassword(Password);
				WifiDb.getInstance(getActivity()).update(account);
			}
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
			SaveConfig();
			Account = AccountView.getText().toString();
			Password = PasswordView.getText().toString();
			LoginButton.setText("登陆中……");
			new Thread(new OnlineThread()).start();
			LoginButton.setEnabled(false);
		}
	}
}
