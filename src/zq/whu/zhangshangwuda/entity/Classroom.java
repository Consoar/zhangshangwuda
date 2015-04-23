package zq.whu.zhangshangwuda.entity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Classroom implements Comparable<Classroom> {
	private String name;
	private List<String> lessonsState;

	private Integer freeLength = 0;
	private boolean isFree;//是否空闲,再选定的节数范围中，有一节空闲即认为空闲
	private String freeTimeString;

	public Classroom(String name, List<String> lessonsState) {
		this.name = name;
		this.lessonsState = lessonsState;

	}

	//这个方法，用于初始化查询，然后在getQueryResult方法中得到查询结果
	public void query(int from, int to) {
		freeTimeString = getFreeTime(lessonsState, from, to);
		if (StringUtils.isBlank(freeTimeString)) {
			isFree = false;
		} else {
			isFree = true;
		}
	}

	
	public boolean isFree() {
		return isFree;
	}

	
	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	//得到查询结果，需先调用query方法
	public String getQueryResult() {
		return name + "   " + freeTimeString;
	}

	//得到一个教室的空闲时间
	public String getFreeTime(List<String> lessonsState, int from, int to) {
		final String separator = "n";
		StringBuilder tempBuilder = new StringBuilder();
		// "!"不是空闲的节数占位符
		// "n"在空闲的节数间的分割符，由于有10,11,12,这样两位数的节数
		for (int i = from; i <= to; i++) {
			if ("0".equals(lessonsState.get(i))) {
				tempBuilder.append(i + separator);
				freeLength++;
			} else {
				tempBuilder.append("!");
			}
		}
		tempBuilder.append("!");
		Pattern pattern = Pattern.compile("([0-9a-z]++)!");
		Matcher matcher = pattern.matcher(tempBuilder.toString());
		StringBuilder resultBuilder = new StringBuilder();
		for (; matcher.find();) {
			String temp = matcher.group(1);
			String noTail = temp.substring(0, temp.length() - 1);
			
			if (countMatches(temp, separator) == 1) {
				resultBuilder.append(noTail + "  ");
			} else {
				String fromNum = StringUtils.substringBefore(temp, separator);
				String toNum = StringUtils
						.substringAfterLast(noTail, separator);
				resultBuilder.append(fromNum + "-" + toNum + "   ");
			}
		}
		return resultBuilder.toString();
	}

	int countMatches(String str, String sub){
		int maxL = str.length() - sub.length() +1;
		String temp;
		int num = 0;
		for (int i = 0; i < maxL; i++){
			temp = str.substring(i,i+sub.length());
			if (temp.equals(sub)) {
				num ++;
				i = i+sub.length()-1;
			}
		}
		return num;
	}
	

	@Override
	public int compareTo(Classroom another) {
		return -this.freeLength.compareTo(another.freeLength);
	}
}
