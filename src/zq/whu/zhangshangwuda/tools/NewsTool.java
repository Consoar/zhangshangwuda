package zq.whu.zhangshangwuda.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;

public class NewsTool {
	private static boolean flag = true;

	/**
	 * 获取新闻类别列表
	 * 
	 * @return
	 * @throws JSONException
	 */
	public static List<Map<String, String>> getNewsCategory(String url)
			throws JSONException {
		String jsonData = HtmlTool.downLoadZqNewsJson(url);
		// System.out.println(jsonData);
		if (StringUtils.isEmpty(jsonData))
			return null;
		FileCache.getInstance().setUrlCache(jsonData, url);
		JSONArray arr = null;
		arr = new JSONArray(jsonData);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject temp = (JSONObject) arr.get(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("category", temp.getString("category").trim());
			map.put("id", temp.getString("id").trim());
			list.add(map);
		}
		return list;
	}

	/**
	 * 从缓存获取新闻类别列表
	 * 
	 * @return
	 * @throws JSONException
	 */
	public static List<Map<String, String>> getNewsCategoryFromCache(String url)
			throws JSONException {
		String jsonData = FileCache.getInstance().getUrlCache(url);
		// System.out.println(jsonData);
		if (StringUtils.isEmpty(jsonData))
			return null;
		FileCache.getInstance().setUrlCache(jsonData, url);
		JSONArray arr = null;
		arr = new JSONArray(jsonData);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject temp = (JSONObject) arr.get(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("category", temp.getString("category").trim());
			map.put("id", temp.getString("id").trim());
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 获取自强新闻列表
	 * 
	 * @return
	 * @throws JSONException
	 */
	public static List<Map<String, String>> getNewsList(String url)
			throws JSONException {
		String jsonData = HtmlTool.downLoadZqNewsJson(url);
		// System.out.println(jsonData);
		if (StringUtils.isEmpty(jsonData))
			return null;
		if (url.substring(url.indexOf("page=")).equals("page=1"))
			FileCache.getInstance().setUrlCache(jsonData, url);
		JSONArray arr = null;
		arr = new JSONArray(jsonData);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject temp = (JSONObject) arr.get(i);
			// System.out.println(temp.get("title"));
			// System.out.println(temp.get("category"));
			if (temp.get("category").equals("图说"))
				continue;
			Map<String, String> map = new HashMap<String, String>();
			map.put("title", temp.getString("title").trim());
			map.put("time", temp.getString("published"));
			map.put("image", temp.getString("image"));
			map.put("category", temp.getString("category"));
			map.put("href",
					"http://115.29.17.73:8001/news/show/?id="
							+ temp.getString("id"));
			list.add(map);
		}
		return list;
	}

	/**
	 * 从缓存获取自强新闻列表
	 * 
	 * @return list
	 * @throws JSONException
	 */
	public static List<Map<String, String>> getNewsListFromCache(String url)
			throws JSONException {
		String jsonData = FileCache.getInstance().getUrlCache(url);
		if (StringUtils.isEmpty(jsonData))
			return null;
		// System.out.println(jsonData);
		JSONArray arr = null;
		arr = new JSONArray(jsonData);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject temp = (JSONObject) arr.get(i);
			// System.out.println(temp.get("title"));
			// System.out.println(temp.get("category"));
			if (temp.get("category").equals("图说"))
				continue;
			Map<String, String> map = new HashMap<String, String>();
			map.put("title", temp.getString("title").trim());
			map.put("time", temp.getString("published"));
			map.put("image", temp.getString("image"));
			map.put("category", temp.getString("category"));
			map.put("href",
					"http://115.29.17.73:8001/news/show/?id="
							+ temp.getString("id"));
			list.add(map);
		}

		// // System.out.println(list);
		//
		// for (int i = 0; i <80; i++) {
		// Map<String, String> map = new HashMap<String, String>();
		// map.put("title", "title");
		// map.put("time", "time");
		// map.put("hits", "hits");
		// map.put("href",
		// "http://news.ziqiang.net/api/article/?id="
		// + "href");
		// list.add(map);
		// }
		return list;
	}

	/**
	 * 获取自强新闻完整标题+正文代码
	 * 
	 * @param url
	 * @return map
	 * @throws JSONException
	 */
	public static Map<String, String> getNewsContent(String url)
			throws JSONException {
		String jsonData = HtmlTool.downLoadZqNewsJson(url);
		if (StringUtils.isEmpty(jsonData))
			return null;
		JSONArray arr = null;
		arr = new JSONArray(jsonData);
		// System.out.println(jsonData);
		JSONObject temp = (JSONObject) arr.get(0);
		Map<String, String> map = new HashMap<String, String>();
		if(temp.getString("category").contains("广告"))
			map.put("category", "推广");
		else
			map.put("category", temp.getString("category").trim());
		map.put("content", temp.getString("content"));
		// System.out.println(temp.getString("content"));
		map.put("author", temp.getString("author"));
		map.put("tag", temp.getString("tag"));
		map.put("image", temp.getString("image"));
		map.put("time", temp.getString("published"));
		map.put("title", temp.getString("title"));
		map.put("id", temp.getString("id"));
		map.put("href", temp.getString("share_url"));
		return map;
	}

