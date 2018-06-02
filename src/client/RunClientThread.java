package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Arrays;
  
public class RunClientThread extends Thread 
{ 
	public static final int DATA_BUFFER_SIZE = 256;
	private Alarm alarm = null;					// RunClientThread�̺߳�ClockThread�̹߳���alarm����
	
	public RunClientThread(Alarm a)
	{
		alarm = a;
		Thread th = new Thread(this);
		th.start();
	}
	
	public void run()
    {
		// ��TCP server���ճ�ʼ��Ϣ
		String host = "localhost";
		int port = 1122;
		try {
			Socket socket = new Socket(host,port);
			InputStream is = socket.getInputStream();
			BufferedReader bf = new BufferedReader(new InputStreamReader(is));
			String line;
			while((line = bf.readLine())!=null)
			{
				System.out.println(line);
				alarm.put(line);				// ���¼���ӽ������¼��б�
			}
			bf.close();
			is.close();
			socket.close();
			// ��UDP server���չ㲥��Ϣ
			DatagramPacket receive = new DatagramPacket(new byte[DATA_BUFFER_SIZE], DATA_BUFFER_SIZE);
			MulticastSocket dgsocket = new MulticastSocket(8888);  	// ��MulticastSocket�Ͳ���˿ڳ�ͻ
			while (true)
			{
				dgsocket.receive(receive);
				byte[] recvByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength()); 
				String recv = new String(recvByte);
				System.out.println(recv);
				alarm.put(recv);				// ���¼���ӽ������¼��б�
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
}  