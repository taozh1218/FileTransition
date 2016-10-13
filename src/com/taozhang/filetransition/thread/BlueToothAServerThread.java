package com.taozhang.filetransition.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.ui.BluetoothActivity;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;
import com.taozhang.filetransition.util.MessageUtil;
import com.taozhang.filetransition.util.SplitStringUtil;

/**
 * 蓝牙采用的模式是服务器接收文件
 * 
 * @author Administrator
 * 
 */
public class BlueToothAServerThread extends AsyncTask<Void, String, Void> {

	private static final String TAG = "TAG";
	private BluetoothServerSocket mServerSocket;
	private DataInputStream dis;
	private DataOutputStream dos;

	private WaterDropLoadingView water;
	private FrameLayout layout;
	private Button recieving;
	private TextView name;
	private BluetoothAdapter bluetoothAdapter;

	public BlueToothAServerThread(BluetoothAdapter bluetoothAdapter,
			FrameLayout layout) {
		super();
		this.bluetoothAdapter = bluetoothAdapter;
		this.layout = layout;
		water = (WaterDropLoadingView) layout
				.findViewById(R.id.bluetooth_receiveVoice_waterDrop);
		recieving = (Button) layout.findViewById(R.id.bluetooth_recieveTitle);
		name = (TextView) layout
				.findViewById(R.id.bluetooth_tv_receiveVoice_fileName);

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	private boolean flag = true;

	@Override
	protected Void doInBackground(Void... arg0) {
		BluetoothSocket socket = null;
		// Keep listening until exception occurs or a socket is returned

		try {
			mServerSocket = bluetoothAdapter
					.listenUsingRfcommWithServiceRecord("文件传输",
							BluetoothActivity.MY_UUID);
			while (flag) {
				socket = mServerSocket.accept();// 没有连接时，会阻塞
				publishProgress("0", null, "200");
				
				BluetoothDevice remoteDevice = socket.getRemoteDevice();

				dis = new DataInputStream(socket.getInputStream());
				// 发来的文件数量
				int count = Integer.parseInt(MessageUtil.getMsg(dis));
				// 第一，进度增长百分比，第二，文件名，第三，信号
				publishProgress("0", null, "OK");
				Log.e("发来的文件数量", count + "-----------------");
				while (count > 0) {
					receiveFile();
					Log.e("接受文件成功！！！！", "--------------------");
					count--;
				}
				publishProgress("0", null, "Finish");
				
			}
			

		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
		return null;
	}

	private float persent = 0;

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		// 根据百分百计算增长的弧度
		float per = Float.parseFloat(values[0]);// 已经被放大了一百倍
		float du = per * WaterDropLoadingView.MAX / 100;
		persent += per;
		if (persent >= 99) {
			publishProgress("0", null, "Finish");
		}
		recieving.setText("正在接收..." + (int) persent + "%");
		if (du > 0) {
			water.addSweepAngle(du);
			float value = water.getSweepAngle();
			Log.e("percent", value + "");
		}
		// 获取名字，放在字符串第二位
		if (null != values[1]) {
			name.setText(values[1]);
		}
		// 信号
		if (null != values[2]) {
			if ("Error".equals(values[2])) {
				Toast.makeText(App.context, "连接超时", Toast.LENGTH_SHORT).show();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
			} else if ("OK".equals(values[2])) {
				layout.setVisibility(View.VISIBLE);
			} else if ("Finish".equals(values[2])) {
				// 完成
				recieving.setText("传输完毕");
			} else if("200".equals(values[2])){
				Toast.makeText(App.context, "蓝牙服务器创建成功", Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// 停止
		recieving.setText("传输完毕");
	}

	float increate = 0; // 增加量

	/**
	 * 发送一个文件
	 */
	// public boolean sendFile(FileInfo detail) {
	// File file = new File(detail.filePath);
	// publishProgress("0", file.getName(),null);// 第一位是进度，第二位是文件名,第三信号
	// try {
	// FileInputStream fis = new FileInputStream(file);
	// String name = file.getName();
	// long size = file.length();
	// dos.writeUTF(name);// 先发文件名
	// dos.flush();
	// dos.writeLong(size);// 文件大小
	// dos.flush();
	// // buffer
	// byte[] buffer = new byte[MessageUtil.BUFFERSIZE];
	// int read = 0;
	//
	// while ((read = fis.read(buffer, 0, buffer.length)) != -1) {
	// dos.write(buffer, 0, read);
	// // 不用每次都发
	// increate += read; // 记录增量
	// // 增量大于百分之1
	// float value = increate * 100 / size;
	// if (value > 1) {
	// Log.e("进度百分比", value + "--------------");
	// // 每次都要发两位 ，统一长度，不然越界
	// publishProgress(value + "", null,null);
	// // 增量清零
	// increate = 0;
	// }
	//
	// }
	// // 清楚相应的文件
	// AdapterOfGridView_operationComputer.cleanCheckFile(detail);
	// // 历史纪录里面长按弹出来要分享的文件
	// AdapterOfGridView_operationComputer.fileInfos.clear();
	// fis.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// return true;
	// }

	public File receiveFile() {
		File file = null;
		try {
			String name = dis.readUTF();// 文件名
			long size = dis.readLong();// size
			publishProgress("0", name, null);
			byte[] buffer = new byte[MessageUtil.BUFFERSIZE];

			String type = SplitStringUtil.getTypeBySplit(name);
			String path = SplitStringUtil.getFilePathByType(type);

			file = new File(path, name);
			FileOutputStream fos = new FileOutputStream(file);
			int read = 0;
			while ((read = dis.read(buffer, 0, buffer.length)) != -1) {
				// 写文件
				fos.write(buffer, 0, read);
				// 不用每次都发
				increate += read; // 记录增量
				// 增量大于百分之1
				float value = increate * 100 / size;
				if (value > 1) {
					Log.e("进度百分比", value + "--------------");
					// 每次都要发三位 ，统一长度，不然越界
					publishProgress(value + "", null, null);
					// 增量清零
					increate = 0;
				}
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

}
