/*
 * Alarm�ࣺ���������¼��б�
 */

package client;

import java.util.TreeSet;

public class Alarm {
	private TreeSet<Event> alarmList = null;			// ��ʱ��˳������
	
	public Alarm() {
		alarmList = new TreeSet<Event>();
	}
	
	public TreeSet<Event> getAlarmList()				// ��������AlarmList
	{
		return alarmList;
	}
	
	public synchronized Event getFirstElement()						// ������Ԫ�أ���������¼�
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
	
	public synchronized int getNumber()								// ����alarmList�ĳ���
	{
		return alarmList.size();
	}
	
	public synchronized void timeout(Event ev)						// ��һ���¼������ˣ���ɾȥ���¼�
	{
		alarmList.remove(ev);
	}
	
	public synchronized void put(String s)							// ������һ�����¼�
	{
		Event ev = new Event(s);
		alarmList.add(ev);
	}
	
}

class Event implements Comparable<Event>{
	public int year, month, date, hour, minute;	// int���͵Ŀ�ʼʱ�䣬����ʱ��Ƚ�
	public String time_t, time_e, place, activity;// String���͵Ŀ�ʼʱ�䡢����ʱ�䡢�ص㡢�
	
	public Event(String s)				// ͨ����������յ����ַ�������Event����
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
	public int compareTo(Event ev)		// ��д�ȽϷ�������ʱ��Ƚϣ�ʱ��С����ǰ
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