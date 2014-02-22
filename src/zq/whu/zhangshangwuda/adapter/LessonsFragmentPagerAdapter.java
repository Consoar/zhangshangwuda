package zq.whu.zhangshangwuda.adapter;

import zq.whu.zhangshangwuda.ui.lessons.LessonsFragment;
import zq.whu.zhangshangwuda.views.LessonsViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class LessonsFragmentPagerAdapter extends FragmentPagerAdapter {
	private LessonsViewPager viewPager;

	public LessonsFragmentPagerAdapter(FragmentManager fm,
			LessonsViewPager viewPager) {
		super(fm);
		this.viewPager = viewPager;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment temp = LessonsFragment.create(position);
		viewPager.setObjectForPosition(temp, position);
		return temp;
	}

	@Override
	public int getCount() {
		return 99;
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

}