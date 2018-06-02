/*
 * 服务器端主程序
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
		// 启动UDP广播服务器线程
		new BroadcastThread().start();
		System.out.println("UDP server begins to work...");
		// 创建TCP socket
		ServerSocket ss = new ServerSocket(1122);
		System.out.println("TCP server begins to work...");
		while (true)
		{
			Socket soc = ss.accept();
			System.out.println("TCP user accepted!");
			new SingleServerThread(soc).start();	// 每建立一个TCP连接，就创建一个TCP server线程
		}
	}

}
