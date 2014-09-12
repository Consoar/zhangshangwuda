package zq.whu.zhangshangwuda.ui.ringer;

import java.util.List;
import java.util.Map;

import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.ui.MainActivity;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.news.NewsFragmentSupport;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class RingerFragmentSupport extends BaseSherlockFragment
{
	private static final String mpagename = "RingerFragment";
	private View rootView;
	private Button bs,bn;
	
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
        bs = (Button)rootView.findViewById(R.id.test_s);
        bn = (Button)rootView.findViewById(R.id.test_n);
        
        bs.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		RingerTools rt = new RingerTools(getActivity());
        		rt.initAudioManager();
        		rt.setSilent(true);
        		Toast.makeText(getActivity(), "开启静音", Toast.LENGTH_SHORT).show();
        	}
        });
        
        bn.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		RingerTools rt = new RingerTools(getActivity());
        		rt.initAudioManager();
        		rt.setSilent(false);
        		Toast.makeText(getActivity(), "关闭静音", Toast.LENGTH_SHORT).show();
        	}
        });
   
        /////////////////////////////////////////////////////////////
        List<Map<String, String>> mp = LessonsDb.getInstance(getActivity()).getLocalLessonsList();
        
        for (int i = 0; i < mp.size(); i++)
        {
        	Map<String, String> li = mp.get(i);
        	System.out.println("mp[" + i + "]--->" + mp.get(i).toString());
        }
        ////////////////////////////////////////////////////////////
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
