package zq.whu.zhangshangwuda.ui.news;

import imid.swipebacklayout.lib.SwipeBackLayout;

import java.util.Map;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import zq.whu.zhangshangwuda.base.BaseThemeSwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.tools.Constants;
import zq.whu.zhangshangwuda.tools.HtmlTool;
import zq.whu.zhangshangwuda.tools.NewsTool;
import zq.whu.zhangshangwuda.tools.StringUtils;
import zq.whu.zhangshangwuda.tools.ThemeUtility;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.umeng.analytics.MobclickAgent;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NewsContentActivity extends BaseThemeSwipeBackSherlockActivity {
	private static final int MENU_REFRESH = Menu.FIRST;
	private static final int MENU_SAVE = Menu.FIRST + 1;
	private static final int MENU_SNS = Menu.FIRST + 2;
	private boolean isSaveInLocal, isAutoSaveInLocal;
	private TextView titleTextView, anthorTextView, moreinfoTextView;
	private View content_line;
	private LinearLayout contentLinearLayout;
	private String title;
	private String time;
	private String href;
	private Map<String, String> contentmap = null;
	private String content;
	private Spanned contentSpanned;
	private static final int VIBRATE_DURATION = 20;
	private SwipeBackLayout mSwipeBackLayout;

	public boolean isShowImage() {
		SharedPreferences Mysettings = getSharedPreferences(
				Constants.PREFS_NAME_APP_SETTING, 0);
		return Mysettings.getBoolean("news_showimage", true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_REFRESH, 1,
				getResources().getString(R.string.refresh))
				.setIcon(R.drawable.ic_menu_refresh)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		// menu.add(Menu.NONE, MENU_SNS, 2,
		// getResources().getString(R.string.share))
		// .setIcon(R.drawable.ic_menu_share)
		// .setShowAsAction(
		// MenuItem.SHOW_AS_ACTION_IF_ROOM
		// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case MENU_REFRESH:
			refreshData();
			break;
		// case MENU_SNS:
		// if (contentmap != null)
		// sharenews();
		// break;
		}
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.news_content);
		Intent intent = getIntent();
		href = intent.getStringExtra("href");
		// title = NoticeHtmlTool.getZqNoticesContentTitle(href);
		// isSaveInLocal =
		// LocalDataSharedPreferencesTool.news_isSaveInLocal(this);
		// isAutoSaveInLocal = LocalDataSharedPreferencesTool
		// .news_isAutoSaveInLocal(this);
		findViews();
		refreshData();
		getWindow().setBackgroundDrawable(null);
	}

	private void findViews() {
		titleTextView = (TextView) findViewById(R.id.news_content_news_title_TextView);
		anthorTextView = (TextView) findViewById(R.id.news_content_news_author_TextView);
		moreinfoTextView = (TextView) findViewById(R.id.news_content_news_moreinfo_TextView);
		contentLinearLayout = (LinearLayout) findViewById(R.id.news_content_news_content_LinearLayout);
		content_line = (View) findViewById(R.id.news_content_news_content_line);
	}

	/**
	 * 加载/刷新 当前页面
	 * 
	 * @return
	 */
	private void refreshData() {

		setSupportProgressBarIndeterminateVisibility(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				loadContent();
				if (contentmap != null) {
					String id = contentmap.get("id");
					String hitUrl = "http://news.ziqiang.net/article/hits/"
							+ id + "/";
					HtmlTool.downLoadZqNewsJson(hitUrl);
				}
				handler.sendEmptyMessage(0);
			}

		}).start();
	}

	/**
	 * 加载文章正文
	 */
	private void loadContent() {
		// if (!tryToLoadFromDb()) {// 从本地加载不成功则从网络加载，并将加载到的内容存入数据库
		try {
			contentmap = NewsTool.getNewsContent(href);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
	}

	// /**
	// * 尝试从数据库中加载文章正文
	// *
	// * @return 是否成功加载
	// */
	// private boolean tryToLoadFromDb() {
	// News news = NewsDb.getInstance(NewsContentActivity.this)
	// .getNewsByUrl(href);
	// if (news == null) {
	// return false;
	// } else {
	// content = news.getContent();
	// contentSpanned = Html.fromHtml(content);
	// title = news.getTitle();
	// return true;
	// }
	// }

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (contentmap == null) {
				ToastUtil.showToast(NewsContentActivity.this,
						R.string.No_Intenert_Tip);
				content_line.setVisibility(View.INVISIBLE);
			} else {
				content_line.setVisibility(View.VISIBLE);
				contentLinearLayout.removeAllViews();
				boolean showImage = isShowImage();
				titleTextView.setText(contentmap.get("title"));
				anthorTextView.setText(contentmap.get("author"));
				moreinfoTextView.setText(contentmap.get("time") + ' ' + "点击："
						+ contentmap.get("hits"));
				getSupportActionBar().setSubtitle(contentmap.get("category"));
				Document doc = Jsoup.parse(contentmap.get("content"));
				Elements contents = doc.getElementsByTag("p");
				for (Element content : contents) {
					if (showImage) {
						Elements imgs = content.getElementsByTag("img");
						for (Element img : imgs) {
							ImageViewFromUrl pic = new ImageViewFromUrl(
									NewsContentActivity.this);
							String t = img.attr("width");
							int pwidth = 200, pheight = 300;
							if (!t.equals(""))
								pwidth = Integer.parseInt(t);
							t = img.attr("height");
							if (!t.equals(""))
								pheight = Integer.parseInt(t);
							if (!StringUtils.isEmpty(img.absUrl("src")))
								pic.load(img.absUrl("src"), pwidth, pheight);
							contentLinearLayout.addView(pic);
						}
					}
					if (!content.text().equals(Jsoup.parse("&nbsp;").text())) {
						TextView tx = new TextView(NewsContentActivity.this);
						if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
							tx.setTextIsSelectable(true);
						}
						tx.setBackgroundDrawable(null);
						tx.setText(content.text());
						tx.setLineSpacing(3.0f, 1.5f);
						tx.setTextSize(15);
						tx.setTextColor(ThemeUtility
								.getColor(R.attr.newsContentTextColor));
						contentLinearLayout.addView(tx);
					}
				}
			}
			setSupportProgressBarIndeterminateVisibility(false);
		}
	};

	// /**
	// * 显示图片的ImageGetter
	// */
	// private Html.ImageGetter imageGetter = new Html.ImageGetter() {
	// @Override
	// public Drawable getDrawable(String source) {
	// InputStream is = null;
	// try {
	// is = (InputStream) new URL(source).getContent();
	// Drawable d = Drawable.createFromStream(is, "src");
	// d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
	// is.close();
	// return d;
	// } catch (Exception e) {
	// return null;
	// }
	// }
	// };

	// public void sharenews() {
	// // TODO Auto-generated method stub
	// String sharecontent = MobclickAgent.getConfigParams(this,
	// "share_content");
	// // Map<String, String> sharemap = new HashMap<String, String>();
	// // sharemap.put("title", contentmap.get("title"));
	// // sharemap.put("href", contentmap.get("href"));
	// sharecontent = sharecontent.replace("title", contentmap.get("title"));
	// sharecontent = sharecontent.replace("href", contentmap.get("href"));
	// UMSnsService.shareToRenr(NewsContentActivity.this, sharecontent, null);
	// }

	// public void saveData() {
	// News tnews = new News(href, title, time, content);
	// NewsDb.getInstance(NewsContentActivity.this).insert(tnews);
	// }

}
