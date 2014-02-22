package zq.whu.zhangshangwuda.views;

import zq.whu.zhangshangwuda.views.viewpager.JazzyViewPager;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LessonsViewPager extends JazzyViewPager {

	private int childId;
	private boolean flag;
	private boolean canSwitch;
	private View scroll;

	public LessonsViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// System.out.println("onInterceptTouchEvent_ViewPager");
		if (childId > 0) {
			if (!flag) {
				flag = true;
				scroll = findViewById(childId);
				((LeftRightScrollView) scroll).setinViewPager(true);
				((LeftRightScrollView) scroll).setmPager(this);
				((LeftRightScrollView) scroll).getView();
			}
			// if (scroll != null) {
			// Rect rect = new Rect();
			// scroll.getHitRect(rect);
			// if (rect.contains((int) event.getX(), (int) event.getY())) {
			// if (canSwitch)
			// return true;
			// return false;
			// }
			// }
			// return super.onInterceptTouchEvent(event);
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// System.out.println("ACTION_DOWN_ViewPager");
			break;
		case MotionEvent.ACTION_MOVE:
			// System.out.println("ACTION_MOVE_ViewPager");
			break;
		case MotionEvent.ACTION_UP:
			// System.out.println("ACTION_UP_ViewPager");
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void setChildId(int id) {
		this.childId = id;
		flag = false;
	}
}