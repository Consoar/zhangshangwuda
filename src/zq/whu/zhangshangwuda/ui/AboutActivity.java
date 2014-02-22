package zq.whu.zhangshangwuda.ui;

import zq.whu.zhangshangwuda.base.BaseThemeSherlockActivity;

import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;

public class AboutActivity extends BaseThemeSherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
