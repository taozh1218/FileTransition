package com.taozhang.filetransition.runnable;

import com.taozhang.filetransition.util.Connect;


public class SendMessageRunnable implements Runnable{

	private String[] messages;
	private Connect con ;
	

	public SendMessageRunnable(String[] messages,Connect con) {
		this.messages = messages;
		this.con = con;
	}
	
	
	@Override
	public void run() {
		for(String message : messages){
			con.sendMsg(message);
		}
	}

}
