package com.taozhang.filetransition.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.smssdk.SMSSDK;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.ui.fragment.RegisterFrag1;

public class RegisterActivity extends Activity {

	private ImageView mImg_reg_back;
	private TextView mTv_reg_return;
	private RegisterFrag1 mFragment_reg1;
	private Fragment current_frag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	private void init() {
		SMSSDK.initSDK(this, "13b9202bef620",
				"9add26fde9123c5db70c23887be8e2f4");

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		mImg_reg_back = (ImageView) findViewById(R.id.img_reg_back);
		mTv_reg_return = (TextView) findViewById(R.id.tv_reg_return);
		mImg_reg_back.setOnClickListener(mListener);
		mTv_reg_return.setOnClickListener(mListener);

		mFragment_reg1 = new RegisterFrag1(getApplicationContext());
		current_frag = mFragment_reg1;
		fragmentTransaction.add(R.id.frameLayout_RegAct, current_frag);
		fragmentTransaction.commit();

	}

	private View.OnClickListener mListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			Intent intent;
			switch (id) {
			case R.id.img_reg_back:
				intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(intent);
				finish();
				break;
			case R.id.tv_reg_return:
				intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(intent);
				finish();
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}

	/**
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			startActivity(intent);
			RegisterActivity.this.finish();
			break;
		default:
			break;
		}
		return false;
	}

}
