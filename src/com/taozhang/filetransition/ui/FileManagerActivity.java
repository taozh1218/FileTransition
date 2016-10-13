package com.taozhang.filetransition.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfListViewFileManager;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.OpenDocumentUtilZT;
import com.taozhang.filetransition.util.SortFileUtil;
import com.taozhang.filetransition.util.SplitStringUtil;

/**
 * customed fileManager class
 * 
 * can do file operation here
 * 
 * @author taozhang
 * 
 */
public class FileManagerActivity extends Activity {

	private TextView tv;
	private AdapterOfListViewFileManager adapter;
	private List<FileInfo> list;
	private FileInfo fileBean;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_manager);

		initComponent();
		getFiles(Environment.getExternalStorageDirectory().getPath()
				+ "/fileTransition");// 默认打开接收到的文件所在的路径
	}

	/**
	 * 获取文件列表
	 */
	private void getFiles(String filepath) {
		File[] files = new File(filepath).listFiles();
		list = new ArrayList<FileInfo>();
		if (files != null) {
			for (File file : files) {
				fileBean = new FileInfo();
				String path = file.getAbsolutePath();
				String name = file.getName();
				// Log.i("fileInfo", "name:" + name + ",path:" + path);
				// List<String> arrayByIndexOf =
				// SplitStringUtil.getArrayByIndexOf(
				// name, ".");
				// String type = arrayByIndexOf.get(arrayByIndexOf.size()-1);
				List<String> arrayBySplit = SplitStringUtil.getArrayByIndexOf(
						name, ".");
				if (arrayBySplit != null) {
					for (String string : arrayBySplit) {
						String type = arrayBySplit.get(arrayBySplit.size() - 1);
						System.out.println("split ." + string);
						fileBean.fileType = type;
						fileBean.IsDir = false;
					}
				} else {
					fileBean.IsDir = true;
				}
				fileBean.filePath = path;
				fileBean.fileName = name;
				list.add(fileBean);
			}
			adapter.setList(list);
			adapter.notifyDataSetChanged();
		}
		// else{
		// Toast.makeText(getApplicationContext(), "该目录下无文件!",
		// Toast.LENGTH_SHORT).show();
		// }

	}

	private void initComponent() {
		ImageView img = (ImageView) findViewById(R.id.img_fileManager_back);
		tv = (TextView) findViewById(R.id.tv_fileManager_directory);
		tv.setText(Environment.getExternalStorageDirectory().getPath()
				+ "/fileTransition");

		ListView lv = (ListView) findViewById(R.id.lv_fileManagerAct);

		// set Adapter
		adapter = new AdapterOfListViewFileManager(getApplicationContext());
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View conertView,
					int position, long id) {
				fileBean = adapter.getList().get(position);
				String filePath = fileBean.filePath;
				if (fileBean.IsDir) {// 文件夹
					// 改变tv的text，并且重新获取文件信息
					getFiles(filePath);
					tv.setText(filePath);
				} else {// 文件，选择打开
					// 打开文件
					File file = new File(filePath);
					// OpenDocumentUtil.openFile(file, getApplicationContext());
					Intent openFile = OpenDocumentUtilZT.openFile(file);
					startActivity(openFile);
				}

			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View convertView, int position, long id) {
				Log.i("FileManager longClick", "onItemLongClick");

				// TODO 文件的操作
				// 创建dialog
				AlertDialog.Builder builder = new AlertDialog.Builder(
						FileManagerActivity.this);
				// 获取自定义的布局
				View alertDialogView = View.inflate(FileManagerActivity.this,
						R.layout.dialog_filemanager, null);

				dialog = builder.create();
				dialog.setView(alertDialogView, 0, 0, 0, 0);

				// find component
				TextView tv_copy = (TextView) alertDialogView
						.findViewById(R.id.tv_fileMngdialog_copy);
				TextView tv_zhantie = (TextView) alertDialogView
						.findViewById(R.id.tv_fileMngdialog_zhantie);
				TextView tv_delete = (TextView) alertDialogView
						.findViewById(R.id.tv_fileMngdialog_delete);
				TextView tv_reaname = (TextView) alertDialogView
						.findViewById(R.id.tv_fileMngdialog_rename);
				TextView tv_copyPath = (TextView) alertDialogView
						.findViewById(R.id.tv_fileMngdialog_copyPath);
				TextView tv_send = (TextView) alertDialogView
						.findViewById(R.id.tv_fileMngdialog_send);
				TextView tv_detail = (TextView) alertDialogView
						.findViewById(R.id.tv_fileMngdialog_detail);
				tv_copy.setOnClickListener(onClickListener);
				tv_zhantie.setOnClickListener(onClickListener);
				tv_delete.setOnClickListener(onClickListener);
				tv_reaname.setOnClickListener(onClickListener);
				tv_copyPath.setOnClickListener(onClickListener);
				tv_send.setOnClickListener(onClickListener);
				tv_detail.setOnClickListener(onClickListener);
				// 显示dialog
				dialog.show();

				return true;
			}
		});

		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// FileManagerActivity.this.finish();
				toUpDirectory();
			}
		});

		ImageView img_more = (ImageView) findViewById(R.id.img_fileManager_more);
		img_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 显示一个对话框，对文件排序
				// 按名字排序
				File file = new File(tv.getText().toString());
				File[] listSortedFiles = SortFileUtil.sortFilesByName(file);
				for (File file2 : listSortedFiles) {
					System.out.println(file2.getAbsolutePath());
				}

				// 按修改时间排序
				File[] listFiles = file.listFiles();
				List<File> list = new ArrayList<File>();
				if (listFiles != null) {
					for (File file2 : listFiles) {
						list.add(file2);
					}
					SortFileUtil.sortFilesByTime(list);
					for (File file3 : list) {
						Log.i("FileManager sortByTime", file3.getAbsolutePath());

					}
				}

				// 按大小排序
			}
		});
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			dialog.dismiss();// 取消显示
		}
	};

	/**
	 * 跳转到上级目录
	 */
	public void toUpDirectory() {
		String currentPath = tv.getText().toString();
		File file = new File(currentPath);
		String parentPath = file.getParent();

		if (parentPath == null) {
			return;
		}
		if (currentPath.equals(Environment.getExternalStorageDirectory()
				.getAbsolutePath())) {
			Toast.makeText(getApplicationContext(),
					Constant.ALERT_ROOTPATH + ":" + parentPath,
					Toast.LENGTH_LONG).show();
		} else {
			getFiles(parentPath);
			tv.setText(parentPath);
		}

		// 好像文件夹获取不到父文件夹,测试证明还是getParent是正确的
		// List<String> pathList = SplitStringUtil.getArrayByStringTokenizer(
		// fileBean.filePath, "/");
		// String parentPath1 = "";
		// for (int i = 0; i < pathList.size() - 1; i++) {
		// parentPath1 += "/" + pathList.get(i);
		// }
		//
		// Log.e("parent",
		// parentPath + ",parentPath1：" + parentPath1
		// + ",Environment.getExternalStorageDirectory():"
		// + Environment.getExternalStorageDirectory());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_manager, menu);
		return true;
	}
}
