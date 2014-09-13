package zq.whu.zhangshangwuda.ui.ringer;

public class TimeOfLessons 
{
	private int start_hour = 0;
	private int start_min = 0;
	private int end_hour = 0;
	private int end_min = 0;
	private int day = 0;
	
	public TimeOfLessons(int start_lesson, int end_lesson, int lday)
	{
		this.day = lday;
		
		switch (start_lesson)
		{
		case 1:
			start_hour = 8;
			start_min = 0;
			break;
		case 2:
			start_hour = 8;
			start_min = 50;
			break;
		case 3:
			start_hour = 9;
			start_min = 50;
			break;
		case 4:
			start_hour = 10;
			start_min = 40;
			break;
		case 5:
			start_hour = 11;
			start_min = 30;
			break;
		case 6:
			start_hour = 14;
			start_min = 5;
			break;
		case 7:
			start_hour = 14;
			start_min = 55;
			break;
		case 8:
			start_hour = 15;
			start_min = 45;
			break;
		case 9:
			start_hour = 16;
			start_min = 40;
			break;
		case 10:
			start_hour = 17;
			start_min = 30;
			break;
		case 11:
			start_hour = 18;
			start_min = 30;
			break;
		case 12:
			start_hour = 19;
			start_min = 20;
			break;
		case 13:
			start_hour = 20;
			start_min = 10;
			break;
		}
		
		switch (end_lesson)
		{
		case 1:
			end_hour = 8;
			end_min = 45;
			break;
		case 2:
			end_hour = 9;
			end_min = 35;
			break;
		case 3:
			end_hour = 18;
			end_min = 35;
			break;
		case 4:
			end_hour = 11;
			end_min = 25;
			break;
		case 5:
			end_hour = 12;
			end_min = 15;
			break;
		case 6:
			end_hour = 14;
			end_min = 50;
			break;
		case 7:
			end_hour = 15;
			end_min = 40;
			break;
		case 8:
			end_hour = 16;
			end_min = 30;
			break;
		case 9:
			end_hour = 17;
			end_min = 25;
			break;
		case 10:
			end_hour = 18;
			end_min = 15;
			break;
		case 11:
			end_hour = 19;
			end_min = 15;
			break;
		case 12:
			end_hour = 20;
			end_min = 5;
			break;
		case 13:
			end_hour = 20;
			end_min = 55;
			break;
		}
	}
	
	public int getStartHour()
	{
		return start_hour;
	}
	
	public int getStartMin()
	{
		return start_min;
	}
	
	public int getEndHour()
	{
		return end_hour;
	}
	
	public int getEndMin()
	{
		return end_min;
	}
	
	public int getDay()
	{
		return day;
	}
}
