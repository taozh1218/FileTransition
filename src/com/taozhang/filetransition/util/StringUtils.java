package com.taozhang.filetransition.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * 瀛楃涓叉搷浣滃伐鍏峰寘
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	// private final static SimpleDateFormat dateFormater = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// private final static SimpleDateFormat dateFormater2 = new
	// SimpleDateFormat("yyyy-MM-dd");

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * 灏嗗瓧绗︿覆杞负鏃ユ湡绫诲瀷
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}


	/**
	 * 鍒ゆ柇缁欏畾瀛楃涓叉椂闂存槸鍚︿负浠婃棩
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.get().format(today);
			String timeDate = dateFormater2.get().format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 杩斿洖long绫诲瀷鐨勪粖澶╃殑鏃ユ湡
	 * 
	 * @return
	 */
	public static long getToday() {
		Calendar cal = Calendar.getInstance();
		String curDate = dateFormater2.get().format(cal.getTime());
		curDate = curDate.replace("-", "");
		return Long.parseLong(curDate);
	}

	/**
	 * @Description锛�杩斿洖瀛楃涓叉牸寮忕殑褰撳墠鏃堕棿
	 *@return
	 */
	public static String getCurrentTime(){
		return dateFormater.get().format(new Date());
	}
	/**
	 * 鍒ゆ柇缁欏畾瀛楃涓叉槸鍚︾┖鐧戒覆銆�绌虹櫧涓叉槸鎸囩敱绌烘牸銆佸埗琛ㄧ銆佸洖杞︾銆佹崲琛岀缁勬垚鐨勫瓧绗︿覆 鑻ヨ緭鍏ュ瓧绗︿覆涓簄ull鎴栫┖瀛楃涓诧紝杩斿洖true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}


	/**
	 * 瀛楃涓茶浆鏁存暟
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 瀵硅薄杞暣鏁�	 * 
	 * @param obj
	 * @return 杞崲寮傚父杩斿洖 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 瀵硅薄杞暣鏁�	 * 
	 * @param obj
	 * @return 杞崲寮傚父杩斿洖 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}


	/**
	 * 瀵硅薄杞诞鐐�	 *
	 * @param obj
	 * @return 杞崲寮傚父杩斿洖 0
	 */
	public static double toDouble(String obj) {
		try {
			return Double.parseDouble(obj);
		} catch (Exception e) {
		}
		return 0;
	}
	/**
	 * 瀛楃涓茶浆甯冨皵鍊�	 * 
	 * @param b
	 * @return 杞崲寮傚父杩斿洖 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 灏嗕竴涓狪nputStream娴佽浆鎹㈡垚瀛楃涓�	 * 
	 * @param is
	 * @return
	 */
	public static String toConvertString(InputStream is) {
		StringBuffer res = new StringBuffer();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader read = new BufferedReader(isr);
		try {
			String line;
			line = read.readLine();
			while (line != null) {
				res.append(line);
				line = read.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != isr) {
					isr.close();
					isr.close();
				}
				if (null != read) {
					read.close();
					read = null;
				}
				if (null != is) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
			}
		}
		return res.toString();
	}
	// 瑙ｅ帇缂�  
	public static String uncompress(InputStream str) throws IOException {   
		if (str == null ) {   
			return "";   
		}   
		ByteArrayOutputStream out = new ByteArrayOutputStream();   

		GZIPInputStream gunzip = new GZIPInputStream(str);   
		byte[] buffer = new byte[256];   
		int n;   
		while ((n = gunzip.read(buffer))>= 0) {   
			out.write(buffer, 0, n);   
		}   
		// toString()浣跨敤骞冲彴榛樿缂栫爜锛屼篃鍙互鏄惧紡鐨勬寚瀹氬toString(&quot;GBK&quot;)   
		return out.toString("utf-8");   
	}  
	//M5D鍔犲瘑
	public  static String MD5(String s) {
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
		try {
			byte[] btInput = s.getBytes();
			// 鑾峰緱MD5鎽樿绠楁硶鐨�MessageDigest 瀵硅薄
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 浣跨敤鎸囧畾鐨勫瓧鑺傛洿鏂版憳瑕�			mdInst.update(btInput);
			// 鑾峰緱瀵嗘枃
			byte[] md = mdInst.digest();
			// 鎶婂瘑鏂囪浆鎹㈡垚鍗佸叚杩涘埗鐨勫瓧绗︿覆褰㈠紡
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final double EARTH_RADIUS = 6378137.0;  
	public static double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
}
