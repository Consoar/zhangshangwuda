package zq.whu.zhangshangwuda.ui.news;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import zq.whu.zhangshangwuda.adapter.NewsFragmentPagerAdapter;
import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.base.PreferenceHelper;
import zq.whu.zhangshangwuda.base.UmengSherlockFragmentActivity;
import zq.whu.zhangshangwuda.tools.DisplayTool;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.tools.NewsTool;
import zq.whu.zhangshangwuda.tools.SettingSharedPreferencesTool;
import zq.whu.zhangshangwuda.ui.AboutActivity;
import zq.whu.zhangshangwuda.ui.HelpActivity;
import zq.whu.zhangshangwuda.ui.MainActivityTAB;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.SettingActivity;
import zq.whu.zhangshangwuda.ui.news.fragment.NewsFragmentBase;
import zq.whu.zhangshangwuda.views.ViewFlowViewPager;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import zq.whu.zhangshangwuda.views.viewpager.JazzyViewPager.TransitionEffect;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private final int MENU_GROUP = 1;
	private final int MENU_SETTING = Menu.FIRST + 1;
	private final int MENU_HELP = Menu.FIRST + 2;
	private final int MENU_FEEDBACK = Menu.FIRST + 3;
	private final int MENU_ABOUT = Menu.FIRST + 4;
	private static final int MENU_REFRESH = Menu.FIRST;
	private ViewFlowViewPager viewPager;
	private ArrayList<String> InitListTab = new ArrayList<String>();
	private ArrayList<String> InitListURL = new ArrayList<String>();
	private static final String URL_CATEGORY = "http://115.29.17.73:8001/news/categories/";
	private static NewsFragmentPagerAdapter lessonsFragmentPagerAdapter;
	private View rootView;
	private TabPageIndicator indicator;
	private List<Map<String, String>> tempList;

	private SherlockFragment getFragmentByPosition(int position) {
		if (position > lessonsFragmentPagerAdapter.getmFragment().size() - 1)
			return null;
		return (SherlockFragment) lessonsFragmentPagerAdapter.getmFragment()
				.get(position);
	}

	/**
	 * 创建optionMenu，用于刷新
	 * @param menu
	 * @param inflater
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		menu.add(MENU_GROUP, MENU_REFRESH, MENU_REFRESH, getResources().getString(R.string.refresh));
		menu.add(MENU_GROUP, MENU_SETTING, MENU_SETTING, getResources().getString(R.string.LeftMenu_Setting));
		menu.add(MENU_GROUP, MENU_HELP, MENU_HELP, getResources().getString(R.string.LeftMenu_Help));
		menu.add(MENU_GROUP, MENU_FEEDBACK, MENU_FEEDBACK, getResources().getString(R.string.LeftMenu_FeedBack)); 
		menu.add(MENU_GROUP, MENU_ABOUT, MENU_ABOUT, getResources().getString(R.string.LeftMenu_About)); 
	}

	/**
	 * 刷新菜单项的选中处理方法，如果选中刷新，则交由sherlockFragment的onOptionItemSelected处理
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case MENU_REFRESH:
			SherlockFragment fragment = getFragmentByPosition(viewPager
					.getCurrentItem());
			if (fragment != null) {
				fragment.onOptionsItemSelected(item);
			}
			return true;
		case MENU_SETTING:
			intent.setClass(getActivity(),SettingActivity.class);
			startActivity(intent);
			return true;
		case MENU_HELP:
			intent.setClass(getActivity(),HelpActivity.class);
			startActivity(intent);
			return true;
		case MENU_FEEDBACK:
			MainActivityTAB.agent.startFeedbackActivity();
			return true;
		case MENU_ABOUT:
			intent.setClass(getActivity(),AboutActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// System.out.println("NewsFragment_onCreate");
		setHasOptionsMenu(true);
		//设置DisplayTool中的Density以便获取图片时使用
		DisplayTool.setDensity(NewsFragmentSupport.this);
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

	
	/**
	 * 获取第几周
	 * 开启viewPaper特效Tablet
	 * 从缓存加载InitListTab和InitListURL，如果失败则只添加“推荐”
	 * 设置viewpaper的adapter
	 * 实现indicator.setOnPageChangeListener监听器，并且和viewpager绑定
	 * 更新InitListTab和InitListURL
	 * @param savedInstanceState
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// System.out.println("NewsFragment_onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		
		int nowWeek = LessonsTool.getNowWeek(getActivity());
		MainActivityTAB.MainActivityActionBar.setSubtitle("第"
				+ String.valueOf(nowWeek) + "周");
		viewPager = (ViewFlowViewPager) rootView.findViewById(R.id.pager);
		viewPager.setOffscreenPageLimit(1);
		if (SettingSharedPreferencesTool.common_isViewPagerTX(getActivity())) {
			viewPager.setFadeEnabled(true);
			viewPager.setTransitionEffect(TransitionEffect.Tablet);
		}
		try {
			tempList = NewsTool.getNewsCategoryFromCache(URL_CATEGORY);
			if(tempList == null){
				InitListTab.add("推荐");
				InitListURL.add("http://115.29.17.73:8001/news/categories/timeline/?category=0&page=");
			} else {
				InitListTab.clear();
				InitListURL.clear();
				for(int i=0; i<tempList.size(); ++i){
					InitListTab.add(tempList.get(i).get("category"));
					InitListURL.add("http://115.29.17.73:8001/news/categories/timeline/?category="
							+ tempList.get(i).get("id").trim() + "&page=");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			InitListTab.clear();
			InitListURL.clear();
			InitListTab.add("推荐");
			InitListURL.add("http://115.29.17.73:8001/news/categories/timeline/?category=0&page=");
		}
		lessonsFragmentPagerAdapter = new NewsFragmentPagerAdapter(
				getChildFragmentManager(), viewPager, InitListTab, InitListURL);
		viewPager.setAdapter(lessonsFragmentPagerAdapter);
		indicator = (TabPageIndicator) rootView
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
		RefreshCategory();
	}

	/**
	 * 友盟服务
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
	}

	/**
	 * 友盟服务
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
	}

	/**
	 * 刷新服务器上已有的类别
	 * 
	 * */
	public void RefreshCategory(){
		new Thread(new Runnable() {// 在新线程加载数据
			@Override
			public void run() {
				try {
					tempList = NewsTool.getNewsCategory(URL_CATEGORY);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	/**
	 * 定义Handler对象, 刷新新闻类别
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (tempList == null) {
				return;
			} else {
				InitListTab.clear();
				InitListURL.clear();
				for(int i=0; i<tempList.size(); ++i){
					InitListTab.add(tempList.get(i).get("category"));
					InitListURL.add("http://115.29.17.73:8001/news/categories/timeline/?category="
							+ tempList.get(i).get("id").trim() + "&page=");
				}
				lessonsFragmentPagerAdapter.setNewsTab(InitListTab);
				lessonsFragmentPagerAdapter.setNewsURL(InitListURL);
				lessonsFragmentPagerAdapter.notifyDataSetChanged();
				indicator.notifyDataSetChanged();
			}
		}
	};
}