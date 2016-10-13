package com.taozhang.filetransition.ui;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.impl.IntentImpl;
import com.taozhang.filetransition.ui.fragment.HistoryFragment;
import com.taozhang.filetransition.ui.fragment.HomeFragment;
import com.taozhang.filetransition.ui.fragment.ProfileFragment;
import com.taozhang.filetransition.ui.fragment.SearchFragment;

/**
 * the entrance activity
 */
@SuppressLint("NewApi")
public class MainActivity extends Activity implements IntentImpl {

	/**
	 * 标记是否退出
	 */
	private Boolean isExit = false;
	private ImageView mImg_home;
	private ImageView mImg_history;
	private ImageView mImg_profile;
	private ImageView mImg_computer;
	private static Fragment current_frag;
	private TextView mTv_home;
	private TextView mTv_computer;
	private TextView mTv_history;
	private TextView mTv_profile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initComponent();
		// init HomeFragment
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		// check saveInstanceState
		if(savedInstanceState != null){
			// 保存有數據
			int cur = savedInstanceState.getInt(CURRENT);
			// 先把所有fragment隱藏
			fragmentTransaction.hide(HomeFragment.getInstance(App.context));
			fragmentTransaction.hide(SearchFragment.getInstance());
			fragmentTransaction.hide(HistoryFragment.getInstance(App.context));
			fragmentTransaction.hide(ProfileFragment.getInstance(App.context));
			if(cur == 0){
				fragmentTransaction.show(HomeFragment.getInstance(App.context));
			}else if(cur == 1){
				fragmentTransaction.show(SearchFragment.getInstance());
			}else if(cur == 2){
				fragmentTransaction.show(HistoryFragment.getInstance(App.context));
			}else if(cur == 3){
				fragmentTransaction.show(ProfileFragment.getInstance(App.context));
			}
		}else{
			current_frag = HomeFragment.getInstance(getApplicationContext());
			changeHomeColor();
			fragmentTransaction.add(R.id.frameLayout_MainAct, current_frag);
		}
		
		
		fragmentTransaction.commit();
	}

	@SuppressLint("NewApi")
	private void initComponent() {
		LinearLayout ll_home = (LinearLayout) findViewById(R.id.ll_tabBarHome);
		LinearLayout ll_history = (LinearLayout) findViewById(R.id.ll_tabBarHistory);
		LinearLayout ll_profile = (LinearLayout) findViewById(R.id.ll_tabBarProfile);
		LinearLayout ll_tabBarComputer = (LinearLayout) findViewById(R.id.ll_tabBarComputer);
		mImg_home = (ImageView) findViewById(R.id.img_tabBarHome);

		mImg_computer = (ImageView) findViewById(R.id.img_tabBarComputer);
		mImg_history = (ImageView) findViewById(R.id.img_tabBarHistory);
		mImg_profile = (ImageView) findViewById(R.id.img_tabBarProfile);
		mTv_home = (TextView) findViewById(R.id.tv_tabBarHome);
		mTv_computer = (TextView) findViewById(R.id.tv_tabBarComputer);
		mTv_history = (TextView) findViewById(R.id.tv_tabBarHistory);
		mTv_profile = (TextView) findViewById(R.id.tv_tabBarProfile);

		// add Listener
		ll_home.setOnClickListener(mOnClickListener);
		ll_home.setTag(0); // 標記為0號
		ll_tabBarComputer.setOnClickListener(mOnClickListener);
		ll_tabBarComputer.setTag(1);
		ll_history.setOnClickListener(mOnClickListener);
		ll_history.setTag(2);// 標記為1號
		ll_profile.setOnClickListener(mOnClickListener);
		ll_profile.setTag(3);

	}

	private FragmentManager fragmentManager = getFragmentManager();
	private static Fragment toFragment;
	@SuppressLint("NewApi")
	View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			int id = v.getId();
			currentIndex = (Integer) v.getTag(); // 記錄下標
			switch (id) {
			case R.id.ll_tabBarHome:// 主页
				toFragment = HomeFragment.getInstance(getApplication());

				changeHomeColor();
				break;
			case R.id.ll_tabBarComputer:// 电脑
				toFragment = SearchFragment.getInstance();
				changeComputerColor();
				break;
			case R.id.ll_tabBarHistory:// 历史文件
				toFragment = HistoryFragment
						.getInstance(getApplicationContext());
				changeHistoryColor();
				break;
			case R.id.ll_tabBarProfile:// 个人信息
				toFragment = ProfileFragment
						.getInstance(getApplicationContext());
				changeProfileColor();
				break;
			}
			changeFragment(current_frag, toFragment);// 改变fragment
			current_frag = toFragment;// 切换，当前的fragment
		}
	};

	/**
	 * 切换fragment
	 * 
	 * @param from
	 *            原来的framgent
	 * @param to
	 *            要切换的目标fragment
	 */
	private void changeFragment(Fragment from, Fragment to) {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		if (to.isAdded()) {
			// 已经添加过了，显示
			fragmentTransaction.hide(from).show(to).commit();
		} else {
			fragmentTransaction.hide(from).add(R.id.frameLayout_MainAct, to)
					.commit();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private void exitBy2Click() {
		Timer tExit;
		if (!isExit) {// isExit == false的简化版
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
	}

	public void changeHomeColor() {
		mImg_home.setImageResource(R.drawable.phone_red);
		mTv_home.setTextColor(Color.RED);

		// 其他是黑色
		mImg_computer.setImageResource(R.drawable.computer_black);
		mTv_computer.setTextColor(Color.BLACK);
		mImg_history.setImageResource(R.drawable.history_black);
		mTv_history.setTextColor(Color.BLACK);
		mImg_profile.setImageResource(R.drawable.profile_black);
		mTv_profile.setTextColor(Color.BLACK);
	}

	public void changeComputerColor() {
		mImg_home.setImageResource(R.drawable.phone_black);
		mTv_home.setTextColor(Color.BLACK);
		mImg_computer.setImageResource(R.drawable.computer_red);
		mTv_computer.setTextColor(Color.RED);
		mImg_history.setImageResource(R.drawable.history_black);
		mTv_history.setTextColor(Color.BLACK);
		mImg_profile.setImageResource(R.drawable.profile_black);
		mTv_profile.setTextColor(Color.BLACK);
	}

	public void changeHistoryColor() {
		mImg_home.setImageResource(R.drawable.phone_black);
		mTv_home.setTextColor(Color.BLACK);
		mImg_computer.setImageResource(R.drawable.computer_black);
		mTv_computer.setTextColor(Color.BLACK);
		mImg_history.setImageResource(R.drawable.history_red1);
		mTv_history.setTextColor(Color.RED);
		mImg_profile.setImageResource(R.drawable.profile_black);
		mTv_profile.setTextColor(Color.BLACK);
	}

	public void changeProfileColor() {
		mImg_home.setImageResource(R.drawable.phone_black);
		mTv_home.setTextColor(Color.BLACK);
		mImg_computer.setImageResource(R.drawable.computer_black);
		mTv_computer.setTextColor(Color.BLACK);
		mImg_history.setImageResource(R.drawable.history_black);
		mTv_history.setTextColor(Color.BLACK);
		mImg_profile.setImageResource(R.drawable.profile_red);
		mTv_profile.setTextColor(Color.RED);
	}

	@Override
	public void intentToFileActivity(InetSocketAddress address) {

		Intent intent = new Intent(MainActivity.this, FileListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("IP", address);
		intent.putExtra("bundle", bundle);
		startActivity(intent);

	}

	/**
	 * 获取本机ip
	 */
	@Override
	public String getLocalIpAddress() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// 获取32位整型IP地址
		int ipAddress = wifiInfo.getIpAddress();

		// 返回整型地址转换成“*.*.*.*”地址
		return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
	}

	@Override
	public void changeFragmentTo(Fragment to) {
		changeFragment(current_frag, to);
	}

	@Override
	public void intentToAnotherActivity(Class<? extends Activity> clazz) {
		Intent intent = new Intent(MainActivity.this, clazz);
		startActivity(intent);
	}

	private static final String CURRENT = "currentIndex";
	private int currentIndex = 0;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 保存當前fragment
		outState.putInt(CURRENT, currentIndex);
	}
}
