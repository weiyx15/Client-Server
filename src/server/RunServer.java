/*
 * ��������������
 */

package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RunServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		// ����UDP�㲥�������߳�
		new BroadcastThread().start();
		System.out.println("UDP server begins to work...");
		// ����TCP socket
		ServerSocket ss = new ServerSocket(1122);
		System.out.println("TCP server begins to work...");
		while (true)
		{
			Socket soc = ss.accept();
			System.out.println("TCP user accepted!");
			new SingleServerThread(soc).start();	// ÿ����һ��TCP���ӣ��ʹ���һ��TCP server�߳�
		}
	}

}
