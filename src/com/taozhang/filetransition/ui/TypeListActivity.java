package com.taozhang.filetransition.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.adapter.AdapterOfListViewFileManager;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.helper.MySQLiteOpenHelper;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenu;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenuCreator;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenuItem;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenuListView;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.SplitStringUtil;
import com.taozhang.filetransition.util.simplifymi.FileOperationHelper;
import com.taozhang.filetransition.util.simplifymi.FileOperationHelper.IOperationProgressListener;
import com.taozhang.filetransition.util.simplifymi.InformationDialog;
import com.taozhang.filetransition.util.simplifymi.IntentBuilder;
import com.taozhang.filetransition.util.simplifymi.TextInputDialog;
import com.taozhang.filetransition.util.simplifymi.TextInputDialog.OnFinishListener;
import com.taozhang.filetransition.util.simplifymi.Util;

/**
 * 查询对应类型的文件列表
 * 
 * @author taozhang
 * 
 */
public class TypeListActivity extends Activity implements
		IOperationProgressListener {
	private static final String LOG_TAG = "TypeListActivity";
	private AdapterOfListViewFileManager adapter;
	private AlertDialog dialog;
	int position_creator;
	private SwipeMenu menu;
	int index;
	/**
	 * 传入的类型（点击位置）
	 */
	private String positionS;
	/**
	 * 选中的位置（itemLongClick的position)
	 */
	private int mListViewSelectedItem;
	private FileOperationHelper mFileOperationHelper;
	private FileInfo mListViewSelectedFileInfo;
	private LinearLayout ll_parent;
	private String mCurrentPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_list);

		mFileOperationHelper = new FileOperationHelper(this);
		positionS = getIntent().getStringExtra("position");
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.music_list, menu);
		return true;
	}

	public String getCurrentPath() {
		return mCurrentPath;
	}

	public void setCurrentPath(String path) {
		mCurrentPath = path;
	}

	private void init() {
		ImageView img_back = (ImageView) findViewById(R.id.img_typeListAct_back);
		img_back.setOnClickListener(onClickListener);
		SwipeMenuListView lv = (SwipeMenuListView) findViewById(R.id.lv_MusicListAct_files);
		// Collection<File> listFiles =
		// FileUtils.listFiles(Environment.getRootDirectory(), new
		// String[]{".txt"}, true);
		// set Adapter
		adapter = new AdapterOfListViewFileManager(TypeListActivity.this);
		// ArrayList<FileDetail> searchFileByType =
		// DBUtil.searchFileByType("docx");
		// adapter.setList(searchFileByType);
		// TODO 初始化数据
		// List<FileDetail> list = DBUtil.queryAll(getApplicationContext());
		int parseInt = Integer.parseInt(positionS);
		List<FileInfo> filesByPath = new ArrayList<FileInfo>();
		switch (parseInt) {
		case 0:
			mCurrentPath = Constant.BASEPATH_MUSIC;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 1:
			mCurrentPath = Constant.BASEPATH_PIC;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 2:
			mCurrentPath = Constant.BASEPATH_VIDEO;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 3:
			mCurrentPath = Constant.BASEPATH_APK;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 4:
			mCurrentPath = Constant.BASEPATH_OFFICE;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 5:
			mCurrentPath = Constant.BASEPATH_ELSE;
			filesByPath = new ArrayList<FileInfo>();
			break;
		default:
			break;
		}
		filesByPath = getFilesByPath(mCurrentPath);
		adapter.setList(filesByPath);

		lv.setAdapter(adapter);
		lv.setMenuCreator(creator);
		lv.setOnMenuItemClickListener(onMenuItemClickListener);
		lv.setOnItemClickListener(mOnItemClickListener);
		lv.setOnItemLongClickListener(mItemLongClickListener);

		ll_parent = (LinearLayout) findViewById(R.id.ll_typeListAct_parent);
		if (App.isCopy) {
			ll_parent.setVisibility(View.VISIBLE);
		}

		LinearLayout ll_zhantie = (LinearLayout) findViewById(R.id.ll_typeListAct_zhantie);
		LinearLayout ll_cancel = (LinearLayout) findViewById(R.id.ll_typeListAct_cancel);
		ll_zhantie.setOnClickListener(onClickListener);
		ll_cancel.setOnClickListener(onClickListener);
	}

	/**
	 * 遍历路径获取文件列表
	 * 
	 * @param path
	 * @return
	 */
	public List<FileInfo> getFilesByPath(String path) {
		List<FileInfo> list = new ArrayList<FileInfo>();
		File file;
		File[] listFiles;
		FileInfo fileInfo;
		file = new File(path);
		listFiles = file.listFiles();
		if (listFiles != null) {
			for (File file2 : listFiles) {
				fileInfo = new FileInfo();
				fileInfo.fileName = file2.getName();
				fileInfo.filePath = file2.getAbsolutePath();
				fileInfo.IsDir = file2.isDirectory();
				List<String> arrayByStringTokenizer = SplitStringUtil
						.getArrayByStringTokenizer(fileInfo.fileName, ".");
				fileInfo.fileType = arrayByStringTokenizer
						.get(arrayByStringTokenizer.size() - 1);
				fileInfo.fileSize = file2.length();
				fileInfo.ModifiedDate = file2.lastModified();
				fileInfo.canRead = file2.canRead();
				fileInfo.canWrite = file2.canWrite();
				fileInfo.isHidden = file2.isHidden();
				System.out.println(fileInfo.toString());
				list.add(fileInfo);
			}
		}
		return list;
	}

	// 设置删除图标
	SwipeMenuCreator creator = new SwipeMenuCreator() {

		@Override
		public void create(SwipeMenu menu) {
			// create "open" item
			SwipeMenuItem item = new SwipeMenuItem(TypeListActivity.this);
			// item.setBackground(Color.RED);
			item.setBackground(new ColorDrawable(Color.rgb(0xff, 0x00, 0x00)));
			item.setWidth(150);
			item.setIcon(R.drawable.icon_delete_white);
			menu.addMenuItem(item);
		}
	};

	/**
	 * creator的监听器，但是是添加到listview上的
	 */
	OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener() {

		/**
		 * 第一个是item的position，第二个是creator的position
		 */
		@Override
		public void onMenuItemClick(int position, SwipeMenu menu, int index) {
			showDialog();
			mListViewSelectedFileInfo = adapter.getList().get(position);// 给选中的file赋值
			TypeListActivity.this.position_creator = position;
			TypeListActivity.this.menu = menu;
			TypeListActivity.this.index = index;
		}
	};

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			List<FileInfo> filelist = adapter.getList();
			FileInfo fileDetail = filelist.get(position);
			if (fileDetail != null) {
				// ===TODO=============注意，这里的path，因为带有/插入数据库失败==========
				String path = fileDetail.filePath;
				// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝以下几种方式都可以=========
				// Intent openFile = OpenFileUtil.openFile(path);
				// startActivity(openFile);

				// OpenDocumentUtil.openFile(new File(path),
				// getApplicationContext());

				File file = new File(path);
				openFile(file);

			}
		}
	};

	OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View convertView,
				int position, long id) {
			// 设置当前选中的文件，以及位置
			mListViewSelectedItem = position;
			mListViewSelectedFileInfo = (FileInfo) adapter.getItem(position);
			App.fileInfo = mListViewSelectedFileInfo;

			Log.i("FileManager longClick", "onItemLongClick");

			// 创建dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(
					TypeListActivity.this);
			// 获取自定义的布局
			View alertDialogView = View.inflate(TypeListActivity.this,
					R.layout.dialog_filemanager, null);

			dialog = builder.create();
			dialog.setView(alertDialogView, 0, 0, 0, 0);

			// find component
			TextView tv_copy = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_copy);
			TextView tv_move = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_move);
			TextView tv_zhantie = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_zhantie);
			TextView tv_reaname = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_rename);
			TextView tv_copyPath = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_copyPath);
			TextView tv_send = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_send);
			TextView tv_sendByInternet = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_sendByInternet);
			TextView tv_detail = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_detail);
			tv_copy.setOnClickListener(onClickListener);
			tv_move.setOnClickListener(onClickListener);
			tv_zhantie.setOnClickListener(onClickListener);
			tv_reaname.setOnClickListener(onClickListener);
			tv_copyPath.setOnClickListener(onClickListener);
			tv_send.setOnClickListener(onClickListener);
			tv_sendByInternet.setOnClickListener(onClickListener);
			tv_detail.setOnClickListener(onClickListener);
			// 显示dialog
			dialog.show();

			return true;
		}
	};

	// 对文件的操作
	android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.img_typeListAct_back:
				TypeListActivity.this.finish();
				break;
			case R.id.tv_fileMngdialog_copy:
				dialog.dismiss();
				App.isCopy = true;
				ll_parent.setVisibility(View.VISIBLE);
				onOperationCopy();
				Toast.makeText(getApplicationContext(), "复制成功,请去粘贴！",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.tv_fileMngdialog_move:
				dialog.dismiss();
				App.fileInfo = mListViewSelectedFileInfo;
				App.cutPosition = mListViewSelectedItem;
				App.isCopy = true;
				App.isMove = true;
				onOperationMove();
				Toast.makeText(getApplicationContext(), "剪切成功,请去粘贴！",
						Toast.LENGTH_SHORT).show();
				ll_parent.setVisibility(View.VISIBLE);
				break;
			case R.id.tv_fileMngdialog_rename:
				dialog.dismiss();
				onOperationRename();
				break;
			case R.id.tv_fileMngdialog_copyPath:
				dialog.dismiss();
				Util.copyPath(mListViewSelectedFileInfo.filePath,
						getApplicationContext());
				Toast.makeText(getApplicationContext(), "路径复制成功！",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.tv_fileMngdialog_send:
				dialog.dismiss();
				onOperationSend();
				break;
			case R.id.tv_fileMngdialog_sendByInternet:
				dialog.dismiss();
				onOperationSendByInternet();
				break;
			case R.id.tv_fileMngdialog_detail:
				dialog.dismiss();
				onOperationInfo();
				break;
			case R.id.ll_typeListAct_cancel:
				App.isCopy = false;
				ll_parent.setVisibility(View.GONE);
				mFileOperationHelper.clear();
				break;
			case R.id.ll_typeListAct_zhantie:
				App.isCopy = false;
				ll_parent.setVisibility(View.GONE);
				if (mFileOperationHelper.canPaste()) {
					onOperationPaste();
				} else {
					Toast.makeText(getApplicationContext(), "粘贴失败，您复制的文件为空~",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}

	};

	private void onOperationSendByInternet() {
		if (mListViewSelectedFileInfo.IsDir) {// 文件夹
			AlertDialog dialog = new AlertDialog.Builder(TypeListActivity.this)
					.setMessage("sorry,无法发送文件夹").setPositiveButton("确认", null)
					.create();
			dialog.show();
			return;
		}
		// 通过第三方APP分享
		Intent buildSendFile = IntentBuilder
				.buildSendFile(mListViewSelectedFileInfo);
		if (buildSendFile != null) {
			try {
				startActivity(buildSendFile);
			} catch (ActivityNotFoundException e) {
				Log.e(LOG_TAG, "fail to view file: " + e.toString());
			}
		}
	}

	/**
	 * 发送文件（文件夹无法发送）
	 */
	private void onOperationSend() {
		if (mListViewSelectedFileInfo.IsDir) {// 文件夹
			AlertDialog dialog = new AlertDialog.Builder(TypeListActivity.this)
					.setMessage("sorry,无法发送文件夹").setPositiveButton("确认", null)
					.create();
			dialog.show();
			return;
		}

		// Intent intent = new
		// Intent(TypeListActivity.this,BluetoothActivity.class);
		// intent.putExtra(Constant.FILEPATH,
		// mListViewSelectedFileInfo.filePath);
		// startActivity(intent);
		// TODO 测试
		// 分享给他人
		AdapterOfGridView_operationComputer.fileInfos.clear();
		AdapterOfGridView_operationComputer.fileInfos = new ArrayList<FileInfo>();
		AdapterOfGridView_operationComputer.fileInfos
				.add(mListViewSelectedFileInfo);

		Intent intent = new Intent(TypeListActivity.this, SendVoiceActivity.class);
		startActivity(intent);

	}

	public void onOperationInfo() {
		if (mListViewSelectedFileInfo == null)
			return;

		InformationDialog dialog = new InformationDialog(TypeListActivity.this,
				mListViewSelectedFileInfo);
		dialog.show();
	}

	/**
	 * 先粘贴，后删除,删除在回调里面
	 */
	private void onOperationMove() {
		App.cutPath = mCurrentPath;
		mFileOperationHelper.Copy(mListViewSelectedFileInfo);
		// deleteFile(mListViewSelectedItem);
	}

	private void onOperationRename() {
		int pos = mListViewSelectedItem;
		if (pos == -1)
			return;

		if (mListViewSelectedFileInfo == null)
			return;
		// mListViewSelectedFileInfo

		TextInputDialog dialog = new TextInputDialog(TypeListActivity.this,
				"重命名", "请输入新名字", mListViewSelectedFileInfo.fileName,
				new OnFinishListener() {
					@Override
					public boolean onFinish(String text) {
						return doRename(mListViewSelectedFileInfo, text);
					}

				});

		dialog.show();
	}

	private boolean doRename(final FileInfo f, String text) {
		if (TextUtils.isEmpty(text))
			return false;

		if (mFileOperationHelper.Rename(f, text)) {
			f.fileName = text;
			// mFileViewListener.onDataChanged();
			List<FileInfo> filesByPath = getFilesByPath(mCurrentPath);
			adapter.setList(filesByPath);
			adapter.notifyDataSetChanged();
		} else {
			new AlertDialog.Builder(TypeListActivity.this).setMessage("重命名失败")
					.setPositiveButton("确定", null).create().show();
			return false;
		}

		return true;
	}

	private ProgressDialog progressDialog;

	private void onOperationPaste() {
		if (App.isMove) {// 判断是不是剪切
			if (App.cutPath.equals(mCurrentPath)) {
				Toast.makeText(getApplicationContext(), "剪切失败，剪切路径与原路径一致！",
						Toast.LENGTH_LONG).show();
				return;
			}
		}
		if (mFileOperationHelper.Paste(mCurrentPath)) {
			showProgress("正在复制中...");
		}
	}

	private void showProgress(String msg) {
		progressDialog = new ProgressDialog(TypeListActivity.this);
		// dialog.setIcon(R.drawable.icon);
		progressDialog.setMessage(msg);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	/**
	 * 复制文件，存入mFileOperator的list
	 */
	private void onOperationCopy() {
		mFileOperationHelper.Copy(mListViewSelectedFileInfo);
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TypeListActivity.this);
		builder.setTitle("注意");
		builder.setMessage("会同时删除本地文件，确定要删除吗？");
		builder.setCancelable(true);
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				deleteFile(position_creator, menu, index);
			}
		});
		builder.create().show();
	}

	/**
	 * 删除本地文件
	 * 
	 * @param position
	 * @param menu
	 * @param index
	 */
	public void deleteFile(int position, SwipeMenu menu, int index) {
		// 删除相应的item
		List<FileInfo> list = adapter.getList();
		list.remove(position);
		adapter.setList(list);
		adapter.notifyDataSetChanged();

		// 删除本地文件
		onOperationDelete();
	}

	/**
	 * 删除本地文件
	 * 
	 * @param position
	 * @param menu
	 * @param index
	 */
	public void deleteFile(FileInfo fileInfo) {
		// 删除本地文件
		doOperationDelete(fileInfo);
	}

	public void onOperationDelete() {
		doOperationDelete(mListViewSelectedFileInfo);
	}

	/**
	 * 删除
	 * 
	 * @param selectedFile
	 */
	private void doOperationDelete(final FileInfo selectedFile) {
		if (mFileOperationHelper.Delete(selectedFile)) {
			showProgress("正在删除...");
		}
	}

	// 打开文件
	private void openFile(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		startActivity(intent);
	}

	// 获取文件mimetype
	private String getMIMEType(File file) {
		String type = "";
		String name = file.getName();
		// 文件扩展名
		String end = name.substring(name.lastIndexOf(".") + 1, name.length())
				.toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("mp4") || end.equals("3gp")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp") || end.equals("gif")) {
			type = "image";
		} else {
			// 如果无法直接打开，跳出列表由用户选择
			type = "*";
		}
		type += "/*";
		return type;
	}

	@Override
	public void onFinish() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	public void onFileChanged(String path) {
		if (mCurrentPath != null) {
			List<FileInfo> filesByPath = new ArrayList<FileInfo>();
			filesByPath = getFilesByPath(mCurrentPath);
			adapter.setList(filesByPath);
			handler.sendEmptyMessage(1);
		}
		if (App.isMove) {// 如果是剪切
			App.isMove = false;
			handler.sendEmptyMessage(2);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			if (progressDialog != null) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
			switch (what) {
			case 1:// notify
				adapter.notifyDataSetChanged();
				break;
			case 2:// 删除文件
				deleteFile(App.fileInfo);// 因为之前的Activity已经销毁，所以只能删除，不能再notifyadapter
				App.fileInfo = null;
			default:
				break;
			}
		};
	};
}
