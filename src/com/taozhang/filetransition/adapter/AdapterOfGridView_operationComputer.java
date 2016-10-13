package com.taozhang.filetransition.adapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.impl.FileSelecedImpl;
import com.taozhang.filetransition.util.ScanSystemFile;

public class AdapterOfGridView_operationComputer extends BaseAdapter implements OnCheckedChangeListener {

	 private List<FileInfo> files;
	    private Context mContext;
	    private FileInfo file;
	    
	    public static ArrayList<FileInfo> fileInfos = new ArrayList<FileInfo>();
	    // 文件选择的回调
	    private FileSelecedImpl callBack;
	    /**
	     * 储存着被选中的文件,因为要共用，所有用静态，完美
	     */
	    public static ArrayList<FileInfo> checkedFiles = new ArrayList<FileInfo>();
	    /**
	     * Get view from xml layout.
	     */
	    private LayoutInflater mInflater = null;


	    public AdapterOfGridView_operationComputer(Activity context) {
	        mContext = context;
	        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public AdapterOfGridView_operationComputer(List<FileInfo> files, Activity context) {
	        this.files = files;
	        mContext = context;
	        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public void setUpdataListener(FileSelecedImpl listener){
	    	this.callBack = listener;
	    }
	    /**
	     * 获取被选中的文件列表对象
	     * @return
	     */
	    public static ArrayList<FileInfo> getCheckedFiles(){
	    	return checkedFiles;
	    }

	    public static void cleanCheckFile(FileInfo targetFile){
	    	checkedFiles.remove(targetFile);
	    }
	    public static int getCheckedFilesSize(){
	    	return checkedFiles.size();
	    }
	    public List<FileInfo> getFiles() {
	        return files;
	    }

	    public void setFiles(List<FileInfo> files) {
	        this.files = files;
	    }

	    public FileInfo getFile() {
	        return file;
	    }

	    public void setFile(FileInfo file) {
	        this.file = file;
	    }

	    public Context getContext() {
	        return mContext;
	    }

	    public void setContext(Context context) {
	        mContext = context;
	    }


	    @Override
	    public int getCount() {
	        return files == null ? 0 : files.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return files == null ? null : files.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        final ViewHolder holder;
	        if (convertView == null) {
	            holder = new ViewHolder();
	            //映射出convertView
	            convertView = mInflater.inflate(R.layout.whq_gridviewitem_filelist, parent, false);
	            holder.img = (ImageView) convertView.findViewById(R.id.whq_img_gridViewItem_fileList);
	            holder.tv = (TextView) convertView.findViewById(R.id.whq_tv_gridViewItem_fileList);
	            holder.checkBox = (CheckBox) convertView.findViewById(R.id.whq_checkbox_gridViewItem_fileList);
	            if(callBack == null){
	            	// 说明不需要回调,所以这个需求是不需要这个checkbox的
	            	holder.checkBox.setVisibility(View.GONE);
	            }
	            // set listner
	            holder.checkBox.setOnCheckedChangeListener(AdapterOfGridView_operationComputer.this);
	            // 由于背景是白色的，为了避免看不清，设置图片的一下滤镜,有十六种模式，具体可以去百度
	            holder.img.getDrawable().setColorFilter(Color.argb(0xff, 0xff, 0xff, 0xff),PorterDuff.Mode.SRC_ATOP);
	            //         holder.tv_filePath = (TextView) convertView.findViewById(R.id.tv_gridViewItem_fileList_filePath);
	            //         holder.tv_fileType = (TextView) convertView.findViewById(R.id.tv_gridViewItem_fileList_fileType);
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder) convertView.getTag();
	        }
	        FileInfo file = (FileInfo) getItem(position);
	        if (file == null) {//判断获取到的是否为空
	            Toast.makeText(mContext, "该路径下无文件", Toast.LENGTH_SHORT).show();
	            return convertView;
	        }
	        String name = file.fileName;
	        Bitmap bm  = file.thumbnail;
	        String thURI = file.thURI;
	        String type = file.fileType;
	        Resources resources = mContext.getResources();
	        if(thURI != null){
	        	try {
					bm = MediaStore.Images.Media.getBitmap(App.context.getContentResolver(),Uri.parse(thURI) );
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }else if(bm == null){//如果空再去判断，否则就是表示已经从数据库拿到缩略图了
	        	if(null == type){
	        		bm = BitmapFactory.decodeResource(resources,R.drawable.txt);
	        	}else if(type.contains("mpeg")){//mp3
//	        	Toast.makeText(mContext, type, Toast.LENGTH_LONG).show();
	 	    	   bm = BitmapFactory.decodeResource(resources, R.drawable.music_red);
	 	    	   int  index = name.indexOf(".");
	 	    	   name = name.substring(0,index);
	 	       }else if(type.equals(ScanSystemFile.PDF) || type.equals(ScanSystemFile.WORD)){
	 	    	   bm = BitmapFactory.decodeResource(resources, R.drawable.txt);
	 	       }else {
	 	    	  bm = BitmapFactory.decodeResource(resources,R.drawable.txt);
	 	       }
	        }
	        holder.img.setImageBitmap(bm);
	        holder.tv.setText(name);
	      // 把每个file文件存到相应的checkbox里面，这样就可以很方便的拿到选中了文件了
	        holder.checkBox.setTag(file);
	        //根据队列，显示是否被选中
            holder.checkBox.setChecked(checkedFiles.contains(file));
	        return convertView;
	    }

	    public static class ViewHolder {
	        public ImageView img;
	        public TextView tv;
	        public CheckBox checkBox;
	        //        public TextView tv_filePath;
	        //        public TextView tv_fileType;
	    }

		@Override
		public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
			FileInfo checkedFile = (FileInfo) checkBox.getTag();
			if(isChecked){
				if(!checkedFiles.contains(checkedFile)){
					// 如果被选中了，把相应的文件添加到列表中去
					checkedFiles.add(checkedFile);
				}
			}else{
				if(checkedFiles.contains(checkedFile)){
					// 删除
					checkedFiles.remove(checkedFile);
				}
			}
			
			// 更新完成按钮的数据
			if(callBack != null){
				callBack.updata(checkedFiles.size());
			}
		}
}
