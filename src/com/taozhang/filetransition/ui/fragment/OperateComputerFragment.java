package com.taozhang.filetransition.ui.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taozhang.filetransition.R;

/**
 * Description: This is a Frag in which we can operate the Computer Created by
 * taozhang on 2016/5/10. Company:Geowind,University of South China.
 * ContactQQ:962076337
 * 
 * @updateAuthor taozhang
 * @updateDate 2016/5/10
 */
@SuppressLint("ValidFragment")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class OperateComputerFragment extends Fragment {

	private Context context;
	private View computer;
	@SuppressLint("NewApi")
	private Fragment current_frag;


	private static OperateComputerFragment computerFragment_instance = null;

	private OperateComputerFragment(Context context) {
		this.context = context;
	}

	public OperateComputerFragment() {
		super();
	}

	public static OperateComputerFragment getInstance(Context context) {
		if (computerFragment_instance == null) {
			synchronized (OperateComputerFragment.class) {
				if (computerFragment_instance == null) {
					computerFragment_instance = new OperateComputerFragment(
							context);
				}
			}
		}
		return computerFragment_instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// inflate the view of fragment
		computer = inflater.inflate(R.layout.frag_computer, null);
		getComponent();
		return computer;
	}

	private void getComponent() {
		// TODO
	}

}
