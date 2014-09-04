package zq.whu.zhangshangwuda.adapter;

import java.util.List;
import java.util.Map;

import zq.whu.zhangshangwuda.tools.DisplayTool;
import zq.whu.zhangshangwuda.tools.StringUtils;
import zq.whu.zhangshangwuda.tools.ThemeUtility;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 新闻资讯Adapter类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class NewsListViewAdapter extends BaseAdapter {
	private Bitmap failedBitmap = null;
	private Context context;
	private List<Map<String, String>> list;// 数据集合
	private LayoutInflater listContainer;// 视图容器

	static class NewsItemViewHolder { // 自定义控件集合
		public TextView type;
		public TextView title;
		public TextView time;
		public TextView href;
		public ImageView image;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public NewsListViewAdapter(Context context, List<Map<String, String>> data) {
		this.context = context;
		this.listContainer = MyApplication.getLayoutInflater(); // 创建视图容器并设置上下文
		this.list = data;
		failedBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.failimg);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// Log.d("method", "getView");

		// 自定义视图
		NewsItemViewHolder listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.news_item, null);

			listItemView = new NewsItemViewHolder();
			// 获取控件对象
			listItemView.type = (TextView) convertView
					.findViewById(R.id.news_itemTitleType_TextView);
			listItemView.title = (TextView) convertView
					.findViewById(R.id.news_itemTitle_TextView);
			listItemView.time = (TextView) convertView
					.findViewById(R.id.news_itemPostTime_TextView);
			listItemView.href = (TextView) convertView
					.findViewById(R.id.news_itemHref_TextView);
			listItemView.image = (ImageView) convertView
					.findViewById(R.id.news_itemImage_ImageView);;

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (NewsItemViewHolder) convertView.getTag();

		}
		// 设置文字和图片
		Map<String, String> news = list.get(position);
		if (news.get("type") != null) {
			listItemView.type.setVisibility(View.VISIBLE);
			listItemView.type.setText(news.get("type"));
			listItemView.type.setBackgroundColor(getBG(news.get("type")));
			listItemView.image.setVisibility(View.GONE);
		} else {
			listItemView.title.setMaxLines(3);
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) listItemView.title.getLayoutParams();  
			params.setMargins(DisplayTool.px2dip(context, 5), 
					DisplayTool.dip2px(context, 5),
					DisplayTool.dip2px(context, 110), 
					DisplayTool.dip2px(context, 6));  
			listItemView.title.setLayoutParams(params);
		}
		listItemView.title.setText(news.get("title"));
		listItemView.title.setTextColor(ThemeUtility
				.getColor(R.attr.newsItemTextColor));
		listItemView.time.setText(news.get("time"));
		listItemView.href.setText(news.get("href"));
		if (!StringUtils.isEmpty(news.get("image")))
			MyApplication.getInstance().mImageLoader.displayImage(
					DisplayTool.getSmallImageUrl(context, news.get("image")), listItemView.image);
		else
			listItemView.image.setImageBitmap(failedBitmap);
		return convertView;
	}

	public int getBG(String type) {
		if (type.contains("新闻"))
			return context.getResources().getColor(R.color.ListViewType1Color);
		if (type.contains("活动"))
			return context.getResources().getColor(R.color.ListViewType2Color);
		if (type.contains("公告"))
			return context.getResources().getColor(R.color.ListViewType3Color);
		if (type.contains("讲座"))
			return context.getResources().getColor(R.color.ListViewType4Color);
		if (type.contains("院系"))
			return context.getResources().getColor(R.color.ListViewType6Color);
		return context.getResources().getColor(R.color.ListViewType5Color);
	}
}