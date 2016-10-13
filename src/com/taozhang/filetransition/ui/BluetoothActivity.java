package com.taozhang.filetransition.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfBluetoothAct;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.BluetoothBean;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.thread.BlueToothAServerThread;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.MessageUtil;

public class BluetoothActivity extends Activity {
	private static final String TAG = BluetoothActivity.class.getSimpleName();
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice device;
	private BluetoothServerSocket serverSocket;
	public static final UUID MY_UUID = UUID
			.fromString("be3aec08-307e-41d9-94bd-8d1e0b7c2f3f");
	private AcceptThread acceptThread;
	private ListView lv_bonded;
	private ListView lv_discovery;
	private AdapterOfBluetoothAct adapter_lvbonded;
	private AdapterOfBluetoothAct adapter_lvdiscovery;
	private List<BluetoothBean> list_bondedDevice;
	private List<BluetoothBean> list_discoveryDevice;
	private int mState;
	private FrameLayout frame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_bluetooth);
		// 1.有的蓝牙活动都需要请求BluetoothAdapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		adapter_lvbonded = new AdapterOfBluetoothAct(getApplicationContext());
		adapter_lvdiscovery = new AdapterOfBluetoothAct(getApplicationContext());
		getBondedDevice();
		getComponent();
		// 开启BluetoothServerSocket======================
//		// // 开启serverSocket=================================================
//		if (acceptThread == null) {
//			acceptThread = new AcceptThread();
//			acceptThread.start();
//		}
//		new Thread() {
//			public void run() {
//				BluetoothServerSocket tmp = null;
//				try {
					// MY_UUID is the app's UUID string, also used by the client
					// code
//					tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
//							getResources().getString(R.string.app_name),
//							MY_UUID);
		Toast.makeText(App.context, "启动服务器线程", Toast.LENGTH_SHORT).show();
		new BlueToothAServerThread(mBluetoothAdapter, frame).execute();
					
					Log.i(TAG, "serverSocket创建成功！");
//				} catch (IOException e) {
//					e.printStackTrace();
//					Log.i(TAG, "serverSocket创建失败！");
//				}

//			};
//		}.start();

	}

	private void getComponent() {
		frame = (FrameLayout) findViewById(R.id.bluetooth_receiveVoice_frame);
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
	BluetoothSocket mmSocket;

	public void connectServer(List<BluetoothBean> list, int position) {

		BluetoothBean bean = (BluetoothBean) list.get(position);

		if (device == null) {
			device = mBluetoothAdapter.getRemoteDevice(bean.getAddress());
		}
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.cancelDiscovery();
		}
		new Thread() {
			public void run() {

				// MY_UUID is the app's UUID string, also used by the server
				// code
				try {
					mmSocket = device
							.createRfcommSocketToServiceRecord(MY_UUID);
					if (device.getBondState() == BluetoothDevice.BOND_NONE) {
						Method creMethod = BluetoothDevice.class
								.getMethod("createBond");
						Log.e("TAG", "开始配对");
						creMethod.invoke(device);
					}
					mBluetoothAdapter.cancelDiscovery();
					mmSocket.connect();
					Log.e("连接蓝牙成功", "蓝牙连接成功");
				} catch (IOException e) {
					e.printStackTrace();
				}catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				// mBluetoothAdapter.cancelDiscovery();
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				

				// new BluetoothClientThread(mmSocket, frame).execute();
				ConnectThread clientThread = new ConnectThread(device);
				clientThread.start();

			};
		}.start();

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

		// TODO
		// String direction;
		// int dir = getIntent().getIntExtra("direction", 0);
		// if (dir == BluetoothShare.DIRECTION_OUTBOUND) {
		// setTitle("蓝牙分享：已发送的文件");
		// direction = "(" + BluetoothShare.DIRECTION + " == "
		// + BluetoothShare.DIRECTION_OUTBOUND + ")";
		// } else {
		// setTitle("蓝牙分享：已接收的文件");
		// direction = "(" + BluetoothShare.DIRECTION + " == "
		// + BluetoothShare.DIRECTION_INBOUND + ")";
		// }
		//
		// final String selection = BluetoothShare.STATUS + " >= '200' AND " +
		// "("
		// + BluetoothShare.VISIBILITY + " IS NULL OR "
		// + BluetoothShare.VISIBILITY + " == '"
		// + BluetoothShare.VISIBILITY_VISIBLE + "'" + ")" + " AND "
		// + direction;
		// final String sortOrder = BluetoothShare.TIMESTAMP + " DESC";
		// @SuppressWarnings("deprecation")
		// Cursor cursor = managedQuery(BluetoothShare.CONTENT_URI, new String[]
		// {
		// "_id", BluetoothShare.FILENAME_HINT, BluetoothShare.STATUS,
		// BluetoothShare.TOTAL_BYTES, BluetoothShare._DATA,
		// BluetoothShare.TIMESTAMP, BluetoothShare.VISIBILITY,
		// BluetoothShare.DESTINATION, BluetoothShare.DIRECTION },
		// selection, null, sortOrder);
		// if (cursor != null) {
		// int id = cursor.getColumnIndexOrThrow(BluetoothShare._ID);
		// // TODO
		// String FILENAME_HINT = cursor.getString(cursor
		// .getColumnIndexOrThrow(BluetoothShare.FILENAME_HINT));
		// }

		// old
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

		private final BluetoothServerSocket mServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			try {
				// MY_UUID is the app's UUID string, also used by the client
				// code
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
						getResources().getString(R.string.app_name), MY_UUID);
				// serverSocket = mBluetoothAdapter
				// .listenUsingRfcommWithServiceRecord(getResources()
				// .getString(R.string.app_name), MY_UUID);
				Log.i(TAG, "serverSocket创建成功！");
			} catch (IOException e) {
				e.printStackTrace();
				Log.i(TAG, "serverSocket创建失败！");
			}
			mServerSocket = tmp;
		}

		public void run() {
			BluetoothSocket socket = null;
			// Keep listening until exception occurs or a socket is returned
			while (mState != BluetoothAdapter.STATE_CONNECTED) {
				if (mServerSocket != null) {
					try {
						socket = mServerSocket.accept();// 没有连接时，会阻塞
						InputStream ips = socket.getInputStream();
						DataInputStream dis = new DataInputStream(ips);
						String str = dis.readUTF();
						Message message = new Message();
						message.obj = str;
						message.what = Constant.BLUETOOTH_CONNECTSUCCESS;
						handler.sendMessage(message);
						int count = Integer.parseInt(MessageUtil.getMsg(dis));
						while (count > 0) {
							MessageUtil.getFile(dis);
							count--;
						}
						// getDataFromSocket(socket);
						// If a connection was accepted
						if (socket != null) {
							// Do work to manage the connection (in a separate
							// thread)
							// getDataFromSocket(socket);
							// mServerSocket.close();
							break;
						}
					} catch (IOException e) {
						break;
					}
				}
			}

		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
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
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();

				DataOutputStream dos = new DataOutputStream(
						mmSocket.getOutputStream());
				// dos.writeUTF("test success！");
				ArrayList<FileInfo> checkedFiles = AdapterOfGridView_operationComputer
						.getCheckedFiles();
				MessageUtil.sendMsg(checkedFiles.size() + "", dos);
				for (FileInfo detail : checkedFiles) {
					MessageUtil.sendFile(detail, dos);
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					mmSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
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
