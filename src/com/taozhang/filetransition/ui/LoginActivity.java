package com.taozhang.filetransition.ui;

import java.sql.Timestamp;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.bean.UserInfo;
import com.taozhang.filetransition.util.TbUserUtil;

public class LoginActivity extends Activity {

	// component
	private ImageView img_avator;
	private EditText edt_account;
	private EditText edt_psw;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		sharedPreferences = getSharedPreferences("fileTransitionsp",
				Context.MODE_PRIVATE);// 没有则创建
		getComponent();
	}

	private void getComponent() {
		ImageView img_return = (ImageView) findViewById(R.id.img_login_return);
		TextView tv_return = (TextView) findViewById(R.id.tv_login_return);
		Button btn_register = (Button) findViewById(R.id.btn_loginAct_register);
		img_avator = (ImageView) findViewById(R.id.img_logAct_avator);
		edt_account = (EditText) findViewById(R.id.edt_loginAct_account);
		edt_psw = (EditText) findViewById(R.id.edt_loginAct_psw);
		Button btn_login = (Button) findViewById(R.id.sign_in_button);

		img_return.setOnClickListener(onClickListener);
		tv_return.setOnClickListener(onClickListener);
		btn_register.setOnClickListener(onClickListener);
		btn_login.setOnClickListener(onClickListener);
	}

	OnClickListener onClickListener = new OnClickListener() {

		Intent intent;

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.img_login_return:
				intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
				break;
			case R.id.tv_login_return:
				intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
				break;
			case R.id.btn_loginAct_register:
				intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
				break;
			case R.id.sign_in_button:
				// TODO
				String account = edt_account.getText().toString().trim();
				String psw = edt_psw.getText().toString().trim();

				// 读取数据库中用户信息
				List<UserInfo> list = TbUserUtil.queryByAccount(
						getApplicationContext(), account);
				if (list.size() != 0) {
					UserInfo userInfo = list.get(0);
					if (psw.equals(userInfo.psw)) {// 登录成功
						// 写入preference
						Editor edit = sharedPreferences.edit();
						Editor editor = sharedPreferences.edit();
						editor.putString("uid", account);
						editor.putString("psw", psw);
						if (userInfo.sign != null) {
							editor.putString("username", userInfo.username);
							editor.putString("sign", userInfo.sign);
						}
						editor.putString("loginTime",
								new Timestamp(System.currentTimeMillis())
										.toString());
						editor.commit();// 提交修改

						// 跳转到主页面
						intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
						LoginActivity.this.finish();
					} else {
						Toast.makeText(getApplicationContext(), "密码错误！",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(getApplicationContext(), "sorry，账号不存在！",
							Toast.LENGTH_SHORT).show();
				}

				break;
			default:
				break;
			}
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;

		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			this.finish();
		}
		return false;
	}

}
