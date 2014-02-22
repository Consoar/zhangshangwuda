package zq.whu.zhangshangwuda.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HtmlTool {
	// public static String downLoadZqNewsJson(String urlStr) {
	// StringBuffer sb = new StringBuffer();
	// String line = null;
	// String charset = "";
	// BufferedReader buf = null;
	// try {
	// URL url = new URL(urlStr);
	// HttpURLConnection urlConn = (HttpURLConnection) url
	// .openConnection();
	// urlConn.addRequestProperty("User-agent",
	// "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.83 Safari/537.1");
	// urlConn.setRequestMethod("GET");
	// urlConn.setConnectTimeout(8000);
	// urlConn.setReadTimeout(8000);
	// buf = new BufferedReader(new InputStreamReader(
	// urlConn.getInputStream(), "UTF-8"));
	// while ((line = buf.readLine()) != null) {
	// sb.append(line);
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// } finally {
	// try {
	// buf.close();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return sb.toString();
	// }

	public static String downLoadZqNewsJson(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpParams httpParams = httpclient.getParams();
		/* 连接超时 */
		HttpConnectionParams.setConnectionTimeout(httpParams, 4 * 1000);
		/* 请求超时 */
		HttpConnectionParams.setSoTimeout(httpParams, 4 * 1000);
		HttpGet httpget = new HttpGet(url);
		httpget.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.83 Safari/537.1");
		try {
			HttpResponse httpResponse = httpclient.execute(httpget);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(
						httpResponse.getEntity(), "UTF-8");
				return strResult;
			}
		} catch (ClientProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (Exception e) {
			return null;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
}
