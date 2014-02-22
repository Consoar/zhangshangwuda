package zq.whu.zhangshangwuda.base;

import java.lang.reflect.Field;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragment;

public class BaseSherlockFragment extends SherlockFragment {
	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
