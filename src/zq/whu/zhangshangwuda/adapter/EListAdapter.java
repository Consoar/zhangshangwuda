package zq.whu.zhangshangwuda.adapter;

import java.util.List;

import zq.whu.zhangshangwuda.entity.Classroom;
import zq.whu.zhangshangwuda.ui.R;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<String> groupList;
	private List<List<String>> childList;
	private List<List<List<Classroom>>> childContentList;
	private LayoutInflater mInflater;

	private float mutiple;

	public EListAdapter(Context context, List<String> groupList,
			List<List<String>> childList,
			List<List<List<Classroom>>> childContentList) {
		this.groupList = groupList;
		this.childList = childList;
		this.mContext = context;
		this.childContentList = childContentList;
		mInflater = LayoutInflater.from(mContext);

		
		DisplayMetrics metric = new DisplayMetrics();
		if (mContext instanceof Activity) {
			((Activity) mContext).getWindowManager().getDefaultDisplay()
					.getMetrics(metric);
		}
		mutiple = metric.densityDpi / 160f;
		
	}

	@Override
	public int getGroupCount() {
		return groupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder holder=null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.elist_group_item, null);
			holder=new GroupViewHolder();
			holder.textView=(TextView) convertView
					.findViewById(R.id.tv_item_area);
			convertView.setTag(holder);
		}else {
			holder=(GroupViewHolder) convertView.getTag();
		}
		holder.textView.setText(groupList.get(groupPosition));
		return convertView;
	}

	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder holder=null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.elist_child_item, null);
			holder=new ChildViewHolder();
			holder.textView=(TextView) convertView
					.findViewById(R.id.tv_building);
			holder.listView=(ListView) convertView
					.findViewById(R.id.lv_classroom_free);
			convertView.setTag(holder);
		}else{
			holder=(ChildViewHolder) convertView.getTag();
		}
		
		List<Classroom> classroomStateList = childContentList.get(groupPosition)
				.get(childPosition);
		holder.textView.setText(childList.get(groupPosition).get(childPosition));
		ChildListViewAdapter adapter = new ChildListViewAdapter(mContext,
				classroomStateList);
		holder.listView.setAdapter(adapter);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				 (int) (adapter.getCount() * 30 * mutiple));
		holder.listView.setLayoutParams(params);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	private class GroupViewHolder{
		public TextView textView;
	}
	
	private class ChildViewHolder{
		public TextView textView;
		public ListView listView;
	}
	

}
