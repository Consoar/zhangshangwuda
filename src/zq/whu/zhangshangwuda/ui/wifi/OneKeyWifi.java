package zq.whu.zhangshangwuda.ui.wifi;

import java.io.IOException;
import java.io.InputStream;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent.ShortcutIconResource;
import android.graphics.Rect;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.format.Formatter;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import zq.whu.zhangshangwuda.tools.BosCrypto;
import zq.whu.zhangshangwuda.tools.gif.CommonUtil;
import zq.whu.zhangshangwuda.tools.gif.GifHelper.GifFrame;
import zq.whu.zhangshangwuda.tools.gif.PlayGifTask;
import zq.whu.zhangshangwuda.ui.R;

public class OneKeyWifi extends Activity {
	private FrameLayout bg;
	private static ImageView iv = null;
	private PlayGifTask mGifTask;
	private WifiManager wifiManager = null;
	private WifiInfo mWifiInfo = null;
	private static String LogInURL = "https://wlan.whu.edu.cn/portal/login";
	private String Account = null;
	private String Password = null;

	public void CheckNetwork() {
		wifiManager = (WifiManager) OneKeyWifi.this
				.getSystemService(Context.WIFI_SERVICE);
		if (!GetWifiStatus()) {
			ShowCheckMessage(getResources().getString(
					R.string.Wifi_Request_Message));
		}
	}

	public boolean GetWifiStatus() {
		return wifiManager.isWifiEnabled();
	}

	public void ShowCheckMessage(String str) {
		new AlertDialog.Builder(OneKeyWifi.this)
				.setMessage(str)
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// put your code here
						Intent wifiSettingsIntent = new Intent(
								"android.settings.WIFI_SETTINGS");
						startActivity(wifiSettingsIntent);
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// put your code here
						dialog.cancel();
						finish();
					}
				}).create().show();
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.wifi_onekey);
		final Intent intent = getIntent();
		SharedPreferences localSharedPreferences = OneKeyWifi.this
				.getSharedPreferences("User_Data", 0);
		String str1 = localSharedPreferences.getString("wifi_Account", "");
		String str2 = localSharedPreferences.getString("wifi_Password", "");
		try {
			Account = BosCrypto.decrypt(BosCrypto.Excalibur, str1);
			Password = BosCrypto.decrypt(BosCrypto.Excalibur, str2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wifiManager = (WifiManager) OneKeyWifi.this
				.getSystemService(Context.WIFI_SERVICE);
		bg = (FrameLayout) findViewById(R.id.anim_layout);
		Rect rect = intent.getSourceBounds();
		if (rect != null) {
			// int i = getResources().getDimensionPixelSize(0x7f0b001d);
			int i = 5;
			int j = rect.left + rect.width() / 2;
			int k = i + rect.top;
			android.widget.RelativeLayout.LayoutParams layoutparams = (android.widget.RelativeLayout.LayoutParams) bg
					.getLayoutParams();
			layoutparams.leftMargin = j - layoutparams.width / 2;
			layoutparams.topMargin = k - layoutparams.height / 2;
			bg.setLayoutParams(layoutparams);
		}
		final ImageView iv = (ImageView) findViewById(R.id.anim_image);
		iv.setScaleType(ScaleType.CENTER);

		final InputStream is = getResources().openRawResource(
				R.drawable.wifi_onekey_cat);
		final GifFrame[] frames = CommonUtil.getGif(is);
		// System.out.println("delay:" + frames[1].delay + ",size:"
		// + frames.length);
		if (GetWifiStatus())
			new Thread(new OnlineThread()).start();
		else
			CheckNetwork();
		// new Thread(new OnlineThread()).start();
		mGifTask = new PlayGifTask(iv, frames);
		mGifTask.start();
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

	Handler OnlineHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// Toast.makeText(MainActivity.this, temp,
			// Toast.LENGTH_SHORT).show();
			if (msg.arg1 == 0) {
				Toast.makeText(OneKeyWifi.this, "哈哈！验证成功啦~", Toast.LENGTH_SHORT)
						.show();
			}

			if (msg.arg1 == 1) {
				String strtemp = (String) msg.obj;
				Toast.makeText(OneKeyWifi.this, strtemp, Toast.LENGTH_SHORT)
						.show();
			}

			if (msg.arg1 == 2) {
				Toast.makeText(OneKeyWifi.this, "服务器不搭理我T_T",
						Toast.LENGTH_SHORT).show();
			}
			if (msg.arg1 == 3) {
				Toast.makeText(OneKeyWifi.this, "网络错误啦……服务器不理我了T_T",
						Toast.LENGTH_SHORT).show();
			}
			finish();
		}

	};

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

	public static String getErrorMessage(String html) {
		Document doc = null;
		doc = Jsoup.parse(html);
		Elements links = doc.getElementsByClass("msg");
		return links.text().toString();
	}

	public String IpIntToString(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}
}
