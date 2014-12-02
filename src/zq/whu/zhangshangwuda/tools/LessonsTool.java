package zq.whu.zhangshangwuda.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class LessonsTool {
	public static String thtml = "";

	public static List<Map<String, String>> sortLessonsByTime(List<Map<String, String>> list)
	{
		int size = list.size();

		//整理格式 输出time为: 1-2节，1-2周
		Map<String, String> tmap = new HashMap<String, String>();
		for (int i = 0; i < size; i++)
		{
			//获取准确上课地点
			if (list.get(i).get("place").length() < 4) 
			{
				list.get(i).put("place", list.get(i).get("other"));
			}
			String time = list.get(i).get("time");
			
			if (time.indexOf("节") == -1)
			{
				list.get(i).put("time", list.get(i).get("time") + "节，" + list.get(i).get("ste") + "周");
			}
			else 
			{
				list.get(i).put("time", list.get(i).get("time") + "，" + list.get(i).get("ste") + "周");
			}
		}
		return list;
	}
	
	private static String parseWeekDay(String day)
	{
		if (day.equals("一"))
		{
			return "1";
		}
		else if (day.equals("二"))
		{
			return "2";
		}
		else if (day.equals("三"))
		{
			return "3";
		}
		else if (day.equals("四"))
		{
			return "4";
		}
		else if (day.equals("五"))
		{
			return "5";
		}
		else if (day.equals("六"))
		{
			return "6";
		}
		else 
		{
			return "7";
		}
	}
	
	public static List<List<Map<String, String>>> getLessonsList(Context context, String html) 
	{
		//我也不想用这个奇葩的数据结构不过貌似这样最简单了 = =
		List<List<Map<String, String>>> qList = new ArrayList<List<Map<String, String>>>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<Map<String, String>> alist = new ArrayList<Map<String, String>>();
		try 
		{
			JSONArray jsonArray = new JSONArray(html);
			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String tid = jsonObject.getString("indentifier");	//id
				String college = jsonObject.getString("college");	//学院
				String name = jsonObject.getString("name");			//name
				String instructor = jsonObject.getString("instructor");		//老师
				String credits = jsonObject.getString("credits");	//学分
				String grade = jsonObject.getString("grade");		//成绩
				String retake = jsonObject.getString("retake");		//
				String type = jsonObject.getString("type");			//课程类型
				String major = jsonObject.getString("major");		//专业
				String note = jsonObject.getString("note");			//备注
				String status = jsonObject.getString("status");		//缴费状态
				JSONArray lessons = jsonObject.getJSONArray("lessons");

				//筛选出只有成绩的课程，作为绩点显示，丢弃掉既没有lessons又没有成绩的课程
				if (lessons.length() == 0 && !grade.equals("") && !grade.equals("0"))
				{
					Map<String, String> amap = new HashMap<String, String>();
					amap.put("id", tid);
					amap.put("name", name);
					amap.put("type", type);
					amap.put("credit", credits);
					amap.put("academy", college);
					amap.put("major", major);
					amap.put("teacher", instructor);
					amap.put("score", grade);
					
					alist.add(amap);
				}
				//有lessons的课程，作为课程表显示
				for (int j = 0; j < lessons.length(); j++)
				{
					JSONObject lesson = lessons.getJSONObject(j);
					Map<String, String> map = new HashMap<String, String>();
					map.put("id", tid);
					map.put("name", name);
					map.put("teacher", instructor);
					map.put("day", parseWeekDay(lesson.getString("weekday")));
					map.put("ste", lesson.getString("weekFrom") + "-" + lesson.getString("weekTo"));
					map.put("mjz", lesson.getString("repeats"));
					map.put("time", lesson.getString("classBegin") + "-" + lesson.getString("classOver"));
					map.put("place", lesson.getString("location"));
					map.put("other", note);
					
					map.put("academy", college);
					map.put("credit", credits);
					map.put("type", type);
					map.put("major", major);
					map.put("score", grade);
					
					list.add(map);
				}
			}
			qList.add(list);
			qList.add(alist);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return qList;
		
		/////////////////////////////////////////////
//		Document doc = null;
//		thtml = html;
//		if (StringUtils.isEmpty(html)) {
//			return null;
//		}
//		doc = Jsoup.parse(thtml);
//		if (doc == null) {
//			return null;
//		}
//
//		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//		Elements lessons = doc.select("tr[align=center]");
//		for (Element lesson : lessons) {
//			Elements times = lesson.select("td[width=113]");
//			int weekday = 0;
//			for (Element time : times) {
//				String tinfo = time.text();
//				if (tinfo.length() < 2) {
//					++weekday;
//					continue;
//				} else {
//					Map<String, String> map = new HashMap<String, String>();
//					Integer tid = LessonsSharedPreferencesTool
//							.getLessonsId(context);
//					++tid;
//					LessonsSharedPreferencesTool.setLessonsId(context, tid);
//					// 设置课程ID
//					map.put("id", String.valueOf(tid));
//					// 提取课程名
//					map.put("name", lesson.select("td[width=80]").text());
//					// 提取教师名
//					map.put("teacher", lesson.select("td[width=52]").text());
//					// 提取第几星期上课
//					++weekday;
//					map.put("day", Integer.toString(weekday));
//					// 提取起止周数
//					int tpos = tinfo.indexOf("周");
//					map.put("ste", tinfo.substring(0, tpos));
//					// 提取每几周
//					tinfo = tinfo.substring(tpos + 3);
		/////////////
//					map.put("mjz", tinfo.substring(0, 1));
//					// 提取第几节上课
//					tinfo = tinfo.substring(4);
//					tpos = tinfo.indexOf("节");
		////
//					map.put("time", tinfo.substring(0, tpos));
//					// 提取上课地点
//					if (tinfo.length() > tpos + 2) {
//						tinfo = tinfo.substring(tpos + 2);
//						map.put("place", tinfo.substring(0));
//					} else {
//						map.put("place", "");
//					}
//					// 提取备注信息
//					map.put("other", lesson.select("td[width=100]").text());
//					list.add(map);
//				}
//			}
//		}
//		return list;
	}

	public static List<Map<String, String>> washLessonsByWeek(
			List<Map<String, String>> list, int nowWeek) {
		int tsize = list.size();
		String tstring;
		boolean isDel;
		for (int i = 0; i < tsize; ++i) {
			isDel = false;
			Map<String, String> map = new HashMap<String, String>();
			map = list.get(i);
			tstring = map.get("time");
			// 提取上课起止周数
			String tsw = map.get("ste");
			int startweek = StringUtils
					.toInt(tsw.substring(0, tsw.indexOf("-")));
			int endweek = Integer.parseInt(tsw.substring(tsw.indexOf("-") + 1));
			if (startweek == 0 || endweek == 0)
				isDel = true;
			int mjz = StringUtils.toInt(map.get("mjz"));
			// 判断双周是否该上
			if (mjz == 2 && (nowWeek - startweek) % mjz == 1)
				isDel = true;
			// 判断是否已经停课
			if (!(startweek <= nowWeek && nowWeek <= endweek))
				isDel = true;
			if (isDel) {
				list.remove(i);
				i--;
				tsize--;
			}
		}
		return list;
	}

	public static int getWhichWeek(int fyear, int fmonth, int fday, int year,
			int month, int day) {
		Calendar a = Calendar.getInstance();
		a.set(fyear, fmonth - 1, fday, 0, 0, 0);
		a.set(Calendar.MILLISECOND, 0);
		while (a.get(Calendar.DAY_OF_WEEK) != 1)
			a.add(Calendar.DATE, +1);
		Calendar b = Calendar.getInstance();
		b.set(year, month - 1, day, 0, 0, 0);
		b.set(Calendar.MILLISECOND, 0);
		long diff = b.getTimeInMillis() - a.getTimeInMillis();
		if (diff < 0)
			return 0;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return (int) (diffDays / 7 + 1);
	}

	public static int getWeek(int fyear, int fmonth, int fday) {
		int week = 0;
		int year, month, day;
		Calendar a = Calendar.getInstance();
		year = a.get(Calendar.YEAR);
		month = a.get(Calendar.MONTH) + 1;
		day = a.get(Calendar.DATE);
		week = LessonsTool.getWhichWeek(fyear, fmonth, fday, year, month, day);
		if (week > 30)
			week = 0;
		return week;
	}

	public static int getNowWeek(Context context) {
		String TermFirstDay = LessonsSharedPreferencesTool
				.getTermFirstDay(context);
		String[] splitStr = TermFirstDay.split("-");
		int fyear = StringUtils.toInt(splitStr[0]);
		int fmonth = StringUtils.toInt(splitStr[1]);
		int fday = StringUtils.toInt(splitStr[2]);
		int nowWeek = LessonsTool.getWeek(fyear, fmonth, fday);
		return nowWeek;
	}

	// 检查字符串是否符合规范
	private static boolean checkStr(String str) {
		if (str.indexOf("-") < 0)
			return false;
		String a = str.substring(0, str.indexOf("-"));
		String b = str.substring(str.indexOf("-") + 1);
		return StringUtils.isNumeric(a) && StringUtils.isNumeric(b);
	}
}
