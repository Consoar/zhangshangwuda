package zq.whu.zhangshangwuda.tools;

import static zq.whu.zhangshangwuda.tools.LogUtils.makeLogTag;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileCache {
	private static LogUtils LOG;
	private static final String TAG = makeLogTag(FileCache.class);
	private static FileCache fileCache; // 本类的引用
	private static String strTxtPath;// 文本保存的路径

	// private Context context;

	private FileCache() {
		// this.context = context;
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory()
					.getPath()
					+ "/Android/data/zq.whu.zhangshangwuda.ui/cache/TXT/");
			if (!file.exists()) {
				if (file.mkdirs()) {
					strTxtPath = file.getAbsolutePath();
				}
			} else {
				strTxtPath = file.getAbsolutePath();
			}
		} else {
			File file = new File(
					"/data/data/zq.whu.zhangshangwuda.ui/cache/TXT/");
			if (!file.exists()) {
				if (file.mkdirs()) {
					strTxtPath = file.getAbsolutePath();
				}
			} else {
				strTxtPath = file.getAbsolutePath();
			}
		}
	}

	public static FileCache getInstance() {
		if (null == fileCache) {
			fileCache = new FileCache();
		}
		return fileCache;
	}

	public String getUrlCache(String url) {
		if (url == null) {
			return null;
		}

		String result = null;
		File file = new File(strTxtPath + "/"
				+ StringUtils.replaceUrlWithPlus(url));
		if (file.exists() && file.isFile()) {
			try {
				result = FileUtils.readTextFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void setUrlCache(String data, String url) {
		if (strTxtPath == null) {
			return;
		}
		File dir = new File(strTxtPath);
		if (!dir.exists()
				&& Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
			dir.mkdirs();
		}
		File file = new File(strTxtPath + "/"
				+ StringUtils.replaceUrlWithPlus(url));
		try {
			// 创建缓存数据到磁盘，就是创建文件
			FileUtils.writeTextFile(file, data);
		} catch (IOException e) {
			LOG.D(TAG, "write " + file.getAbsolutePath() + " data failed!");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除SD卡上的全部缓存
	 * */
	public int clearAllData() {

		// File imgDir = new File(strImgPath);
		File txtDir = new File(strTxtPath);
		// File[] imgFiles = imgDir.listFiles();
		File[] txtFiles = txtDir.listFiles();
		// int m = imgFiles.length;
		int x = txtFiles.length;

		int g = 0;
		int t = 0;
		// for (int i = 0; i < m; i++) {
		// if (imgFiles[i].exists()) {
		// if (imgFiles[i].delete())
		// g++;
		// } else
		// g++;
		//
		// }
		for (int i = 0; i < x; i++) {
			if (txtFiles[i].exists()) {
				if (txtFiles[i].delete()) {
					t++;
				}
			} else
				t++;
		}
		// if (g == m && t == x) {
		// return 1;
		// }
		if (t == x) {
			return 1;
		}
		return 0;
	}

}