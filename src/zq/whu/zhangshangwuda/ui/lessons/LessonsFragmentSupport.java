package zq.whu.zhangshangwuda.ui.lessons;

import java.lang.reflect.Field;

import zq.whu.zhangshangwuda.adapter.LessonsFragmentPagerAdapter;
import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.base.UmengSherlockFragmentActivity;
import zq.whu.zhangshangwuda.tools.LessonsSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.tools.SettingSharedPreferencesTool;
import zq.whu.zhangshangwuda.ui.MainActivity;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.views.LessonsViewPager;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import zq.whu.zhangshangwuda.views.viewpager.JazzyViewPager.TransitionEffect;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class LessonsFragmentSupport extends BaseSherlockFragment {
	private static final String mPageName = "LessonsFragment";
	private static int ACTIONBAR_MODEL = 0;
	private static final int MENU_LOGOFF = Menu.FIRST;
	private static final int MENU_ADD = Menu.FIRST + 1;
	private static final int MENU_TODAY = Menu.FIRST + 2;
	private static final int MENU_COURSES=Menu.FIRST+3;
	private static MyApplication application;
	private boolean lessonsHave;
	private String sWeekFormat;
	private LessonsViewPager viewPager;
	private LessonsFragmentPagerAdapter lessonsFragmentPagerAdapter;
	private int nowWeek;
	private View rootView;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(Menu.NONE, MENU_ADD, 1,
				getResources().getString(R.string.Lessons_add_lessons))
				.setIcon(R.drawable.ic_menu_new)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		if (ACTIONBAR_MODEL == 1) {
			menu.add(Menu.NONE, MENU_TODAY, 2,
					getResources().getString(R.string.Lessons_go_today))
					.setIcon(R.drawable.ic_menu_today)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		menu.add(Menu.NONE, MENU_LOGOFF, 3,
				getResources().getString(R.string.logoff))
				.setIcon(R.drawable.ic_menu_logoff)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(Menu.NONE, MENU_COURSES, 4,
				getResources().getString(R.string.course_management))
				.setIcon(R.drawable.ic_menu_menu)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_LOGOFF:
			Intent intent = new Intent();
			intent.setClass(getActivity(), LessonsLoginActivity.class);
			getActivity().startActivity(intent);
			return true;
		case MENU_ADD:
			Intent intent1 = new Intent();
			intent1.setClass(getActivity(), LessonsAddActivity.class);
			getActivity().startActivity(intent1);
			return true;
		case MENU_TODAY:
			nowWeek = LessonsTool.getNowWeek(getActivity());
			viewPager.setCurrentItem(nowWeek - 1);
			ACTIONBAR_MODEL = 0;
			getSherlockActivity().invalidateOptionsMenu();
			return true;
		case MENU_COURSES:
			Intent intent2 = new Intent();
			intent2.setClass(getActivity(), LessonsManagementActivity.class);
			getActivity().startActivity(intent2);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// System.out.println("LessonsFragmentSupport_onCreate");
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		regBroadcastRecv();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// System.out.println("LessonsFragmentSupport_onCreateView");
		application = (MyApplication) getActivity().getApplication();
		rootView = inflater.inflate(R.layout.lessons_viewpager, container,
				false);
		init();
		return rootView;
	}

	private void init() {
		// TODO Auto-generated method stub
		sWeekFormat = getResources().getString(R.string.Lessons_now_day);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// System.out.println("LessonsFragmentSupport_onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		lessonsHave = LessonsSharedPreferencesTool
				.getLessonsHave(getActivity());
		if (!lessonsHave) {
			ToastUtil.showToast(getSherlockActivity(), getResources().getString(R.string.No_Lessons_Tip));
			Intent intent = new Intent();
			intent.setClass(getActivity(), LessonsLoginActivity.class);
			this.startActivity(intent);
		}
		nowWeek = LessonsTool.getNowWeek(getActivity());
		viewPager = (LessonsViewPager) rootView.findViewById(R.id.pager);
		viewPager.setOffscreenPageLimit(1);
		if (SettingSharedPreferencesTool.common_isViewPagerTX(getActivity())) {
			viewPager.setFadeEnabled(true);
			viewPager.setTransitionEffect(TransitionEffect.Tablet);
		}
		lessonsFragmentPagerAdapter = new LessonsFragmentPagerAdapter(
				getChildFragmentManager(), viewPager);
		viewPager.setAdapter(lessonsFragmentPagerAdapter);
		viewPager.setChildId(R.id.lessons_main_msc);
		// int nowWeek = 1;
 
		String sWeekInfo=String.format(sWeekFormat,nowWeek); 
		MainActivity.MainActivityActionbar.setSubtitle(sWeekInfo);		
		
		if(nowWeek==0){
			viewPager.setPagingEnabled(false);
			viewPager.setCurrentItem(30);
			ACTIONBAR_MODEL = 0;
			getSherlockActivity().invalidateOptionsMenu();
			return;
		}
		viewPager.setPagingEnabled(true);
		viewPager.setCurrentItem(nowWeek - 1);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				String sWeekInfo=String.format(sWeekFormat,arg0 + 1); 
				MainActivity.MainActivityActionbar.setSubtitle(sWeekInfo);
				MyApplication.getInstance().setLessonsWeek(arg0 + 1);
				if (arg0 + 1 != nowWeek) {
					if (ACTIONBAR_MODEL != 1) {
						ACTIONBAR_MODEL = 1;
						getSherlockActivity().invalidateOptionsMenu();
					}
				} else {
					ACTIONBAR_MODEL = 0;
					getSherlockActivity().invalidateOptionsMenu();
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
	}

	private void refreshLessons() {
		// TODO Auto-generated method stub
		lessonsFragmentPagerAdapter.notifyDataSetChanged();
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getStringExtra("Type").equals("Login")) {
				int nowWeek = LessonsTool.getNowWeek(getActivity());
				String sWeekInfo=String.format(sWeekFormat,nowWeek); 
				MainActivity.MainActivityActionbar.setSubtitle(sWeekInfo);
				if(nowWeek==0){
					viewPager.setPagingEnabled(false);
					viewPager.setCurrentItem(30);
					ACTIONBAR_MODEL = 0;
					getSherlockActivity().invalidateOptionsMenu();
					return;
				}
				viewPager.setPagingEnabled(true);
				viewPager.setCurrentItem(nowWeek - 1);
				ACTIONBAR_MODEL = 0;
				getSherlockActivity().invalidateOptionsMenu();
			}
			refreshLessons();
		}

	};

	// 广播接收器注册
	private void regBroadcastRecv() {
		IntentFilter intentFilter = new IntentFilter(
				"zq.whu.zhangshangwuda.lessonsShow");
		getActivity().registerReceiver(mReceiver, intentFilter);
	}
}