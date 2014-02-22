package zq.whu.zhangshangwuda.ui.news;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import zq.whu.zhangshangwuda.ui.MyApplication;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageViewFromUrl extends FrameLayout {

	private ProgressBar mLoading;
	private ImageView mImage;
	private float mDensity = 1f;
	private float mTargetW, mTargetH;
	private Context mContext;
	public ImageViewFromUrl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ImageViewFromUrl(Context context) {
		this(context, null);
	}

	public Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private ExecutorService executorService = Executors.newFixedThreadPool(5);
	private final Handler handler = new Handler();

	private void init(Context context) {
		mContext=context;
		// init layout params
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		// loading progress bar
		mLoading = new ProgressBar(context);
		mLoading.setLayoutParams(params);
		mLoading.setProgress(android.R.attr.progressBarStyleSmall);
		// image view to display the bitmap
		mImage = new ImageView(context);
		mImage.setLayoutParams(params);
		removeAllViews();
		addView(mLoading);
		addView(mImage);
	}

	public void load(final String imageUrl, final int w, final int h) {
		// 如果缓存过就从缓存中取出数据
		mImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.putExtra("url", imageUrl);
				intent.setClass(mContext, TouchImageViewActivity.class);
				mContext.startActivity(intent);
			}
		});
		MyApplication.getInstance().mImageLoader.loadImage(imageUrl,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						hide(mLoading);
						mImage.setImageBitmap(loadedImage);
						show(mImage);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				});
	}

	protected Bitmap changeBitmapSize(Bitmap orgBitmap, int w, int h) {
		mTargetH = h * mDensity;
		mTargetW = w * mDensity;

		int orgWidth = orgBitmap.getWidth();
		int orgHeight = orgBitmap.getHeight();

		float scaleWidth = mTargetW / orgWidth;
		float scaleHeight = mTargetH / orgHeight;
		float scale = Math.min(scaleWidth, scaleHeight);
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap resizedBitmap = Bitmap.createBitmap(orgBitmap, 0, 0, orgWidth,
				orgHeight, matrix, true);

		return resizedBitmap;
	}

	private void hide(View v) {
		if (v != null)
			v.setVisibility(View.GONE);
	}

	private void show(View v) {
		if (v != null)
			v.setVisibility(View.VISIBLE);
	}
}
