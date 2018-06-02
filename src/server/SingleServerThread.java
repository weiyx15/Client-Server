/*
 * 客户端刚刚与服务器连接上时用TCP把当前文件中所有内容发送给客户端
 */

package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.net.Socket;

public class SingleServerThread extends Thread{

	Socket socket;
	OutputStream os;
	public SingleServerThread(Socket soc) { // 构造函数
		socket = soc;
	}

	public void run() { // 线程主体
		try {
			os = socket.getOutputStream();
			String line;
			BufferedReader is = new BufferedReader(new FileReader("events.properties"));
			while ((line=is.readLine())!=null) {	// 文件不到末尾
				line = line + "\n";					// 加换行符
				byte[] msg = line.getBytes();  		// 转换成字节流
		        os.write(msg);  
			}
			is.close(); // 关闭文件
			socket.close(); // 关闭Socket
		} catch (Exception e) {
			System.out.println("Error:" + e);
			// 出错，打印出错信息
		}
	}
}