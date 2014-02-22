package zq.whu.zhangshangwuda.ui.news;

import java.lang.reflect.Field;

import zq.whu.zhangshangwuda.adapter.NewsFragmentPagerAdapter;
import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.base.PreferenceHelper;
import zq.whu.zhangshangwuda.base.UmengSherlockFragmentActivity;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.tools.SettingSharedPreferencesTool;
import zq.whu.zhangshangwuda.ui.MainActivity;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.news.fragment.NewsFragmentBase;
import zq.whu.zhangshangwuda.views.ViewFlowViewPager;
import zq.whu.zhangshangwuda.views.viewpager.JazzyViewPager.TransitionEffect;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

public class NewsFragmentSupport extends BaseSherlockFragment {
	private static final String mPageName = "NewsFragment";
	private static final int MENU_REFRESH = Menu.FIRST;
	private ViewFlowViewPager viewPager;
	private static NewsFragmentPagerAdapter lessonsFragmentPagerAdapter;
	private View rootView;

	private SherlockFragment getFragmentByPosition(int position) {
		if (position > lessonsFragmentPagerAdapter.getmFragment().size() - 1)
			return null;
		return (SherlockFragment) lessonsFragmentPagerAdapter.getmFragment()
				.get(position);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(Menu.NONE, MENU_REFRESH, 1,
				getResources().getString(R.string.refresh))
				.setIcon(R.drawable.ic_menu_refresh)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			SherlockFragment fragment = getFragmentByPosition(viewPager
					.getCurrentItem());
			if (fragment != null) {
				fragment.onOptionsItemSelected(item);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// System.out.println("NewsFragment_onCreate");
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// System.out.println("NewsFragment_onCreateView");
		// 使用ContextThemeWrapper通过目标Theme生成一个新的Context
		int mTheme = PreferenceHelper.getTheme(getActivity());
		Context ctxWithTheme;
		if (mTheme == R.style.MyLightTheme)
			ctxWithTheme = new ContextThemeWrapper(MyApplication.getActivity(),
					R.style.CustomTitlePageIndicator);
		else
			ctxWithTheme = new ContextThemeWrapper(MyApplication.getActivity(),
					R.style.Theme_PageIndicatorDefaults);
		// 通过生成的Context创建一个LayoutInflater
		LayoutInflater localLayoutInflater = inflater
				.cloneInContext(ctxWithTheme);
		// 使用生成的LayoutInflater创建View
		rootView = localLayoutInflater.inflate(R.layout.news, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// System.out.println("NewsFragment_onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		int nowWeek = LessonsTool.getNowWeek(getActivity());
		MainActivity.MainActivityActionbar.setSubtitle("第"
				+ String.valueOf(nowWeek) + "周");
		viewPager = (ViewFlowViewPager) rootView.findViewById(R.id.pager);
		viewPager.setOffscreenPageLimit(1);
		if (SettingSharedPreferencesTool.common_isViewPagerTX(getActivity())) {
			viewPager.setFadeEnabled(true);
			viewPager.setTransitionEffect(TransitionEffect.Tablet);
		}
		lessonsFragmentPagerAdapter = new NewsFragmentPagerAdapter(
				getChildFragmentManager(), viewPager);
		viewPager.setAdapter(lessonsFragmentPagerAdapter);
		TabPageIndicator indicator = (TabPageIndicator) rootView
				.findViewById(R.id.indicator);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				NewsFragmentBase fragment = (NewsFragmentBase) getFragmentByPosition(viewPager
						.getCurrentItem());
				if (fragment != null)
					fragment.setShowMessage(true);
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
		indicator.setViewPager(viewPager);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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

}