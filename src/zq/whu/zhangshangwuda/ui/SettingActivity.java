package zq.whu.zhangshangwuda.ui;

import zq.whu.zhangshangwuda.base.BaseThemeSherlockPreferenceActivity;
import zq.whu.zhangshangwuda.base.PreferenceHelper;
import zq.whu.zhangshangwuda.tools.BosCrypto;
import zq.whu.zhangshangwuda.tools.LessonsSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.SharedPreferencesUtils;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

import com.actionbarsherlock.view.MenuItem;
import com.lidroid.xutils.view.annotation.event.OnPreferenceChange;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class SettingActivity extends BaseThemeSherlockPreferenceActivity
		implements Preference.OnPreferenceChangeListener {
	private EditTextPreference number_editPreference;
	private ListPreference listPreferencePicsizes;
	private ListPreference start_tab_list;

	// 资讯推送开关
	private CheckBoxPreference cbpReceiveInform;

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
		cbpReceiveInform = (CheckBoxPreference) findPreference("isReceiveInform");
		cbpReceiveInform.setOnPreferenceChangeListener(this);
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

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals("lessons_TermFirstDay")) {
			LessonsSharedPreferencesTool.setTermFirstDay(
					getApplicationContext(), String.valueOf(newValue));
		} else if (preference.getKey().equals("isReceiveInform")) {

			if (Boolean.parseBoolean(newValue.toString())) {
				String studntNum = SharedPreferencesUtils.getString(this,
						"User_Data", "lessons_Account", "");
				if (!TextUtils.isEmpty(studntNum)) {
					try {
						studntNum = BosCrypto.decrypt(BosCrypto.Excalibur,
								studntNum);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 第二个参数acount不能为null
					XGPushManager.registerPush(getApplicationContext(),
							studntNum);
				} else {
					XGPushManager.registerPush(getApplicationContext());
				}
			} else {
				XGPushManager.unregisterPush(getApplicationContext());
			}
		}
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
