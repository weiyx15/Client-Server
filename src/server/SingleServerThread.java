/*
 * �ͻ��˸ո��������������ʱ��TCP�ѵ�ǰ�ļ����������ݷ��͸��ͻ���
 */

package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.net.Socket;

public class SingleServerThread extends Thread{

	Socket socket;
	OutputStream os;
	public SingleServerThread(Socket soc) { // ���캯��
		socket = soc;
	}

	public void run() { // �߳�����
		try {
			os = socket.getOutputStream();
			String line;
			BufferedReader is = new BufferedReader(new FileReader("events.properties"));
			while ((line=is.readLine())!=null) {	// �ļ�����ĩβ
				line = line + "\n";					// �ӻ��з�
				byte[] msg = line.getBytes();  		// ת�����ֽ���
		        os.write(msg);  
			}
			is.close(); // �ر��ļ�
			socket.close(); // �ر�Socket
		} catch (Exception e) {
			System.out.println("Error:" + e);
			// ������ӡ������Ϣ
		}
	}
}