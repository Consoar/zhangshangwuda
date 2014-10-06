package zq.whu.zhangshangwuda.ui;

import zq.whu.zhangshangwuda.base.BaseThemeFragmentActivityWithoutAnime;
import zq.whu.zhangshangwuda.base.PreferenceHelper;
import zq.whu.zhangshangwuda.tools.SettingSharedPreferencesTool;
import zq.whu.zhangshangwuda.ui.lessons.LessonsFragmentSupport;
import zq.whu.zhangshangwuda.ui.news.NewsContentActivity;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivityTAB extends BaseThemeFragmentActivityWithoutAnime
{
	private static final String STATE_CURRENT_FRAGMENT = "MainActivity_Tab";
	private final static String TAB_TAG_NEWS = "news";
	private final static String TAB_TAG_LESSONS = "lessons";
	private final static String TAB_TAG_WIFI = "wifi";
	private final static String TAB_TAG_RINGER = "ringer";
	private final static int CONTENT_TAB = R.id.tab_content;
	
	public static ActionBar MainActivityActionBar;
	public static FeedbackAgent agent;
	
	private String href = null;
	private boolean isShow = false;
	private String mCurrentFragmentTag;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private RadioGroup mRadioGroup;
	private RadioButton rb_news, rb_lessons, rb_wifi, rb_ringer;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		Intent intent = getIntent();
		href = intent.getStringExtra("href");
		super.onCreate(savedInstanceState);
		init();
		if (savedInstanceState != null)
		{
			mCurrentFragmentTag = savedInstanceState.getString(STATE_CURRENT_FRAGMENT);
		}
		else
		{
			initStartTab();
		}
	}
	
	private void init()
	{
		int mTheme = PreferenceHelper.getTheme(this);
		if (mTheme == R.style.MyLightTheme)
			setContentView(R.layout.tab_main);
		else
			setContentView(R.layout.tab_main_dark);
		
		MainActivityActionBar = getSupportActionBar();
		mFragmentManager = getSupportFragmentManager();
		initTabBar();
		agent = new FeedbackAgent(this);
		agent.sync();
		MobclickAgent.openActivityDurationTrack(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
	}
	
	private void initTabBar()
	{
		mRadioGroup = (RadioGroup)findViewById(R.id.rg_tab);
		rb_news = (RadioButton)findViewById(R.id.rb_news);
		rb_lessons = (RadioButton)findViewById(R.id.rb_lessons);
		rb_wifi = (RadioButton)findViewById(R.id.rb_wifi);
		rb_ringer = (RadioButton)findViewById(R.id.rb_ringer);
		
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{
				if (checkedId == rb_news.getId())
				{
					selectItem(TAB_TAG_NEWS);
					getSupportActionBar().setTitle(R.string.News);
				}
				else if (checkedId == rb_lessons.getId())
				{
					selectItem(TAB_TAG_LESSONS);
					getSupportActionBar().setTitle(R.string.Lessons);
				}
				else if (checkedId == rb_wifi.getId())
				{
					selectItem(TAB_TAG_WIFI);
					getSupportActionBar().setTitle(R.string.Wifi);
				}
				else
				{
					selectItem(TAB_TAG_RINGER);
					getSupportActionBar().setTitle(R.string.Ringer);
				}
			}
		});
	}
	
	private void selectItem(String tab) 
	{
		hideOtherFragment(tab);
		mCurrentFragmentTag = tab;
		attachFragment(CONTENT_TAB, getFragment(mCurrentFragmentTag), mCurrentFragmentTag);
		commitTransactions();
	}
	
	private void hideOtherFragment(String tab) 
	{
		if (!tab.equals(TAB_TAG_NEWS))
			hideFragment(getFragment(TAB_TAG_NEWS));
		if (!tab.equals(TAB_TAG_LESSONS))
			hideFragment(getFragment(TAB_TAG_LESSONS));
		if (!tab.equals(TAB_TAG_WIFI))
			hideFragment(getFragment(TAB_TAG_WIFI));
		if (!tab.equals(TAB_TAG_RINGER))
			hideFragment(getFragment(TAB_TAG_RINGER));
	}
	
	protected void attachFragment(int layout, Fragment f, String tag) 
	{
		if (f != null) 
		{
			if (!f.isAdded()) 
			{
				ensureTransaction();
				mFragmentTransaction.add(layout, f, tag);
				mFragmentTransaction.show(f);
			} 
			else 
			{
				mFragmentTransaction.show(f);
			}
		}
	}
	
	protected void hideFragment(Fragment f) 
	{
		if (f != null) 
		{
			ensureTransaction();
			mFragmentTransaction.hide(f);
		}
	}

	protected void commitTransactions() 
	{
		if (mFragmentTransaction != null && !mFragmentTransaction.isEmpty()) 
		{
			mFragmentTransaction.commitAllowingStateLoss();
			mFragmentTransaction = null;
		}
	}

	private Fragment getFragment(String tag) 
	{
		Fragment f = mFragmentManager.findFragmentByTag(tag);
		if (f == null) 
		{
			if (tag.equals("news")) 
			{
				f = new NewsFragmentSupport();
			}
			if (tag.equals("lessons")) 
			{
				f = new LessonsFragmentSupport();
			}
			if (tag.equals("wifi")) 
			{
				f = new WifiFragmentSupport();
			}
			if (tag.equals("ringer"))
				f = new RingerFragmentSupport();
		}
		return f;
	}
	
	protected FragmentTransaction ensureTransaction() 
	{
		if (mFragmentTransaction == null) 
		{
			mFragmentTransaction = mFragmentManager.beginTransaction();
					//.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
					//Fragment切换动画,效果不佳弃用；
			mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}
		return mFragmentTransaction;
	}
	
	private void initStartTab()
	{
		String StartTab = SettingSharedPreferencesTool.getStartTab(getApplication());
		int StartTabNo = 1;
		if (StartTab.equals("news"))
			StartTabNo = 1;
		if (StartTab.equals("lessons"))
			StartTabNo = 2;
		if (StartTab.equals("wifi"))
			StartTabNo = 3;
		if (StartTab.equals("ringer"))
			StartTabNo = 4;
		if (getIntent().getStringExtra("page") != null)
		{
			if (getIntent().getStringExtra("page").equals("ringer"))
				StartTabNo = 4;
		}
		switch (StartTabNo) 
		{
		case 1:
			rb_news.setChecked(true);
			getSupportActionBar().setTitle(R.string.News);
			break;
		case 2:
			rb_lessons.setChecked(true);
			getSupportActionBar().setTitle(R.string.Lessons);
			break;
		case 3:
			rb_wifi.setChecked(true);
			getSupportActionBar().setTitle(R.string.Wifi);
			break;
		case 4:
			rb_ringer.setChecked(true);
			getSupportActionBar().setTitle(R.string.Ringer);
			break;
		}
	}
	
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		if (mCurrentFragmentTag != null)
			selectItem(mCurrentFragmentTag);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		outState.putString(STATE_CURRENT_FRAGMENT, mCurrentFragmentTag);
	}
	
	@Override
	protected void onDestroy() 
	{
		if (BuildConfig.DEBUG) System.out.println("onDestroy");
		//DataSharedPreferencesTool.set_notifi_isShow(this, false);
		super.onDestroy();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		if (BuildConfig.DEBUG) System.out.println("onResume "+" "+isShow);

		if (href != null && !isShow)
		{
			isShow = true;
			Intent intent = new Intent(this, NewsContentActivity.class);
			intent.putExtra("href", href);
			startActivity(intent);
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) 
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) 
		{
			MainActivityExit();
		}
		return super.dispatchKeyEvent(event);
	}
	
	private void MainActivityExit() 
	{
		AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle(getResources().getString(R.string.exit_tip));
		dialog.setPositiveButton(getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		dialog.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() 
				{

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}
}
