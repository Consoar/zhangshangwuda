package zq.whu.zhangshangwuda.ui;

import net.simonvt.menudrawer.MenuDrawer;
import zq.whu.zhangshangwuda.base.BaseThemeFragmentActivityWithoutAnime;
import zq.whu.zhangshangwuda.base.PreferenceHelper;
import zq.whu.zhangshangwuda.tools.LessonsSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.tools.SettingSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.SmileyPickerUtility;
import zq.whu.zhangshangwuda.tools.StringUtils;
import zq.whu.zhangshangwuda.ui.lessons.LessonsFragmentSupport;
import zq.whu.zhangshangwuda.ui.news.NewsFragmentSupport;
import zq.whu.zhangshangwuda.ui.ringer.RingerFragmentSupport;
import zq.whu.zhangshangwuda.ui.wifi.WifiFragmentSupport;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseThemeFragmentActivityWithoutAnime {
	public static int mDrawerState = 0;
	private static final String STATE_CURRENT_FRAGMENT = "MainActivity_Tab";
	private final static String TAB_TAG_NEWS = "news";
	private final static String TAB_TAG_LESSONS = "lessons";
	private final static String TAB_TAG_WIFI = "wifi";
	private final static String TAB_TAG_RINGER = "ringer";
	private final static String TAB_TAG_SETTING = "setting";
	private final static String TAB_TAG_HELP = "help";
	private final static String TAB_TAG_FEED = "feed";
	private final static String TAB_TAG_ABOUT = "about";
	
	private String mCurrentFragmentTag;
	public MenuDrawer mMenuDrawer;
	private FeedbackAgent agent;
	public static ActionBar MainActivityActionbar;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private Fragment mContent;

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// System.out.println("onCreate");
		super.onCreate(savedInstanceState);
		init();
		if (savedInstanceState != null) {
			mCurrentFragmentTag = savedInstanceState
					.getString(STATE_CURRENT_FRAGMENT);
			selectItem(mCurrentFragmentTag);
		} else {
			initStartTab();
		}
		getWindow().setBackgroundDrawable(null);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mCurrentFragmentTag != null)
			selectItem(mCurrentFragmentTag);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// System.out.println("onSaveInstanceState");
		super.onSaveInstanceState(outState);
		outState.putString(STATE_CURRENT_FRAGMENT, mCurrentFragmentTag);
	}

	private void init() {
		MainActivityActionbar = getSupportActionBar();
		mFragmentManager = getSupportFragmentManager();
		setDrawer();
		initWeekTitle();
		agent = new FeedbackAgent(this);
		agent.sync();
		// MobclickAgent.setDebugMode(true);
		MobclickAgent.openActivityDurationTrack(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
	}

	private void selectItem(String tab) {
		// System.out.println("selectItem");
		// System.out.println("tab " + tab);
		// System.out.println("mCurrentFragmentTag " + mCurrentFragmentTag);
		hideOtherFragment(tab);
		mCurrentFragmentTag = tab;
		attachFragment(mMenuDrawer.getContentContainer().getId(),
				getFragment(mCurrentFragmentTag), mCurrentFragmentTag);
		commitTransactions();
	}

	private void hideOtherFragment(String tab) {
		if (!tab.equals(TAB_TAG_NEWS))
			hideFragment(getFragment(TAB_TAG_NEWS));
		if (!tab.equals(TAB_TAG_LESSONS))
			hideFragment(getFragment(TAB_TAG_LESSONS));
		if (!tab.equals(TAB_TAG_WIFI))
			hideFragment(getFragment(TAB_TAG_WIFI));
		if (!tab.equals(TAB_TAG_RINGER))
			hideFragment(getFragment(TAB_TAG_RINGER));
	}

	protected FragmentTransaction ensureTransaction() {
		if (mFragmentTransaction == null) {
			mFragmentTransaction = mFragmentManager.beginTransaction();
			mFragmentTransaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}
		return mFragmentTransaction;
	}

	protected void attachFragment(int layout, Fragment f, String tag) {
		if (f != null) {
			if (!f.isAdded()) {
				ensureTransaction();
				mFragmentTransaction.add(layout, f, tag);
				mFragmentTransaction.show(f);
			} else {
				mFragmentTransaction.show(f);
			}
		}
	}

	protected void hideFragment(Fragment f) {
		if (f != null) {
			ensureTransaction();
			mFragmentTransaction.hide(f);
		}
	}

	protected void commitTransactions() {
		if (mFragmentTransaction != null && !mFragmentTransaction.isEmpty()) {
			mFragmentTransaction.commitAllowingStateLoss();
			mFragmentTransaction = null;
		}
	}

	private Fragment getFragment(String tag) {
		Fragment f = mFragmentManager.findFragmentByTag(tag);
		if (f == null) {
			if (tag.equals("news")) {
				f = new NewsFragmentSupport();
			}
			if (tag.equals("lessons")) {
				f = new LessonsFragmentSupport();
			}
			if (tag.equals("wifi")) {
				f = new WifiFragmentSupport();
			}
			if (tag.equals("ringer"))
				f = new RingerFragmentSupport();
		}
		return f;
	}

	private void setDrawer() {
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY);
		mMenuDrawer.setContentView(R.layout.main);
		int mTheme = PreferenceHelper.getTheme(this);
		if (mTheme == R.style.MyLightTheme)
			mMenuDrawer.setMenuView(R.layout.left_menu_light);
		else
			mMenuDrawer.setMenuView(R.layout.left_menu);
		mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer_black);
		// Whether the previous drawable should be shown
		mMenuDrawer.setDrawerIndicatorEnabled(true);
		initLeftMenu();
	}

	private void initStartTab() {
		// TODO Auto-generated method stub
		String StartTab = SettingSharedPreferencesTool
				.getStartTab(getApplication());
		int StartTabNo = 1;
		if (StartTab.equals("news"))
			StartTabNo = 1;
		if (StartTab.equals("lessons"))
			StartTabNo = 2;
		if (StartTab.equals("wifi"))
			StartTabNo = 3;
		if (StartTab.equals("ringer"))
			StartTabNo = 5;
		switch (StartTabNo) {
		case 1:
			mMenuDrawer.setActiveView(findViewById(R.id.left_menu_news));
			selectItem(TAB_TAG_NEWS);
			getSupportActionBar().setTitle(R.string.News);
			break;
		case 2:
			mMenuDrawer.setActiveView(findViewById(R.id.left_menu_lessons));
			selectItem(TAB_TAG_LESSONS);
			getSupportActionBar().setTitle(R.string.Lessons);
			break;
		case 3:
			mMenuDrawer.setActiveView(findViewById(R.id.left_menu_wifi));
			selectItem(TAB_TAG_WIFI);
			getSupportActionBar().setTitle(R.string.Wifi);
			break;
		case 5:
			mMenuDrawer.setActiveView(findViewById(R.id.left_menu_ringer));
			selectItem(TAB_TAG_RINGER);
			getSupportActionBar().setTitle(R.string.Ringer);
			break;
		}
	}

	private void initWeekTitle() {
		// TODO Auto-generated method stub
		String TermFirstDay = LessonsSharedPreferencesTool
				.getTermFirstDay(this);
		String[] splitStr = TermFirstDay.split("-");
		int fyear = StringUtils.toInt(splitStr[0]);
		int fmonth = StringUtils.toInt(splitStr[1]);
		int fday = StringUtils.toInt(splitStr[2]);
		if (fyear != 0 && fmonth != 0 && fday != 0) {
			int nowWeek = LessonsTool.getWeek(fyear, fmonth, fday);
			String sWeekFormat = getResources().getString(R.string.Lessons_now_day); 
			String sWeekInfo=String.format(sWeekFormat,nowWeek); 
			getSupportActionBar().setSubtitle(sWeekInfo);
		}
	}

	private void initLeftMenu() {
		// TODO Auto-generated method stub
		MenuScrollView msv = (MenuScrollView) mMenuDrawer.getMenuView();
		msv.setOnScrollChangedListener(new MenuScrollView.OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				mMenuDrawer.invalidate();
			}
		});
		findViewById(R.id.left_menu_news).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mMenuDrawer.setActiveView(arg0);
						selectItem(TAB_TAG_NEWS);
						mMenuDrawer.closeMenu();
						getSupportActionBar().setTitle(R.string.News);
						int nowWeek = LessonsTool
								.getNowWeek(getApplicationContext());
						getSupportActionBar().setSubtitle(
								"第" + String.valueOf(nowWeek) + "周");
					}
				});
		findViewById(R.id.left_menu_lessons).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mMenuDrawer.setActiveView(arg0);
						selectItem(TAB_TAG_LESSONS);
						mMenuDrawer.closeMenu();
						getSupportActionBar().setTitle(R.string.Lessons);
						int week = MyApplication.getInstance().getLessonsWeek();
						getSupportActionBar().setSubtitle(
								"第" + String.valueOf(week) + "周");
					}
				});
		findViewById(R.id.left_menu_wifi).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mMenuDrawer.setActiveView(arg0);
						selectItem(TAB_TAG_WIFI);
						mMenuDrawer.closeMenu();
						getSupportActionBar().setTitle(R.string.Wifi);
						int nowWeek = LessonsTool
								.getNowWeek(getApplicationContext());
						getSupportActionBar().setSubtitle(
								"第" + String.valueOf(nowWeek) + "周");
					}
				});
		findViewById(R.id.left_menu_ringer).setOnClickListener(
				new OnClickListener()
				{
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						mMenuDrawer.setActiveView(arg0);
						selectItem(TAB_TAG_RINGER);
						mMenuDrawer.closeMenu();
						getSupportActionBar().setTitle(R.string.Ringer);
						int nowWeek = LessonsTool.getNowWeek(getApplicationContext());
						getSupportActionBar().setSubtitle("第" +  nowWeek  + "周");
					}
				});
		findViewById(R.id.left_menu_setting).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(),
								SettingActivity.class);
						startActivity(intent);
					}
				});
		findViewById(R.id.left_menu_help).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(),
								HelpActivity.class);
						startActivity(intent);
					}
				});
		findViewById(R.id.left_menu_feedback).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						agent.startFeedbackActivity();
					}
				});
		findViewById(R.id.left_menu_about).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(),
								AboutActivity.class);
						startActivity(intent);
					}
				});
	}

	// 截取按键动作
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN
				|| drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return true;
		}
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			MainActivityExit();
		}
		return super.dispatchKeyEvent(event);
	}

	private void MainActivityExit() {
		AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle(getResources().getString(R.string.exit_tip));
		dialog.setPositiveButton(getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		dialog.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}
}
