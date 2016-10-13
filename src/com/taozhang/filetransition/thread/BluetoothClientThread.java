package com.taozhang.filetransition.thread;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;
import com.taozhang.filetransition.util.MessageUtil;

/**
 * 蓝牙客户端发送文件
 * 
 * @author Administrator
 * 
 */
public class BluetoothClientThread extends AsyncTask<Void, String, Void> {

	private static final UUID MY_UUID = UUID
			.fromString("be3aec08-307e-41d9-94bd-8d1e0b7c2f3f");
	private BluetoothSocket mmSocket;
	private BluetoothDevice mDevice;
	private DataOutputStream ops;

	private WaterDropLoadingView water;
	private FrameLayout layout;
	private Button sending;
	private TextView name;

	public BluetoothClientThread( BluetoothSocket mmSocket, FrameLayout layout) {
		super();
		this.mmSocket = mmSocket;
		this.layout = layout;
		water = (WaterDropLoadingView) layout
				.findViewById(R.id.bluetooth_receiveVoice_waterDrop);
		sending = (Button) layout.findViewById(R.id.bluetooth_recieveTitle);
		name = (TextView) layout
				.findViewById(R.id.bluetooth_tv_receiveVoice_fileName);
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			Log.e("客户端，发送者", "进入了doinbackground");
			ops = new DataOutputStream(mmSocket.getOutputStream());
			publishProgress("0",null,"OK");
			
			ArrayList<FileInfo> checkedFiles = AdapterOfGridView_operationComputer
					.getCheckedFiles();
			ArrayList<FileInfo> fileInfos = AdapterOfGridView_operationComputer.fileInfos;
			
			fileInfos.addAll(checkedFiles);
			
			MessageUtil.sendMsg(fileInfos.size() + "", ops);
			for (FileInfo detail : fileInfos) {
				sendFile(detail);
			}
		} catch (IOException e) {
			e.printStackTrace();
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
		
		sending.setText("正在传输..." + (int) persent + "%");
		if (du > 0) {
			water.addSweepAngle(du);
			float value = water.getSweepAngle();
			Log.e("percent", value + "");
		}
		// 获取名字，放在字符串第二位
		if (null != values[1]) {
			name.setText(values[1]);
		}
		
		if(null != values[2]){
			if("OK".equals(values[2])){
				this.layout.setVisibility(View.VISIBLE);
				Toast.makeText(App.context,"进入了", Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// 停止
		sending.setText("传输完毕");
	}

	float increate = 0; // 增加量

	/**
	 * 发送一个文件
	 */
	public boolean sendFile(FileInfo detail) {
		File file = new File(detail.filePath);
		publishProgress("0", file.getName(),null);// 第一位是进度，第二位是文件名
		try {
			FileInputStream fis = new FileInputStream(file);
			String name = file.getName();
			long size = file.length();
			ops.writeUTF(name);// 先发文件名
			ops.flush();
			ops.writeLong(size);// 文件大小
			ops.flush();
			// buffer
			byte[] buffer = new byte[MessageUtil.BUFFERSIZE];
			int read = 0;

			while ((read = fis.read(buffer, 0, buffer.length)) != -1) {
				ops.write(buffer, 0, read);
				// 不用每次都发
				increate += read; // 记录增量
				// 增量大于百分之1
				float value = increate * 100 / size;
				if (value > 1) {
					Log.e("进度百分比", value + "--------------");
					// 每次都要发san位 ，统一长度，不然越界
					publishProgress(value + "", null,null);
					// 增量清零
					increate = 0;
				}

			}
			// 清楚相应的文件
			AdapterOfGridView_operationComputer.cleanCheckFile(detail);
			// 历史纪录里面长按弹出来要分享的文件
			AdapterOfGridView_operationComputer.fileInfos.remove(detail);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
