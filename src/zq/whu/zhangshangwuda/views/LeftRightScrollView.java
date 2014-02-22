package zq.whu.zhangshangwuda.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class LeftRightScrollView extends CustomScrollView {
	private static final String tag = "LazyScrollView";
	private View view;
	private static ViewPager mPager;
	private boolean isMove;
	private boolean inViewPager;
	private int startX;
	private int Direction = -1;

	public LeftRightScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public LeftRightScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ViewPager getmPager() {
		return mPager;
	}

	public void setmPager(ViewPager mPager) {
		this.mPager = mPager;
	}

	public void setinViewPager(boolean inViewPager) {
		this.inViewPager = inViewPager;
	}

	// 这个获得总的高度
	public int computeVerticalScrollRange() {
		return super.computeHorizontalScrollRange();
	}

	public int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// System.out.println("onInterceptTouchEvent_LeftRightScrollView");
		if (mPager != null)
			mPager.requestDisallowInterceptTouchEvent(true);
		if (view == null)
			getView();
		isMove = false;
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// System.out.println("onTouchEvent_LeftRightScrollView");
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// System.out.println("ACTION_DOWN_LeftRightScrollView");
			startX = (int) ev.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			// System.out.println("ACTION_MOVE_LeftRightScrollView");
			int tempX = (int) ev.getX();
			if (tempX - startX > 0)
				Direction = 1;// 1 right
			else
				Direction = 0;// 0 left
			if (view.getMeasuredWidth() <= getScrollX() + getWidth()
					&& Direction == 0) {
				// if (!isMove){
				// break;
				// }
				// System.out.println("onRight");
				if (mPager != null)
					mPager.requestDisallowInterceptTouchEvent(false);
			} else if (getScrollX() == 0 && Direction == 1
					&& mPager.getCurrentItem() != 0) {
				// System.out.println("onLeft");
				// if (!isMove){
				// break;
				// }
				if (mPager != null)
					mPager.requestDisallowInterceptTouchEvent(false);
			} else {
				// System.out.println("onScroll");
				isMove = true;
				Direction = -1;
				if (mPager != null)
					mPager.requestDisallowInterceptTouchEvent(true);
				// return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			// System.out.println("ACTION_MOVE_LeftRightScrollView");
			if (mPager != null)
				mPager.requestDisallowInterceptTouchEvent(false);
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mPager != null)
				mPager.requestDisallowInterceptTouchEvent(false);
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
	 */
	public void getView() {
		this.view = getChildAt(0);
	}
}
