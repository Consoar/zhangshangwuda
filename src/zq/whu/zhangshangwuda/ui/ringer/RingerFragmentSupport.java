package zq.whu.zhangshangwuda.ui.ringer;

import java.util.Calendar;

import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.base.PreferenceHelper;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.ui.AboutActivity;
import zq.whu.zhangshangwuda.ui.HelpActivity;
import zq.whu.zhangshangwuda.ui.MainActivityTAB;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.SettingActivity;
import zq.whu.zhangshangwuda.views.toast.ToastUtil;
import android.content.Context;
import android.content.Intent;
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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class RingerFragmentSupport extends BaseSherlockFragment
{
	private final int MENU_GROUP = 1;
	private final int MENU_SETTING = Menu.FIRST;
	private final int MENU_HELP = Menu.FIRST + 1;
	private final int MENU_FEEDBACK = Menu.FIRST + 2;
	private final int MENU_ABOUT = Menu.FIRST + 3;
	
	private static final String mpagename = "RingerFragment";
	private View rootView;
	private SeekBar seekBar_hour, seekBar_min;
	private TextView text_hour, text_min;
	private Button set_after_time;
	private CheckBox set_auto_time;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private Calendar now_time;
	private RingerTools rt;
	
	private int after_time_hour = 0;
	private int after_time_min = 0;
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
		menu.add(MENU_GROUP, MENU_SETTING, MENU_SETTING, getResources().getString(R.string.LeftMenu_Setting));
		menu.add(MENU_GROUP, MENU_HELP, MENU_HELP, getResources().getString(R.string.LeftMenu_Help));
		menu.add(MENU_GROUP, MENU_FEEDBACK, MENU_FEEDBACK, getResources().getString(R.string.LeftMenu_FeedBack)); 
		menu.add(MENU_GROUP, MENU_ABOUT, MENU_ABOUT, getResources().getString(R.string.LeftMenu_About)); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent = new Intent();
		switch (item.getItemId())
		{
		case MENU_SETTING:
			intent.setClass(getActivity(),SettingActivity.class);
			startActivity(intent);
			return true;
		case MENU_HELP:
			intent.setClass(getActivity(),HelpActivity.class);
			startActivity(intent);
			return true;
		case MENU_FEEDBACK:
			MainActivityTAB.agent.startFeedbackActivity();
			return true;
		case MENU_ABOUT:
			intent.setClass(getActivity(),AboutActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		int mTheme = PreferenceHelper.getTheme(getActivity());
		if (mTheme == R.style.MyLightTheme)
			rootView = MyApplication.getLayoutInflater().inflate(R.layout.ringer, container, false);
		else
			rootView = MyApplication.getLayoutInflater().inflate(R.layout.ringer_dark, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		int nowWeek = LessonsTool.getNowWeek(getActivity());
		MainActivityTAB.MainActivityActionBar.setSubtitle("第" + nowWeek + "周");
		init();
	}
	
	/**
	 * 初始化
	 */
	@SuppressWarnings("deprecation")
	private void init()
	{        
		now_time = Calendar.getInstance();
		//now_time.setTimeInMillis(System.currentTimeMillis());
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
        
        rt.initAudioManager();
        rt.initAlarmManager();
        rt.initNotificationManager();
        
        initListener();
        threadForSeekBar();
	}
	
	private void threadForSeekBar()
	{
		new Thread()
        {
        	public void run()
        	{
        		while (true)
        		{
        			initSeekBar();
            		try 
            		{
    					sleep(60 * 1000);
    				} 
            		catch (InterruptedException e) 
            		{
    					e.printStackTrace();
    				}
        		}
        	}
        }.start();
	}
	
	private void initSeekBar()
	{
		long set_time = preferences.getLong("set_time", 0);
		if (set_time != 0)
		{
			now_time.setTimeInMillis(System.currentTimeMillis() - set_time);
			System.out.println(System.currentTimeMillis() - set_time);
			int set_hour = preferences.getInt("set_hour", 0);
			int set_min = preferences.getInt("set_min", 0);
			
			long time = System.currentTimeMillis() - set_time;  //已经经历的时间
			int past_hour = (int)(time/(60 * 60 * 1000));
			int past_min = (int)((time - past_hour * 60 * 60 * 1000)/(60 * 1000));
			
			seekBar_hour.setProgress(set_hour - past_hour);
			seekBar_min.setProgress(set_min - past_min);
		}
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
				after_time_hour = arg1;
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
				after_time_min =  progress;
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
				if (after_time_hour != 0 || after_time_min != 0)
				{
					editor.putLong("set_time", System.currentTimeMillis());
					editor.putInt("set_hour", after_time_hour); //设定几小时后
					editor.putInt("set_min", after_time_min);   //设定几分钟后
					editor.commit();
					ToastUtil.showToast(getActivity(), "开始静音至" + after_time_hour + "小时" 
							+ after_time_min + "分钟后");
					rt.setSilent(true);
					rt.setAfterTimeNoSilent(after_time_hour, after_time_min);
					rt.showNotificationByTime(after_time_hour, after_time_min);
				}
				else
				{
					ToastUtil.showToast(getActivity(), "关闭定时静音");
					Intent i = new Intent(getActivity(), OffSilentReceiver.class);
					i.putExtra("isAfter", "yes");
					getActivity().sendBroadcast(i);
				}
			}
		});
		
		set_auto_time.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
			{
				ToastUtil.showToast(getActivity(), arg1 ? "开启自动静音": "关闭自动静音");
//				Toast.makeText(getSherlockActivity(), arg1 ? "开启自动静音": "关闭自动静音",
//						Toast.LENGTH_SHORT).show();
				editor.putBoolean("ringer_check", arg1);
				editor.commit();
				if (!rt.setTimeOfSilent(arg1))
				{
					set_auto_time.setChecked(false);
				}
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
