package com.taozhang.filetransition.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.bean.FileBean;

/**
 * Description: the adapter of file in GridView
 * Created by taozhang on 2016/5/4.
 * Company:Geowind,University of South China.
 * ContactQQ:962076337
 *
 * @updateAuthor taozhang
 * @updateDate 2016/5/4
 */
public class AdapterOfGridView extends BaseAdapter {

    private List<FileBean> files;
    private Context mContext;
    private FileBean file;
    /**
     * Get view from xml layout.
     */
    private LayoutInflater mInflater = null;


    public AdapterOfGridView(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public AdapterOfGridView(List<FileBean> files, Context context) {
        this.files = files;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public List<FileBean> getFiles() {
        return files;
    }

    public void setFiles(List<FileBean> files) {
        this.files = files;
    }

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
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
            convertView = mInflater.inflate(R.layout.gridviewitem_filelist, parent, false);
            holder.img = (ImageView) convertView.findViewById(R.id.img_gridViewItem_fileList);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_gridViewItem_fileList);
            //            holder.tv_filePath = (TextView) convertView.findViewById(R.id.tv_gridViewItem_fileList_filePath);
            //            holder.tv_fileType = (TextView) convertView.findViewById(R.id.tv_gridViewItem_fileList_fileType);
                convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileBean file = (FileBean) getItem(position);
        if (file == null) {//判断获取到的是否为空
            Toast.makeText(mContext, "该路径下无文件", Toast.LENGTH_SHORT).show();
            return convertView;
        }
        holder.img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //        imageView.setImageResource(file.getIconId());
        holder.img.setImageResource(file.getId());
        holder.tv.setText(file.getName());
        //        holder.tv_fileType.setText(file.getType());
        //        holder.tv_filePath.setText(file.getPath());

        return convertView;
    }

    public static class ViewHolder {
        public ImageView img;
        public TextView tv;
        //        public TextView tv_filePath;
        //        public TextView tv_fileType;
    }
}
