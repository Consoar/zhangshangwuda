package zq.whu.zhangshangwuda.views;

import zq.whu.zhangshangwuda.views.viewpager.JazzyViewPager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewFlowViewPager extends JazzyViewPager {
	private boolean willIntercept = true;

	public ViewFlowViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (willIntercept) {
			return super.onInterceptTouchEvent(arg0);
		} else {
			return false;
		}

	}

	public void setTouchIntercept(boolean value) {
		willIntercept = value;
	}
}