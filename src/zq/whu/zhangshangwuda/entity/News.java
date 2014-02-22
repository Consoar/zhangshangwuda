package zq.whu.zhangshangwuda.entity;

public class News {

	/**
	 * url地址
	 */
	private String url;
	/**
	 * 文章标题
	 */
	private String title;
	/**
	 * 文章时间
	 */
	private String time;
	/**
	 * 文章正文
	 */
	private String content;

	public News() {
	}

	public News(String url, String title, String time, String content) {
		super();
		this.url = url;
		this.title = title;
		this.time = time;
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
