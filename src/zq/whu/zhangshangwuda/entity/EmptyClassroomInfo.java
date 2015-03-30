package zq.whu.zhangshangwuda.entity;

import java.util.List;
import java.util.TreeMap;

public class EmptyClassroomInfo {

	private TreeMap<String,TreeMap<String,TreeMap<String,List<String>>>> info;
	private String success;

	/**
	 * @return the info
	 */
	public TreeMap<String,TreeMap<String,TreeMap<String,List<String>>>> getInfo() {
		return info;
	}

	/**
	 * @return the success
	 */
	public String getSuccess() {
		return success;
	}

}
