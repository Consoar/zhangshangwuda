package zq.whu.zhangshangwuda.views;

import java.util.ArrayList;
import java.util.List;

import zq.whu.zhangshangwuda.ui.R;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

public class DropPopMenu {
	private ArrayList<String> itemList;
	private Context context;
	private PopupWindow popupWindow;
	private ListView listView;
	private BaseAdapter mAdapter;

	public DropPopMenu(Context context) {
		this.context = context;
		initList();
	}

	public void setWidth(int width) {
		popupWindow.setWidth(width);
	}

	public void setmAdapter(BaseAdapter mAdapter) {
		this.mAdapter = mAdapter;
		listView.setAdapter(mAdapter);
	}

	// 设置菜单项点击监听器
	public void setOnItemClickListener(
			android.widget.AdapterView.OnItemClickListener listener) {
		listView.setOnItemClickListener(listener);
	}

	// 下拉式 弹出 pop菜单 parent
	public void showAsDropDown(View parent) {
		// 保证尺寸是根据屏幕像素密度来的
		popupWindow.showAsDropDown(parent, 2, 0);
	}

	// 隐藏菜单
	public void dismiss() {
		popupWindow.dismiss();
	}

	public boolean isShowing() {
		return popupWindow.isShowing();
	}

	private void initList() {
		View popupWindow_view = LayoutInflater.from(context).inflate(
				R.layout.droppopmenu, null);
		popupWindow_view.setFocusableInTouchMode(true);
		// 设置popupWindow的布局
		popupWindow = new PopupWindow(popupWindow_view,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new ColorDrawable(
				android.R.color.transparent));
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		listView = (ListView) popupWindow_view
				.findViewById(R.id.droppopmenu_listView);
		popupWindow.update();
	}
}