package zq.whu.zhangshangwuda.ui.wifi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.os.Parcelable;
import zq.whu.zhangshangwuda.ui.R;

public class CreateOneKeyWifi extends Activity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.wifi_onekey);

		final Intent intent = getIntent();
		final String action = intent.getAction();
		if (Intent.ACTION_CREATE_SHORTCUT.equals(action)) {
			createShortCut();
			finish();
			return;
		}
	}

	void createShortCut() {
		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.setClass(this, OneKeyWifi.class);
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.Wifi_onekey));
		Parcelable shortIcon = Intent.ShortcutIconResource.fromContext(this,
				R.drawable.wifi_onekey_icon);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortIcon);
		setResult(RESULT_OK, intent);
	}

	private void addShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.Wifi_onekey));
		shortcut.putExtra("duplicate", false); // 不允许重复创建
		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.setClassName(this, OneKeyWifi.class.getName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				this, R.drawable.wifi_onekey_icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		sendBroadcast(shortcut);
	}

	private void delShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.Wifi_onekey));
		String appClass = this.getPackageName() + "."
				+ this.getLocalClassName();
		ComponentName comp = new ComponentName(this.getPackageName(), appClass);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
				Intent.ACTION_MAIN).setComponent(comp));

		sendBroadcast(shortcut);

	}
}
