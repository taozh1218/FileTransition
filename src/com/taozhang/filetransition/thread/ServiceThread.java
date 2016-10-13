 package com.taozhang.filetransition.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.MessageUtil;
import com.taozhang.filetransition.util.SplitStringUtil;

/**
 * 处理客户端的线程
 * 
 * @author Administrator
 * 
 */
public class ServiceThread extends AsyncTask<Void, String, Void> {
	private Socket socket;
	private DataInputStream ips;
	private DataOutputStream ops;
	private WaterDropLoadingView water;
	private FrameLayout layout;
	private Button sending;
	private TextView name;
	public ServiceThread(Socket socket, FrameLayout layout) throws IOException {
		this.socket = socket;
		this.layout = layout;
		
		water = (WaterDropLoadingView) layout.findViewById(R.id.sendVoice_waterDrop);
		sending = (Button) layout.findViewById(R.id.button_sending);
		name = (TextView) layout.findViewById(R.id.tv_sendVoice_fileName);
	}

	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	@Override
	protected Void doInBackground(Void... arg0) {

		try {
			ips = new DataInputStream(socket.getInputStream());
			ops = new DataOutputStream(socket.getOutputStream());
//			// 給客戶端發送信息
//			MessageUtil.sendMsg("200", ops);
//			
//			String msg = MessageUtil.getMsg(ips);
//			Log.e("客户端发来的信息", msg);
//			System.err.println("已经向客户端发送主机名");
			
			// 获取命令
			String command = MessageUtil.getMsg(ips);
			if (command.equals(Constant.REQUESTFILES)) {
				// 向客户端发送文件
				ArrayList<FileInfo> checkedFiles = AdapterOfGridView_operationComputer
						.getCheckedFiles();
				 ArrayList<FileInfo> fileInfos = AdapterOfGridView_operationComputer.fileInfos;
				 fileInfos.addAll(checkedFiles);
				// 先发文件数量
				MessageUtil.sendMsg(fileInfos.size() + "", ops);
				// 发送文件
				for(FileInfo info : fileInfos){
					sendFile(info);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private float persent = 0;
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		// 根据百分百计算增长的弧度
		float per = Float.parseFloat(values[0]);//已经被放大了一百倍
		float du = per * WaterDropLoadingView.MAX / 100  ;
		persent += per ;
		sending.setText("正在传输..." + (int)persent + "%");
		if(du > 0){
			water.addSweepAngle(du);
			float value = water.getSweepAngle();
			Log.e("percent", value + "");
		}
		// 获取名字，放在字符串第二位
		if(null != values[1] ){
			name.setText(values[1]);
		}
		
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// 停止
		sending.setText("传输完毕");
	}
	

	float increate = 0; //增加量
	/**
	 * 发送一个文件
	 */
	public boolean sendFile(FileInfo detail) {
		File file = new File(detail.filePath);
		publishProgress("0",file.getName());// 第一位是进度，第二位是文件名
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
				increate += read; //记录增量
				// 增量大于百分之1
				float value = increate * 100 / size ;
				if( value > 1){
					Log.e("进度百分比", value + "--------------");
					// 每次都要发两位 ，统一长度，不然越界
					publishProgress(value + "",null);
					// 增量清零
					increate = 0;
				}
				
			}
			// 清楚相应的文件
			AdapterOfGridView_operationComputer.cleanCheckFile(detail);
			//历史纪录里面长按弹出来要分享的文件
			AdapterOfGridView_operationComputer.fileInfos.remove(detail);
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
			long size = ips.readLong();// size
			byte[] buffer = new byte[MessageUtil.BUFFERSIZE];
			int total = 0;

			String type = SplitStringUtil.getTypeBySplit(name);
			String path = SplitStringUtil.getFilePathByType(type);
			
			file = new File(path, name);
			FileOutputStream fos = new FileOutputStream(file);
			int read = 0;
			while ((read = ips.read(buffer, 0, buffer.length)) != -1) {
				// 写文件
				fos.write(buffer, 0, read);
				total += read;
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

	

	
//	public   void receiveFiles() {
//		String mFilePath = Constant.BASEPATH_ELSE;
//	    File dirs = new File(mFilePath);
//	    if (!dirs.exists()) {
//	      dirs.mkdirs();
//	    }
//	    int fileNum = 0;
//	    long totalSize = 0;
//	    List<FileInfo>  fileInfos = null;
//	    try {
//	      fileNum = ips.readInt();
//	      fileInfos = new ArrayList<FileInfo>();
//	      
//	      for (int i = 0; i < fileNum; i++) {
//	        FileInfo file = new FileInfo();
//	        file.fileName = ips.readUTF();
//	        file.fileSize = ips.readLong();
//	        fileInfos.add(file);
//	      }
//	      totalSize = ips.readLong();
//	    } catch (IOException e) {
//	      e.printStackTrace();
//	      System.exit(0);
//	    }
////	    System.out.println(fileNum);
////	    System.out.println(totalSize);
////	    for (FileInfo fileinfo : fileInfos) {
////	      System.out.println(fileinfo.fileName);
////	      System.out.println(fileinfo.fileSize);
////	    }
//	    // // /////////////////////////////////////////////////////////////////
//	    int leftLen = 0; // 写满文件后缓存区中剩余的字节长度。
//	    int bufferedLen = 0; // 当前缓冲区中的字节数
//	    int writeLen = 0; // 每次向文件中写入的字节数
//	    long writeLens = 0; // 当前已经向单个文件中写入的字节总数
//	    long totalWriteLens = 0; // 写入的所有字节数
//	    byte buf[] = new byte[8192];
//	    for (int i = 0; i < fileNum; i++) {
//	      writeLens = 0;
//	      try {
//	        FileOutputStream fout = new FileOutputStream(mFilePath + "/"
//	            + fileInfos.get(i).fileName);
//	        while (true) {
//	          if (leftLen > 0) {
//	            bufferedLen = leftLen;
//	          } else {
//	            bufferedLen = ips.read(buf);
//	          }
//	          if (bufferedLen == -1)
//	            return;
//	          System.out.println("readlen" + bufferedLen);
//	          // 如果已写入文件的字节数加上缓存区中的字节数已大于文件的大小，只写入缓存区的部分内容。
//	          if (writeLens + bufferedLen >= fileInfos.get(i).fileSize) {
//	            leftLen = (int) (writeLens + bufferedLen - fileInfos.get(i).fileSize);
//	            writeLen = bufferedLen - leftLen;
//	            fout.write(buf, 0, writeLen); // 写入部分
//	            totalWriteLens += writeLen;
////	            move(buf, writeLen, leftLen);
//	            break;
//	          } else {
//	            fout.write(buf, 0, bufferedLen); // 全部写入
//	            writeLens += bufferedLen;
//	            totalWriteLens += bufferedLen;
//	            if (totalWriteLens >= totalSize) {
//	              //mListener.report(GroupChatActivity.FAIL, null);
//	              return;
//	            }
//	            leftLen = 0;
//	          }
//	          //mListener.report(GroupChatActivity.PROGRESS,
//	              //(int) (totalWriteLens * 100 / totalSize));
//	        } // end while
//	        fout.close();
//
//	      } catch (Exception e) {
//	        e.printStackTrace();
//	      }
//	    } // end for
//	    //mListener.report(GroupChatActivity.FAIL, null);
//	  }
	
//	public  boolean  sendFiles(List<FileInfo> files) {  
//        long totalSize = 0;  
//        byte buf[] = new byte[8192];  
//        int len;  
//        try {  
//            ops.writeInt(files.size());  
//            for(FileInfo file : files){
//                ops.writeUTF(file.fileName);  
//                ops.flush();  
//                ops.writeLong(file.fileSize);  
//                ops.flush();  
//                totalSize += file.fileSize;  
//            }  
//            ops.writeLong(totalSize);  
//            BufferedInputStream din = null;
//            for(FileInfo file : files){
//            	din = new BufferedInputStream(  
//                        new FileInputStream(file.filePath));  
//                while ((len = din.read(buf)) != -1) {  
//                    ops.write(buf, 0, len);  
//                }  
//            }  
//            din.close();
//            System.out.println("文件传输完成");  
//  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//            return false;
//        }  
//        return true;  
//    }  
}
