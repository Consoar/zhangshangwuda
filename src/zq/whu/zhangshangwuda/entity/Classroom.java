package zq.whu.zhangshangwuda.entity;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import android.R.integer;

public class Classroom implements Comparable<Classroom> {
	private String name;
	private List<String> lessonsState;

	private Integer freeLength = 0;
	private boolean isFree;// ֻҪ��һ�ڿο��ж������
	private String freeTimeString;

	public Classroom(String name, List<String> lessonsState) {
		this.name = name;
		this.lessonsState = lessonsState;

	}

	// �������������ʼ����Ѱ�Ĳ���
	public void query(int from, int to) {
		freeTimeString = getFreeTime(lessonsState, from, to);
		if (StringUtils.isBlank(freeTimeString)) {
			isFree = false;
		} else {
			isFree = true;
		}
	}

	/**
	 * @return the isFree
	 */
	public boolean isFree() {
		return isFree;
	}

	/**
	 * @param isFree
	 *            the isFree to set
	 */
	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	// �����Ȳ�ѯ��
	public String getQueryResult() {
		return name + "   " + freeTimeString;
	}

	// ��ȡĳ��ʱ��ĳ�������е��޿�ʱ��,���û���޿�ʱ�Σ����ص�����Ϊ����
	public String getFreeTime(List<String> lessonsState, int from, int to) {
		final String separator = "n";
		StringBuilder tempBuilder = new StringBuilder();
		// "!"������ʾ���ǲ��ǿ��еĽ��ң�����Ϊ��ƥ�䷽���ں����һ��������
		// "n"��Ϊ����λ��ĿΣ���������ָ�
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
			if (StringUtils.countMatches(temp, separator) == 1) {
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

	

	@Override
	public int compareTo(Classroom another) {
		return -this.freeLength.compareTo(another.freeLength);
	}
}
