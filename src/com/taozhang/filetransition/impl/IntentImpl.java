package com.taozhang.filetransition.impl;

import java.net.InetSocketAddress;

import android.app.Activity;
import android.app.Fragment;

public interface IntentImpl {
	
	public void intentToFileActivity(InetSocketAddress address);
	public void intentToAnotherActivity(Class<? extends Activity> clazz);
	public String getLocalIpAddress(); 
	
	/**
	 * 切换到目标fragment
	 * @param to 目标fragment
	 */
	public void changeFragmentTo(Fragment to);
}
