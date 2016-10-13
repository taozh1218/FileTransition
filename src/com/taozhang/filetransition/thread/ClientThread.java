package com.taozhang.filetransition.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.PublicKey;

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
import com.taozhang.filetransition.util.Connect;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.MessageUtil;
import com.taozhang.filetransition.util.SplitStringUtil;

/**
 * 客户端线程，接受服务器发来的文件
 * 
 * @author Administrator
 * 
 */
public class ClientThread extends AsyncTask<String, String, Void> {

	private DataInputStream ips;
	private DataOutputStream ops;
	private WaterDropLoadingView water;
	private FrameLayout layout;
	private Button recieving;
	private TextView name;

	public ClientThread(FrameLayout layout) throws IOException {
		this.layout = layout;
		water = (WaterDropLoadingView) layout
				.findViewById(R.id.receiveVoice_waterDrop);
		recieving = (Button) layout.findViewById(R.id.recieveTitle);
		name = (TextView) layout.findViewById(R.id.tv_receiveVoice_fileName);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(String... value) {
		try {

			InetSocketAddress address = new InetSocketAddress(value[0],
					Constant.PORT);
			Connect con = Connect.getInstance();
			
			// con.disConnect();
			int times = 0;
			while (!con.connectServer(address, Constant.PORT)) {
				Thread.sleep(100);
				times++;
				if (times > 100) {
					// 协议： 第一位 是进度百分比，第二位是文件名，第三位是信号
					publishProgress("0", null, "Error");
				}

			}
			ips = con.dIps;
			ops = con.dOps;
			publishProgress("0",null,"OK");
			// mHanlder.sendEmptyMessage(200);
//			String recieve;
//			recieve = con.getMsg();
//			Log.e("接受的信息", recieve);
//			if (recieve.equals("200")) {
//				Log.e("asd", "有回复了");
//			}
//			con.sendMsg("请发送信息");
			con.sendMsg(Constant.REQUESTFILES);// 发送一百，请求服务器发送文件过来
			// 发来的文件数量
			int count = Integer.parseInt(con.getMsg());
			while (count > 0) {
				receiveFile();
				Log.e("接受文件成功！！！！", "--------------------");
				count--;
			}
			// mAct.mHanlder.sendEmptyMessage(400);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
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
		if(persent >= 99){
			publishProgress("0",null,"Finish");
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
				Toast.makeText(App.context, "链接超时", Toast.LENGTH_SHORT).show();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}else if("OK".equals(values[2])){
				layout.setVisibility(View.VISIBLE);
			}else if("Finish".equals(values[2])){
				// 完成
				recieving.setText("传输完毕");
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
	public boolean sendFile(FileInfo detail) {
		File file = new File(detail.filePath);

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
			}
			// 清楚相应的文件
			AdapterOfGridView_operationComputer.cleanCheckFile(detail);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public File receiveFile() {
		File file = null;
		try {

			String name = ips.readUTF();// 文件名
			publishProgress("0", name, null);// 第一位是进度，第二位是文件名,第三是信号
			long size = ips.readLong();// size
			byte[] buffer = new byte[MessageUtil.BUFFERSIZE];

			String type = SplitStringUtil.getTypeBySplit(name);
			String path = SplitStringUtil.getFilePathByType(type);

			file = new File(path, name);
			FileOutputStream fos = new FileOutputStream(file);
			int read = 0;
			while ((read = ips.read(buffer, 0, buffer.length)) != -1) {
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
