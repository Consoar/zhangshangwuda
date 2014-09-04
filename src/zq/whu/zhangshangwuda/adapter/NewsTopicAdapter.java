package zq.whu.zhangshangwuda.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zq.whu.zhangshangwuda.adapter.NewsListViewAdapter.NewsItemViewHolder;
import zq.whu.zhangshangwuda.tools.StringUtils;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.news.NewsContentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsTopicAdapter extends BaseAdapter {
	private Bitmap failedBitmap = null;
	private Context mContext;
	private LayoutInflater mInflater;
	private final int IMAGE_COUNT = 5;
	private List<Map<String, String>> pic = new ArrayList<Map<String, String>>();

	static class NewsTopicItemViewHolder { // 自定义控件集合
		public ImageView img;
	}

	public NewsTopicAdapter(Context context, List<Map<String, String>> tpic) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pic = tpic;
		failedBitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.failimg);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE; // 返回很大的值使得getView中的position不断增大来实现循环
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		NewsTopicItemViewHolder topicItemView = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.news_sy_image_item, null);
			topicItemView = new NewsTopicItemViewHolder();
			topicItemView.img = ((ImageView) convertView
					.findViewById(R.id.imgView));
			convertView.setTag(topicItemView);
		} else {
			topicItemView = (NewsTopicItemViewHolder) convertView.getTag();
		}
		ImageView img = topicItemView.img;
		String url = pic.get(position % IMAGE_COUNT).get("image");
		if (!StringUtils.isEmpty(url))
			MyApplication.getInstance().mImageLoader.displayImage(url, img);
		else
			img.setImageBitmap(failedBitmap);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, NewsContentActivity.class);
				intent.putExtra("time", pic.get(position % IMAGE_COUNT).get("time"));
				intent.putExtra("href", pic.get(position % IMAGE_COUNT).get("href"));
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}
}
