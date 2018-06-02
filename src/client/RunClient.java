package client;

public class RunClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Alarm alarm = new Alarm();			// 设置线程共享的闹钟事件管理器
		new RunClientThread(alarm);			// 启动网络通信线程
		new ClockThread(alarm);				// 启动时钟线程
	}

}
