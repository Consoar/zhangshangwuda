/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zq.whu.zhangshangwuda.ui.lessons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zq.whu.zhangshangwuda.base.BaseSherlockFragment;
import zq.whu.zhangshangwuda.db.LessonsDb;
import zq.whu.zhangshangwuda.tools.DisplayTool;
import zq.whu.zhangshangwuda.tools.LessonsTool;
import zq.whu.zhangshangwuda.tools.SettingSharedPreferencesTool;
import zq.whu.zhangshangwuda.tools.SmileyPickerUtility;
import zq.whu.zhangshangwuda.tools.StringUtils;
import zq.whu.zhangshangwuda.tools.ViewCompat;
import zq.whu.zhangshangwuda.ui.MyApplication;
import zq.whu.zhangshangwuda.ui.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LessonsFragment extends BaseSherlockFragment {
	public static final String ARG_PAGE = "page";
	private View rootView;
	private int minClassHeight = 50;
	private int minClassWidth = 100;
	private int minClassWidthPX = 50;
	private int minClassHeightPX = 50;
	private int padding = 2;
	private int displayWidth;
	private int displayHeight;
	private int densityDpi;
	private boolean isTablet;
	private LinearLayout tday;
	private int nowWeek;
	private static int ccolor = -1;
	private List<Map<String, String>> lessonsList;
	private boolean isShowNowLessons;
	private String colors[] = { "#EEFFFF", "#33B5E5", "#AA66CC", "#99CC00",
			"#FFBB33", "#FF4444" };
	private int mPageNumber;
	private List<String> PdList = new ArrayList<String>();
	private double lessonsWidth, lessonsHeight;

	public static BaseSherlockFragment create(int pageNumber) {
		LessonsFragment fragment = new LessonsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public LessonsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.lessons_main, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// System.out.println("NewsFragmentCommon_onActivityCreated");
		displayWidth = MyApplication.getDisplayWidth();
		displayHeight = MyApplication.getDisplayHeight();
		densityDpi = MyApplication.getDensityDpi();
		isTablet = DisplayTool.isTablet(getActivity());
		isShowNowLessons = SettingSharedPreferencesTool
				.lessons_isShowNowLessons(getActivity());
		calLessonsWidth();
		calLessonsHeight();
		showLessons();
	}

	private void calLessonsWidth() {
		// 计算宽
		if (displayWidth > displayHeight)
			lessonsWidth = displayWidth;
		else
			lessonsWidth = displayHeight;
		int leftWidth = dip2px(getSherlockActivity(), 20);
		lessonsWidth = lessonsWidth - leftWidth;
		lessonsWidth = lessonsWidth / 7;
		// System.out.println("lessonsWidthDP "
		// + px2dip(getSherlockActivity(), Math.round(lessonsWidth)));
		if (px2dip(getSherlockActivity(), Math.round(lessonsWidth)) > minClassWidth) {
			minClassWidth = (int) Math.round(lessonsWidth);
			minClassWidth = px2dip(getSherlockActivity(), minClassWidth);
		}
		minClassWidthPX = dip2px(getSherlockActivity(), minClassWidth);
		setDayItemWidth(minClassWidthPX);
	}

	private void calLessonsHeight() {
		// 计算高
		// if (displayWidth < displayHeight)
		// lessonsHeight = displayWidth;
		// else
		// lessonsHeight = displayHeight;
		// int actionbarHeight = dip2px(getSherlockActivity(), 48);
		// int statusBarHeight = dip2px(getSherlockActivity(), 25);
		// int titleHeight = dip2px(getSherlockActivity(), 30);
		// lessonsHeight = lessonsHeight - actionbarHeight - titleHeight
		// - statusBarHeight;
		// lessonsHeight = lessonsHeight / 5.5;
		// System.out.println("lessonsHeightDP "+px2dip(getSherlockActivity(),
		// Math.round(lessonsHeight)));
		// if (px2dip(getSherlockActivity(), Math.round(lessonsHeight)) >
		// minClassWidth) {
		// minClassHeight = (int) Math.round(lessonsHeight);
		// minClassHeight = px2dip(getSherlockActivity(), minClassHeight);
		// }

		// 直接为宽度一半
		int titleHeight = dip2px(getSherlockActivity(), 30);
		minClassHeight = minClassWidth / 2;
		minClassHeightPX = dip2px(getSherlockActivity(), minClassHeight);
		setLeftItemHeight(minClassHeightPX, titleHeight);
	}

	private void setLeftItemHeight(int minHeight, int nonHeight) {
		rootView.findViewById(R.id.left_non).setMinimumHeight(nonHeight);
		rootView.findViewById(R.id.left_1).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_2).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_3).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_4).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_5).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_6).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_7).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_8).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_9).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_10).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_11).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_12).setMinimumHeight(minHeight);
		rootView.findViewById(R.id.left_13).setMinimumHeight(minHeight);
	}

	private void setDayItemWidth(int minWidth) {
		rootView.findViewById(R.id.day_1).setLayoutParams(
				new LinearLayout.LayoutParams(minWidth,
						LayoutParams.WRAP_CONTENT));
		rootView.findViewById(R.id.day_2).setLayoutParams(
				new LinearLayout.LayoutParams(minWidth,
						LayoutParams.WRAP_CONTENT));
		rootView.findViewById(R.id.day_3).setLayoutParams(
				new LinearLayout.LayoutParams(minWidth,
						LayoutParams.WRAP_CONTENT));
		rootView.findViewById(R.id.day_4).setLayoutParams(
				new LinearLayout.LayoutParams(minWidth,
						LayoutParams.WRAP_CONTENT));
		rootView.findViewById(R.id.day_5).setLayoutParams(
				new LinearLayout.LayoutParams(minWidth,
						LayoutParams.WRAP_CONTENT));
		rootView.findViewById(R.id.day_6).setLayoutParams(
				new LinearLayout.LayoutParams(minWidth,
						LayoutParams.WRAP_CONTENT));
		rootView.findViewById(R.id.day_7).setLayoutParams(
				new LinearLayout.LayoutParams(minWidth,
						LayoutParams.WRAP_CONTENT));

	}

	public void showLessons() {
		// nowWeek = LessonsTool.getNowWeek(getActivity());
		nowWeek = mPageNumber + 1;
		// getSupportActionBar().setSubtitle("第" + String.valueOf(nowWeek) +
		// "周");
		for (int i = 1; i <= 7; ++i) {
			setEveryDayLessons(i);
		}
	}

	public void setEveryDayLessons(int day) {
		// 每天的课程设置
		switch (day) {
		case 1:
			tday = (LinearLayout) rootView.findViewById(R.id.day_1);
			break;
		case 2:
			tday = (LinearLayout) rootView.findViewById(R.id.day_2);
			break;
		case 3:
			tday = (LinearLayout) rootView.findViewById(R.id.day_3);
			break;
		case 4:
			tday = (LinearLayout) rootView.findViewById(R.id.day_4);
			break;
		case 5:
			tday = (LinearLayout) rootView.findViewById(R.id.day_5);
			break;
		case 6:
			tday = (LinearLayout) rootView.findViewById(R.id.day_6);
			break;
		case 7:
			tday = (LinearLayout) rootView.findViewById(R.id.day_7);
			break;
		}
		tday.addView(getTitleView(day));
		PdList.clear();
		lessonsList = LessonsDb.getInstance(getActivity()).getLessonsByDay(
				Integer.toString(day));
		lessonsList = LessonsTool.sortLessonsByTime(lessonsList);
		// 处理课程
		int size = lessonsList.size();
		String tstring = null, sbegin, send, stime, name, time, place, id;
		int a, b, t;
		t = 1;
		for (int i = 0; i < size; ++i) {
			Map<String, String> map = new HashMap<String, String>();
			map = lessonsList.get(i);
			tstring = map.get("time");
			// 提取上课起止周数
			String tsw = map.get("ste");
			int startweek = StringUtils
					.toInt(tsw.substring(0, tsw.indexOf("-")));
			int endweek = Integer.parseInt(tsw.substring(tsw.indexOf("-") + 1));
			if (startweek == 0 || endweek == 0)
				continue;
			int mjz = StringUtils.toInt(map.get("mjz"));
			if (isShowNowLessons) {
				// 判断双周是否该上
				if (mjz == 2 && (nowWeek - startweek) % mjz == 1)
					continue;
				// 判断是否已经停课
				if (!(startweek <= nowWeek && nowWeek <= endweek))
					continue;
			}
			// 提取上课节数
			tstring = tstring.substring(0, tstring.indexOf("节"));
			a = Integer.parseInt(tstring.substring(0, tstring.indexOf("-")));
			b = Integer.parseInt(tstring.substring(tstring.indexOf("-") + 1));
			// 生成上课时间
			sbegin = getBeginTime(a);
			send = getEndTime(b);
			stime = sbegin + "-" + send;
			if (a - t > 0) {
				// for (int j = t; j <= a - 1; ++j)
				// setNoClass(tday, 1, 0);
				setNoClass(tday, a - 1 - t + 1, 0);
				t = b + 1;
			}
			name = map.get("name");
			int tt = name.indexOf(" ");
			if (tt > 0)
				name = name.substring(0, tt);
			place = map.get("place");
			time = map.get("time");
			id = map.get("id");
			String time1, time2, StrPd;
			time1 = time;
			time1 = time1.substring(0, time1.indexOf("节"));
			StrPd = String.valueOf(day) + time1;
			//这里缺少判断周数的逻辑，但是我不想改了，好麻烦
			if (PdList.contains(StrPd))
				continue;
			boolean flag = false;
			if (i + 1 < size) {
				time2 = lessonsList.get(i + 1).get("time");
				time2 = time2.substring(0, time2.indexOf("节"));
				if (time1.equals(time2))
					flag = true;
				if (!PdList.contains(StrPd))
					PdList.add(StrPd);
			}
			ccolor++;
			setClass(tday, id, String.valueOf(day), name, place, time, stime, b
					- a + 1, ccolor, flag);
			t = b + 1;
		}
		if (t < 14)
			setNoClass(tday, 14 - t + 1, 0);
	}

	public void removeEveryDayLessons(int day) {
		// 每天的课程设置
		switch (day) {
		case 1:
			tday = (LinearLayout) rootView.findViewById(R.id.day_1);
			break;
		case 2:
			tday = (LinearLayout) rootView.findViewById(R.id.day_2);
			break;
		case 3:
			tday = (LinearLayout) rootView.findViewById(R.id.day_3);
			break;
		case 4:
			tday = (LinearLayout) rootView.findViewById(R.id.day_4);
			break;
		case 5:
			tday = (LinearLayout) rootView.findViewById(R.id.day_5);
			break;
		case 6:
			tday = (LinearLayout) rootView.findViewById(R.id.day_6);
			break;
		case 7:
			tday = (LinearLayout) rootView.findViewById(R.id.day_7);
			break;
		}
		tday.removeAllViewsInLayout();
	}

	// 获取每节课的开始时间
	String getBeginTime(int k) {
		switch (k) {
		case 1:
			return "08:00";
		case 2:
			return "08:50";
		case 3:
			return "09:50";
		case 4:
			return "10:40";
		case 5:
			return "11:30";
		case 6:
			return "14:05";
		case 7:
			return "14:55";
		case 8:
			return "15:45";
		case 9:
			return "16:40";
		case 10:
			return "17:30";
		case 11:
			return "18:30";
		case 12:
			return "19:20";
		case 13:
			return "20:10";
//		case 14:
//			return "";
		}
		return null;
	}

	// 获取每节课的结束时间
	String getEndTime(int k) {
		switch (k) {
		case 1:
			return "08:45";
		case 2:
			return "09:35";
		case 3:
			return "10:35";
		case 4:
			return "11:25";
		case 5:
			return "12:15";
		case 6:
			return "14:50";
		case 7:
			return "15:40";
		case 8:
			return "16:30";
		case 9:
			return "17:25";
		case 10:
			return "18:15";
		case 11:
			return "19:15";
		case 12:
			return "20:05";
		case 13:
			return "20:55";
//		case 14:
//			return "";
		}
		return null;
	}

	// 设置星期标题
	private View getTitleView(int k) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.lessons_item_title, null);
		String title = "MON";
		switch (k) {
		case 1:
			title = "MON";
			break;
		case 2:
			title = "TUE";
			break;
		case 3:
			title = "WED";
			break;
		case 4:
			title = "THU";
			break;
		case 5:
			title = "FRI";
			break;
		case 6:
			title = "SAT";
			break;
		case 7:
			title = "SUN";
			break;
		}
		((TextView) view.findViewById(R.id.lessons_item_title_TextView))
				.setText(title);
		return view;
	}

	// 设置课程的方法
	public void setClass(LinearLayout ll, String id, String day, String title,
			String place, String last, String time, int classes, int color,
			boolean isMore) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.lessons_item, null);
		view.setMinimumHeight(minClassHeightPX * classes);
		// view.setMinimumWidth(minClassWidthPX);
		int tcolor = color % 5 + 1;
		view.setBackgroundDrawable(getStateSelector(colors[tcolor]));
		if (isMore) {
			((TextView) view.findViewById(R.id.lessons_item_ismore))
					.setText("TRUE");
			((ImageView) view.findViewById(R.id.lessons_item_more))
					.setVisibility(View.VISIBLE);
		} else {
			((TextView) view.findViewById(R.id.lessons_item_ismore))
					.setText("FALSE");
		}
		((TextView) view.findViewById(R.id.lessons_item_id)).setText(id);
		((TextView) view.findViewById(R.id.lessons_item_day)).setText(day);
		((TextView) view.findViewById(R.id.lessons_item_title)).setText(title);
		((TextView) view.findViewById(R.id.lessons_item_place)).setText(place);
		((TextView) view.findViewById(R.id.lessons_item_last)).setText(last);
		((TextView) view.findViewById(R.id.lessons_item_time)).setText(time);
		view.setOnClickListener(new LessonOnClickListener());
		ll.addView(view);
	}

	public void setNoClass(LinearLayout ll, int classes, int color) {
		TextView blank = new TextView(getActivity());
		if (color == 0) {
			blank.setMinHeight(classes * minClassHeightPX);
			blank.setMinWidth(minClassWidthPX);
		}
		blank.setBackgroundColor(Color.parseColor(colors[color]));
		ll.addView(blank);
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** * 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	private StateListDrawable getStateSelector(String color) {
		StateListDrawable stalistDrawable = new StateListDrawable();
		// int normal = android.R.attr.state_empty;
		// int pressed = android.R.attr.state_pressed;
		// int focused = android.R.attr.state_focused;
		// int selected = android.R.attr.state_selected;
		ColorDrawable normalBG = new ColorDrawable(Color.parseColor(color));
		ColorDrawable pressedBG = new ColorDrawable(Color.parseColor("#AAAAAA"));
		int pressed = android.R.attr.state_pressed;
		int window_focused = android.R.attr.state_window_focused;
		int focused = android.R.attr.state_focused;
		int selected = android.R.attr.state_selected;
		stalistDrawable.addState(new int[] { pressed, window_focused },
				pressedBG);
		stalistDrawable.addState(new int[] { pressed, -focused }, pressedBG);
		stalistDrawable.addState(new int[] { selected }, pressedBG);
		stalistDrawable.addState(new int[] { focused }, pressedBG);
		// 没有任何状态时显示的图片，我们给它设置我空集合
		stalistDrawable.addState(new int[] {}, normalBG);
		return stalistDrawable;
	}

	// 点击课程的监听器
	class LessonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Toast.makeText(getActivity(), "你点击的是:" + id,
			// Toast.LENGTH_SHORT).show();
			String ismore, id, day, last;
			ismore = (String) ((TextView) v
					.findViewById(R.id.lessons_item_ismore)).getText();
			id = (String) ((TextView) v.findViewById(R.id.lessons_item_id))
					.getText();
			day = (String) ((TextView) v.findViewById(R.id.lessons_item_day))
					.getText();
			last = (String) ((TextView) v.findViewById(R.id.lessons_item_last))
					.getText();
			if (ismore.equals("TRUE")) {
				Intent intent = new Intent(getActivity(),
						LessonsDayActivity.class);
				intent.putExtra("day", Integer.valueOf(day));
				intent.putExtra("last", last.substring(0, last.indexOf("节")));
				startActivity(intent);
			} else {
				Intent intent = new Intent(getActivity(),
						LessonsAddActivity.class);
				intent.putExtra("id", id);
				startActivity(intent);
			}

		}
	}

	public void refreshLessons() {
		for (int i = 1; i <= 7; ++i) {
			removeEveryDayLessons(i);
		}
		showLessons();
	}

}