	/**
	 * 获取自强新闻首页头条列表
	 * 
	 * @param urlString
	 * @return list
	 * @throws JSONException
	 */
	public static List<Map<String, String>> getPicList(String url)
			throws JSONException {
		String jsonData = HtmlTool.downLoadZqNewsJson(url);
		// System.out.println(jsonData);
		if (StringUtils.isEmpty(jsonData))
			return null;
		FileCache.getInstance().setUrlCache(jsonData, url);
		JSONArray arr = null;
		arr = new JSONArray(jsonData);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject temp = (JSONObject) arr.get(i);
			// System.out.println(temp.get("title"));
			// System.out.println(temp.get("category"));
			Map<String, String> map = new HashMap<String, String>();
			// System.out.println(temp);
			if(temp.getString("category").contains("广告"))
				map.put("title", "【推广】" + temp.getString("title").trim());
			else
				map.put("title", temp.getString("title").trim());
			if (!StringUtils.isEmpty(temp.getString("image")))
				map.put("image", DisplayTool.getMyImageUrl(temp.getString("image")));
			else
				map.put("image", null);
			map.put("href",
					"http://115.29.17.73:8001/news/show/?id="
							+ temp.getString("id"));
			map.put("time", temp.getString("published"));
			list.add(map);
		}
		return list;
	}

	/**
	 * 从缓存获取自强新闻首页头条列表
	 * 
	 * @param urlString
	 * @return list
	 * @throws JSONException
	 */
	public static List<Map<String, String>> getPicListFromCache(String url)
			throws JSONException {
		String jsonData = FileCache.getInstance().getUrlCache(url);
		// System.out.println(jsonData);
		if (StringUtils.isEmpty(jsonData))
			return null;
		JSONArray arr = null;
		arr = new JSONArray(jsonData);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject temp = (JSONObject) arr.get(i);
			// System.out.println(temp.get("title"));
			// System.out.println(temp.get("category"));
			Map<String, String> map = new HashMap<String, String>();
			if(temp.getString("category").contains("广告"))
				map.put("title", "【推广】" + temp.getString("title").trim());
			else
				map.put("title", temp.getString("title").trim());
			if (!StringUtils.isEmpty(temp.getString("image")))
				map.put("image", DisplayTool.getMyImageUrl(temp.getString("image")));
			else
				map.put("image", null);
			map.put("href",
					"http://115.29.17.73:8001/news/show/?id="
							+ temp.getString("id"));
			map.put("time", temp.getString("published"));
			list.add(map);
		}
		return list;
	}

	/**
	 * 获得新闻类型以便于在首页推荐处显示
	 * 
	 * @param list, String
	 * @return list
	 * @throws JSONException
	 * */
	public static List<Map<String, String>> getNewstype(
			List<Map<String, String>> list, String NonType) {
		if (list == null)
			return null;
		int size = list.size();
		String ttitle, tstring;
		List<Map<String, String>> ans = new ArrayList<Map<String, String>>();
		for (int i = 0; i < size; ++i) {
			if (list.get(i).get("category") != null) {
				list.get(i).put("type", list.get(i).get("category"));
				ans.add(list.get(i));
				continue;
			}
			ttitle = list.get(i).get("title").trim();
			tstring = ttitle.substring(0, 4);
			if (tstring.indexOf("】") < 0) {
				list.get(i).put("type", NonType);
				ans.add(list.get(i));
				continue;
			}
			String title = list.get(i).get("title");
			list.get(i).put("title", title.substring(tstring.indexOf("】") + 1));
			list.get(i).put("type", tstring.substring(1, tstring.indexOf("】")));
			ans.add(list.get(i));
		}
		return ans;
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + "...";
		else
			return s;
	}

	public static String washHtml(String html) {
		Document document = Jsoup.parse(html);
		document.select("br").append("\\n");
		document.select("p").prepend("\\n\\n");
		return document.text().replaceAll("\\\\n", "\n");
	}

	public static String changeStr(String s) {
		int errorSpaceValue = 160;
		int rightSpaceValue = 32;
		char[] c = s.toCharArray();
		int charToInt = 0;
		for (int i = 0; i < c.length; i++) {
			charToInt = c[i];
			// 查看后发现?对应的char值为160
			// 而正常的空格对应的值应该为32则可以进行转换下在输出
			if (charToInt == errorSpaceValue) {
				c[i] = (char) rightSpaceValue;
			}
		}
		return new String(c);
	}
}
