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
import android.view.ViewGroup;

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
	public static final String URL_MAIN_SY = "http://news.ziqiang.net/api/article/?n=15&s=全部&p=";
	public static final String URL_MAIN_XW = "http://news.ziqiang.net/api/article/?n=15&s=最新&p=";
	public static final String URL_MAIN_HD = "http://news.ziqiang.net/api/article/?n=15&s=公告&p=";
	public static final String URL_MAIN_SD = "http://news.ziqiang.net/api/article/?n=15&s=深度&p=";
	public static final String URL_MAIN_TZ = "http://news.ziqiang.net/api/article/?n=15&s=公告&p=";
	public static final String URL_MAIN_YX = "http://news.ziqiang.net/api/article/?n=15&s=院系&p=";
	private static final String[] CONTENT = new String[] { "首页", "新闻", "活动",
			"深度", "通知", "院系" };
	private static final String[] URL = new String[] { URL_MAIN_SY,
			URL_MAIN_XW, URL_MAIN_HD, URL_MAIN_SD, URL_MAIN_TZ, URL_MAIN_YX };
	private static List<NewsFragmentBase> mFragment;
	private ViewFlowViewPager viewPager;
	private static int nowPos;

	public List<NewsFragmentBase> getmFragment() {
		return mFragment;
	}

	public NewsFragmentPagerAdapter(FragmentManager fm,
			ViewFlowViewPager viewPager) {
		super(fm);
		this.viewPager = viewPager;
		mFragment = new ArrayList<NewsFragmentBase>();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return CONTENT[position % CONTENT.length];
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
			page = NewsFragmentWithTopic.create(URL[position],
					CONTENT[position]);
			((NewsFragmentWithTopic) page).setmPager(viewPager);
		} else {
			page = NewsFragmentCommon.create(URL[position], CONTENT[position]);
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
		// super.destroyItem(container, position, object);
	}

	@Override
	public int getCount() {
		return CONTENT.length;
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

}