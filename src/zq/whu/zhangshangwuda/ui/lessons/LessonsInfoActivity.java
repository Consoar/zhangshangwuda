package zq.whu.zhangshangwuda.ui.lessons;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;

import zq.whu.zhangshangwuda.base.BaseThemeSwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.db.CourseDataUtil;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.ui.R;

public class LessonsInfoActivity extends BaseThemeSwipeBackSherlockActivity {
	private String _id;
	private TextView nameTextView,teacherTextView,creditTextView
		,typeTextView,academyTextView,majorTextView,scoreTextView;
	Map<String,String> map;
	
	
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
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().setBackgroundDrawable(null);
		Intent intent = getIntent();
		_id = intent.getStringExtra("_id");
		setContentView(R.layout.lessons_info);
		findViews();
		refreshData();
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		nameTextView.setText(map.get("name"));
		teacherTextView.setText(map.get("teacher"));
		creditTextView.setText(map.get("credit"));
		typeTextView.setText(map.get("type"));
		academyTextView.setText(map.get("academy"));
		majorTextView.setText(map.get("major"));
		scoreTextView.setText(map.get("score"));
	}

	private void refreshData() {
		// TODO Auto-generated method stub
		CourseDataUtil courseDataUtil=new CourseDataUtil(this);
		map=courseDataUtil.getLessonsBy_Id(_id);
        courseDataUtil.close();
        courseDataUtil=null;
	}

	private void findViews() {
		// TODO Auto-generated method stub
		nameTextView = (TextView) findViewById(R.id.lessons_info_name);
		teacherTextView= (TextView) findViewById(R.id.lessons_info_teacher);
		creditTextView= (TextView) findViewById(R.id.lessons_info_credit);
		typeTextView= (TextView) findViewById(R.id.lessons_info_type);
		academyTextView= (TextView) findViewById(R.id.lessons_info_academy);
		majorTextView= (TextView) findViewById(R.id.lessons_info_major);
		scoreTextView= (TextView) findViewById(R.id.lessons_info_score);
	}
	
}
