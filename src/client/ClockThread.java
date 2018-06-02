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
	public static final int WIDTH = 600;										// 窗体宽度
	public static final int HEIGHT = 400;										// 窗体高度
	public static final String[] TABLE_HEAD = {"开始时间","结束时间","地点","活动"};	// 表头
	public static final int[] MONTH = // 每月的天数
	{31,28,31,30,31,30,31,31,30,31,30,31};
	private Alarm alarm = null;				// RunClientThread线程和ClockThread线程共享alarm对象
	
	JFrame f;											// 窗体
	Label time;											// 当前时间
	JTextField Jhour, Jmin, Jtext;						// 闹钟设定编辑框
	JButton b_ok;										// 闹钟编辑确定按钮
	JTextField tf_switch;								// 调时编辑框，在其中按格式输入时间
	JButton b_switch;									// 确认调时按钮, 按下修改当前时间
	JButton b_del;										// 删除表格选定行按钮
	JTable disp;										// 表格组件，用于盛放model
	DefaultTableModel model = new DefaultTableModel();	// 表格内容
	int hour_set = 25, min_set = 61;					// 设定的闹钟时间(时:分)
	String alarm_text_set = "自定义闹钟时间到！";			// 设定闹钟自定义弹出框文字
	int c_year, c_month, c_date, c_hour, c_minute, c_second;	// 整数形式的当前时间
	
	private void setModel()								// 填写表格内容
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
	
	private void triggerURLalarm()						// 触发网络通信自动设置的闹钟，只触发一次
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
	
	private void triggerDailyAlarm()					// 触发手动设定的闹钟，每天此时触发
	{
		if (c_hour == hour_set && c_minute == min_set && c_second == 0)
		{
			new PopAutoThread().start();
		}
	}
	
	class PopURLThread extends Thread
	{
		private String activity;			// 活动内容
		private String place;				// 活动地点
		public PopURLThread(String pl, String ac)
		{
			place = pl;
			activity = ac;
		}
		public void run()
		{
			JOptionPane.showMessageDialog
			(null, place+": "+activity+"！", "闹钟提示",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	class PopAutoThread extends Thread					// 手动设定闹钟提示框触发线程
	// 用多线程弹出提示框防止提示框弹出的时间内时钟不动
	{
		public void run()
		{
			JOptionPane.showMessageDialog
			(null, alarm_text_set, "闹钟提示",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void addOneSecond()							// 计时+1s
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
	
	private String timeString()								// 将用整数表示的时间合成字符串
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
		f.setSize(WIDTH, HEIGHT);								// 设置窗体大小
		f.addWindowListener(new WindowAdapter()					// 设置关闭窗口功能
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		JPanel contentPane = new JPanel();						// 中间容器
		contentPane.setLayout(new GridLayout(2,1));				// 总体两行一列布局
		f.setContentPane(contentPane);							// 设置内容面板
		
		JPanel jp1 = new JPanel();								// 上方的Panels
		jp1.setLayout(new GridLayout(2,1));						// 总体两行一列布局
		JPanel jp11 = new JPanel();
		jp11.setBorder(BorderFactory.createTitledBorder("当前时间"));
		JPanel jp12 = new JPanel();
		jp12.setBorder(BorderFactory.createTitledBorder("闹钟设置"));
		time = new Label(getTime());
		time.setFont(new Font("Arial", Font.PLAIN, 36));
		jp11.add(time);
		jp12.setLayout(new GridLayout(1,4));
		JPanel jp121 = new JPanel();
		JPanel jp122 = new JPanel();
		JPanel jp123 = new JPanel();
		JPanel jp124 = new JPanel();
		Label Lhour = new Label("时：");
		Jhour = new JTextField(5);
		Label Lmin = new Label("分：");
		Jmin = new JTextField(5);
		Label Ltext = new Label("提示文字：");
		Jtext = new JTextField(5);
		b_ok = new JButton("确定");
		b_ok.addActionListener(new ActionListener()				// 按下"确认"按钮后设定闹钟时间
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
		jp2.setLayout(new GridLayout(2,1));						// 两行一列布局
		JPanel jp21 = new JPanel();	
		jp21.setLayout(new GridLayout(2,1));					// 两行一列布局			
		JPanel jp211 = new JPanel();
		JPanel jp212 = new JPanel();
		b_del = new JButton("删除选定行");
		b_del.addActionListener(new ActionListener()				// 按下"确认"按钮后设定闹钟时间
		{
			public void actionPerformed(ActionEvent ae)
			{
				int numrow=disp.getSelectedRow();			// 待删除行的序号
				model.removeRow(numrow);					// 从表格中删除
				TreeSet<Event> al = alarm.getAlarmList();
				int i=0;
				try {
	                for (Event ev : al){
	                    if (i==numrow)
	                    {
	                    	al.remove(ev);						// 从事件列表中删除
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
		Label Lswitch = new Label("调时格式 yyyy-MM-dd HH:mm:ss");
		jp211.add(Lswitch);
		jp211.add(tf_switch);
		b_switch = new JButton("确认调时");
		b_switch.addActionListener(new ActionListener()	// 按下"确认调时"按钮后修改当前时间
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
		disp = new JTable(model);								// 表格组件，未处理的事件	
		disp.setSize(600,600);
		JScrollPane scroll = new JScrollPane(disp);				// 把表格放在滚动条组件里
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
		int cnt = 0;							// 计数器计到10s更新一下表格项
		while (true)
		{
			try {
				Thread.sleep(1000);				// 每隔一秒
				addOneSecond();					// +1s
				time.setText(timeString());		// 刷新时间显示
				triggerDailyAlarm();			// 触发自定义闹钟
				triggerURLalarm();				// 触发网络通信自动设定闹钟
				if (cnt==9)
				{
					setModel();						// 刷新事件列表
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
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
