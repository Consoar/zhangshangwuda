package zq.whu.zhangshangwuda.adapter;

import java.util.List;

import zq.whu.zhangshangwuda.db.WifiDb;
import zq.whu.zhangshangwuda.entity.WifiAccount;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.views.DropPopMenu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class DropMenuAdapter extends BaseAdapter {
	private Context context;
	private List<WifiAccount> list;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private DropPopMenu dropPopMenu;

	static class DropItemViewHolder { // 自定义控件集合
		public TextView title;
		public TextView id;
		public ImageButton delete;
	}

	public DropMenuAdapter(Context context, List<WifiAccount> data,
			DropPopMenu dropPopMenu) {
		this.context = context;
		this.listContainer = MyApplication.getLayoutInflater(); // 创建视图容器并设置上下文
		this.list = data;
		this.dropPopMenu = dropPopMenu;
	}

	public List<WifiAccount> getList() {
		return list;
	}

	public void setList(List<WifiAccount> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 自定义视图
		DropItemViewHolder listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(
					R.layout.droppopmenu_listview_item, null);

			listItemView = new DropItemViewHolder();
			// 获取控件对象
			listItemView.title = (TextView) convertView
					.findViewById(R.id.droppopmenu_listView_item_title);
			listItemView.id = (TextView) convertView
					.findViewById(R.id.droppopmenu_listView_item_id);
			listItemView.delete = (ImageButton) convertView
					.findViewById(R.id.droppopmenu_listView_item_delete);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (DropItemViewHolder) convertView.getTag();
		}
		final WifiAccount item = list.get(position);
		listItemView.title.setText(item.getUsername());
		listItemView.id.setText(String.valueOf(item.getId()));
		listItemView.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dropPopMenu.dismiss();
				WifiDb.getInstance(context).deleteByName(item.getUsername());
			}
		});
		return convertView;
	}

}
