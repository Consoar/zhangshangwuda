package zq.whu.zhangshangwuda.adapter;

import java.util.List;

import zq.whu.zhangshangwuda.ui.emptyclassroom.WheelAdapter;


public class WheelViewAdapter implements WheelAdapter {

	private List<String> list;
	private int width;
	public WheelViewAdapter(List<String> list,int width) {
		this.list = list;
		this.width=width;
	}

	@Override
	public int getItemsCount() {
		return list.size();
	}

	@Override
	public String getItem(int index) {
		if(index>=list.size()){
			index=list.size()-1;
		}
		return list.get(index);
	}

	//这个方法决定了WheelView的宽度，大致数字和宽度的字数对应
	
	@Override
	public int getMaximumLength() {
		return width;
	}

}
