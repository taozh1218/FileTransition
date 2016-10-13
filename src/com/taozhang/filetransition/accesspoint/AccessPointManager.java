package com.taozhang.filetransition.accesspoint;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.DeviceUtils;

public class AccessPointManager extends WifiManagerWrap {

	private static int mWifiApStateDisabled;
	private static int mWifiApStateDisabling;
	private static int mWifiApStateEnabled;
	private static int mWifiApStateEnabling;
	private static int mWifiApStateFailed;
	private static String mWifiApStateChangedAction;
	private static String mExtraWifiApState;
	private static String mExtraPreviousWifiApState;
	private String mWifiApSSID;

	static {
		try {
			mWifiApStateDisabled = WifiManager.class.getField(
					"WIFI_AP_STATE_DISABLED").getInt(WifiManager.class);
			mWifiApStateDisabling = WifiManager.class.getField(
					"WIFI_AP_STATE_DISABLING").getInt(WifiManager.class);
			mWifiApStateEnabled = WifiManager.class.getField(
					"WIFI_AP_STATE_ENABLED").getInt(WifiManager.class);
			mWifiApStateEnabling = WifiManager.class.getField(
					"WIFI_AP_STATE_ENABLING").getInt(WifiManager.class);
			mWifiApStateFailed = WifiManager.class.getField(
					"WIFI_AP_STATE_FAILED").getInt(WifiManager.class);

			mWifiApStateChangedAction = (String) WifiManager.class.getField(
					"WIFI_AP_STATE_CHANGED_ACTION").get(WifiManager.class);
			mExtraWifiApState = (String) WifiManager.class.getField(
					"EXTRA_WIFI_AP_STATE").get(WifiManager.class);
			mExtraPreviousWifiApState = (String) WifiManager.class.getField(
					"EXTRA_PREVIOUS_WIFI_AP_STATE").get(WifiManager.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** wifi hot spot is disabled */
	public static final int WIFI_AP_STATE_DISABLED = mWifiApStateDisabled;
	/** wifi hot spot is disabling */
	public static final int WIFI_AP_STATE_DISABLING = mWifiApStateDisabling;
	/** wifi hot spot is enabled */
	public static final int WIFI_AP_STATE_ENABLED = mWifiApStateEnabled;
	/** wifi hot spot is enabling */
	public static final int WIFI_AP_STATE_ENABLING = mWifiApStateEnabling;
	/** wifi hot spot state : failed */
	public static final int WIFI_AP_STATE_FAILED = mWifiApStateFailed;
	/** wifi hot spot state changed */
	public static final String WIFI_AP_STATE_CHANGED_ACTION = mWifiApStateChangedAction;
	/** wifi hot spot state */
	public static final String EXTRA_WIFI_AP_STATE = mExtraWifiApState;
	/** the former wifi hot spot state */
	public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = mExtraPreviousWifiApState;

	private OnWifiApStateChangeListener mOnApStateChangeListener;

	private BroadcastReceiver mWifiApStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (WIFI_AP_STATE_CHANGED_ACTION.equals(action)
					&& mOnApStateChangeListener != null) {
				mOnApStateChangeListener.onWifiStateChanged(intent.getIntExtra(
						EXTRA_WIFI_AP_STATE, WIFI_AP_STATE_FAILED));
			}
		}
	};

	public AccessPointManager(Context context) {
		super(context);
		if (!TextUtils.isEmpty(WIFI_AP_STATE_CHANGED_ACTION)) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(WIFI_AP_STATE_CHANGED_ACTION);
			context.registerReceiver(mWifiApStateReceiver, intentFilter);
		} else {
		}
	}

	/** set listener to monitor the state of access point */
	public void setWifiApStateChangeListener(
			OnWifiApStateChangeListener listener) {
		mOnApStateChangeListener = listener;
	}

