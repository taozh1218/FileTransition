package com.taozhang.filetransition.util;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


/**
 * Created by 閮攢宄�on 2015/8/24.
 */
public class NetworkUtils
{

    public static String getCurrentSSID(Context context)
    {
        WifiManager wifiMan = (WifiManager) (context
                .getSystemService(Context.WIFI_SERVICE));
        WifiInfo wifiInfo = wifiMan.getConnectionInfo();

        if (wifiInfo != null)
            return wifiInfo.getSSID();
        else
            return null;
    }

    public synchronized static Inet4Address getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        if (inetAddress instanceof Inet4Address)
                        {
                            return ((Inet4Address) inetAddress);
                        }
                    }
                }
            }
        }
        catch (SocketException ex)
        {
        }
        return null;
    }

    public synchronized static String[] getMACAddress(InetAddress ia) throws Exception
    {
        //鑾峰緱缃戠粶鎺ュ彛瀵硅薄锛堝嵆缃戝崱锛夛紝骞跺緱鍒癿ac鍦板潃锛宮ac鍦板潃瀛樺湪浜庝竴涓猙yte鏁扮粍涓�
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

        //涓嬮潰浠ｇ爜鏄妸mac鍦板潃鎷艰鎴怱tring
        String[] str_array = new String[2];
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();

        for (int i = 0; i < mac.length; i++)
        {
            if (i != 0)
            {
                sb1.append(":");
            }
            //mac[i] & 0xFF 鏄负浜嗘妸byte杞寲涓烘鏁存暟
            String s = Integer.toHexString(mac[i] & 0xFF);
            sb1.append(s.length() == 1 ? 0 + s : s);
            sb2.append(s.length() == 1 ? 0 + s : s);
        }
        //鎶婂瓧绗︿覆鎵�湁灏忓啓瀛楁瘝鏀逛负澶у啓鎴愪负姝ｈ鐨刴ac鍦板潃骞惰繑鍥�        str_array[0] = sb1.toString();
        str_array[1] = sb2.toString();
        return str_array;
        //return sb1.toString().toUpperCase();
    }

    public static String getLocalIp(Context context)
    {
        //鑾峰彇wifi鏈嶅姟
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        //鍒ゆ柇wifi鏄惁寮�惎
        if (!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);

        return ip;
    }

    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        //鑾峰彇鐘舵�
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        //鍒ゆ柇wifi宸茶繛鎺ョ殑鏉′欢
        if (wifi == NetworkInfo.State.CONNECTED)
            return true;
        else
            return false;
    }

    private static String intToIp(int i)
    {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
            + (i >> 24 & 0xFF);
    }

    /**
     * 鑾峰彇骞挎挱鍦板潃
     * @param context
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress getBroadcastAddress(Context context)
            throws UnknownHostException
    {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null)
        {
            return InetAddress.getByName("255.255.255.255");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
    
    
    
    
    
    
}
