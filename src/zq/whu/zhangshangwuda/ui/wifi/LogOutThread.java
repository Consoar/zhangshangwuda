package zq.whu.zhangshangwuda.ui.wifi;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class LogOutThread implements Runnable {

	Handler LogOutHandler;
	private static String LogOutURL = "http://wlan.whu.edu.cn/portal/logOff";
	
	public LogOutThread(Handler LogOutHandler){
		this.LogOutHandler = LogOutHandler;
	}
	
	@Override
	public void run() {
		Message msg = LogOutHandler.obtainMessage();
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpLogOff = new HttpGet(LogOutURL);
		try {
			HttpResponse httpResponse = httpclient.execute(httpLogOff);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				msg.obj = (strResult);
				//System.out.println("LOGOUT strResult = "+ strResult);
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
