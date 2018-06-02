package client;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ClockThread implements Runnable
{
	public static final int WIDTH = 600;										// ������
	public static final int HEIGHT = 400;										// ����߶�
	public static final String[] TABLE_HEAD = {"��ʼʱ��","����ʱ��","�ص�","�"};	// ��ͷ
	public static final int[] MONTH = // ÿ�µ�����
	{31,28,31,30,31,30,31,31,30,31,30,31};
	private Alarm alarm = null;				// RunClientThread�̺߳�ClockThread�̹߳���alarm����
	
	JFrame f;											// ����
	Label time;											// ��ǰʱ��
	JTextField Jhour, Jmin, Jtext;						// �����趨�༭��
	JButton b_ok;										// ���ӱ༭ȷ����ť
	JTextField tf_switch;								// ��ʱ�༭�������а���ʽ����ʱ��
	JButton b_switch;									// ȷ�ϵ�ʱ��ť, �����޸ĵ�ǰʱ��
	JButton b_del;										// ɾ�����ѡ���а�ť
	JTable disp;										// ������������ʢ��model
	DefaultTableModel model = new DefaultTableModel();	// �������
	int hour_set = 25, min_set = 61;					// �趨������ʱ��(ʱ:��)
	String alarm_text_set = "�Զ�������ʱ�䵽��";			// �趨�����Զ��嵯��������
	int c_year, c_month, c_date, c_hour, c_minute, c_second;	// ������ʽ�ĵ�ǰʱ��
	
	private void setModel()								// ��д�������
	{
		int n = alarm.getNumber(), i = 0;
		String[][] content = new String[n][4];
		TreeSet<Event> al = alarm.getAlarmList();
		for (Event e : al)
		{
			content[i][0] = e.time_t;
			content[i][1] = e.time_e;
			content[i][2] = e.place;
			content[i][3] = e.activity;
			i++;
		}
		model.setDataVector(content, TABLE_HEAD);
	}
	
	private void triggerURLalarm()						// ��������ͨ���Զ����õ����ӣ�ֻ����һ��
	{
		Event ev = alarm.getFirstElement();
		if (ev!=null)
		{
			if (ev.year==c_year && ev.month==c_month && ev.date==c_date
					&& ev.hour==c_hour && ev.minute==c_minute && c_second==0)
			{
				alarm.timeout(ev);
				new PopURLThread(ev.place, ev.activity).start();
			}
		}
	}
	
	private void triggerDailyAlarm()					// �����ֶ��趨�����ӣ�ÿ���ʱ����
	{
		if (c_hour == hour_set && c_minute == min_set && c_second == 0)
		{
			new PopAutoThread().start();
		}
	}
	
	class PopURLThread extends Thread
	{
		private String activity;			// �����
		private String place;				// ��ص�
		public PopURLThread(String pl, String ac)
		{
			place = pl;
			activity = ac;
		}
		public void run()
		{
			JOptionPane.showMessageDialog
			(null, place+": "+activity+"��", "������ʾ",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	class PopAutoThread extends Thread					// �ֶ��趨������ʾ�򴥷��߳�
	// �ö��̵߳�����ʾ���ֹ��ʾ�򵯳���ʱ����ʱ�Ӳ���
	{
		public void run()
		{
			JOptionPane.showMessageDialog
			(null, alarm_text_set, "������ʾ",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void addOneSecond()							// ��ʱ+1s
	{
		if (c_second<59)
		{
			c_second++;
		}
		else
		{
			c_second = 0;
			if (c_minute<59)
			{
				c_minute++;
			}
			else
			{
				c_minute = 0;
				if (c_hour<23)
				{
					c_hour++;
				}
				else
				{
					c_hour = 0;
					if (c_date<MONTH[c_month])
					{
						c_date++;
					}
					else
					{
						c_date = 1;
						if (c_month<12)
						{
							c_month++;
						}
						else
						{
							c_month = 1;
							c_year++;
						}
					}
				}
			}
		}
	}
	
	private String timeString()								// ����������ʾ��ʱ��ϳ��ַ���
	{
		return String.format("%04d",c_year)+"-"+
		String.format("%02d",c_month)+"-"+
		String.format("%02d", c_date)+" "+
		String.format("%02d",c_hour)+":"+
		String.format("%02d", c_minute)+":"+
		String.format("%02d",c_second);
	}
	
	public ClockThread(Alarm a)
	{
		alarm = a;
		setModel();
		f = new JFrame("Weiyx's Clock");
		f.setSize(WIDTH, HEIGHT);								// ���ô����С
		f.addWindowListener(new WindowAdapter()					// ���ùرմ��ڹ���
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		JPanel contentPane = new JPanel();						// �м�����
		contentPane.setLayout(new GridLayout(2,1));				// ��������һ�в���
		f.setContentPane(contentPane);							// �����������
		
		JPanel jp1 = new JPanel();								// �Ϸ���Panels
		jp1.setLayout(new GridLayout(2,1));						// ��������һ�в���
		JPanel jp11 = new JPanel();
		jp11.setBorder(BorderFactory.createTitledBorder("��ǰʱ��"));
		JPanel jp12 = new JPanel();
		jp12.setBorder(BorderFactory.createTitledBorder("��������"));
		time = new Label(getTime());
		time.setFont(new Font("Arial", Font.PLAIN, 36));
		jp11.add(time);
		jp12.setLayout(new GridLayout(1,4));
		JPanel jp121 = new JPanel();
		JPanel jp122 = new JPanel();
		JPanel jp123 = new JPanel();
		JPanel jp124 = new JPanel();
		Label Lhour = new Label("ʱ��");
		Jhour = new JTextField(5);
		Label Lmin = new Label("�֣�");
		Jmin = new JTextField(5);
		Label Ltext = new Label("��ʾ���֣�");
		Jtext = new JTextField(5);
		b_ok = new JButton("ȷ��");
		b_ok.addActionListener(new ActionListener()				// ����"ȷ��"��ť���趨����ʱ��
		{
			public void actionPerformed(ActionEvent ae)
			{
				hour_set = Integer.parseInt(Jhour.getText());
				min_set = Integer.parseInt(Jmin.getText());
				alarm_text_set = Jtext.getText();
			}
		});
		jp121.add(Lhour);
		jp121.add(Jhour);
		jp122.add(Lmin);
		jp122.add(Jmin);
		jp123.add(Ltext);
		jp123.add(Jtext);
		jp124.add(b_ok);
		jp12.add(jp121);
		jp12.add(jp122);
		jp12.add(jp123);
		jp12.add(jp124);
		jp1.add(jp11);
		jp1.add(jp12);
		
		JPanel jp2 = new JPanel();
		jp2.setLayout(new GridLayout(2,1));						// ����һ�в���
		JPanel jp21 = new JPanel();	
		jp21.setLayout(new GridLayout(2,1));					// ����һ�в���			
		JPanel jp211 = new JPanel();
		JPanel jp212 = new JPanel();
		b_del = new JButton("ɾ��ѡ����");
		b_del.addActionListener(new ActionListener()				// ����"ȷ��"��ť���趨����ʱ��
		{
			public void actionPerformed(ActionEvent ae)
			{
				int numrow=disp.getSelectedRow();			// ��ɾ���е����
				model.removeRow(numrow);					// �ӱ����ɾ��
				TreeSet<Event> al = alarm.getAlarmList();
				int i=0;
				try {
	                for (Event ev : al){
	                    if (i==numrow)
	                    {
	                    	al.remove(ev);						// ���¼��б���ɾ��
	                    }
	                    i++;
	                }
				} catch (ConcurrentModificationException e)
				{
					e.printStackTrace();
				}
			}
		});
		tf_switch = new JTextField(20);
		Label Lswitch = new Label("��ʱ��ʽ yyyy-MM-dd HH:mm:ss");
		jp211.add(Lswitch);
		jp211.add(tf_switch);
		b_switch = new JButton("ȷ�ϵ�ʱ");
		b_switch.addActionListener(new ActionListener()	// ����"ȷ�ϵ�ʱ"��ť���޸ĵ�ǰʱ��
		{
			public void actionPerformed(ActionEvent ae)
			{
				try {
					String mytime = tf_switch.getText();
					String[] ymdhms = mytime.split(" ");
					String[] ymd = ymdhms[0].split("-");
					String[] hms = ymdhms[1].split(":");
					int my_year = Integer.parseInt(ymd[0]);
					if (my_year>=1990)
					{
						c_year = my_year;
					}
					int my_month = Integer.parseInt(ymd[1]);
					if (my_month>=1 && my_month<=12)
					{
						c_month = my_month;
					}
					int my_date = Integer.parseInt(ymd[2]);
					if (my_date>=1 && my_date<=MONTH[c_month])
					{
						c_date = my_date;
					}
					int my_hour = Integer.parseInt(hms[0]);
					if (my_hour>=0 && my_hour<=23)
					{
						c_hour = my_hour;
					}
					int my_minute = Integer.parseInt(hms[1]);
					if (my_minute>=0 && my_minute<=60)
					{
						c_minute = my_minute;
					}
					int my_second = Integer.parseInt(hms[2]);
					if (my_second>=0 && my_second<=60)
					{
						c_second = my_second;
					}
				} catch (ArrayIndexOutOfBoundsException e)
				{
					e.printStackTrace();
				}
			}
		});
		jp212.add(b_switch);
		jp212.add(b_del);
		jp21.add(jp211);
		jp21.add(jp212);
		disp = new JTable(model);								// ��������δ������¼�	
		disp.setSize(600,600);
		JScrollPane scroll = new JScrollPane(disp);				// �ѱ����ڹ����������
		scroll.setBounds(20,20,600,600);
		jp2.add(jp21);
		jp2.add(scroll);
		
		contentPane.add(jp1);
		contentPane.add(jp2);
		f.setVisible(true);
		
		Thread th = new Thread(this);
		th.start();
	}
	
	public void run()
	
	{
		int cnt = 0;							// �������Ƶ�10s����һ�±����
		while (true)
		{
			try {
				Thread.sleep(1000);				// ÿ��һ��
				addOneSecond();					// +1s
				time.setText(timeString());		// ˢ��ʱ����ʾ
				triggerDailyAlarm();			// �����Զ�������
				triggerURLalarm();				// ��������ͨ���Զ��趨����
				if (cnt==9)
				{
					setModel();						// ˢ���¼��б�
					cnt = 0;
				}
				else
				{
					cnt++;
				}
			}
			catch (InterruptedException e)
			{
				System.err.println(e);
			}
		}
	}
	
	private String getTime()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
		String c_date_form = df.format(new Date());
		String[] dt = c_date_form.split(" ");
		String[] ymd = dt[0].split("-");
		c_year = Integer.parseInt(ymd[0]);
		c_month = Integer.parseInt(ymd[1]);
		c_date = Integer.parseInt(ymd[2]);
		String[] hms = dt[1].split(":");
		c_hour = Integer.parseInt(hms[0]);
		c_minute = Integer.parseInt(hms[1]);
		c_second = Integer.parseInt(hms[2]);
		return c_date_form;
	}
}
