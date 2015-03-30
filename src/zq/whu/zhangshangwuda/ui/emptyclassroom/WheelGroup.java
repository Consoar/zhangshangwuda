package zq.whu.zhangshangwuda.ui.emptyclassroom;

import java.util.ArrayList;
import java.util.List;

import zq.whu.zhangshangwuda.ui.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;


public class WheelGroup extends LinearLayout {

	private int wheelNumber;
	private Context mContext;
	private List<WheelView> wheelContainer;
	private float textSize;
	private float additionalItemHeight;
	private int valueTextColor;
	private float leftIndicatorWidth;

	public WheelGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		wheelContainer = new ArrayList<WheelView>();
		mContext = context;
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.WheelGroup);
		wheelNumber = typedArray.getInt(R.styleable.WheelGroup_wheelNumber, 1);
		additionalItemHeight = typedArray.getDimension(
				R.styleable.WheelGroup_additionalItemHeight, 25);
		textSize = typedArray.getDimension(R.styleable.WheelGroup_textSize, 23);
		valueTextColor = typedArray.getColor(
				R.styleable.WheelGroup_valueTextColor, 0xffff0000);
		leftIndicatorWidth = typedArray.getDimension(
				R.styleable.WheelGroup_leftIndicatorWidth, 15);
		initView();

	}

	public List<WheelView> getWheelContainer() {
		return wheelContainer;
	}

	private void initView() {
		setGravity(Gravity.CENTER_VERTICAL);
		// ������ı�ʾ
		this.addView(getSeperator());
		View rect = new View(mContext);
		LayoutParams params = new LayoutParams((int) leftIndicatorWidth,
				(int) (textSize + additionalItemHeight));
		rect.setBackgroundColor(valueTextColor);
		rect.setLayoutParams(params);
		this.addView(rect);
		this.addView(getSeperator());

		for (int i = 0; i < wheelNumber; i++) {
			WheelView wheelView = new WheelView(mContext);
			LayoutParams wheelViewParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			wheelViewParams.weight = 1;
			wheelView.setLayoutParams(wheelViewParams);
			wheelView.setCyclic(true);
			wheelView.TEXT_SIZE = (int) textSize;
			wheelView.VALUE_TEXT_COLOR = valueTextColor;
			wheelView.ADDITIONAL_ITEM_HEIGHT = (int) additionalItemHeight;
			this.addView(wheelView);
			// �ָ���

			this.addView(getSeperator());

			wheelContainer.add(wheelView);
		}
	}

	private View getSeperator() {
		View view = new View(mContext);
		LayoutParams lineParams = new LayoutParams(1, LayoutParams.MATCH_PARENT);
		view.setLayoutParams(lineParams);
		view.setBackgroundColor(0xffb9b9bb);
		return view;
	}

}
