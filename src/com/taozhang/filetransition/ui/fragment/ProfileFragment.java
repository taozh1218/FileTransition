package com.taozhang.filetransition.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.UserInfo;
import com.taozhang.filetransition.ui.HelpActivity;
import com.taozhang.filetransition.ui.LoginActivity;
import com.taozhang.filetransition.util.TbUserUtil;

/**
 * Description: Created by taozhang on 2016/5/10. Company:Geowind,University of
 * South China. ContactQQ:962076337
 * 
 * @updateAuthor taozhang
 * @updateDate 2016/5/10
 */
@SuppressLint({ "ValidFragment", "NewApi" })
public class ProfileFragment extends Fragment {

	private String TAG = getClass().getName();

	private View profile;
	private Context context;
	private AlertDialog dialog;
	private TextView tv_userName;
	private TextView tv_sign;

	private SharedPreferences sharedPreferences;
	private FrameLayout frame;
	private RelativeLayout rl_content;

	private static ProfileFragment fragment = null;

	private ProfileFragment(Context context) {
		this.context = context;
	}

	public ProfileFragment() {
		super();
	}

	@SuppressLint("ValidFragment")
	public static ProfileFragment getInstance(Context context) {
		if (fragment == null) {
			synchronized (OperateComputerFragment.class) {
				if (fragment == null) {
					fragment = new ProfileFragment(context);
				}
			}
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		profile = inflater.inflate(R.layout.frag_profile, null);

		frame = (FrameLayout) profile
				.findViewById(R.id.frameLayout_profileFrag);
		rl_content = (RelativeLayout) profile
				.findViewById(R.id.rl_profileFrag_content);
		// 判断是否要登录
		sharedPreferences = App.mPreferences;
		String uid = sharedPreferences.getString("uid", "");
		if (uid.equals("")) {
			frame.setVisibility(View.VISIBLE);
			rl_content.setVisibility(View.GONE);
		} else {
			frame.setVisibility(View.GONE);
			rl_content.setVisibility(View.VISIBLE);
		}

		getComponent();
		return profile;
	}

	private void getComponent() {
		// frame里面的component
		TextView tv_isLogin = (TextView) profile
				.findViewById(R.id.tv_proflieFrag_isLogin);
		tv_isLogin.setOnClickListener(onClickListener);

		// rl_content的component
		ImageView img_avator = (ImageView) profile
				.findViewById(R.id.img_fragProfile_avatar);// 头像
		tv_userName = (TextView) profile
				.findViewById(R.id.tv_fragProfile_userName);// userName
		tv_sign = (TextView) profile.findViewById(R.id.tv_fragProfile_sign);// 签名
		TextView tv_help = (TextView) profile
				.findViewById(R.id.tv_proflieFrag_help);// 帮助
		Button btn_logout = (Button) profile
				.findViewById(R.id.btn_fragProfile_logout);
		String username = sharedPreferences.getString("username", "");
		String sign = sharedPreferences.getString("sign", "");
		if (!username.equals("")) {
			tv_userName.setText(username);
			if (!sign.equals("")) {
				tv_sign.setText(sign);
			}
		}

		img_avator.setOnClickListener(onClickListener);
		tv_userName.setOnClickListener(onClickListener);
		tv_sign.setOnClickListener(onClickListener);
		tv_help.setOnClickListener(onClickListener);
		btn_logout.setOnClickListener(onClickListener);
	}

	OnClickListener onClickListener = new OnClickListener() {
		Intent intent;
		Editor editor;
		UserInfo user;

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {

			case R.id.tv_proflieFrag_isLogin:
				intent = new Intent(getActivity(), LoginActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
				break;
			case R.id.img_fragProfile_avatar:
				break;
			case R.id.tv_proflieFrag_help:// 帮助页面
				intent = new Intent(getActivity(), HelpActivity.class);
				startActivity(intent);
				break;
			case R.id.tv_fragProfile_userName:
				AlertDialog.Builder builder = new Builder(getActivity());
				// 获取自定义的布局
				final View alertDialogView = View.inflate(getActivity(),
						R.layout.dialog_edittext, null);
				builder.setTitle("请输入用户名");
				builder.setNegativeButton("取消", null);
				builder.setPositiveButton("确认",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int id) {
								EditText edt = (EditText) alertDialogView
										.findViewById(R.id.dialog_edt);
								String trim = edt.getText().toString().trim();
								tv_userName.setText(trim);

								if (sharedPreferences != null) {
									String uid = sharedPreferences.getString(
											"uid", "");
									String psw = sharedPreferences.getString(
											"psw", "");
									// 更新数据库
									editor = sharedPreferences.edit();
									editor.putString("username", trim);
									editor.commit();

									String sign = sharedPreferences.getString(
											"sign", "");
									user = new UserInfo();
									user.uid = uid;
									user.psw = psw;
									if (!sign.equals("")) {
										user.sign = sign;
									}
									TbUserUtil.updateTb(context, user);
								}
							}
						});
				dialog = builder.create();
				dialog.setView(alertDialogView, 0, 0, 0, 0);
				dialog.show();

				break;
			case R.id.tv_fragProfile_sign:
				AlertDialog.Builder builder1 = new Builder(getActivity());
				// 获取自定义的布局
				final View alertDialogView1 = View.inflate(getActivity(),
						R.layout.dialog_edittext, null);
				builder1.setTitle("请输入签名");
				builder1.setNegativeButton("取消", null);
				builder1.setPositiveButton("确认",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int id) {
								EditText edt = (EditText) alertDialogView1
										.findViewById(R.id.dialog_edt);
								String trim = edt.getText().toString().trim();
								tv_sign.setText(trim);

								if (sharedPreferences != null) {
									String uid = sharedPreferences.getString(
											"uid", "");
									String psw = sharedPreferences.getString(
											"psw", "");
									// 更新数据库
									editor = sharedPreferences.edit();
									editor.putString("sign", trim);
									editor.commit();

									String username = sharedPreferences
											.getString("username", "");
									user = new UserInfo();
									user.uid = uid;
									user.psw = psw;
									if (!username.equals("")) {// 获取的不为空
										user.username = username;
									}
									TbUserUtil.updateTb(context, user);
								}
							}
						});
				dialog = builder1.create();
				dialog.setView(alertDialogView1, 0, 0, 0, 0);
				dialog.show();
				break;
			case R.id.btn_fragProfile_logout:
				if (sharedPreferences != null) {
					// preferences置空
					editor = sharedPreferences.edit();
					editor.putString("username", "");
					editor.commit();
				}

				intent = new Intent(getActivity(), LoginActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();

				break;

			default:
				break;
			}
		}

	};
}
