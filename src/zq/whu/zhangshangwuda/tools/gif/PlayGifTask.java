package zq.whu.zhangshangwuda.tools.gif;

import zq.whu.zhangshangwuda.tools.gif.GifHelper.GifFrame;
import android.widget.ImageView;

public class PlayGifTask implements Runnable {
	int i = 0;
	ImageView iv;
	GifFrame[] frames;

	public PlayGifTask(ImageView iv, GifFrame[] frames) {
		this.iv = iv;
		this.frames = frames;
	}

	@Override
	public void run() {
		if (!frames[i].image.isRecycled()) {
			iv.setImageBitmap(frames[i].image);
		}
		iv.postDelayed(this, frames[i++].delay);
		i %= frames.length;
	}

	public void start() {
		iv.post(this);
	}

	public void stop() {
		if (null != iv)
			iv.removeCallbacks(this);
		iv = null;
		if (null != frames) {
			for (GifFrame frame : frames) {
				if (frame.image != null && !frame.image.isRecycled()) {
					frame.image.recycle();
					frame.image = null;
				}
			}
			frames = null;
		}
	}
}