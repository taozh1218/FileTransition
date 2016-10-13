package com.taozhang.filetransition.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.taozhang.filetransition.R;

@SuppressLint("ValidFragment")
public class RegisterFrag2 extends Fragment {

	private static final String TAG = RegisterFrag2.class.getSimpleName();

	private Context mContext;
	private View frag;
	private EditText mEdt_code;
	private Button btn_resend;
	private Button btn_commit;

	private static final String UID = "uid";
	private String mUid;
	private int time = 60;

	public RegisterFrag2() {
	}

	public static RegisterFrag2 newInstance(String uid) {
		RegisterFrag2 fragment = new RegisterFrag2();
		Bundle args = new Bundle();
		args.putString("uid", uid);
		fragment.setArguments(args);
		return fragment;
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mUid = getArguments().getString(UID);
		}
		SMSSDK.registerEventHandler(eh);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		frag = inflater.inflate(R.layout.frag_reg2, container, false);
		getComponent();
		reminderText();
		return frag;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	private void getComponent() {
		mEdt_code = (EditText) frag.findViewById(R.id.edt_reg2_code);
		btn_commit = (Button) frag.findViewById(R.id.btn_reg2_commitCheckCode);
		btn_resend = (Button) frag.findViewById(R.id.btn_reg2_resend);

		btn_commit.setOnClickListener(clickListener);
		btn_resend.setOnClickListener(clickListener);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.btn_reg2_commitCheckCode:
				String code = mEdt_code.getText().toString().trim();
				if (!TextUtils.isEmpty(code)) {
					if (code.length() == 4) {
						btn_commit.setBackgroundColor(getResources().getColor(
								R.color.custom_green));
						SMSSDK.submitVerificationCode("86", mUid, code);
						// Fragment_reg3 fragment_reg3 = new Fragment_reg3();

					} else {
						Toast.makeText(getActivity(), "验证码错误",
								Toast.LENGTH_LONG).show();
						mEdt_code.requestFocus();
					}
				}
				break;
			case R.id.btn_reg2_resend:
				SMSSDK.getVerificationCode("86", mUid);
				btn_resend.setClickable(false);
				reminderText();
				break;
			default:
				break;
			}
		}

	};

	private void reminderText() {
		handler.sendEmptyMessageDelayed(2, 1000);
	}

	Handler handler = new Handler() {
		@SuppressLint("ResourceAsColor")
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:// 验证码正确
				FragmentTransaction fragmentTransaction = getFragmentManager()
						.beginTransaction();
				RegisterFrag3 fragment_reg3 = RegisterFrag3.newInstance(mUid);
				fragment_reg3.setContext(mContext);
				fragmentTransaction.remove(RegisterFrag2.this);
				fragmentTransaction.add(R.id.frameLayout_RegAct, fragment_reg3);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
				break;
			case 2:
				if (time > 0) {
					btn_resend.setText(time + "s可重新发送");
					btn_resend.setBackgroundColor(Color.GRAY);
					btn_resend.setClickable(false);
					time--;
					handler.sendEmptyMessageDelayed(2, 1000);
				} else {
					btn_resend.setText("重新发送");
					btn_resend.setBackgroundColor(getResources().getColor(
							R.color.custom_green));
					btn_resend.setClickable(true);
					time = 60;
					btn_resend.setClickable(true);
				}
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
					handler.sendEmptyMessage(1);
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
				} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
				}
			} else {
				Toast.makeText(getActivity(), "验证失败！", Toast.LENGTH_SHORT)
						.show();
				((Throwable) data).printStackTrace();
			}
		}
	};

	public void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}

}
