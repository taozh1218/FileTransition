package com.taozhang.filetransition.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.util.Log;

import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.bean.FileInfo;

public class MessageUtil {

	public static int BUFFERSIZE = 5 * 1024;
	/**
	 * 发送信息的方法
	 * 
	 * @param msg
	 */
	public static void sendMsg(String msg, DataOutputStream dOps) {
		try {
			dOps.writeUTF(msg);
			dOps.flush();// 强制输出
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 从客户端获取信息的方法
	 * @param ips
	 * @return client messages
	 * @throws IOException
	 */
	public static String getMsg(DataInputStream dIps) throws IOException {
		String readUTF = dIps.readUTF();
		return readUTF;
	}

	/**
	 * 转16进制
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	/**
	 * 发送一个文件
	 */
	public static boolean sendFile(FileInfo detail,DataOutputStream dos){
		
		File file = new File(detail.filePath);
		try {
			FileInputStream fis = new FileInputStream(file);
			String name = file.getName();
			long size = file.length();
			int total = 0;
			dos.writeUTF(name);// 先发文件名
			
			dos.writeLong(size);// 文件大小
			// buffer
			byte[] buffer = new byte[BUFFERSIZE];
			int read = 0;
			while((read = fis.read(buffer,0,BUFFERSIZE)) != -1 ){
				dos.write(buffer, 0, read);
				total += read;
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 发送一个文件
	 */
	public static boolean sendFile(String filePath,DataOutputStream dos){
		
		File file = new File(filePath);
		try {
			FileInputStream fis = new FileInputStream(file);
			String name = file.getName();
			long size = file.length();
			int total = 0;
			dos.writeUTF(name);// 先发文件名
			
			dos.writeLong(size);// 文件大小
			// buffer
			byte[] buffer = new byte[BUFFERSIZE];
			int read = 0;
			while((read = fis.read(buffer,0,BUFFERSIZE)) != -1){
				dos.write(buffer, 0, read);
				total += read;
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	

	
	
	public static boolean  sendFiles(DataOutputStream dout, List<FileInfo> files) {  
        long totalSize = 0;  
        byte buf[] = new byte[8192];  
        int len;  
        try {  
           
             
            dout.writeInt(files.size());  
            for(FileInfo file : files){
            		
                dout.writeUTF(file.fileName);  
                dout.flush();  
                dout.writeLong(file.fileSize);  
                dout.flush();  
                totalSize += file.fileSize;  
            }  
            dout.writeLong(totalSize);  
            BufferedInputStream din = null;
            for(FileInfo file : files){
            	din = new BufferedInputStream(  
                        new FileInputStream(file.filePath));  
                while ((len = din.read(buf)) != -1) {  
                    dout.write(buf, 0, len);  
                }  
            }  
            din.close();
            System.out.println("文件传输完成");  
  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;
        }  
        return true;  
    }  
	
	
	public static File getFile(DataInputStream dis){
		File file = null;
		try {
			String name = dis.readUTF();//文件名
			long size = dis.readLong();
			int total = 0;
			byte[] buffer = new byte[BUFFERSIZE];
			

			String type = SplitStringUtil.getTypeBySplit(name);
			String path = SplitStringUtil.getFilePathByType(type);
			
			file = new File(path,name);
			FileOutputStream fos = new FileOutputStream(file);
			int read = 0;
			while((read = dis.read(buffer,0,BUFFERSIZE)) != -1){
				// 写文件
				fos.write(buffer, 0, read);
				total += read;
				
				
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return file;
	}
	
	public static  void receiveFiles(DataInputStream din) {
		String mFilePath = Constant.BASEPATH_ELSE;
	    File dirs = new File(mFilePath);
	    if (!dirs.exists()) {
	      dirs.mkdirs();
	    }
	    int fileNum = 0;
	    long totalSize = 0;
	    List<FileInfo>  fileInfos = null;
	    try {
	      fileNum = din.readInt();
	      fileInfos = new ArrayList<FileInfo>();
	      
	      for (int i = 0; i < fileNum; i++) {
	        FileInfo file = new FileInfo();
	        file.fileName = din.readUTF();
	        file.fileSize = din.readLong();
	        fileInfos.add(file);
	      }
	      totalSize = din.readLong();
	    } catch (IOException e) {
	      e.printStackTrace();
	      System.exit(0);
	    }
	    System.out.println(fileNum);
	    System.out.println(totalSize);
	    for (FileInfo fileinfo : fileInfos) {
	      System.out.println(fileinfo.fileName);
	      System.out.println(fileinfo.fileSize);
	    }
	    // // /////////////////////////////////////////////////////////////////
	    int leftLen = 0; // 写满文件后缓存区中剩余的字节长度。
	    int bufferedLen = 0; // 当前缓冲区中的字节数
	    int writeLen = 0; // 每次向文件中写入的字节数
	    long writeLens = 0; // 当前已经向单个文件中写入的字节总数
	    long totalWriteLens = 0; // 写入的所有字节数
	    byte buf[] = new byte[8192];
	    for (int i = 0; i < fileNum; i++) {
	      writeLens = 0;
	      try {
	        FileOutputStream fout = new FileOutputStream(mFilePath + "/"
	            + fileInfos.get(i).fileName);
	        while (true) {
	          if (leftLen > 0) {
	            bufferedLen = leftLen;
	          } else {
	            bufferedLen = din.read(buf);
	          }
	          if (bufferedLen == -1)
	            return;
	          System.out.println("readlen" + bufferedLen);
	          // 如果已写入文件的字节数加上缓存区中的字节数已大于文件的大小，只写入缓存区的部分内容。
	          if (writeLens + bufferedLen >= fileInfos.get(i).fileSize) {
	            leftLen = (int) (writeLens + bufferedLen - fileInfos.get(i).fileSize);
	            writeLen = bufferedLen - leftLen;
	            fout.write(buf, 0, writeLen); // 写入部分
	            totalWriteLens += writeLen;
//	            move(buf, writeLen, leftLen);
	            break;
	          } else {
	            fout.write(buf, 0, bufferedLen); // 全部写入
	            writeLens += bufferedLen;
	            totalWriteLens += bufferedLen;
	            if (totalWriteLens >= totalSize) {
	              //mListener.report(GroupChatActivity.FAIL, null);
	              return;
	            }
	            leftLen = 0;
	          }
	          //mListener.report(GroupChatActivity.PROGRESS,
	              //(int) (totalWriteLens * 100 / totalSize));
	        } // end while
	        fout.close();

	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	    } // end for
	    //mListener.report(GroupChatActivity.FAIL, null);
	  }
}
