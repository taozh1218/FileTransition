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
import android.widget.TextView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.bean.FileInfo;

/**
 * 文件管理器的Adapter
 * 
 * @author taozhang
 * 
 */
public class AdapterOfListViewFileManager extends BaseAdapter {
	private Context context;
	private List<FileInfo> list;

	public List<FileInfo> getList() {
		return list;
	}

	public void setList(List<FileInfo> list) {
		this.list = list;
	}

	/**
	 * Get view from xml layout.
	 */
	private LayoutInflater mInflater = null;

	public AdapterOfListViewFileManager(Context context) {
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
			convertView = mInflater.inflate(R.layout.list_item_fileview_act,
					null);
			holder.img = (ImageView) convertView.findViewById(R.id.img_filePic);
			holder.name = (TextView) convertView.findViewById(R.id.tv_fileName);
			holder.path = (TextView) convertView.findViewById(R.id.tv_filePath);
			holder.type = (TextView) convertView.findViewById(R.id.tv_fileType);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FileInfo bean = (FileInfo) getItem(position);
		if (bean != null) {
			if (bean.IsDir == false) {// 如果是文件
				String type = bean.fileType;
				holder.type.setText(bean.fileType);
				if (type.equals("apk")) {// apk
					holder.img.setImageBitmap(getBitmap(R.drawable.app_blue));
				} else if (type.equals("mp3")) {// 音乐
					holder.img.setImageBitmap(getBitmap(R.drawable.music_red));
				} else if (type.equals("png") || type.equals("jpg")
						|| type.equals("jpeg") || type.equals("gif")
						|| type.equals("bmp") || type.equals("wbmp")) {// 图片
					holder.img.setImageBitmap(getBitmap(R.drawable.pic_blue));
				} else if (type.equals("mp4") || type.equals("wmv")
						|| type.equals("mpeg") || type.equals("3gp")
						|| type.equals("3gpp") || type.equals("asf")) {// 视频
					holder.img.setImageBitmap(getBitmap(R.drawable.audio_red));
				} else if (type.equals("docx") || type.equals("xlsx")
						|| type.equals("pptx") || type.equals("accdb")
						|| type.equals("doc") || type.equals("xls")
						|| type.equals("ppt")) {// office
					holder.img.setImageBitmap(getBitmap(R.drawable.office_red));
				} else {// else
					holder.img
							.setImageBitmap(getBitmap(R.drawable.doc_blue_32));
				}
			} else {// 是文件夹
				holder.img.setImageResource(R.drawable.folder_yellow_32);
			}
			holder.name.setText(bean.fileName);
			holder.path.setText(bean.filePath);
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
