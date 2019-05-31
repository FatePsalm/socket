package java.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者 Name:CaoGang
 * @version 创建时间：2017年10月25日 下午2:29:07 类说明
 */
public class SocketServer {
	/*
	 * 定义一个集合，用于共享和转发消息 每当一个用户线程启动时候，就向这个集合添加一个 输出流对象。 是所有客户端服务线程 ClientHandler 可以共享
	 * 访问的集合对象。
	 */
	private List<PrintWriter> list = new ArrayList<PrintWriter>();

	public static void main(String[] args) throws Exception {
		// 静态方法中不能直接访问成员内部类ClientHandler
		// main 方法调用 start 即可
		SocketServer server = new SocketServer();
		server.start();
	}

	public void start() throws Exception {
		// 主线程执行了 start 方法
		// 在成员方法中可以访问 ClientHandler 类
		// new ClientHandler();
		// 创建 ServerSocket对象
		// 使用循环反复调用 ss.accpet()等待客户端的连接
		// 每次有客户端连接 就启动子线程处理客户端
		// 的通讯
		// start方法处理的是接线员功能
		ServerSocket ss = new ServerSocket(8890);
		while (true) {
			// 等待连接
			Socket s = ss.accept();
			// 将连接交给子线程
			ClientHandler handler = new ClientHandler(s);
			// 启动线程
			new Thread(handler).start();
			// 返回继续...
		}
		// 如上代码的目的，可以处理多个用户的连接
		// 称为多线程 Socket 通信
	}

	/*
	 * 客户服务线程 为了共享数据方便，使用内部类声明线程类 因为客户端之间需要转发消息，所以需要共享 数据，便于转发消息 Client 客户端 Handler:
	 * 司 管理XXX的
	 */
	class ClientHandler implements Runnable {
		/*
		 * Socket 对象对应一个成功连接的客户端 socket 代表某个已经成功连接点的客户端！
		 */
		Socket socket;

		public ClientHandler(Socket s) {
			socket = s;
		}

		public void run() {
			// 将当前客户端的发送的消息，转发每个已经
			// 连接的的客户端

			/*
			 * 从socket中获取两个流 in out 包装成高级文本流（UTF-8）
			 * 
			 * 将 out 添加到 list, 用于接收消息
			 * 
			 * 循环处理in获得客户端发送的消息 收到客户端的消息以后，将这个消息 转发给每个 list 中的out流 异常处理：in在读取时候有异常(连接断了)
			 * 将list中的out对象删除（退出）
			 * 
			 */
			PrintWriter out = null;
			BufferedReader in = null;
			try {
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
				// 将out加入到集合，用于转发消息
				synchronized (list) {
					list.add(out);
				}
				// 接收客户端送来的消息，转发每个客户端
				while (true) {
					// readLine() 3种情况：
					// 1 返回字符串 发给其他人
					// 2 返回null，Linux 表示网络断了！结束通信
					// 3 抛出异常, Windows 表示网络断了
					String str = in.readLine();
					if (str == null) {// 抛出异常结束 循环
						throw new IOException("再见！");
					}
					// 发给其他人
					// 遍历 list 集合，将消息发送每个 out
					synchronized (list) {
						for (PrintWriter writer : list) {
							writer.println(str + "你好发送完成!");
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				// 网络通信失败
				// 删除已经加入到list中的out对象
				synchronized (list) {
					list.remove(out);
				}
			} finally {
				try {
					// 可靠关闭网络连接
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
