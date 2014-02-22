package zq.whu.zhangshangwuda.ui.lessons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import zq.whu.zhangshangwuda.base.BaseThemeSwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.ui.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LessonsDayActivity extends BaseThemeSwipeBackSherlockActivity {

	private static final int MENU_REFRESH = Menu.FIRST + 1;
	private int tday;
	private String time;
	private Button topgoback;
	private ListView lessonsListView;
	private TextView toolbarheader;
	private List<Map<String, String>> lessonsList;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().setBackgroundDrawable(null);
		setContentView(R.layout.lessons_day_all);
		Intent intent = getIntent();
		tday = intent.getIntExtra("day", 1);
		time = intent.getStringExtra("last");
		init();
		findViews();
		setListener();
		refreshData();
		showLessonsList();
	}

	private void init() {
		lessonsList = new ArrayList<Map<String, String>>();
	}

	private void findViews() {
		lessonsListView = (ListView) findViewById(R.id.lessons_day_all_ListView);
	}

	private void setListener() {
		lessonsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String Sid;
				Sid = (String) ((TextView) view
						.findViewById(R.id.lessons_day_all_ListView_Item_Id))
						.getText();
				Intent intent = new Intent(getApplicationContext(),
						LessonsAddActivity.class);
				intent.putExtra("id", Sid);
				startActivity(intent);
				finish();
			}
		});
	}

	// 定义Handler对象,接收消息
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			showLessonsList();
		}
	};

	private void refreshData() {
		// lessonsList =
		// LessonsDb.getInstance(this).getLessonsByDay(Integer.toString(tday));

		lessonsList = LessonsDb.getInstance(this).getLessonsByDay(
				Integer.toString(tday));
		lessonsList = LessonsTool.sortLessonsByTime(lessonsList);
		if (time != null && time.length() > 0) {
			for (Iterator it = lessonsList.iterator(); it.hasNext();) {
				Map<String, String> str = (Map<String, String>) it.next();
				if (!str.get("time").contains(time)) {
					it.remove();
				}
			}
		}
	}

	/**
	 * 显示课程列表
	 * 
	 * @param list
	 */
	private void showLessonsList() {
		lessonsListView
				.setAdapter(new SimpleAdapter(
						this,
						lessonsList,
						R.layout.lessons_day_all_item,
						new String[] { "id", "name", "time", "place", "teacher" },
						new int[] {
								R.id.lessons_day_all_ListView_Item_Id,
								R.id.lessons_day_all_ListView_Item_CourseName_TextView,
								R.id.lessons_day_all_ListView_Item_Time_TextView,
								R.id.lessons_day_all_ListView_Item_Place_TextView,
								R.id.lessons_day_all_ListView_Item_TeacherName_TextView }));
	}

}