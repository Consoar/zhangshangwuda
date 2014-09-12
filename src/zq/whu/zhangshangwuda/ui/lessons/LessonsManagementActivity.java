package zq.whu.zhangshangwuda.ui.lessons;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.MenuItem;

import zq.whu.zhangshangwuda.adapter.CourseListAdapter;
import zq.whu.zhangshangwuda.base.BaseThemeSwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.db.CourseDataUtil;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.ui.R;

public class LessonsManagementActivity extends BaseThemeSwipeBackSherlockActivity {
	private ListView lessonsListView;
	private List<Map<String,String>> courseScoreList,courseList,allCourseList;
	private CourseListAdapter courseListAdapter;
	@SuppressWarnings("rawtypes")
	private List mListItems;
	private boolean flag;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().setBackgroundDrawable(null);
		setContentView(R.layout.lessons_management_listview);
		flag=false;
		findViews();
		refreshData();
		init();
		setListener();		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(flag=true){
			refreshData();
			mListItems.clear();
			mListItems.addAll(CourseListAdapter.initItems(courseScoreList, courseList));
			courseListAdapter.notifyDataSetChanged();
			setListener();
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		flag=true;
	}
	
	private void findViews(){
		lessonsListView=(ListView) findViewById(R.id.lessons_management_listview);
	}
	
	@SuppressWarnings("unchecked")
	private void init(){
		mListItems=CourseListAdapter.initItems(courseScoreList, courseList);
		courseListAdapter=new CourseListAdapter(this,mListItems);
		lessonsListView.setAdapter(courseListAdapter);
	}
	
	private void refreshData(){
		CourseDataUtil courseDataUtil=new CourseDataUtil(this);
		courseScoreList=courseDataUtil.getAllCourseScoreData();
        courseDataUtil.close();
        courseDataUtil=null;
        courseList=LessonsDb.getInstance(this).getLocalLessonsListGroupByName();
        allCourseList=LessonsDb.getInstance(this).getLocalLessonsList();        
	}
	
	private void setListener(){
		lessonsListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String Sscore=(String) ((TextView)arg1.findViewById(R.id.lessons_management_content_item_score)).getText();
				String Sid=(String) ((TextView)arg1.findViewById(R.id.lessons_management_content_item_id)).getText();
				if(!Sscore.equals("")){
					Intent intent=new Intent();
					intent.setClass(LessonsManagementActivity.this, LessonsInfoActivity.class);
					intent.putExtra("_id", Sid);
					startActivity(intent);
				}else{
					final String[] ids=getIds(Sid);	
					AlertDialog.Builder dialog = new AlertDialog.Builder(LessonsManagementActivity.this);
					dialog.setTitle(getResources().getString(R.string.Lessons_choose_course));
					dialog.setItems(getCoursesTime(ids),new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getApplicationContext(),
									LessonsAddActivity.class);
							intent.putExtra("id", ids[arg1]);
							startActivity(intent);
						}
					});
					dialog.show();
				}
			}
		});
	}
	
	private String[] getIds(String id){
		String[] ids=id.substring(0, id.length()).split(";");
		return ids;
	}
	
	private String[] getCoursesTime(String[] ids){
		String[] coursesTime=new String[ids.length];
		for(int i=0;i<ids.length;i++){
			for(Map<String,String> course:allCourseList){
				if(course.get("id").equals(ids[i])){
					coursesTime[i]=getWeekDay(course.get("day"))+","+course.get("time")+"节";
				}
			}
		}		
		return coursesTime;
	}
	
	
	private String getWeekDay(String weekDayNumber){
		if(weekDayNumber.equals("1")) return "周一";
		else if(weekDayNumber.equals("2")) return "周二";
		else if(weekDayNumber.equals("3")) return "周三";
		else if(weekDayNumber.equals("4")) return "周四";
		else if(weekDayNumber.equals("5")) return "周五";
		else if(weekDayNumber.equals("6")) return "周六";
		else return "周日";
	}
}
