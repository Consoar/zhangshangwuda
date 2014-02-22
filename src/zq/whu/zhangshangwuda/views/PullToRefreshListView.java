package zq.whu.zhangshangwuda.views;

import java.util.Date;

import zq.whu.zhangshangwuda.tools.LogUtils;
import zq.whu.zhangshangwuda.ui.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PullToRefreshListView extends ListView implements OnScrollListener {

	private final static String TAG = "PullToRefreshListView";
	private LogUtils LOG;
	// 下拉刷新标志
	private final static int PULL_To_REFRESH = 0;
	// 松开刷新标志
	private final static int RELEASE_To_REFRESH = 1;
	// 正在刷新标志
	private final static int REFRESHING = 2;
	// 刷新完成标志
	private final static int DONE = 3;
	// 普通状态
	private final static int LOADMORE_NORMAL = 4;
	// 加载中状态
	private final static int LOADMORE_LOADING = 5;
	// 加载完毕状态
	private final static int LOADMORE_OVER = 6;
	private LayoutInflater inflater;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 2;
	private View mHeadView;// 头部mHeadView
	private TextView mRefreshTextview; // 刷新msg（mHeadView）
	private TextView mLastUpdateTextView;// 更新事件（mHeadView）
	private ImageView mArrowImageView;// 下拉图标（mHeadView）
	private ProgressBar mHeadProgressBar;// 刷新进度体（mHeadView）

	private View mFootView;// 尾部mFootView
	private View mLoadMoreView;// mFootView 的view(mFootView)
	private TextView mLoadMoreTextView;// 加载更多.(mFootView)
	private View mLoadingView;// 加载中...View(mFootView)
	// 用来设置箭头图标动画效果
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;

	private int headContentWidth;
	private int headContentHeight;
	private int headContentOriginalTopPadding;
	private boolean isRefreshable;
	private boolean isLoadmoreable;
	private int startY;
	private int firstItemIndex;
	private int currentScrollState;

	private int Refreshstate;
	private int Loadmorestate;

	private boolean isBack;
	public OnRefreshListener refreshListener;
	public OnLoadMoreListener loadmoreListener;
	private Context context;
	private boolean isLastRow;
	private boolean isAutoLoadMore;

	public void setAutoLoadMore(boolean isAutoLoadMore) {
		this.isAutoLoadMore = isAutoLoadMore;
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/***
	 * 初始化动画
	 */
	private void initAnimation() {
		// 设置滑动效果
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(100);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(100);
		reverseAnimation.setFillAfter(true);
	}

	private void init(Context context) {
		this.context = context;
		initHeadView();// 初始化头部
		initAnimation();// 初始化动画
		isRefreshable = false;
		isLoadmoreable = false;
	}

	/***
	 * 初始化头部
	 */
	private void initHeadView() {

		inflater = LayoutInflater.from(context);
		mHeadView = (LinearLayout) inflater.inflate(
				R.layout.pulldownlistview_head, null);

		mArrowImageView = (ImageView) mHeadView
				.findViewById(R.id.pulldownlistview_head_arrow_ImageView);
		mArrowImageView.setMinimumWidth(50);
		mArrowImageView.setMinimumHeight(50);
		mHeadProgressBar = (ProgressBar) mHeadView
				.findViewById(R.id.pulldownlistview_head_progressBar);
		mRefreshTextview = (TextView) mHeadView
				.findViewById(R.id.pulldownlistview_head_tips_TextView);
		mLastUpdateTextView = (TextView) mHeadView
				.findViewById(R.id.pulldownlistview_head_lastUpdated_TextView);

		headContentOriginalTopPadding = mHeadView.getPaddingTop();

		measureView(mHeadView);
		headContentHeight = mHeadView.getMeasuredHeight();
		headContentWidth = mHeadView.getMeasuredWidth();

		mHeadView.setPadding(mHeadView.getPaddingLeft(),
				-1 * headContentHeight, mHeadView.getPaddingRight(),
				mHeadView.getPaddingBottom());
		mHeadView.invalidate();

		// LOG.D("初始高度："+headContentHeight);
		// LOG.D("初始TopPad："+headContentOriginalTopPadding);

		addHeaderView(mHeadView);
		setOnScrollListener(this);
	}

	/***
	 * 初始化底部加载更多控件
	 */
	private void initLoadMoreView() {
		mFootView = LayoutInflater.from(context).inflate(
				R.layout.pulldownlistview_footer, null);

		mLoadMoreView = mFootView.findViewById(R.id.load_more_view);

		mLoadMoreTextView = (TextView) mFootView
				.findViewById(R.id.pulldownlistview_footer_loadmore_TextView);

		mLoadingView = (LinearLayout) mFootView
				.findViewById(R.id.pulldownlistview_footer_loadinglayout_LinearLayout);

		mLoadMoreView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onLoadMore();
			}
		});

		addFooterView(mFootView);
	}

	public void onScroll(AbsListView view, int firstVisiableItem,
			int visibleItemCount, int totalItemCount) {
		isLastRow = false;
		firstItemIndex = firstVisiableItem;
		if (firstVisiableItem + visibleItemCount == totalItemCount
				&& totalItemCount > 0) {
			isLastRow = true;
		}

	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		currentScrollState = scrollState;
		if (isAutoLoadMore
				&& isLastRow
				&& scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			onLoadMore();
			isLastRow = false;
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					startY = (int) event.getY();
					isRecored = true;
					// LOG.D("当前-按下高度-ACTION_DOWN-Y："+startY);
				}
				break;

			case MotionEvent.ACTION_CANCEL:// 失去焦点&取消动作
			case MotionEvent.ACTION_UP:

				if (Refreshstate != REFRESHING) {
					if (Refreshstate == DONE) {
						// LOG.D("当前-抬起-ACTION_UP：DONE什么都不做");
					} else if (Refreshstate == PULL_To_REFRESH) {
						Refreshstate = DONE;
						changeHeaderViewByState();
						// LOG.D("当前-抬起-ACTION_UP：PULL_To_REFRESH-->DONE-由下拉刷新状态到刷新完成状态");
					} else if (Refreshstate == RELEASE_To_REFRESH) {
						Refreshstate = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
						// LOG.D("当前-抬起-ACTION_UP：RELEASE_To_REFRESH-->REFRESHING-由松开刷新状态，到刷新完成状态");
					}
				}

				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				// LOG.D("当前-滑动-ACTION_MOVE Y："+tempY);
				if (!isRecored && firstItemIndex == 0) {
					// LOG.D("当前-滑动-记录拖拽时的位置 Y："+tempY);
					isRecored = true;
					startY = tempY;
				}
				if (Refreshstate != REFRESHING && isRecored) {
					// 可以松开刷新了
					if (Refreshstate == RELEASE_To_REFRESH) {
						// 往上推，推到屏幕足够掩盖head的程度，但还没有全部掩盖
						if (((tempY - startY) / RATIO < headContentHeight + 20)
								&& ((tempY - startY) / RATIO) > 0) {
							Refreshstate = PULL_To_REFRESH;
							changeHeaderViewByState();
							// LOG.D("当前-滑动-ACTION_MOVE：RELEASE_To_REFRESH--》PULL_To_REFRESH-由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶
						else if ((tempY - startY) / RATIO <= 0) {
							Refreshstate = DONE;
							changeHeaderViewByState();
							// LOG.D("当前-滑动-ACTION_MOVE：RELEASE_To_REFRESH--》DONE-由松开刷新状态转变到done状态");
						}
						// 往下拉，或者还没有上推到屏幕顶部掩盖head
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					else if (Refreshstate == PULL_To_REFRESH) {
						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight + 20
								&& currentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
							Refreshstate = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
							// LOG.D("当前-滑动-PULL_To_REFRESH--》RELEASE_To_REFRESH-由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if ((tempY - startY) / RATIO <= 0) {
							Refreshstate = DONE;
							changeHeaderViewByState();
							// LOG.D("当前-滑动-PULL_To_REFRESH--》DONE-由Done或者下拉刷新状态转变到done状态");
						}
					}
					// done状态下
					else if (Refreshstate == DONE) {
						if ((tempY - startY) / RATIO > 0) {
							Refreshstate = PULL_To_REFRESH;
							changeHeaderViewByState();
							// LOG.D("当前-滑动-DONE--》PULL_To_REFRESH-由done状态转变到下拉刷新状态");
						}
					}

					// 更新mHeadView的size
					if (Refreshstate == PULL_To_REFRESH) {
						int topPadding = (int) ((-1 * headContentHeight + ((tempY - startY) / RATIO)));
						mHeadView.setPadding(mHeadView.getPaddingLeft(),
								topPadding, mHeadView.getPaddingRight(),
								mHeadView.getPaddingBottom());
						mHeadView.invalidate();
						// LOG.D("当前-下拉刷新PULL_To_REFRESH-TopPad："+topPadding);
					}

					// 更新mHeadView的paddingTop
					if (Refreshstate == RELEASE_To_REFRESH) {
						int topPadding = (int) (((tempY - startY) / RATIO - headContentHeight));
						mHeadView.setPadding(mHeadView.getPaddingLeft(),
								topPadding, mHeadView.getPaddingRight(),
								mHeadView.getPaddingBottom());
						mHeadView.invalidate();
						// LOG.D("当前-释放刷新RELEASE_To_REFRESH-TopPad："+topPadding);
					}
				}
				break;
			}
		}
		/***
		 * 如果是ListView本身的拉动，那么返回true，这样ListView不可以拖动.
		 * 如果不是ListView的拉动，那么调用父类方法，这样就可以上拉执行.
		 */
		if (!isBack) {
			if (event.getY() < 0)
				return true;
			else
				return super.onTouchEvent(event);
		} else {
			return true;
		}
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (Refreshstate) {
		case RELEASE_To_REFRESH:

			mArrowImageView.setVisibility(View.VISIBLE);
			mHeadProgressBar.setVisibility(View.GONE);
			mRefreshTextview.setVisibility(View.VISIBLE);
			mLastUpdateTextView.setVisibility(View.VISIBLE);

			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(animation);

			mRefreshTextview.setText("松开刷新");

			// Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:

			mHeadProgressBar.setVisibility(View.GONE);
			mRefreshTextview.setVisibility(View.VISIBLE);
			mLastUpdateTextView.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.VISIBLE);
			if (isBack) {
				isBack = false;
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(reverseAnimation);
			}
			mRefreshTextview.setText("下拉刷新");

			// Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:
			// LOG.D("刷新REFRESHING-TopPad："+headContentOriginalTopPadding);
			mHeadView.setPadding(mHeadView.getPaddingLeft(),
					headContentOriginalTopPadding, mHeadView.getPaddingRight(),
					mHeadView.getPaddingBottom());
			mHeadView.invalidate();

			mHeadProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			mRefreshTextview.setText("正在刷新中哦~");
			mLastUpdateTextView.setVisibility(View.GONE);

			// Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			// LOG.D("完成DONE-TopPad："+(-1 * headContentHeight));
			mHeadView.setPadding(mHeadView.getPaddingLeft(), -1
					* headContentHeight, mHeadView.getPaddingRight(),
					mHeadView.getPaddingBottom());
			mHeadView.invalidate();

			mHeadProgressBar.setVisibility(View.GONE);
			mArrowImageView.clearAnimation();
			// 此处更换图标
			// mArrowImageView.setImageResource(R.drawable.pulldownlistview_arrow);

			mRefreshTextview.setText("下拉刷新");
			mLastUpdateTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态，done");
			break;
		}
	}

	// 更新Footview视图
	private void changeLoadMoreViewByState() {
		if (isLoadmoreable)
			switch (Loadmorestate) {
			// 普通状态
			case LOADMORE_NORMAL:
				mLoadingView.setVisibility(View.GONE);
				mLoadMoreTextView.setVisibility(View.VISIBLE);
				mLoadMoreTextView.setText("查看更多");
				// Log.v(TAG, "当前状态，查看更多");
				break;
			// 加载中状态
			case LOADMORE_LOADING:
				mLoadingView.setVisibility(View.VISIBLE);
				mLoadMoreTextView.setVisibility(View.GONE);
				// Log.v(TAG, "当前状态，加载中");
				break;
			// 加载完毕状态
			case LOADMORE_OVER:
				mLoadingView.setVisibility(View.GONE);
				mLoadMoreTextView.setVisibility(View.VISIBLE);
				mLoadMoreTextView.setText("加载完毕");
				// Log.v(TAG, "当前状态，加载完毕");
				break;
			default:
				break;
			}
	}

	// 点击刷新
	public void clickRefresh() {
		setSelection(0);
		Refreshstate = REFRESHING;
		changeHeaderViewByState();
		onRefresh();
	}

	// 点击刷新
	public void clickLoadMore() {
		onLoadMore();
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public void setOnLoadMoreListener(OnLoadMoreListener loadmoreListener) {
		this.loadmoreListener = loadmoreListener;
		isLoadmoreable = true;
		initLoadMoreView();
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public interface OnLoadMoreListener {
		public void onLoadMore();
	}

	public void onRefreshComplete(String update) {
		mLastUpdateTextView.setText(update);
		onRefreshComplete();
	}

	@SuppressWarnings("deprecation")
	public void onRefreshComplete() {
		mLastUpdateTextView.setText("最近更新" + new Date().toLocaleString());
		Refreshstate = DONE;
		changeHeaderViewByState();
	}

	/***
	 * 点击加载更多
	 * 
	 * @param flag
	 *            数据是否已全部加载完毕
	 */
	public void onLoadMoreComplete(boolean flag) {
		if (flag) {
			Loadmorestate = LOADMORE_OVER;
			changeLoadMoreViewByState();
		} else {
			Loadmorestate = LOADMORE_NORMAL;
			changeLoadMoreViewByState();
		}

	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	private void onLoadMore() {
		// 防止重复点击
		if (loadmoreListener != null && Loadmorestate == LOADMORE_NORMAL) {
			Loadmorestate = LOADMORE_LOADING;
			changeLoadMoreViewByState();
			loadmoreListener.onLoadMore();// 对外提供方法加载更多.
		}
	}

	// 计算mHeadView的width及height值
	@SuppressWarnings("deprecation")
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

}
