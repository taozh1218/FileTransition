package com.taozhang.filetransition.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.bean.FileDetail;

public class AdapterOfFileDetail extends BaseAdapter {

	private Context context;
	private List<FileDetail> list;

	public List<FileDetail> getList() {
		return list;
	}

	public void setList(List<FileDetail> list) {
		this.list = list;
	}

	/**
	 * Get view from xml layout.
	 */
	private LayoutInflater mInflater = null;

	public AdapterOfFileDetail(Context context) {
		this.context = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list == null ? null : list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listview_item_filedetail,
					null);
			holder.rl = (RelativeLayout) convertView
					.findViewById(R.id.rl_listItem_fileDetail);
			holder.img = (ImageView) convertView
					.findViewById(R.id.img_listItem_fileAdapter);
			holder.name = (TextView) convertView
					.findViewById(R.id.tv_listItem_fileAdapter_fileName);
			holder.path = (TextView) convertView
					.findViewById(R.id.tv_listItem_fileAdapter_filePath);
			holder.type = (TextView) convertView
					.findViewById(R.id.tv_listItem_fileAdapter_filetype);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FileDetail bean = (FileDetail) getItem(position);
		if (bean != null) {
			String type = bean.getType();
			if (type.equals("apk")) {
				holder.img.setImageBitmap(getBitmap(R.drawable.app_blue));
			} else if (type.equals("mp3")) {
				holder.img.setImageBitmap(getBitmap(R.drawable.music_red));
			} else if (type.equals("png") || type.equals("jpg")) {
				holder.img.setImageBitmap(getBitmap(R.drawable.pic_grey));
			} else if (type.equals("mp4")) {
				holder.img.setImageBitmap(getBitmap(R.drawable.audio_red));
			} else if (type.equals("docx") || type.equals("xlsx")
					|| type.equals("pptx") || type.equals("accdb")) {
				holder.img.setImageBitmap(getBitmap(R.drawable.office_red));
			} else {
				holder.img.setImageBitmap(getBitmap(R.drawable.file_blue_32));
			}
			holder.name.setText(bean.getName());
			holder.type.setText(bean.getType());
			holder.path.setText(bean.getPath());
			final String path = holder.path.getText().toString();
			// holder.rl.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// //TODO 测试
			// File file = new File(path);
			// OpenDocumentUtil.openFile(file,context);
			// }
			// });
		}
		return convertView;
	}

	/**
	 * viewHolder只是用来临时保存数据的一个容器
	 * 
	 * @author taozhang
	 * 
	 */
	static class ViewHolder {
		RelativeLayout rl;
		ImageView img;
		TextView name;
		TextView path;
		TextView type;
	}

	/**
	 * 根据resource id获取Bitmap
	 * 
	 * @param id
	 * @return
	 */
	public Bitmap getBitmap(int id) {
		Bitmap bitmap = BitmapFactory
				.decodeResource(context.getResources(), id);
		return bitmap;
	}

}
