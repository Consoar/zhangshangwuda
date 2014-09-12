package zq.whu.zhangshangwuda.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import zq.whu.zhangshangwuda.tools.GetScoreTools;
import zq.whu.zhangshangwuda.ui.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CourseListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	List<ListItems> mListItems;
	final int VIEW_TYPE = 3;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    final int TYPE_3 = 2;
	
    //几个值都不能为null
	public CourseListAdapter(Context context,List<ListItems> mListItems){
		mContext=context;
		inflater = LayoutInflater.from(mContext);
		this.mListItems=mListItems;
	}
	
	public static List<ListItems> initItems(List<Map<String,String>> courseScoreList,
			List<Map<String,String>> courseList){
		List<ListItems> mListItems=new ArrayList<ListItems>();
		int typesCount;
		List<String> termList;
		
		termList=GetScoreTools.getTerms(courseScoreList,false);
		typesCount=termList.size();
		mListItems.add(new LableItem("当前可管理课程"));
		for(Map<String,String> course:courseList){
			mListItems.add(new ContentItem(course));
		}
		
		
		mListItems.add(new LableItem("各学期平均GPA"));
		Map<String, String> map=new HashMap<String, String>();
		map.put("averageCourseGPA","所有课程GPA:"+String.format("%.2f", GetScoreTools.getAvarageGPA(courseScoreList)));
		map.put("requiredCourseGpa","必修课程GPA:"+String.format("%.2f",GetScoreTools.getRequiredCourseGPA(courseScoreList)));
		mListItems.add(new GPAItem(map));
		
		List<String> rawTermList=GetScoreTools.getTerms(courseScoreList,true);
		for(int i=0;i<typesCount;i++){
			mListItems.add(new LableItem(termList.get(i)));
			String rawTerm=rawTermList.get(i);
			List<Map<String,String>> courseInTheTerm=GetScoreTools.getCoursesGroupedByTerm(rawTerm, courseScoreList);
			Map<String, String> map1=new HashMap<String, String>();
			map1.put("averageCourseGPA","所有课程GPA:"+String.format("%.2f", GetScoreTools.getAvarageGPA(courseInTheTerm)));
			map1.put("requiredCourseGpa","必修课程GPA:"+String.format("%.2f",GetScoreTools.getRequiredCourseGPA(courseInTheTerm)));
			mListItems.add(new GPAItem(map1));
			for(Map<String,String> course:courseInTheTerm){
				mListItems.add(new ContentItem(course));
			}
		}
		
		
		return mListItems;		
	}
	
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mListItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LabelViewHolder labelViewHolder = null;
		ContentViewHolder contentViewHolder = null;
		GPAViewHolder gpaViewHolder =null;
		int type = getItemViewType(position);
		if(convertView == null){
			switch(type){
				case TYPE_1:
					convertView=mListItems.get(position).getView(mContext, convertView, inflater);
					labelViewHolder=new LabelViewHolder();
					labelViewHolder.title=(TextView) convertView.findViewById(R.id.lessons_management_lable_item_title);
					convertView.setTag(labelViewHolder);
					break;
				case TYPE_2:
					convertView=mListItems.get(position).getView(mContext, convertView, inflater);
					contentViewHolder=new ContentViewHolder();
					contentViewHolder.id=(TextView) convertView.findViewById(R.id.lessons_management_content_item_id);
					contentViewHolder.courseName=(TextView) convertView.findViewById(R.id.lessons_management_content_item_courseName);
					contentViewHolder.score=(TextView) convertView.findViewById(R.id.lessons_management_content_item_score);
					convertView.setTag(contentViewHolder);
					break;
				case TYPE_3:
					convertView=mListItems.get(position).getView(mContext, convertView, inflater);
					gpaViewHolder=new GPAViewHolder();
					gpaViewHolder.averageCourseGPA=(TextView) convertView.findViewById(R.id.lessons_management_content_averagecoursegpa);
					gpaViewHolder.requiredCourseGpa=(TextView) convertView.findViewById(R.id.lessons_management_content_requiredcoursegpa);
					convertView.setTag(gpaViewHolder);
					break;
			}
		}else{
			switch(type){
				case TYPE_1:labelViewHolder=(LabelViewHolder) convertView.getTag();break;
				case TYPE_2:contentViewHolder=(ContentViewHolder) convertView.getTag();break;
				case TYPE_3:gpaViewHolder=(GPAViewHolder) convertView.getTag();break;
			}
		}
		switch(type){
			case TYPE_1:labelViewHolder.setView((String) mListItems.get(position).getItem());break;
			case TYPE_2:contentViewHolder.setView((Map<String, String>) mListItems.get(position).getItem());break;
			case TYPE_3:gpaViewHolder.setView((Map<String, String>) mListItems.get(position).getItem());break;
		}
		return convertView;
	}
	
	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return mListItems.get(position).isClickable();
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		ListItems mlistItem=mListItems.get(position);
		if(mlistItem instanceof LableItem){
			return TYPE_1;
		}
		else if(mlistItem instanceof ContentItem) return TYPE_2;
		return TYPE_3;
	}



	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return VIEW_TYPE;
	}
}

