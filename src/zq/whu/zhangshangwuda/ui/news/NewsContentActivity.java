package zq.whu.zhangshangwuda.ui.news;

import imid.swipebacklayout.lib.SwipeBackLayout;
import org.htmlparser.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import zq.whu.zhangshangwuda.base.BaseThemeSwipeBackSherlockActivity;
import zq.whu.zhangshangwuda.base.PreferenceHelper;
import zq.whu.zhangshangwuda.tools.Constants;
import zq.whu.zhangshangwuda.tools.DisplayTool;
import zq.whu.zhangshangwuda.tools.HtmlTool;
import zq.whu.zhangshangwuda.tools.NewsTool;
import zq.whu.zhangshangwuda.tools.StringUtils;
import zq.whu.zhangshangwuda.tools.ThemeUtility;
import zq.whu.zhangshangwuda.ui.BuildConfig;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.R.color;
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
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sina.weibo.SinaWeibo.ShareParams;
import cn.sharesdk.wechat.friends.Wechat;

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
	private WebView contentWebView;
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
		menu.add(Menu.NONE, MENU_SNS, 2,
				getResources().getString(R.string.share))
				.setIcon(R.drawable.ic_menu_share)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS);
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
		case MENU_SNS:
			if (contentmap != null)
				sharenews();
			break;
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
		if (BuildConfig.DEBUG) System.out.println("href=="+href);
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
		contentWebView = (WebView) findViewById(R.id.news_content_webview_content);
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
				boolean showImage = isShowImage();
				titleTextView.setText(contentmap.get("title"));
				if(!StringUtils.isEmpty(contentmap.get("author")))
					anthorTextView.setText(contentmap.get("author"));
				else
					anthorTextView.setText(contentmap.get("tag"));
				moreinfoTextView.setText(contentmap.get("time"));
				getSupportActionBar().setSubtitle(contentmap.get("category"));
				Document documentNewsContent = Jsoup.parse(contentmap.get("content"));
				//调整图片的尺寸
				Elements pics = documentNewsContent.getElementsByTag("img");
				for (Element pic : pics) {
					if(showImage){
						String picHref = pic.attr("src");
						pic.attr("src", DisplayTool.getMyImageUrl(picHref));
						double picWidth;
						double picHeight;
						if(!pic.attr("width").equals("") && !pic.attr("height").equals("")){
							picWidth = Integer.parseInt(pic.attr("width"));
							picHeight = Integer.parseInt(pic.attr("height"));
						} else {
							picWidth = 450;
							picHeight = 300;
						}
						double maxWidth = DisplayTool.px2dip(NewsContentActivity.this, 
								MyApplication.getDisplayWidth()-DisplayTool.dip2px(NewsContentActivity.this, 48));
						//宽高比例
						double factorWidthOverHeight = picWidth / picHeight;	
						if(picWidth > maxWidth){
							picWidth = maxWidth;
							picHeight = (int) (picWidth / factorWidthOverHeight);
							pic.attr("width", picWidth + "");
							pic.attr("height", picHeight + "");
						}
					} else {
						//不显示图片时把图片标签改成p标签
						pic.tagName("p");
						pic.removeAttr("src");
						pic.removeAttr("width");
						pic.removeAttr("height");
						pic.append("<图片>(更改设置后显示)");
					}
				}
				int mTheme = PreferenceHelper.getTheme(NewsContentActivity.this);
				//黑版要用CSS把文字弄成白色的
				if (mTheme != R.style.MyLightTheme){
					Elements ps = documentNewsContent.getElementsByTag("p");
					for (Element p : ps) {
						p.attr("style", "color:#FFFFFF");
					}
				}
				String htmlData = documentNewsContent.html();
				//WebView的背景色要和background一致
				contentWebView.setBackgroundColor(0x111111);
				contentWebView.setVisibility(View.VISIBLE);
				contentWebView.loadDataWithBaseURL(null, htmlData, "text/html", "utf-8", null);
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

	 public void sharenews() {
		// TODO Auto-generated method stub
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		//新浪微博定制分享，有140字的字数限制
		oks.setShareContentCustomizeCallback(new ShareContentCustomize());
		
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.icon, getString(R.string.app_name));
		
		// title标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、 
		//易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供
		oks.setTitle(contentmap.get("title"));
		
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(contentmap.get("share_url"));
		
		// 构建分享文本，所有平台都需要这个字段
		//标题、作者、时间
		StringBuilder sb = new StringBuilder(contentmap.get("title"));
		sb.append("\n\n");
		if(!StringUtils.isEmpty(contentmap.get("author")))
			sb.append(contentmap.get("author"));
		else
			sb.append(contentmap.get("tag"));
		sb.append(" ");
		sb.append(contentmap.get("time"));
		sb.append("\n\n");
		
		//直接抽离HTML
		Document doc = Jsoup.parse(contentmap.get("content"));
		Elements contents = doc.getElementsByTag("p");
		for (Element content : contents) {
			sb.append(content.text()).append("\n");
		}
		sb.append("\n   【来自 掌上武大 的分享】");
		
		//imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 
		oks.setImageUrl(contentmap.get("image"));
		
		//text是分享文本
		oks.setText(sb.toString());
		
		// url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供 
		oks.setUrl(contentmap.get("share_url"));
		
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://www.ziqiang.net");
		
		// 启动分享GUI
		oks.show(this);
			
			
	}

	// public void saveData() {
	// News tnews = new News(href, title, time, content);
	// NewsDb.getInstance(NewsContentActivity.this).insert(tnews);
	// }
	
	 /**
	  * 快捷分享自定义-新浪微博
	  *
	  */
	 class ShareContentCustomize implements ShareContentCustomizeCallback {
		 @Override
		 public void onShare(Platform platform,
				 cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
			 
			 //定制新浪微博
			 if (SinaWeibo.NAME.equals(platform.getName())) {
				 StringBuilder sb = new StringBuilder(contentmap.get("title"));
				 sb.append("\n");
				 Document doc = Jsoup.parse(contentmap.get("content"));
				 Elements contents = doc.getElementsByTag("p");
				 for (Element content : contents) {
					 sb.append(content.text()).append("\n");
				 }
				 sb.delete(80, sb.length()-1).append("...\n")
				 .append("更多：").append(contentmap.get("share_url"));
				 paramsToShare.setText(sb.toString());
			 }
		 }
	 }

}
