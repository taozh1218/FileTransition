package com.taozhang.filetransition.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.bean.UserInfo;
import com.taozhang.filetransition.ui.LoginActivity;
import com.taozhang.filetransition.util.TbUserUtil;

@SuppressLint("ValidFragment")
public class RegisterFrag3 extends Fragment {

	private static final String TAG = RegisterFrag3.class.getSimpleName();

	private Context mContext;
	private View frag;

	private static final String UID = "uid";
	private String mUid;
	private EditText mEdt_reg3_psw;
	private EditText mEdt_reg3_confirmPsw;
	private EditText medt_reg3_userName;
	private Button mBtn_reg3_commit;
	private String userName;
	private String mPsw;

	public RegisterFrag3() {
	}

	public static RegisterFrag3 newInstance(String uid) {
		RegisterFrag3 fragment = new RegisterFrag3();
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		frag = inflater.inflate(R.layout.frag_reg3, container, false);
		getComponent();
		return frag;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	private void getComponent() {
		mEdt_reg3_psw = (EditText) frag.findViewById(R.id.edt_reg3_psw);
		mEdt_reg3_confirmPsw = (EditText) frag
				.findViewById(R.id.edt_reg3_confirmPsw);
		medt_reg3_userName = (EditText) frag
				.findViewById(R.id.edt_reg3_userName);
		mBtn_reg3_commit = (Button) frag.findViewById(R.id.btn_reg3_commit);

		mBtn_reg3_commit.setOnClickListener(clickListener);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.btn_reg3_commit:
				mPsw = mEdt_reg3_psw.getText().toString().trim();
				userName = medt_reg3_userName.getText().toString().trim();
				String psw_confirm = mEdt_reg3_confirmPsw.getText().toString()
						.trim();

				if (!TextUtils.isEmpty(mPsw) && !TextUtils.isEmpty(psw_confirm)) {//
					if (!mPsw.equals(psw_confirm)) {
						Toast.makeText(getActivity(), "两次密码不一致",
								Toast.LENGTH_LONG).show();
					} else {
						// new Thread() {
						// @Override
						// public void run() {
						// super.run();
						// TODO
						// UserInfo user = new UserInfo();
						// user.setUid(mUid);
						// user.setUsername(userName);
						// String psw_md5 = EncryptUtil.getMD5(mPsw);
						// user.setPassword(psw_md5);
						// user.setRole(role);
						// Gson gson = new Gson();
						// Map<String, String> params = new
						// HashMap<String, String>();
						// params.put("uid", mUid);
						// params.put("userName", userName);
						// params.put("password", psw_md5);
						//
						// System.out.println("params:"
						// + params.toString());
						// //
						// http://192.168.0.158:8080/plant/android/registAction
						// String responseJson = HttpClientJson
						// .post("http://192.168.0.148:8080/plant/android/registAction",
						// params);
						// Message msg = new Message();
						// msg.what = 1;
						// msg.obj = responseJson;
						// handler.sendMessage(msg);
						// }
						// }.start();
						// 插入数据库
						UserInfo user = new UserInfo();
						user.uid = mUid;
						user.psw = psw_confirm;
						long insertIntoTB = TbUserUtil.insertIntoTB(mContext,
								user);
						Log.i(TAG, "insert long " + insertIntoTB);

						Intent intent = new Intent(getActivity(),
								LoginActivity.class);
						startActivity(intent);
						getActivity().finish();
					}
				} else {//
					Toast.makeText(getActivity(), "不能有一项为空！",
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
			switch (what) {
			case 1:
				// TODO
				break;

			default:
				break;
			}
		};
	};

}
