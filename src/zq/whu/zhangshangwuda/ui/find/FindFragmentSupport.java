package zq.whu.zhangshangwuda.ui.find;

import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.base.PreferenceHelper;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.ui.AboutActivity;
import zq.whu.zhangshangwuda.ui.HelpActivity;
import zq.whu.zhangshangwuda.ui.MainActivityTAB;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.SettingActivity;
import zq.whu.zhangshangwuda.ui.emptyclassroom.EmptyClassroomFragment;
import zq.whu.zhangshangwuda.ui.ringer.RingerFragmentSupport;
import zq.whu.zhangshangwuda.views.FindLinearLayout;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FindFragmentSupport extends BaseSherlockFragment {
	private final int THEME_LIGHT = 0;
	private final int THEME_DARK = 1;

	private View rootView;
	private FindLinearLayout mFindLinearLayout;
	private Button find_button_setting;
	private Button find_button_help;
	private Button find_button_feedback;
	private Button find_button_about;

	// ////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////增加新功能只需要修改以下内容///////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////
	/**
	 * 用TAB来区分你的新功能
	 */
	public static String[] TABS = { "RINGER", "EMPTY_CLASSROOM" };

	/**
	 * 添加else就好
	 * 
	 * @param TAB
	 * @return
	 */
	public static Fragment getFragmentByTAB(String TAB) {
		if (TAB.equals(TABS[0])) {
			return new RingerFragmentSupport();
		} else if (TAB.equals(TABS[1])) {
			return new EmptyClassroomFragment();
		}

		return null;
	}

	private void addContents() {
		addContent("  定时静音", TABS[0], R.drawable.ringer_tab_light,
				R.drawable.ringer_tab_dark);
		// Add your Fragment here.
		addContent("  空闲教室", TABS[1], R.drawable.classroom_tab_black,
				R.drawable.classroom_tab_light);
	}

	// ////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = MyApplication.getLayoutInflater().inflate(
				R.layout.find_fragment_light, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		int nowWeek = LessonsTool.getNowWeek(getActivity());
		MainActivityTAB.MainActivityActionBar.setSubtitle("第" + nowWeek + "周");
		init();
	}

	private void init() {
		find_button_setting = (Button) rootView
				.findViewById(R.id.find_button_setting);
		find_button_help = (Button) rootView
				.findViewById(R.id.find_button_help);
		find_button_feedback = (Button) rootView
				.findViewById(R.id.find_button_feedback);
		find_button_about = (Button) rootView
				.findViewById(R.id.find_button_about);

		if (getmTheme() == R.style.MyLightTheme) {
			find_button_setting.setBackgroundResource(R.drawable.ringer_button);
			find_button_help.setBackgroundResource(R.drawable.ringer_button);
			find_button_feedback
					.setBackgroundResource(R.drawable.ringer_button);
			find_button_about.setBackgroundResource(R.drawable.ringer_button);
		} else {
			find_button_setting
					.setBackgroundResource(R.drawable.ringer_dark_button);
			find_button_help
					.setBackgroundResource(R.drawable.ringer_dark_button);
			find_button_feedback
					.setBackgroundResource(R.drawable.ringer_dark_button);
			find_button_about
					.setBackgroundResource(R.drawable.ringer_dark_button);
		}

		mFindLinearLayout = (FindLinearLayout) rootView
				.findViewById(R.id.findlinearlayout);
		addContents();

		setButtonsListener();
	}

	/**
	 * 
	 * @param title
	 *            按钮上的文字
	 * @param extra
	 *            传递到另外一个activity的参数
	 * @param iconId_l
	 *            白版的iconID
	 * @param iconId_d
	 *            黑版的iconID
	 */
	private void addContent(String title, String extra, int iconId_l,
			int iconId_d) {
		if (getmTheme() == R.style.MyLightTheme)
			mFindLinearLayout.addButton(getActivity(), title, extra, iconId_l,
					THEME_LIGHT);
		else
			mFindLinearLayout.addButton(getActivity(), title, extra, iconId_d,
					THEME_DARK);
	}

	private int getmTheme() {
		int mTheme = PreferenceHelper.getTheme(getActivity());
		return mTheme;
	}

	private void setButtonsListener() {
		find_button_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), SettingActivity.class);
				startActivity(intent);
			}
		});

		find_button_help.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), HelpActivity.class);
				startActivity(intent);
			}
		});

		find_button_feedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivityTAB.agent.startFeedbackActivity();
			}
		});

		find_button_about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), AboutActivity.class);
				startActivity(intent);
			}
		});
	}
}
