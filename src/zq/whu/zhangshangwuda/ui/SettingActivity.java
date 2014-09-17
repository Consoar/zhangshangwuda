package zq.whu.zhangshangwuda.ui;

import zq.whu.zhangshangwuda.base.BaseThemeSherlockPreferenceActivity;
import zq.whu.zhangshangwuda.base.PreferenceHelper;
import zq.whu.zhangshangwuda.tools.LessonsSharedPreferencesTool;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class SettingActivity extends BaseThemeSherlockPreferenceActivity
		implements Preference.OnPreferenceChangeListener {
	private EditTextPreference number_editPreference;
	private ListPreference listPreferencePicsizes;
	private ListPreference start_tab_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		addPreferencesFromResource(R.xml.settings);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		number_editPreference = (EditTextPreference) findPreference("lessons_TermFirstDay");
		number_editPreference.setText(LessonsSharedPreferencesTool
				.getTermFirstDay(getApplicationContext()));
		number_editPreference.setOnPreferenceChangeListener(this);
		listPreferencePicsizes = (ListPreference) findPreference("common_theme");
		listPreferencePicsizes
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (String.valueOf(newValue).equals("light")) {
							PreferenceHelper.setTheme(getApplicationContext(),
									R.style.MyLightTheme);
							listPreferencePicsizes
									.setSummary(R.string.theme_tabs_light);
						} else {
							PreferenceHelper.setTheme(getApplicationContext(),
									R.style.MyBlackTheme);
							listPreferencePicsizes
									.setSummary(R.string.theme_tabs_black);
						}
						// Toast.makeText(getApplicationContext(),
						// "更换主题请重启本软件~", Toast.LENGTH_SHORT).show();
						return true;
					}
				});
		start_tab_list = (ListPreference) findPreference("start_tab");
		start_tab_list
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (String.valueOf(newValue).equals("news")) {
							start_tab_list.setSummary(R.string.start_tabs_news);

						} else if (String.valueOf(newValue).equals("lessons")) {
							start_tab_list
									.setSummary(R.string.start_tabs_lessons);

						} else if (String.valueOf(newValue).equals("wifi")) {
							start_tab_list.setSummary(R.string.start_tabs_wifi);

						} else if (String.valueOf(newValue).equals("ringer")) {
							start_tab_list
									.setSummary(R.string.start_tabs_ringer);

						}
						return true;
					}
				});

		final PreferenceScreen updata = (PreferenceScreen) findPreference("common_update");
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int arg0, UpdateResponse arg1) {
				switch (arg0) {
				case 0: // has update
					// UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
					updata.setSummary("有新版本升级");
					break;
				case 1: // has no update
					updata.setSummary("当前是最新版");
					break;
				case 2: // none wifi

					break;
				case 3: // time out

					break;
				}

			}
		});
		updata.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				UmengUpdateAgent.setUpdateAutoPopup(true);
				UmengUpdateAgent.forceUpdate(SettingActivity.this);
				return false;
			}
		});
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

	public boolean onPreferenceChange(Preference preference, Object objValue) {
		// System.out.println(String.valueOf(objValue));
		LessonsSharedPreferencesTool.setTermFirstDay(getApplicationContext(),
				String.valueOf(objValue));
		return true; // 保存更新后的值
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
