package zq.whu.zhangshangwuda.adapter;

import java.util.ArrayList;
import java.util.List;

import zq.whu.zhangshangwuda.ui.news.fragment.NewsFragmentBase;
import zq.whu.zhangshangwuda.ui.news.fragment.NewsFragmentCommon;
import zq.whu.zhangshangwuda.ui.news.fragment.NewsFragmentWithTopic;
import zq.whu.zhangshangwuda.views.ViewFlowViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<String> newsTab; //动态标签栏
	private ArrayList<String> newsURL; //动态网址（对应标签）
	private static List<NewsFragmentBase> mFragment;
	private ViewFlowViewPager viewPager;
	private static int nowPos;

	public List<NewsFragmentBase> getmFragment() {
		return mFragment;
	}

	public NewsFragmentPagerAdapter(FragmentManager fm,
			ViewFlowViewPager viewPager, List<String> newsTab, List<String> newsURL) {
		super(fm);
		this.viewPager = viewPager;
		this.newsTab = (ArrayList<String>) newsTab;
		this.newsURL = (ArrayList<String>) newsURL;
		mFragment = new ArrayList<NewsFragmentBase>();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return newsTab.get(position % newsTab.size());
	}

	@Override
	public Fragment getItem(int position) {
		// System.out.println("getItem");
		NewsFragmentBase page = null;
		while (position >= mFragment.size()) {
			mFragment.add(null);
		}
		page = new NewsFragmentBase();
		if (position == 0) {
			page = NewsFragmentWithTopic.create(newsURL.get(position),
					newsTab.get(position));
			((NewsFragmentWithTopic) page).setmPager(viewPager);
		} else {
			page = NewsFragmentCommon.create(newsURL.get(position),
					newsTab.get(position));
		}
		if (nowPos == position)
			((NewsFragmentBase) page).setShowMessage(true);
		mFragment.set(position, page);
		viewPager.setObjectForPosition(page, position);
		return page;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		//super.destroyItem(container, position, object);  
	}

	@Override
	public int getCount() {
		return newsTab.size();
	
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setNewsTab(ArrayList<String> newsTab) {
		this.newsTab = newsTab;
	}
	
	public void setNewsURL(ArrayList<String> newsURL) {
		this.newsURL = newsURL;
	}
}