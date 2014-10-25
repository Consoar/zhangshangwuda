package zq.whu.zhangshangwuda.views;

import zq.whu.zhangshangwuda.ui.R;
import zq.whu.zhangshangwuda.ui.find.FindContentActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class FindLinearLayout extends LinearLayout
{
	private final int THEME_LIGHT = 0;
	private final int THEME_DARK = 1;
	private Context mContext;
	
	public FindLinearLayout(Context context)
	{
		this(context, null);
	}
	public FindLinearLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		init();
	}
	public FindLinearLayout(Context context, AttributeSet attrs, int defStyle) 
	{
		this(context, attrs);
	}
	
	private void init()
	{
		
	}
	
	public void addButton(final Context context, String title, final String TAB, int icon_id, int theme)
	{
		Button mButton = new Button(mContext);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 15, 0, 0);
		mButton.setGravity(0x03);
		mButton.setLayoutParams(params);
		mButton.setTextSize(20);
		mButton.setText(title);
		mButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(icon_id), null, null, null);
		if (theme == THEME_DARK)
		{
			mButton.setBackgroundResource(R.drawable.ringer_dark_button);
		}
		else
		{
			mButton.setBackgroundResource(R.drawable.ringer_button);
		}
		mButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent();
				i.setClass(context, FindContentActivity.class);
				i.putExtra("TAB", TAB);
				context.startActivity(i);
				((Activity)context).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			}
		});
		
		addView(mButton);
	}
}
