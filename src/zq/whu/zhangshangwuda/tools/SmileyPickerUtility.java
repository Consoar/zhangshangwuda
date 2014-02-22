package zq.whu.zhangshangwuda.tools;

import zq.whu.zhangshangwuda.ui.MyApplication;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * User: qii Date: 13-1-18 from top to bottom:statusbar, actionbar, app content,
 * keyboard
 */
public class SmileyPickerUtility {
	public static void hideSoftInput(EditText paramEditText) {
		((InputMethodManager) MyApplication.getInstance().getSystemService(
				"input_method")).hideSoftInputFromWindow(
				paramEditText.getWindowToken(), 0);
	}

	public static void showKeyBoard(final EditText paramEditText) {
		paramEditText.requestFocus();
		paramEditText.post(new Runnable() {
			@Override
			public void run() {
				((InputMethodManager) MyApplication.getInstance()
						.getSystemService("input_method")).showSoftInput(
						paramEditText, 0);
			}
		});
	}

	public static int getScreenHeight(Activity paramActivity) {
		Display display = paramActivity.getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return metrics.heightPixels;
	}

	public static int getStatusBarHeight(Activity paramActivity) {
		Rect localRect = new Rect();
		paramActivity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		return localRect.top;

	}

	public static int getActionBarHeight(Activity paramActivity) {
		// // test on samsung 9300 android 4.1.2, this value is 96px
		// // but on galaxy nexus android 4.2, this value is 146px
		// // statusbar height is 50px
		// // I guess 4.1 Window.ID_ANDROID_CONTENT contain statusbar
		// int contentViewTop = paramActivity.getWindow()
		// .findViewById(Window.ID_ANDROID_CONTENT).getTop();
		//
		// // return contentViewTop - getStatusBarHeight(paramActivity);
		//
		// int[] attrs = new int[] { android.R.attr.actionBarSize };
		// TypedArray ta = paramActivity.obtainStyledAttributes(attrs);
		// return ta.getDimensionPixelSize(0,
		// DisplayTool.dip2px(MyApplication.getInstance(), 48));
		return 0;
	}

	// below status bar,include actionbar, above softkeyboard
	public static int getAppHeight(Activity paramActivity) {
		Rect localRect = new Rect();
		paramActivity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		return localRect.height();
	}

	// below actionbar, above softkeyboard
	public static int getAppContentHeight(Activity paramActivity) {
		// return SmileyPickerUtility.getScreenHeight(paramActivity)
		// - SmileyPickerUtility.getStatusBarHeight(paramActivity)
		// - SmileyPickerUtility.getActionBarHeight(paramActivity)
		// - SmileyPickerUtility.getKeyboardHeight(paramActivity);
		return SmileyPickerUtility.getScreenHeight(paramActivity)
				- SmileyPickerUtility.getStatusBarHeight(paramActivity)
				- SmileyPickerUtility.getKeyboardHeight(paramActivity);
	}

	public static int getKeyboardHeight(Activity paramActivity) {

		int height = SmileyPickerUtility.getScreenHeight(paramActivity)
				- SmileyPickerUtility.getStatusBarHeight(paramActivity)
				- SmileyPickerUtility.getAppHeight(paramActivity);
		if (height == 0) {
			height = DisplayTool.dip2px(MyApplication.getInstance(), 180);
		}
		return height;
	}

	public static boolean isKeyBoardShow(Activity paramActivity) {
		int height = SmileyPickerUtility.getScreenHeight(paramActivity)
				- SmileyPickerUtility.getStatusBarHeight(paramActivity)
				- SmileyPickerUtility.getAppHeight(paramActivity);
		return height != 0;
	}
}
