package zq.whu.zhangshangwuda.ui.find;

import zq.whu.zhangshangwuda.base.BaseThemeFragmentActivityWithoutAnime;
import zq.whu.zhangshangwuda.ui.R;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class FindContentActivity extends BaseThemeFragmentActivityWithoutAnime
{
	public static ActionBar FindActivityActionBar;
	
	private final int FIND_CONTENT = R.id.find_content;
	
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	
	private String TAB;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
			break;
		}
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.findcontent);
		init();
	}
	
	@SuppressLint("NewApi") 
	private void init()
	{
		FindActivityActionBar = getSupportActionBar();
		
		Intent intent = getIntent();
		TAB = intent.getStringExtra("TAB");
		
		mFragmentManager = getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(FIND_CONTENT, FindFragmentSupport.getFragmentByTAB(TAB));
		mFragmentTransaction.commit();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) 
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) 
		{
			finish();
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		}
		return super.dispatchKeyEvent(event);
	}
}