	/** set WiFi hot spot is enableed */
	private boolean setWifiApEnabled(WifiConfiguration configuration,
			boolean enabled) {
		try {
			if (enabled) {
				mWifiManager.setWifiEnabled(false);
			}

			Method method = mWifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			return (Boolean) method
					.invoke(mWifiManager, configuration, enabled);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/** get WiFi hot spot state */
	public int getWifiApState() {
		try {
			Method method = mWifiManager.getClass().getMethod("getWifiApState");
			return (Integer) method.invoke(mWifiManager);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mWifiApStateFailed;
	}

	/**
	 * 鑾峰彇Wifi鐑偣閰嶇疆
	 * 
	 * @return WifiConfiguration
	 */
	private WifiConfiguration getWifiApConfiguration() {
		try {
			Method method = mWifiManager.getClass().getMethod(
					"getWifiApConfiguration");
			WifiConfiguration config = (WifiConfiguration) method
					.invoke(mWifiManager);
			loadWifiConfigurationFromProfile(config);
			return config;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unused")
	private boolean setWifiApConfiguration(WifiConfiguration configuration) {
		try {
			Method method = mWifiManager.getClass().getMethod(
					"setWifiApConfiguration", WifiConfiguration.class);
			return (Boolean) method.invoke(mWifiManager, configuration);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * wifi hot spot is enabled?
	 * 
	 * @return wifi鐑偣鏄惁鏈夋晥
	 */
	public boolean isWifiApEnabled() {
		try {
			Method method = mWifiManager.getClass()
					.getMethod("isWifiApEnabled");
			return (Boolean) method.invoke(mWifiManager);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/** create WiFi Hot Spot SSID suffix */
	public void createWifiApSSID(String suffix) {
		mWifiApSSID = DEFAULT_SSID + suffix;
	}

	/** create WiFi hot spot */
	public boolean startWifiAp() {
		// close WiFi
		// if(mWifiManager.isWifiEnabled()){
		closeWifi();
		// close the open WiFi hot spot
		if (DeviceUtils.isHTC()) { // 2013-12-31 add special for htc
			enableHtcHotspot(getWifiApConfiguration(), false);
			return setHtcHotspot();
		}

		if (isWifiApEnabled())
			setWifiApEnabled(getWifiApConfiguration(), false);
		// envoke the hot spot need to be created
		acquireWifiLock();
		WifiConfiguration wifiConfig = new WifiConfiguration();
		// 手机型号
		// String model = android.os.Build.MODEL + "_ap";
		createWifiApSSID("whqtest"); // ap名称
		wifiConfig.SSID = getWifiApSSID();
	
		// wifiConfig.preSharedKey = DEFAULT_PASSWORD;
		// setWifiConfigAsWPA(wifiConfig); // have password
		setWifiConfig(wifiConfig); // no password
		return setWifiApEnabled(wifiConfig, true);
	}

	/**
	 * connect the ap
	 * 
	 * @param SSID
	 *            the ap's ssid
	 * @param password
	 *            the ap's sharedKey,null if not need password
	 * @return true if connect success,false otherwise;
	 */
	public boolean connectAP(String SSID, String password) {

		openWifi();
		// if it had connected ,stop connect
		stopWifi();

		WifiConfiguration config = new WifiConfiguration();
		config.SSID = "\"" + SSID  + "\"" ;
		
		if (password != null) {
			config.preSharedKey = "\"" +  password + "\"" ;//主意格式
		}
		config.hiddenSSID = true;
		config.status = WifiConfiguration.Status.ENABLED;
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		config.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.TKIP);
		config.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.CCMP);
		config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		
		int netId = mWifiManager.addNetwork(config);
		
		return mWifiManager.enableNetwork(netId, true);
		
	}

	/** set HTC WiFi hotspot parameter, 2013-12-31 add special for htc */
	private boolean setHtcHotspot() {

		WifiConfiguration apConfig = new WifiConfiguration();

		apConfig.SSID = getWifiApSSID();

		// set encrypt type
		apConfig.wepKeys[0] = null;
		apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		apConfig.wepTxKeyIndex = 0;

		if (!setHTCSSID(apConfig))
			return false;

		boolean flag = enableHtcHotspot(apConfig, true);

		return flag;
	}

	/** enable and disable htc WiFi hotspot, 2013-12-31 add special for htc */
	private boolean enableHtcHotspot(WifiConfiguration apConfig, boolean enable) {

		Method method = null;
		try {
			method = mWifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, Boolean.TYPE);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		// 杩斿洖鐑偣鎵撳紑鐘舵�
		boolean flag = false;
		try {
			flag = (Boolean) method.invoke(mWifiManager, apConfig, enable);
		} catch (IllegalArgumentException e) {
			flag = false;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			flag = false;
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			flag = false;
			e.printStackTrace();
		}

		return flag;
	}

	/** set htc hot spot SSID etc. 2013-12-31 add special for htc */
	public boolean setHTCSSID(WifiConfiguration config) {
		Field localField1;
		boolean successed = true;
		try {
			localField1 = WifiConfiguration.class
					.getDeclaredField("mWifiApProfile");
			localField1.setAccessible(true);
			Object localoObject1 = localField1.get(config);
			localField1.setAccessible(false);

			if (localoObject1 != null) {
				Field localField2 = localoObject1.getClass().getDeclaredField(
						"SSID");
				localField2.setAccessible(true);
				localField2.set(localoObject1, config.SSID);
				localField2.setAccessible(false);

				Field localField4 = localoObject1.getClass().getDeclaredField(
						"BSSID");
				localField4.setAccessible(true);
				localField4.set(localoObject1, config.BSSID);
				localField4.setAccessible(false);

				Field localField3 = localoObject1.getClass().getDeclaredField(
						"dhcpEnable");
				localField3.setAccessible(true);
				localField3.set(localoObject1, 1);
				localField3.setAccessible(false);

				// open涓嶅姞鑾�wpa2-psk wpa-psk鎴戜滑浣跨敤wpa-psk鏂瑰紡鍔犲瘑鍜屼箣鍓嶇増鏈吋瀹�
				// wap-psk 涓簑ep鏂瑰紡鍔犲瘑, open涓嶅姞瀵�
				Field locaField4 = localoObject1.getClass().getDeclaredField(
						"secureType");
				locaField4.setAccessible(true);
				locaField4.set(localoObject1, "open"); // very important, no
														// encrypt, must open
				locaField4.setAccessible(false);

			}
		} catch (Exception e) {
			successed = false;
		}
		return successed;
	}

	/** stop WiFi Hot spot and refresh the former WiFi state */
	public void stopWifiAp() {
		releaseWifiLock();
		// close WiFi hot spot
		if (isWifiApEnabled())
			setWifiApEnabled(getWifiApConfiguration(), false);
		else
			enableHtcHotspot(getWifiApConfiguration(), false);
	}

	/** stop WiFi AP and start original state */
	public void stopWifiAp(boolean isWiFiActive) {
		releaseWifiLock();
		// close WiFi hot spot
		if (isWifiApEnabled())
			setWifiApEnabled(getWifiApConfiguration(), false);
		else
			enableHtcHotspot(getWifiApConfiguration(), false);

		if (isWiFiActive)
			openWifi();
	}

	
	public void destroy(Context context) {
		stopWifiAp();
		removeNetwork(SSID_PREFIX);
		context.unregisterReceiver(mWifiApStateReceiver);
	}

	/**
	 * get access point ssid
	 * 
	 * @return ssid string
	 */
	public String getWifiApSSID() {
		return mWifiApSSID;
	}

	/** get WiFi hot spot gate */
	public String getWiFiHotSpotGate() {
		String gateString = null;
		try {
			gateString = getLocalIpAddress();
			if (gateString == null)
				gateString = Constant.FREE_SERVER; // 鏆傛椂鍐欐銆傝疆璇㈢殑鏂瑰紡锛屽緱鍒扮綉鍏冲湴鍧�細涓簄ull锛坰ometimes)
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return gateString;
	}

	/** scan all net Adapter to get all IP, just return 192 */
	public static String getLocalIpAddress() throws UnknownHostException {

		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& (inetAddress.getAddress().length == 4)
							&& inetAddress.getHostAddress().startsWith(
									"192.168")) {
						return inetAddress.getHostAddress();
					}

				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	/**
	 * wifi AP缃戠粶鐘舵�鐩戝惉鍣�
	 */
	public static interface OnWifiApStateChangeListener {
		/**
		 * access point status listener
		 * 
		 * @param state
		 *            wifi state now
		 */
		public void onWifiStateChanged(int state);
	}

}
