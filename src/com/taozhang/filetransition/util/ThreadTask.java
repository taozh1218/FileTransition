package com.taozhang.filetransition.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import android.os.Handler;
import android.util.Log;

import com.taozhang.filetransition.adapter.AdapterOfServerListView;
import com.taozhang.filetransition.bean.ServiceBean;

public class ThreadTask implements Runnable {
    private ArrayList<ServiceBean> list = new ArrayList<ServiceBean>();
    private AdapterOfServerListView adapter;
    private String ip;
    private Handler handler;
    public volatile static int count = 0;

    public ThreadTask(AdapterOfServerListView adapter, String ip, Handler handler) {
        super();
        this.adapter = adapter;
        this.ip = ip;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            InetAddress lIp = InetAddress.getByName("localhost");// 本机ip
            Log.e("UDP线程启动", "准备向服务器发送信息");
            DatagramSocket socket = new DatagramSocket();
            byte[] address = lIp.getAddress();
            socket.setSoTimeout(3000);
            // 向服务器发送本机ip
            DatagramPacket pack = new DatagramPacket(address, address.length,
                    new InetSocketAddress(ip, 10088));
            socket.send(pack);
            ++count;//计数加1
            Log.e("UDP线程启动", "已经向服务器发送信息，当前线程位置：" + count);
            // 接受服务器发来的主机名
            DatagramPacket receivePacket = new DatagramPacket(new byte[1024],
                    1024);
            socket.receive(receivePacket);
            String hostName = new String(receivePacket.getData());
            Log.e("打印结果", hostName + "----------------");
            String name = new String(hostName);
            InetSocketAddress socketAddress = (InetSocketAddress) receivePacket.getSocketAddress();
            ServiceBean info = new ServiceBean(name, socketAddress);
            list.add(info);
            socket.close();

            adapter.addData(list);//加入listView
            //主线程更新ui
            handler.post(new Runnable() {

                @Override
                public void run() {
                    //adapter 的notifyDataSetChanged()一定要在主线程
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
