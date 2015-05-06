package zq.whu.zhangshangwuda.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetScoreTools {
	private String html;
	private List<Map<String, String>> courseInfo;
	
	public GetScoreTools(String html){
		this.html=html;
		courseInfo=new ArrayList<Map<String, String>>();
	}
	
	public void parse(){
		Document htmlD = Jsoup.parse(html);
		Elements coursesE=htmlD.select("tr[null],tr[style]");
		
		Map<String, String> course;
		for(Element courseE:coursesE){
		  Elements childs=courseE.select("td");
		  course=new HashMap<String, String>();
		  course.put("name", childs.get(3).text());
		  course.put("teacher", childs.get(4).text());
		  course.put("academy", childs.get(5).text());
		  course.put("id", getId(childs.get(6).text(),childs.get(7).text()));
		  course.put("credit", childs.get(8).text());
		  course.put("type", childs.get(9).text());
		  course.put("major", childs.get(10).text());
		  course.put("score", childs.get(11).text());
		  courseInfo.add(course);
		}
	}
	
	public String getId(String year,String term){
		  String id = null;
		  if(term.equals("上")) id=year+"1";
		  else id=year+"2";
		  return id;
	  }

	public List<Map<String, String>> getList(){
		return courseInfo;
	}
	
	public static float getGPA(String scoreS){
		float gpa;
		float score=Float.parseFloat(scoreS);
		if(score<=100&&score>=90)gpa=(float) 4.0;
		else if(score>=85)gpa=(float)3.7;
		else if(score>=82)gpa=(float)3.3;
		else if(score>=78)gpa=(float)3.0;
		else if(score>=75)gpa=(float)2.7;
		else if(score>=72)gpa=(float)2.3;
		else if(score>=68)gpa=(float)2.0;
		else if(score>=64)gpa=(float)1.5;
		else if(score>=60)gpa=(float)1.0;
		else gpa=0;		
		return gpa;
	}
	
	public static float getAvarageGPA(List<Map<String, String>> courseInfo){
		float sum=0;
		float totalCredit=0;
		Map<String,String> courses=new HashMap<String,String>();
		for(Map<String, String> course:courseInfo){
			courses.put(course.get("name"),course.get("credit")+"-"+course.get("score"));
		}
		Iterator<Entry<String, String>> iter = courses.entrySet().iterator();
		while (iter.hasNext()){
			Entry<String, String> entry = (Entry<String, String>) iter.next();
			String[] val = entry.getValue().split("-");
			float credit =Float.parseFloat(val[0]);
			totalCredit=totalCredit+credit;
			sum=sum+getGPA(val[1])*credit;			
		}
		return sum/totalCredit;
	}
	
	public static float getRequiredCourseGPA(List<Map<String, String>> courseInfo){
		float sum=0;
		float totalCredit=0;
		Map<String,String> courses=new HashMap<String,String>();
		for(Map<String, String> course:courseInfo){
			if((course.get("type").equals("专业必修")&&!course.get("major").equals("辅修"))||course.get("type").equals("公共必修")){
				courses.put(course.get("name"),course.get("credit")+"-"+course.get("score"));
			}
		}
		Iterator<Entry<String, String>> iter = courses.entrySet().iterator();
		while (iter.hasNext()){
			Entry<String, String> entry = (Entry<String, String>) iter.next();
			String[] val = entry.getValue().split("-");
			float credit =Float.parseFloat(val[0]);
			totalCredit=totalCredit+credit;
			sum=sum+getGPA(val[1])*credit;					
		}
		return sum/totalCredit;
	}
	
	public static List<String> getTerms(List<Map<String,String>> courseInfo,Boolean rawTerm){
		SortedSet<String> termSet=new TreeSet<String>(new TermComparator());
		List<String> termList=new ArrayList<String>();
		for(Map<String,String> course:courseInfo){
			termSet.add(course.get("id").substring(0,5));
		}
		if(rawTerm) {
			for(String term:termSet){
				termList.add(term);
			}
			return termList;
		}
		else{
			for(String term:termSet){
				if(term.charAt(4)=='1') termList.add(term.substring(0,4)+"-"+(Integer.parseInt(term.substring(0,4))+1)+"学年上学期");
				else termList.add(term.substring(0,4)+"-"+(Integer.parseInt(term.substring(0,4))+1)+"学年下学期");
			}
			return termList;
		}
	}
	
	public static List<Map<String,String>> getCoursesGroupedByTerm(String term,List<Map<String,String>> courseInfo){
		List<Map<String,String>> courseList=new ArrayList<Map<String,String>>();		
		for(Map<String,String> course:courseInfo){
			if(course.get("id").substring(0,5).equals(term)) courseList.add(course);
		}
		return courseList;
	}
}

class TermComparator implements Comparator<String>{

	@Override
	public int compare(String arg0, String arg1) {
		// TODO Auto-generated method stub
		if(Integer.parseInt(arg0)>Integer.parseInt(arg1)) return -1;
		else if(Integer.parseInt(arg0)==Integer.parseInt(arg1)) return 0;
		else return 1;
	}
}
