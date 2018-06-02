/*
 * 轮询文件，如果文件内容有修改就把新增的内容广播出去
 */

package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastThread extends Thread{
	public static long SLEEP_TIME = 5000;
	private static int lineCounter = 0;
	public BroadcastThread() {
		super();
		lineCounter = 0;
	}
	
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress inetAddr = InetAddress.getByName("255.255.255.255");
			int port = 8888;
			String line;
			int currentCounter = 0;
			while (true)
			{
				BufferedReader bf = new BufferedReader(new FileReader("events.properties"));
				currentCounter = 0;
				while ((line = bf.readLine())!=null)
				{
					currentCounter++;
					if (currentCounter>lineCounter)
					{
						line += "\n";
						byte[] msg = line.getBytes();
						DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddr, port);  
						socket.send(sendPack);
					}
				}
				lineCounter = currentCounter;
				bf.close();
				try {
					sleep(SLEEP_TIME);
				} catch (InterruptedException e1){
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	  
}
