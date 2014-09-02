package zq.whu.zhangshangwuda.ui.ringer;

import java.util.ArrayList;
import java.util.List;

import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.ui.MainActivity;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenuItem;

import com.umeng.analytics.MobclickAgent;

public class RingerFragmentSupport extends BaseSherlockFragment
{
	private static final String mpagename = "RingerFragment";
	private View rootView;
	private SatelliteMenu sate_menu;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = MyApplication.getLayoutInflater().inflate(R.layout.ringer, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		int nowWeek = LessonsTool.getNowWeek(getActivity());
		MainActivity.MainActivityActionbar.setSubtitle("第" + nowWeek + "周");
		init();
	}
	
	private void init()
	{
		sate_menu = (SatelliteMenu)rootView.findViewById(R.id.sate_menu);
		float distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        sate_menu.setSatelliteDistance((int) distance);
        sate_menu.setExpandDuration(500);
        sate_menu.setCloseItemsOnClick(true);
        sate_menu.setTotalSpacingDegree(90);
        sate_menu.setMainImage(R.drawable.sate_menu);
        
        List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
        items.add(new SatelliteMenuItem(4, R.drawable.sate_menu));
        items.add(new SatelliteMenuItem(4, R.drawable.sate_menu));
        items.add(new SatelliteMenuItem(4, R.drawable.sate_menu));
        items.add(new SatelliteMenuItem(3, R.drawable.sate_menu));
        items.add(new SatelliteMenuItem(2, R.drawable.sate_menu));
        items.add(new SatelliteMenuItem(1, R.drawable.sate_menu));
//        items.add(new SatelliteMenuItem(5, R.drawable.sat_item));
        sate_menu.addItems(items); 
	}
	
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPageEnd(mpagename);
	}
	
	public void onResume()
	{
		super.onResume();
		MobclickAgent.onPageStart(mpagename);
	}
}
