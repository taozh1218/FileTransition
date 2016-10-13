package com.taozhang.filetransition.ui;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.libra.sinvoice.Common;
import com.libra.sinvoice.LogHelper;
import com.libra.sinvoice.SinVoicePlayer;
import com.taozhang.filetransition.R;
import com.taozhang.filetransition.accesspoint.AccessPointManager;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.impl.FileProgressListner;
import com.taozhang.filetransition.thread.ServiceThread;
import com.taozhang.filetransition.ui.customComponent.RippleOutLayout;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;
import com.taozhang.filetransition.util.Constant;

/**
 * 发送声波的activity
 * 
 * @author Administrator
 * 
 */
public class SendVoiceActivity extends Activity implements
		SinVoicePlayer.Listener {

	// frameLayout component
	private static FrameLayout frame;
	private static WaterDropLoadingView water;
	private static TextView tv_fileName;

	private PowerManager.WakeLock mWakeLock;
	private final static String TAG = "MainActivityxx";
	private SinVoicePlayer mSinVoicePlayer;
	public RippleOutLayout rippleLayout;
	public Button title;
	private AccessPointManager accessPoint;
	static {
		System.loadLibrary("sinvoice");
		LogHelper.d(TAG, "sinvoice jnicall loadlibrary");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_third);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
		accessPoint = new AccessPointManager(this);
		// 关wifi，开热点
		accessPoint.stopWifi();
		if (!accessPoint.isWifiApEnabled()) {
			accessPoint.startWifiAp();
		}
		handler = new RippleHandler(this);

		getComponent();
		title = (Button) findViewById(R.id.sendVoiceTitle);
		// 动画
		rippleLayout = (RippleOutLayout) findViewById(R.id.rippleOutLayout);
		rippleLayout.startRippleAnimation();
		
		// 啟動服務器
		new Thread() {
			public void run() {

				startService();
			};
		}.start();

	}

	private void getComponent() {
		frame = (FrameLayout) findViewById(R.id.sendVoice_frame);
		water = (WaterDropLoadingView) findViewById(R.id.sendVoice_waterDrop);
		tv_fileName = (TextView) findViewById(R.id.tv_sendVoice_fileName);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSinVoicePlayer = new SinVoicePlayer();
		mSinVoicePlayer.init(this);
		mSinVoicePlayer.setListener(this);
		 int time = 0;
		while (!accessPoint.isWifiApEnabled()){
			// 堵塞
			try {
				time ++;
				if(time > 20)  break;
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		sendAddress(); // 启动完成后，把本机ip和服务器端口号用声波发送

	}

	public SparseArray<Socket> threadPool = new SparseArray<Socket>();
	private int index = 0;
	RippleHandler handler;

	private static class RippleHandler extends Handler {
		WeakReference<SendVoiceActivity> activity;
		SendVoiceActivity outAct ;
		public RippleHandler(SendVoiceActivity activity) {
			this.activity = new WeakReference<SendVoiceActivity>(activity);
			
		}

		@Override
		public void handleMessage(Message msg) {
			SendVoiceActivity sendActivity = activity.get();// 獲取實際引用
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				sendActivity.rippleLayout.stopRippleAnimation();
				sendActivity.title.setVisibility(View.GONE);
				sendActivity.rippleLayout.setVisibility(View.GONE);
				sendActivity.frame.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 启动服务
	 */
	private boolean isExit = false;
	ServerSocket serverSocket;

	private void startService() {

		try {
			serverSocket = new ServerSocket(Constant.PORT);
			// String hostName = InetAddress.getLocalHost().getHostName();
			Log.e("tag", "服务器启动成功");
			handler.post(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(App.context, "服务器启动成功", Toast.LENGTH_SHORT)
							.show();
				}
			});
			while (true) {
				Log.e("tag", "进入循环");
				if (isExit) {
					break;
				}
				Socket accept = serverSocket.accept();// 获取链接的socket
				Log.e("SendVoiceAct", "link success ");
				handler.sendEmptyMessage(1);
				// 加入线程池
				threadPool.put(index, accept);
				index++;
				// 交给线程去处理这个客户端
				ServiceThread st = new ServiceThread(accept,frame);
				st.execute();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final static int[] TOKENS = { 32, 32, 32, 32, 32, 32 };
	private final static int TOKEN_LEN = TOKENS.length;

	private void sendAddress() {
		try {
			String localIpAddress = getLocalIpAddress();
			Log.e("本服务器的ip", localIpAddress);
			StringBuilder ssid = new StringBuilder();

			ssid.append("whqtest").append(":"); // 文件头信息
			// 加上文件头信息
			ssid.append(localIpAddress);
//			Toast.makeText(App.context, localIpAddress, Toast.LENGTH_SHORT)
//					.show();
			byte[] strs = ssid.toString().getBytes();

			if (null != strs) {
				int len = strs.length;
				int[] tokens = new int[len];
				int maxEncoderIndex = mSinVoicePlayer.getMaxEncoderIndex();
				LogHelper.d(TAG, "maxEncoderIndex:" + maxEncoderIndex);
				String encoderText = ssid.toString();
				for (int i = 0; i < len; ++i) {
					if (maxEncoderIndex < 255) {
						tokens[i] = Common.DEFAULT_CODE_BOOK
								.indexOf(encoderText.charAt(i));
					} else {
						tokens[i] = strs[i];
					}
				}
				mSinVoicePlayer.play(tokens, len, false, 2000);
			} else {
				mSinVoicePlayer.play(TOKENS, TOKEN_LEN, false, 2000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getLocalIpAddress() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// 获取32位整型IP地址
		int ipAddress = wifiInfo.getIpAddress();

		// 返回整型地址转换成“*.*.*.*”地址
		return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mWakeLock.isHeld()) {
			mWakeLock.release();
		}
		mSinVoicePlayer.stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		accessPoint.destroy(this);
		mSinVoicePlayer.stop();
		mSinVoicePlayer.uninit();
	}

	@Override
	public void onSinToken(int[] tokens) {

	}

	@Override
	public void onSinVoicePlayStart() {
		LogHelper.d(TAG, "start play");
	}

	@Override
	public void onSinVoicePlayEnd() {
		LogHelper.d(TAG, "stop play");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		isExit = true;
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isExit = true;
			finish();
		}
		return true;
	}

	
}
