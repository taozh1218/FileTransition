package com.taozhang.filetransition.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.adapter.AdapterOfViewPager;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.impl.FileSelecedImpl;
import com.taozhang.filetransition.util.ScanSystemFile;

public class SelectedFileActivity extends Activity implements FileSelecedImpl,
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selected_file);
		initComponent();

	}

	private View $(int id) {
		return findViewById(id);
	}

	List<View> views;
	private TextView finish;

	private void initComponent() {

		handler = new SelectHandler(this);
		// 完成按钮
		finish = (TextView) $(R.id.selected_file_finish);
		finish.setOnClickListener(this);
		ViewPager viewPager = (ViewPager) $(R.id.selected_file_viewPager_fileListAct);
		PagerTabStrip tabStrip = (PagerTabStrip) $(R.id.seleced_file_pagerTabStrip_fileListAct);
		tabStrip.setDrawFullUnderline(false);
		// titles
		String[] tilies = new String[] { "音乐", "Office" ,"图片", "视频"};
		// views
		views = new ArrayList<View>();
		View view1 = getLayoutInflater().inflate(R.layout.viewpager_first,
				null, false);
		views.add(view1);
		View view2 = getLayoutInflater().inflate(R.layout.viewpager_first,
				null, false);
		views.add(view2);
		View view3 = getLayoutInflater().inflate(R.layout.viewpager_first,
				null, false);
		views.add(view3);
		View view4 = getLayoutInflater().inflate(R.layout.viewpager_first,
				null, false);
		views.add(view4);
		
		AdapterOfViewPager viewPagerAdapter = new AdapterOfViewPager(views,
				tilies, App.context);
		viewPager.setAdapter(viewPagerAdapter);

		initViewPagerFirst();
		initViewPagerSecond();
		initViewPagerFouth();
		initViewPagerThirst();
	}

	

	private SelectHandler handler;

	static class SelectHandler extends Handler {
		private WeakReference<SelectedFileActivity> act;
		private SelectedFileActivity activity;

		public SelectHandler(SelectedFileActivity act) {
			this.act = new WeakReference<SelectedFileActivity>(act);
			activity = this.act.get();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			GridView view = null;
			ArrayList<FileInfo> data = null;
			switch (what) {
			case 0: // 頁面1
				view = activity.musicGridView;
				data = App.sysMusics;
				activity.progress1.setVisibility(View.GONE);
				break;
			case 1: // 頁面2
				view = activity.officGridView;
				data = App.sysOffice;
				activity.progress2.setVisibility(View.GONE);
				break;
			case 2: // 頁面4
				view = activity.videoGridView;
				data = App.sysVideo;
				activity.progress3.setVisibility(View.GONE);
				break;

			default:
				break;
			}

			if (view != null) {
				AdapterOfGridView_operationComputer adapter = (AdapterOfGridView_operationComputer) view
						.getAdapter();
				adapter.setFiles(data);
				adapter.notifyDataSetChanged();
			}

		};

	}

	GridView musicGridView;
	View progress1;

	/**
	 * 初始化第一个页面
	 */
	private void initViewPagerFirst() {
		View inflate = views.get(0);

		musicGridView = (GridView) inflate
				.findViewById(R.id.gridView_viewPager);
		progress1 = inflate.findViewById(R.id.progressLayout);
		// adapterOfGridView.setFiles(mFiles);
		final AdapterOfGridView_operationComputer adapterOfGridView = new AdapterOfGridView_operationComputer(
				this);
		adapterOfGridView.setUpdataListener(this);
		musicGridView.setAdapter(adapterOfGridView);
		if (!App.isMusicScaning) {
			progress1.setVisibility(View.GONE);
			adapterOfGridView.setFiles(App.sysMusics);
			adapterOfGridView.notifyDataSetChanged();
		} else {
			new Thread() {
				public void run() {

					while (App.isMusicScaning)
						;
					handler.sendEmptyMessage(0);

				};
			}.start();

		}

	}

	GridView officGridView;
	View progress2;

	/**
	 * 第二个pager
	 */
	private void initViewPagerSecond() {
		View inflate = views.get(1);

		officGridView = (GridView) inflate
				.findViewById(R.id.gridView_viewPager);
		progress2 = inflate.findViewById(R.id.progressLayout);
		AdapterOfGridView_operationComputer adapterOfGridViewSecond = new AdapterOfGridView_operationComputer(
				this);
		adapterOfGridViewSecond.setUpdataListener(this);
		officGridView.setAdapter(adapterOfGridViewSecond);
		if (!App.isOfficeScaning) {
			progress2.setVisibility(View.GONE);
			adapterOfGridViewSecond.setFiles(App.sysOffice);
			adapterOfGridViewSecond.notifyDataSetChanged();
		} else {
			new Thread() {
				public void run() {

					while (App.isOfficeScaning)
						;
					handler.sendEmptyMessage(1);

				};
			}.start();

		}

	}

	GridView videoGridView;
	View progress3;

	private void initViewPagerThirst() {
		View inflate = views.get(3); //视频放到第四位

		videoGridView = (GridView) inflate
				.findViewById(R.id.gridView_viewPager);
		progress3 = inflate.findViewById(R.id.progressLayout);
		AdapterOfGridView_operationComputer adapterOfGridViewThirst = new AdapterOfGridView_operationComputer(
				this);
		adapterOfGridViewThirst.setUpdataListener(this);
		videoGridView.setAdapter(adapterOfGridViewThirst);
		if (!App.isVideoScaning) {
			progress3.setVisibility(View.GONE);
			adapterOfGridViewThirst.setFiles(App.sysVideo);
			adapterOfGridViewThirst.notifyDataSetChanged();
		} else {
			new Thread() {
				public void run() {

					while (App.isVideoScaning)
						;
					handler.sendEmptyMessage(2);

				};
			}.start();

		}

	}
	GridView pictureGridView;
	View progress4;

	private void initViewPagerFouth() {
		View inflate = views.get(2); //图片放到第三位

		pictureGridView = (GridView) inflate
				.findViewById(R.id.gridView_viewPager);
		progress4 = inflate.findViewById(R.id.progressLayout);
		AdapterOfGridView_operationComputer adapterOfGridViewFouth = new AdapterOfGridView_operationComputer(
				this);
		adapterOfGridViewFouth.setUpdataListener(this);
		pictureGridView.setAdapter(adapterOfGridViewFouth);
		ScanSystemFile scan = new ScanSystemFile();
		scan.initImage(); //有鎖的,這樣就不用去判斷是否已經結束獲取了
	
		ArrayList<FileInfo> imageList = new ArrayList<FileInfo>(200);
		// 遍历hashmpa
		Set<String> keySet = App.folders.keySet();
		for(String key : keySet){
			
			List<FileInfo> list = App.folders.get(key);
			imageList.addAll(list);
		}
		adapterOfGridViewFouth.setFiles(imageList);
		// 去掉progress
		progress4.setVisibility(View.GONE);
		adapterOfGridViewFouth.notifyDataSetChanged();
		
//		if (!App.isVideoScaning) {
//			progress3.setVisibility(View.GONE);
//			adapterOfGridViewFouth.setFiles(App.sysVideo);
//			adapterOfGridViewFouth.notifyDataSetChanged();
//		} else {
//			new Thread() {
//				public void run() {
//
//					while (App.isVideoScaning)
//						;
//					handler.sendEmptyMessage(3);
//
//				};
//			}.start();
//
//		}
	}

	public void getlocalFile() {
		// // 为了方便集成过来的，这里不需要用到这个参数
		// File file = new File(Constant.ROOT);
		// File[] listFiles = file.listFiles();
		// List<FileDetail> list = new ArrayList<FileDetail>();
		//
		// for(File f : listFiles){
		// String name = f.getName();
		// String[] array = name.split(".");
		// String type ;
		// if(array.length > 1){
		// type = array[1];//后缀
		// }else{
		// type = "directory";
		// }
		// FileDetail fd = new FileDetail(f.getName(),type ,
		// f.getAbsolutePath());
		// Log.e("文件类型", type + "________________________" );
		// list.add(fd);
		// }
		// adapterOfGridView.setFiles(list);
		// adapterOfGridView.notifyDataSetChanged();

	}

	@Override
	public void updata(int count) {
		if (count == 0) {
			finish.setText("完成");
		} else {
			finish.setText("完成(" + count + ")");
		}
		finish.setEnabled(true);// 设置可点击
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		
		switch (id) {
		case R.id.selected_file_finish:
			
//			new AlertDialog.Builder(SelectedFileActivity.this)
//			   .setTitle("选择连接方式")
//			   .setItems(new String[]{"声波连接", "蓝牙链接"},
//					    new DialogInterface.OnClickListener() {
//				   			Intent intent = null;
//						@Override
//						public void onClick(DialogInterface arg0, int which) {
//								if(which == 0){
//									intent = new Intent(SelectedFileActivity.this,SendVoiceActivity.class);
//								}else if(which == 1){
//									intent = new Intent(SelectedFileActivity.this,BluetoothActivity.class);
//								}
//								startActivity(intent);
//							}
//						} 
//			   
//					   )
//				.show();
			
			Intent intent = new Intent(SelectedFileActivity.this,SendVoiceActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	@Override
	public void onsendFilesToPC() {
		// 这里不需要实现这个接口
	}
}
