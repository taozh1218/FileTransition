package com.taozhang.filetransition.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfFileDetail;
import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.util.DBUtil;

@SuppressLint("ValidFragment")
public class FileListFrag extends Fragment {

	private Context context;
	private View view;
	private AdapterOfFileDetail adapter;
	private String type;
	private List<FileDetail> list = new ArrayList<FileDetail>();
	@SuppressLint("ValidFragment")
	private static FileListFrag frag;

	private FileListFrag(Context context) {
		this.context = context;
	}

	public FileListFrag() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static FileListFrag getInstance(Context context) {
		if (frag == null) {
			synchronized (FileListFrag.class) {
				if (frag == null) {
					frag = new FileListFrag(context);
				}
			}
		}
		return frag;
	}

	public List<FileDetail> getList() {
		return list;
	}

	public void setList(List<FileDetail> list) {
		this.list = list;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_flielist, null);
		getComponent();
		return view;
	}

	private void getComponent() {
		ListView lv = (ListView) view.findViewById(R.id.lv_fileList_frag);

		adapter = new AdapterOfFileDetail(getActivity());
		if (type != null) {
			// 查询数据库，获取数据
			List<FileDetail> fileList = DBUtil.queryByType(getActivity(),
					type);
			if (fileList != null) {
				adapter.setList(fileList);
			}
		}
		lv.setAdapter(adapter);
	}

}
