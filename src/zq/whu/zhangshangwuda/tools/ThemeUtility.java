package zq.whu.zhangshangwuda.tools;

import zq.whu.zhangshangwuda.ui.MyApplication;
import android.content.Context;
import android.content.res.TypedArray;

/**
 * User: qii Date: 13-8-4
 */
public class ThemeUtility {

	public static int getColor(int attr) {
		int[] attrs = new int[] { attr };
		Context context = MyApplication.getActivity();
		// if (context == null)
		// context = GlobalContext.getInstance();
		TypedArray ta = context.obtainStyledAttributes(attrs);
		return ta.getColor(0, 430);

	}

}
