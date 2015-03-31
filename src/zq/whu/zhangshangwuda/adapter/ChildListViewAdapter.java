package zq.whu.zhangshangwuda.adapter;

import java.util.List;

import zq.whu.zhangshangwuda.entity.Classroom;
import zq.whu.zhangshangwuda.ui.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChildListViewAdapter extends BaseAdapter{

	private Context mContext;
	private List<Classroom> list;
	private LayoutInflater mInflater;
	public ChildListViewAdapter(Context context, List<Classroom> list) {
		this.mContext = context;
		this.list = list;
		mInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(list.size()%2==0){
			return list.size()/2;
		}else{
			return list.size()/2+1;
		}
		
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			convertView=mInflater.inflate(R.layout.child_list_view_item, null);
			holder=new ViewHolder();
			holder.tv1=(TextView) convertView.findViewById(R.id.tv_classroom_1);
			holder.tv2=(TextView) convertView.findViewById(R.id.tv_classroom_2);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.tv1.setText(list.get(position*2).getQueryResult());
		int num=position*2+1;
		if(num<list.size()){
			holder.tv2.setText(list.get(num).getQueryResult());
		}
		return convertView;
	}
	
	private class ViewHolder{
		public TextView tv1;
		public TextView tv2;
	}

}
