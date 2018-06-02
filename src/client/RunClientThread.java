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
	private Alarm alarm = null;					// RunClientThread线程和ClockThread线程共享alarm对象
	
	public RunClientThread(Alarm a)
	{
		alarm = a;
		Thread th = new Thread(this);
		th.start();
	}
	
	public void run()
    {
		// 从TCP server接收初始信息
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
				alarm.put(line);				// 将事件添加进闹钟事件列表
			}
			bf.close();
			is.close();
			socket.close();
			// 从UDP server接收广播消息
			DatagramPacket receive = new DatagramPacket(new byte[DATA_BUFFER_SIZE], DATA_BUFFER_SIZE);
			MulticastSocket dgsocket = new MulticastSocket(8888);  	// 用MulticastSocket就不会端口冲突
			while (true)
			{
				dgsocket.receive(receive);
				byte[] recvByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength()); 
				String recv = new String(recvByte);
				System.out.println(recv);
				alarm.put(recv);				// 将事件添加进闹钟事件列表
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
    }
}  