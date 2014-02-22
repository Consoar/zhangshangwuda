package zq.whu.zhangshangwuda.ui.news.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import zq.whu.zhangshangwuda.adapter.NewsListViewAdapter;
import zq.whu.zhangshangwuda.tools.Constants;
import zq.whu.zhangshangwuda.tools.NewsTool;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.news.NewsContentActivity;
import zq.whu.zhangshangwuda.views.PullToRefreshListView;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NewsFragmentCommon extends NewsFragmentBase {
	private static final int MENU_REFRESH = Menu.FIRST;
	public static final String URL = "URL";
	public static final String TABNAME = "TABNAME";
	private View rootView;
	private String URL_MAIN = "http://news.ziqiang.net/api/article/?n=15&s=全部&p=";
	private String URL_ALL;
	private String TAB_NAME;
	private int page = 1;
	private NewsListViewAdapter Adapter;
	private ProgressBar PB;
	private PullToRefreshListView ListView;
	private List<Map<String, String>> newsList;
	private List<Map<String, String>> tempList;
	private int lastpos = 1;
	private boolean loadMoreError;
	private boolean isLoadMore;
	private boolean isAutoRefresh = false;
	private boolean isShowMessage = false;

	public boolean isShowMessage() {
		return isShowMessage;
	}

	public void setShowMessage(boolean isShowMessage) {
		this.isShowMessage = isShowMessage;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(Menu.NONE, MENU_REFRESH, 1,
				getResources().getString(R.string.refresh))
				.setIcon(R.drawable.ic_menu_refresh)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			ListView.clickRefresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static NewsFragmentCommon create(String url, String tabName) {
		NewsFragmentCommon fragment = new NewsFragmentCommon();
		Bundle args = new Bundle();
		args.putString(URL, url);
		args.putString(TABNAME, tabName);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (getArguments() != null) {
			URL_MAIN = getArguments().getString(URL);
			TAB_NAME = getArguments().getString(TABNAME);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// System.out.println("NewsFragmentCommon_onCreateView");
		LayoutInflater mInflater = MyApplication.getActivity()
				.getLayoutInflater();
		rootView = mInflater.inflate(R.layout.news_viewpager_listview, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// System.out.println("NewsFragmentCommon_onActivityCreated");
		isAutoRefresh = isAutoRefresh();
		InitList();
		InitView();
		getDataFromCache();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		// System.out.println(TAB_NAME);
		// System.out.println("NewsFragmentCommon_onDestroyView");
		// TODO Auto-generated method stub
		Adapter = null;
		super.onDestroyView();
	}

	private void InitList() {
		newsList = new ArrayList<Map<String, String>>();
	}

	private void setAutoLoadMore(PullToRefreshListView mListView) {
		if (!isAutoLoadMore())
			return;
		mListView.setAutoLoadMore(true);
	}

	public boolean isAutoLoadMore() {
		SharedPreferences Mysettings = getActivity().getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("news_isAutoLoadMore", true);
	}

	public boolean isAutoRefresh() {
		SharedPreferences Mysettings = getActivity().getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("news_isAutoRefresh", true);
	}

	/**
	 * 初始化View
	 */
	private void InitView() {
		// System.out.println("InitViewPager");
		// 设置首页
		// loadMoreButton = getLoadMoreButton();
		newsList = new ArrayList<Map<String, String>>();
		ListView = (PullToRefreshListView) rootView
				.findViewById(R.id.news_all_ListView);
		PB = (ProgressBar) rootView
				.findViewById(R.id.news_all_ListView_progressBar);
		ListView.setOnItemClickListener(new ListViewItemClickListener());
		ListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				refreshData();
			}
		});
		ListView.setOnLoadMoreListener(new PullToRefreshListView.OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore();
			}
		});
	}

	private class ListViewItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if (id == -1 || view == null) {
				return;
			}
			if (view.findViewById(R.id.pulldownlistview_head_arrow_ImageView) != null)
				return;
			TextView hrefTextView = (TextView) view
					.findViewById(R.id.news_itemHref_TextView);
			if (hrefTextView == null)
				return;
			String href = hrefTextView.getText().toString();
			Intent intent = new Intent(getActivity(), NewsContentActivity.class);
			intent.putExtra("href", href);
			startActivity(intent);
		}

	}

	/**
	 * 加载下一页数据到ListView里
	 */
	private void loadMore() {
		isLoadMore = true;
		loadMoreError = false;
		new Thread(new Runnable() {// 在新线程加载数据

					@Override
					public void run() {
						++page;
						URL_ALL = URL_MAIN + page;
						try {
							lastpos = newsList.size();
							tempList = NewsTool.getNewsList(URL_ALL);
							if (tempList != null) {
								if (TAB_NAME.contains("活动")) {
									tempList = NewsTool.getNewsHDList(tempList);
								}
								if (TAB_NAME.contains("通知")) {
									tempList = NewsTool.getNewsTZList(tempList);
								}
							} else {
								loadMoreError = true;
								--page;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				}).start();
	}

	/**
	 * 加载/刷新首页数据
	 * 
	 */
	private void refreshData() {
		lastpos = 1;
		isLoadMore = false;
		loadMoreError = false;
		if (newsList.isEmpty()) {
			PB.setVisibility(View.VISIBLE);
		}
		new Thread(new Runnable() {// 在新线程加载数据
					@Override
					public void run() {
						// 获取普通新闻
						page = 1;
						URL_ALL = URL_MAIN + page;
						try {
							tempList = NewsTool.getNewsList(URL_ALL);
							if (tempList != null) {
								if (TAB_NAME.contains("活动")) {
									tempList = NewsTool.getNewsHDList(tempList);
								}
								if (TAB_NAME.contains("通知")) {
									tempList = NewsTool.getNewsTZList(tempList);
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				}).start();
	}

	// 从缓存得到首页数据
	private void getDataFromCache() {
		new Thread(new Runnable() {// 在新线程加载数据
					@Override
					public void run() {

						// 获取普通新闻
						page = 1;
						URL_ALL = URL_MAIN + page;
						try {
							tempList = NewsTool.getNewsListFromCache(URL_ALL);
							if (tempList != null) {
								if (TAB_NAME.contains("活动")) {
									tempList = NewsTool.getNewsHDList(tempList);
								}
								if (TAB_NAME.contains("通知")) {
									tempList = NewsTool.getNewsTZList(tempList);
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler_Cache.sendEmptyMessage(0);
					}
				}).start();
	}

	// 定义Handler对象,接收消息
	private Handler handler_Cache = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (tempList == null || tempList.isEmpty()) {
				if (isShowMessage)
					ToastUtil.showToast(getActivity(), R.string.No_Cache_Tip);
			} else {
				showNewsList();
			}
			if (isAutoRefresh) {
				ListView.clickRefresh();
			}
		}
	};
	// 定义Handler对象,接收消息
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (tempList == null || loadMoreError) {
				ListView.onRefreshComplete();
				ListView.onLoadMoreComplete(false);
				if (isShowMessage)
					ToastUtil
							.showToast(getActivity(), R.string.No_Intenert_Tip);
			} else {
				if (tempList != null) {
					if (isLoadMore) {
						String tip = String.format(
								getResources()
										.getString(R.string.Load_More_Tip),
								tempList.size());
						if (isShowMessage)
							ToastUtil.showToast(getActivity(), tip);
					} else {
						if (isShowMessage)
							ToastUtil.showToast(getActivity(),
									R.string.Refresh_Done_Tip);
					}
				}
				showNewsList();
				// ListView.setSelection(lastpos - 1);
			}
			PB.setVisibility(View.GONE);
		}
	};

	/**
	 * 显示新闻列表
	 * 
	 * @param list
	 */
	private void showNewsList() {
		if (page == 1)
			newsList.clear();
		newsList.addAll(tempList);
		ListView.onRefreshComplete();
		ListView.onLoadMoreComplete(false);
		if (Adapter == null) {
			Adapter = new NewsListViewAdapter(getActivity(), newsList);
			ListView.setAdapter(Adapter);
			setAutoLoadMore(ListView);
		} else
			Adapter.notifyDataSetChanged();
	}
}