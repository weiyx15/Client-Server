package client;

public class RunClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Alarm alarm = new Alarm();			// �����̹߳���������¼�������
		new RunClientThread(alarm);			// ��������ͨ���߳�
		new ClockThread(alarm);				// ����ʱ���߳�
	}

}
