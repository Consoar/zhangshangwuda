package zq.whu.zhangshangwuda.ui.news.fragment;

import zq.whu.zhangshangwuda.base.BaseSherlockFragment;

public class NewsFragmentBase extends BaseSherlockFragment {
	private boolean isShowMessage = false;

	public boolean isShowMessage() {
		return isShowMessage;
	}

	public void setShowMessage(boolean isShowMessage) {
		this.isShowMessage = isShowMessage;
	}
}