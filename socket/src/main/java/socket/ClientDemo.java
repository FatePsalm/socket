package java.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientDemo {
	public static void main(String[] args)
		throws Exception {
		//Client 客户端
		/*
		 * 创建Socket对象，必须的参数：地址和端口号
		 *  地址: 可以使IP/服务器的名称(域名) 
		 *    ip："192.168.20.37"
		 *    名称: "www.tedu.cn"
		 *          "localhost" -> "127.0.0.1"
		 *  如果 服务器名称、端口号错误，就会出现
		 *  连接异常！ 
		 *  客户端程序执行之前，服务器务必先启动！
		 * Socket的构造器会自动找到服务器并且进行
		 * 连接，如果连接成功就返回 Socket对象
		 */
		Socket s = new Socket("wss://socket.wemew.com", 1443);
		/*
		 * socket 对象代表与服务器的连接！
		 * socket 包含两个流 in, out 分别对应连接
		 * 到服务器的 out 和 in
		 */
		/*
		 * 为了配合服务器发送和接收文本，也将两个流
		 * 包装为高级流！
		 */
		PrintWriter out = new PrintWriter(
				new OutputStreamWriter(
						s.getOutputStream(),
						"utf-8"),  true); 
		BufferedReader in= new BufferedReader(
			 new InputStreamReader(
					 s.getInputStream(),"utf-8"));
		/*
		 * 向服务器发送文本信息
		 * 将文字经过编码以后向服务器发送，发送文本
		 * 以后发送了一个回车字符！
		 * 服务器端，使用readLine()等待回车字符！
		 */
		out.println("有鱼丸吗?");
		System.out.println("发送了：有鱼丸吗?" ); 
		/*
		 * 等待结束服务器发回来的信息
		 * println()
		 */
		String str = in.readLine();
		System.out.println("服务器发回:"+str);
		/*
		 * 客户端
		 */
		/*
		 * 得到本地的 IP 和 端口号 
		 */
		InetAddress ip = s.getLocalAddress();
		System.out.println(ip); //客户端ip
		int port = s.getLocalPort();
		System.out.println(port);
		/*
		 * 得到对方的IP 和 端口号
		 */
		InetAddress remoteIp = s.getInetAddress();
		int remotePort = s.getPort();
		System.out.println(
			"服务器："+remoteIp+":"+remotePort);
		s.close();
	}
}