package com.taozhang.filetransition.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.R.id;
import com.taozhang.filetransition.R.layout;
import com.taozhang.filetransition.R.menu;
import com.taozhang.filetransition.R.string;
import com.taozhang.filetransition.adapter.AdapterOfBluetoothAct;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.bean.BluetoothBean;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.MessageUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BluetoothTestActivity extends Activity {

	private static final String TAG = BluetoothTestActivity.class.getSimpleName();
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice device;
	private BluetoothServerSocket serverSocket;
	private static final UUID MY_UUID = UUID
			.fromString("be3aec08-307e-41d9-94bd-8d1e0b7c2f3f");

	private ListView lv_bonded;
	private ListView lv_discovery;
	private AdapterOfBluetoothAct adapter_lvbonded;
	private AdapterOfBluetoothAct adapter_lvdiscovery;
	private AcceptThread acceptThread;
	private List<BluetoothBean> list_bondedDevice;
	private List<BluetoothBean> list_discoveryDevice;
	private int mState;

	private final int MESSAGE_READ = 123;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_bluetooth);
		// 1.有的蓝牙活动都需要请求BluetoothAdapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		adapter_lvbonded = new AdapterOfBluetoothAct(getApplicationContext());
		adapter_lvdiscovery = new AdapterOfBluetoothAct(getApplicationContext());
		getBondedDevice();
		// 开启serverSocket======================
		if (acceptThread == null) {
			acceptThread = new AcceptThread();
			acceptThread.start();
		}
		getComponent();
	}

	private void getComponent() {
		Button btn_discovery = (Button) findViewById(R.id.btn_bluetooth_discovery);
		lv_bonded = (ListView) findViewById(R.id.lv_bluetooth_bonded);
		lv_discovery = (ListView) findViewById(R.id.lv_bluetooth_discovery);

		// set listener & adapter
		btn_discovery.setOnClickListener(clickListener);
		adapter_lvbonded.setList(list_bondedDevice);
		lv_bonded.setAdapter(adapter_lvbonded);
		lv_bonded.setOnItemClickListener(itemClickListener);

		lv_discovery.setAdapter(adapter_lvdiscovery);
		lv_discovery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				connectServer(list_discoveryDevice, position);
			}
		});

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			switch (what) {
			case Constant.BLUETOOTH_CONNECTSUCCESS:
				Toast.makeText(getApplicationContext(), (String) msg.obj,
						Toast.LENGTH_SHORT).show();
				// 建立客户端
				break;
			case Constant.HANDLER_DATAFROMIO:
				Toast.makeText(getApplicationContext(), msg.obj.toString(),
						Toast.LENGTH_SHORT).show();

				Log.i("handler data", msg.obj.toString());
				break;
			case Constant.DISCOVERDEVICE:
				if (!(list_discoveryDevice.size() > 0)) {
					Toast.makeText(getApplicationContext(), "未发现新设备！",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case MESSAGE_READ:
				Log.i(TAG, msg.obj.toString());
				break;
				
			default:
				break;
			}
		}
	};

	OnItemClickListener itemClickListener = new OnItemClickListener() {
		// // 开启serverSocket=================================================
		// if (acceptThread == null) {
		// acceptThread = new AcceptThread();
		// acceptThread.start();
		// }
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			connectServer(list_bondedDevice, position);
		}
	};

	/**
	 * 创建客户端线程，连接服务器
	 * 
	 * @param position
	 */
	public void connectServer(List<BluetoothBean> list, int position) {

		BluetoothBean bean = (BluetoothBean) list.get(position);

		if (device == null) {
			device = mBluetoothAdapter.getRemoteDevice(bean.getAddress());
		}
		ConnectThread clientThread = new ConnectThread(device);
		clientThread.start();
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// search
			list_discoveryDevice = new ArrayList<BluetoothBean>();
			if (mBluetoothAdapter == null) {// 判断是否支持蓝牙
				// Device does not support Bluetooth
				Toast.makeText(getApplicationContext(),
						"Sorry,your Device not support Bluetooth",
						Toast.LENGTH_SHORT).show();
			}
			// 2. 蓝牙是否开启，使本机蓝牙可见
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent();
				enableBtIntent
						.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);// 启动修改蓝牙开启
				enableBtIntent
						.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);// 启动修改蓝牙可见性的Intent
				startActivityForResult(enableBtIntent,
						Constant.REQUEST_ENABLE_BT);
			}
			// 3. 如何查找已配对的设备，或者使用设备检测找到新的设备。
			IntentFilter filter = new IntentFilter();
			filter.addAction(BluetoothDevice.ACTION_FOUND);// 发现设备
			filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);// 设备连接状态改变
			filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);// 状态改变
			registerReceiver(mReceiver, filter); // Don't forget to unregister
													// during onDestroy
			searchDevice();
		}
	};

	/**
	 * 获取已配对的设备
	 */
	public void getBondedDevice() {

		if (!mBluetoothAdapter.isEnabled()) {// 如果蓝牙未开启
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, Constant.REQUEST_ENABLE_BT);
		}
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();// 获取绑定的设备
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			BluetoothBean bean;
			list_bondedDevice = new ArrayList<BluetoothBean>();
			for (BluetoothDevice device : pairedDevices) {
				if (device != null) {
					// Log.i(TAG, device.getName());
					// Add the name and address to an array adapter to show in a
					// ListView
					bean = new BluetoothBean();
					bean.setAddress(device.getAddress());
					bean.setName(device.getName());
					list_bondedDevice.add(bean);
					Log.i(TAG, bean.toString());
				}
			}
			notifyBondedDataChanged();
		}
	}

	private void notifyBondedDataChanged() {
		adapter_lvbonded.setList(list_bondedDevice);
		adapter_lvbonded.notifyDataSetChanged();
	}

	/**
	 * 搜索新设备，并且是自己可被发现
	 */
	private void searchDevice() {
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();// 异步搜索
		}
		mBluetoothAdapter.startDiscovery();
		Toast.makeText(getApplicationContext(), "正在搜索...", Toast.LENGTH_SHORT)
				.show();
		getBondedDevice();
	}

	/**
	 * 广播接收者
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a
				// ListView
				BluetoothBean bean = new BluetoothBean();
				Log.i(TAG, "mReceiver,name:" + device.getName() + ",address:"
						+ device.getAddress());
				bean.setAddress(device.getAddress());
				bean.setName(device.getName());
				list_discoveryDevice.add(bean);
				adapter_lvdiscovery.setList(list_discoveryDevice);
				adapter_lvdiscovery.notifyDataSetChanged();
			} else if (action
					.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				if (list_discoveryDevice.size() == 0) {
					Toast.makeText(getApplicationContext(), "未搜索到蓝牙设备！",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	/**
	 * serverThread
	 * 
	 * @author taozhang
	 * 
	 */
	private class AcceptThread extends Thread {
	    private final BluetoothServerSocket mmServerSocket;
	 
	    public AcceptThread() {
	        // Use a temporary object that is later assigned to mmServerSocket,
	        // because mmServerSocket is final
	        BluetoothServerSocket tmp = null;
	        try {
	            // MY_UUID is the app's UUID string, also used by the client code
	            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(getResources().getString(R.string.app_name), MY_UUID);
	        } catch (IOException e) { }
	        mmServerSocket = tmp;
	    }
	 
	    public void run() {
	        BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
	        while (true) {
	            try {
	                socket = mmServerSocket.accept();
	            } catch (IOException e) {
	                break;
	            }
	            // If a connection was accepted
	            if (socket != null) {
	                // Do work to manage the connection (in a separate thread)
	            	 byte[] buffer = new byte[1024];  // buffer store for the stream
	     	        int bytes; // bytes returned from read()
	     	 
	     	        // Keep listening to the InputStream until an exception occurs
	     	        while (true) {
	     	            try {
	     	            	socket.getOutputStream().write(new String("嗨咯，我是服务器！").getBytes());
	     	                // Read from the InputStream
	     	                bytes = socket.getInputStream().read(buffer);
	     	                // Send the obtained bytes to the UI activity
	     	                handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
	     	                        .sendToTarget();
	     	            } catch (IOException e) {
	     	                break;
	     	            }
	     	        }
	                break;
	            }
	        }
	    }
	 
	    /** Will cancel the listening socket, and cause the thread to finish */
	    public void cancel() {
	        try {
				mmServerSocket.close();
	        } catch (IOException e) { }
	    }
	}

	/**
	 * 客户端连接服务器的线程
	 * 
	 * @author taozhang
	 * 
	 */
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
//	        manageConnectedSocket(mmSocket);
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()
	 
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	            	mmSocket.getOutputStream().write(new String("嗨咯，我是客户端！").getBytes());
	                // Read from the InputStream
	                bytes = mmSocket.getInputStream().read(buffer);
	                // Send the obtained bytes to the UI activity
	                handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
	                        .sendToTarget();
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			Log.i(TAG, "requestCode:" + requestCode + "resultCode:"
					+ resultCode + "data:" + data.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bluetooth, menu);
		return true;
	}


}
