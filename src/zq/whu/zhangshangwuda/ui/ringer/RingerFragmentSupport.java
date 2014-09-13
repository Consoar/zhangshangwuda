package zq.whu.zhangshangwuda.ui.ringer;

import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.ui.MainActivity;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class RingerFragmentSupport extends BaseSherlockFragment
{
	private static final String mpagename = "RingerFragment";
	private View rootView;
	private SeekBar seekBar_hour, seekBar_min;
	private TextView text_hour, text_min;
	private Button set_after_time;
	private CheckBox set_auto_time;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	private RingerTools rt;
	
	private int after_time_hour = 0;
	private int after_time_min = 0;
	
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
	
	/**
	 * 初始化
	 */
	private void init()
	{        
		rt = new RingerTools(getActivity());
		preferences = getActivity().getSharedPreferences("Data", Context.MODE_WORLD_READABLE);
		editor = preferences.edit();
        seekBar_hour = (SeekBar)rootView.findViewById(R.id.seekbar_hour);
        seekBar_min = (SeekBar)rootView.findViewById(R.id.seekbar_min);
        text_hour = (TextView)rootView.findViewById(R.id.hourtext);
        text_min = (TextView)rootView.findViewById(R.id.mintext);
        set_after_time = (Button)rootView.findViewById(R.id.button_set_after_time);
        set_auto_time = (CheckBox)rootView.findViewById(R.id.checkbox_set_auto_time);
        
        set_auto_time.setChecked(preferences.getBoolean("ringer_check", false));
        
        rt.initAlarmManager();
        rt.initNotificationManager();
        
        initListener();
        /////////////////////////////////////////////////////////////
//        List<Map<String, String>> mp = LessonsDb.getInstance(getActivity()).getLocalLessonsList();
//        
//        for (int i = 0; i < mp.size(); i++)
//        {
//        	Map<String, String> li = mp.get(i);
//        	System.out.println("mp[" + i + "]--->" + mp.get(i).toString());
//        }
        ////////////////////////////////////////////////////////////
	}
	
	
	/**
	 * 设置监听器
	 */
	private void initListener()
	{
		seekBar_hour.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) 
			{
				text_hour.setText(arg1 + " 小时");
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) 
			{
			}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) 
			{
			}
        });
		
		seekBar_min.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) 
			{
				text_min.setText(progress + " 分钟");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) 
			{
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) 
			{	
			}
		});
		
		set_after_time.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				Toast.makeText(getActivity(), "chilc", Toast.LENGTH_SHORT).show();
			}
		});
		
		set_auto_time.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
			{
				Toast.makeText(getSherlockActivity(), "change" + arg1, Toast.LENGTH_SHORT).show();
				editor.putBoolean("ringer_check", arg1);
				editor.commit();
			}
		});
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
