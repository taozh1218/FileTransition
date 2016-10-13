package com.taozhang.filetransition.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.util.CheckPhoneNumUtil;

@SuppressLint("ValidFragment")
public class RegisterFrag1 extends Fragment {

	private static final String TAG = RegisterFrag1.class.getSimpleName();

	private Context mContext;
	private View frag;
	private TextView tv_phoneNo;
	private CheckBox checkBox;
	private String phoneNo;
	private static final int CAN_GETCHECKCODE = 1;

	public RegisterFrag1(Context context) {
		mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SMSSDK.registerEventHandler(eh);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		frag = inflater.inflate(R.layout.frag_reg1, container, false);

		getComponent();

		return frag;
	}

	private void getComponent() {
		tv_phoneNo = (TextView) frag.findViewById(R.id.edt_reg1_phoneNum);
		Button btn_getCheckCode = (Button) frag
				.findViewById(R.id.btn_reg1_getCheckCode);
		checkBox = (CheckBox) frag.findViewById(R.id.checkbox_reg1);
		TextView tv_protocol = (TextView) frag
				.findViewById(R.id.tv_reg1_protocol);
		String s = tv_protocol.getText().toString();
		tv_protocol.setText(Html.fromHtml("<u>" + s + "</u>"));

		btn_getCheckCode.setOnClickListener(clickListener);
		tv_protocol.setOnClickListener(clickListener);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.btn_reg1_getCheckCode:
				if (checkBox.isChecked()) {
					phoneNo = tv_phoneNo.getText().toString().trim();
					if (CheckPhoneNumUtil.isMobileNO(phoneNo)
							|| CheckPhoneNumUtil.isEmail(phoneNo)) {
						SMSSDK.getVerificationCode("86", phoneNo);
					} else {
						Toast.makeText(getActivity().getApplicationContext(),
								"请填写正确手机号！", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getActivity().getApplicationContext(),
							"请同意用户协议！", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.tv_reg1_protocol:
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://www.baidu.com"));
				intent.setClassName("com.android.browser",
						"com.android.browser.BrowserActivity");
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case CAN_GETCHECKCODE:
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();

				RegisterFrag2 fragment_reg2 = RegisterFrag2
						.newInstance(phoneNo);
				fragment_reg2.setContext(mContext);
				fragmentTransaction.remove(RegisterFrag1.this);
				fragmentTransaction.add(R.id.frameLayout_RegAct, fragment_reg2);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
				Toast.makeText(getActivity(), "验证码已经发送", Toast.LENGTH_SHORT)
						.show();
				break;

			default:
				break;
			}
		};
	};

	EventHandler eh = new EventHandler() {

		@Override
		public void afterEvent(int event, int result, Object data) {

			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					Log.i(TAG, "EVENT_GET_VERIFICATION_CODE");
					handler.sendEmptyMessage(CAN_GETCHECKCODE);
				} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
				}
			} else {
				((Throwable) data).printStackTrace();
			}
		}
	};

	public void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	};
}
