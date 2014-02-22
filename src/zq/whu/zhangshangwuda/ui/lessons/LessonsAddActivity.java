package zq.whu.zhangshangwuda.ui.lessons;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zq.whu.zhangshangwuda.base.BaseThemeSwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.base.SwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.entity.Lessons;
import zq.whu.zhangshangwuda.tools.LessonsSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.StringUtils;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class LessonsAddActivity extends BaseThemeSwipeBackSherlockActivity {
	private String id, name, day, ste, mjz, time, place, teacher, other;
	private EditText nameEditText, dayEditText, steStartEditText,
			steEndEditText, timeStartEditText, timeEndEditText, placeEditText,
			teacherEditText, otherEditText;
	private CheckBox mjzCheckBox;
	private Button saveButton, deleteButton;

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
		setContentView(R.layout.lessons_add);
		findViews();
		setListener();
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		if (id != null)
			showlesson();
		if (id == null) {
			deleteButton.setVisibility(View.GONE);
		}
	}

	private boolean checkStr(String str) {
		if (str.indexOf("-") < 0)
			return false;
		String a = str.substring(0, str.indexOf("-"));
		String b = str.substring(str.indexOf("-") + 1);
		return StringUtils.isNumeric(a) && StringUtils.isNumeric(b);
	}

	private void showlesson() {
		Map<String, String> map = new HashMap<String, String>();
		map = LessonsDb.getInstance(getApplicationContext()).getLessonsById(id);
		if (!map.get("name").equals(""))
			nameEditText.setText(map.get("name"));
		if (!map.get("day").equals(""))
			dayEditText.setText(map.get("day"));
		if (!map.get("ste").equals("")) {
			String[] tstring = (map.get("ste")).split("-");
			steStartEditText.setText(tstring[0]);
			steEndEditText.setText(tstring[1]);
		}
		if (!map.get("time").equals("")) {
			String[] tstring = (map.get("time")).split("-");
			timeStartEditText.setText(tstring[0]);
			timeEndEditText.setText(tstring[1]);
		}
		if (!map.get("place").equals(""))
			placeEditText.setText(map.get("place"));
		if (!map.get("teacher").equals(""))
			teacherEditText.setText(map.get("teacher"));
		if (!map.get("other").equals(""))
			otherEditText.setText(map.get("other"));
		if (map.get("mjz").equals("2"))
			mjzCheckBox.setChecked(true);
	}

	private void setListener() {
		saveButton.setOnClickListener(new saveButtonListener());
		deleteButton.setOnClickListener(new deleteButtonListener());
	}

	class saveButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			day = dayEditText.getText().toString();
			ste = steStartEditText.getText().toString() + '-'
					+ steEndEditText.getText().toString();
			time = timeStartEditText.getText().toString() + '-'
					+ timeEndEditText.getText().toString();
			if (checkStr(ste) && checkStr(time) && StringUtils.isNumeric(day))
				saveData();
			else
				ToastUtil.showToast(LessonsAddActivity.this, "格式输入不正确呢……");
		}
	}

	public void saveData() {
		LessonsSharedPreferencesTool.setLessonsHave(LessonsAddActivity.this,
				true);
		Lessons tlesson = new Lessons();
		if (id == null) {
			Integer tid = LessonsSharedPreferencesTool
					.getLessonsId(getApplicationContext());
			++tid;
			LessonsSharedPreferencesTool.setLessonsId(getApplicationContext(),
					tid);
			id = String.valueOf(tid);
		}
		name = nameEditText.getText().toString();
		day = dayEditText.getText().toString();
		ste = steStartEditText.getText().toString() + '-'
				+ steEndEditText.getText().toString();
		time = timeStartEditText.getText().toString() + '-'
				+ timeEndEditText.getText().toString();
		place = placeEditText.getText().toString();
		teacher = teacherEditText.getText().toString();
		other = otherEditText.getText().toString();
		if (mjzCheckBox.isChecked())
			mjz = "2";
		else
			mjz = "1";
		tlesson.setId(id);
		tlesson.setName(name);
		tlesson.setDay(day);
		tlesson.setSte(ste);
		tlesson.setMjz(mjz);
		tlesson.setTime(time);
		tlesson.setPlace(place);
		tlesson.setTeacher(teacher);
		tlesson.setOther(other);
		if (LessonsDb.getInstance(getApplicationContext()).getLessonsById(id) != null)
			// 更新数据
			LessonsDb.getInstance(getApplicationContext()).update(tlesson);
		else
			LessonsDb.getInstance(getApplicationContext()).insert(tlesson);

		Intent intent = new Intent("zq.whu.zhangshangwuda.lessonsShow");
		intent.putExtra("Type", "Add");
		sendBroadcast(intent);
		finish();
	}

	class deleteButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			LessonsDb.getInstance(getApplicationContext()).deleteById(id);
			Intent intent = new Intent("zq.whu.zhangshangwuda.lessonsShow");
			intent.putExtra("Type", "Add");
			sendBroadcast(intent);
			finish();
		}
	}

	private void findViews() {
		nameEditText = (EditText) findViewById(R.id.lessons_add_name_EditText);
		dayEditText = (EditText) findViewById(R.id.lessons_add_day_EditText);
		steStartEditText = (EditText) findViewById(R.id.lessons_add_ste_start_EditText);
		steEndEditText = (EditText) findViewById(R.id.lessons_add_ste_end_EditText);
		timeStartEditText = (EditText) findViewById(R.id.lessons_add_time_start_EditText);
		timeEndEditText = (EditText) findViewById(R.id.lessons_add_time_end_EditText);
		placeEditText = (EditText) findViewById(R.id.lessons_add_place_EditText);
		teacherEditText = (EditText) findViewById(R.id.lessons_add_teacher_EditText);
		otherEditText = (EditText) findViewById(R.id.lessons_add_other_EditText);
		mjzCheckBox = (CheckBox) findViewById(R.id.lessons_add_mjz_CheckBox);
		saveButton = (Button) findViewById(R.id.lessons_add_save_Button);
		deleteButton = (Button) findViewById(R.id.lessons_add_delete_Button);
	}

}