interface ListItems<T>{
    public int getLayout();
    public boolean isClickable();
    public View getView(Context context, View convertView, LayoutInflater inflater);
    public T getItem();
}

class LableItem implements ListItems<String>{

	private String mLabel;
	
	public LableItem(String label){
		mLabel=label;
	}
	
	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.lessons_management_lable_item;
	}

	@Override
	public boolean isClickable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getView(Context context, View convertView,
			LayoutInflater inflater) {
		// TODO Auto-generated method stub
		convertView = inflater.inflate(getLayout(), null);
		TextView title = (TextView) convertView.findViewById(R.id.lessons_management_lable_item_title);
		title.setText(mLabel);
		return convertView;	
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return mLabel.toString();
	}

	@Override
	public String getItem() {
		// TODO Auto-generated method stub
		return mLabel;
	}
}


class ContentItem implements ListItems<Map<String,String>>{
	private Map<String,String> mItem;
	String _score;
	
	public ContentItem(Map<String,String> item){
		mItem = item;
		_score=mItem.get("score");
	}
	
	@Override
	public int getLayout() {
		return R.layout.lessons_management_content_item;
	}

	@Override
	public boolean isClickable() {
		return true;
	}

	@Override
	public View getView(Context context, View convertView, LayoutInflater inflater) {
		convertView = inflater.inflate(getLayout(), null);
		TextView id = (TextView) convertView.findViewById(R.id.lessons_management_content_item_id);
		TextView courseName=(TextView) convertView.findViewById(R.id.lessons_management_content_item_courseName);
		TextView score = (TextView) convertView.findViewById(R.id.lessons_management_content_item_score);
		if(_score==null) {
			id.setText(mItem.get("id"));
			score.setText("");
		}
		//在这里设置一下颜色
		else {
			score.setText(_score);
			id.setText(mItem.get("_id"));
		}
		courseName.setText(mItem.get("name"));
		return convertView;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return mItem.get("name");
	}

	@Override
	public Map<String, String> getItem() {
		// TODO Auto-generated method stub
		return mItem;
	}
}

class GPAItem implements ListItems<Map<String,String>>{
	private Map<String,String> mItem;
	public GPAItem(Map<String, String> map) {
		// TODO Auto-generated constructor stub
		mItem=map;
	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.lessons_manangement_content_gpa;
	}

	@Override
	public boolean isClickable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getView(Context context, View convertView,
			LayoutInflater inflater) {
		// TODO Auto-generated method stub
		convertView = inflater.inflate(getLayout(), null);
		TextView averageCourseGPA = (TextView) convertView.findViewById(R.id.lessons_management_content_averagecoursegpa);
		TextView requiredCourseGpa=(TextView) convertView.findViewById(R.id.lessons_management_content_requiredcoursegpa);
		averageCourseGPA.setText(mItem.get("averageCourseGPA"));
		requiredCourseGpa.setText(mItem.get("requiredCourseGpa"));
		return convertView;
	}

	@Override
	public Map<String, String> getItem() {
		// TODO Auto-generated method stub
		return mItem;
	}
	
}



class LabelViewHolder{
	TextView title;
	public void setView(String title){
		this.title.setText(title);
	}
}

class ContentViewHolder{
	TextView id;
	TextView courseName;
	TextView score;
	public void setView(Map<String, String> mItem){
		String _score=mItem.get("score");
		if(_score==null) {
			id.setText(mItem.get("id"));
			score.setText("");
		}
		//在这里设置一下颜色
		else {
			score.setText(_score);
			id.setText(mItem.get("_id"));
		}
		courseName.setText(mItem.get("name"));
	}
}

class GPAViewHolder{
	TextView averageCourseGPA;
	TextView requiredCourseGpa;
	public void setView(Map<String, String> mItem){
		averageCourseGPA.setText(mItem.get("averageCourseGPA"));
		requiredCourseGpa.setText(mItem.get("requiredCourseGpa"));
	}
}

