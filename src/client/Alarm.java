/*
 * Alarm类：管理闹钟事件列表
 */

package client;

import java.util.TreeSet;

public class Alarm {
	private TreeSet<Event> alarmList = null;			// 按时间顺序排列
	
	public Alarm() {
		alarmList = new TreeSet<Event>();
	}
	
	public TreeSet<Event> getAlarmList()				// 返回整个AlarmList
	{
		return alarmList;
	}
	
	public synchronized Event getFirstElement()						// 返回首元素，即最近的事件
	{
		if (alarmList.size()>0)
		{
			return alarmList.first();
		}
		else
		{
			return null;
		}
	}
	
	public synchronized int getNumber()								// 返回alarmList的长度
	{
		return alarmList.size();
	}
	
	public synchronized void timeout(Event ev)						// 有一个事件到期了，则删去该事件
	{
		alarmList.remove(ev);
	}
	
	public synchronized void put(String s)							// 读入了一个新事件
	{
		Event ev = new Event(s);
		alarmList.add(ev);
	}
	
}

class Event implements Comparable<Event>{
	public int year, month, date, hour, minute;	// int类型的开始时间，用于时间比较
	public String time_t, time_e, place, activity;// String类型的开始时间、结束时间、地点、活动
	
	public Event(String s)				// 通过从网络接收到的字符串构造Event对象
	{
		String[] ss = s.split("\t");
		time_t = ss[0];
		time_e = ss[1];
		place = ss[2];
		activity = ss[3];
		String start_time = ss[0];
		String[] st = start_time.split(" ");
		String start_date = st[0];
		String start_hour_min = st[1];
		String[] ymd = start_date.split("/");
		year = Integer.parseInt(ymd[0]);
		month = Integer.parseInt(ymd[1]);
		date = Integer.parseInt(ymd[2]);
		String[] hm = start_hour_min.split(":");
		hour = Integer.parseInt(hm[0]);
		minute = Integer.parseInt(hm[1]);
		place = ss[2];
		activity = ss[3];
	}
	
	@Override
	public int compareTo(Event ev)		// 重写比较方法，按时间比较，时间小的在前
	{
		long time = ((long)minute)+60*(((long)hour)+24*(((long)date)+31*(((long)month)+12*((long)year))));
		long ev_time = ((long)ev.minute)+60*(((long)ev.hour)+24*(((long)ev.date)
				+31*(((long)ev.month)+12*((long)ev.year))));
		if (time<ev_time)
		{
			return -1;
		}
		else if (time==ev_time)
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
	
	@Override
	public String toString()
	{
		return year+"/"+month+"/"+date+" "+hour+":"+minute+"\t"+activity;
	}
}