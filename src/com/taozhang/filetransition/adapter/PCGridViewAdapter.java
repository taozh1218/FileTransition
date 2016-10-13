package com.taozhang.filetransition.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.bean.FileDetail;

public class PCGridViewAdapter extends BaseAdapter{

	private ArrayList<FileDetail> data = new ArrayList<FileDetail>();
	private Context context;
	private LayoutInflater layoutInflater;
	
	public PCGridViewAdapter(Context context) {
		super();
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}
	
	public void setData( ArrayList<FileDetail> data){
		this.data.addAll(data);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		
		Holder holder = null;
		if(contentView == null){
			holder = new Holder();
			contentView = layoutInflater.inflate(R.layout.computer_gridview_item, null);
			holder.image = (ImageView) contentView.findViewById(R.id.computer_gridview_image);
			holder.name = (TextView) contentView.findViewById(R.id.computer_gridview_text);
			contentView.setTag(holder);
			
		}else{
			holder = (Holder) contentView.getTag();
		}
		FileDetail file = (FileDetail) getItem(position);
		
//		holder.image.setImageBitmap(bm)
		holder.name.setText(file.getName());
		return contentView;
	}

	class Holder{
		ImageView image;
		TextView name;
	}
	
}
