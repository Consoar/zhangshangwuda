package zq.whu.zhangshangwuda.entity;

public class Lessons {
	/**
	 * 课程Id
	 */
	private String id;
	/**
	 * 课程名称
	 */
	private String name;
	/**
	 * 星期几上课
	 */
	private String day;
	/**
	 * 起止星期
	 */
	private String ste;
	/**
	 * 每几星期
	 */
	private String mjz;
	/**
	 * 第几节
	 */
	private String time;
	/**
	 * 上课地点
	 */
	private String place;
	/**
	 * 教师姓名
	 */
	private String teacher;
	/**
	 * 备注信息
	 */
	private String other;

	public Lessons() {
	}

	public Lessons(String id, String name, String day, String ste, String mjz,
			String time, String place, String teacher, String other) {
		this.id = id;
		this.name = name;
		this.day = day;
		this.ste = ste;
		this.mjz = mjz;
		this.time = time;
		this.place = place;
		this.teacher = teacher;
		this.other = other;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getSte() {
		return ste;
	}

	public void setSte(String ste) {
		this.ste = ste;
	}

	public String getMjz() {
		return mjz;
	}

	public void setMjz(String mjz) {
		this.mjz = mjz;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
}
