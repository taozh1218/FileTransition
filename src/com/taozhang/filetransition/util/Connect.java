package com.taozhang.filetransition.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * 锟斤拷锟接凤拷锟斤拷锟斤拷锟斤拷锟斤拷
 * 
 * @author Administrator
 * 
 */
public class Connect {
	private volatile static Connect con;
	private   Socket socket;
	public DataOutputStream dOps;
	public DataInputStream dIps;

	private Connect() {
		
	}

	

	public static Connect getInstance() {
		if (con == null) {
			// 锟斤拷锟斤拷
			synchronized (Connect.class) {
				if (con == null) {
					
					con = new Connect();
				}
			}
		}

		return con;
	}

	public boolean connectServer(InetSocketAddress address, int port){
		try {
			socket = new Socket(address.getAddress().getHostAddress(), port);
			dOps = new DataOutputStream(socket.getOutputStream());
			dIps = new DataInputStream(socket.getInputStream());
			Log.i("socket", "杩炴帴鎴愬姛");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isConnected(){
		return socket == null ? false : true;
	}
	/**
	 * 锟斤拷锟斤拷锟斤拷息锟侥凤拷锟斤拷
	 * 
	 * @param msg
	 */
	public void sendMsg(String msg) {
		try {
			
			dOps.writeUTF(msg);
			dOps.flush();// 寮哄埗杈撳嚭
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public  void disConnect() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO:涓嶆噦涓轰粈涔堜竴瀹氳鏄暟缁�
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getMsg() throws IOException {
		
		String readUTF = dIps.readUTF();
		
		return readUTF;
	}

}
