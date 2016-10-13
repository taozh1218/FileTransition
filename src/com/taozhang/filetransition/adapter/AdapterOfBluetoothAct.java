package com.taozhang.filetransition.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.bean.BluetoothBean;

public class AdapterOfBluetoothAct extends BaseAdapter {

	private Context context;

	private List<BluetoothBean> list;

	public List<BluetoothBean> getList() {
		return list;
	}

	public void setList(List<BluetoothBean> list) {
		this.list = list;
	}

	/**
	 * Get view from xml layout.
	 */
	private LayoutInflater mInflater = null;

	public AdapterOfBluetoothAct(Context context) {
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
			convertView = mInflater.inflate(R.layout.listitem_bluetooth,
					null);
			holder.img = (ImageView) convertView
					.findViewById(R.id.img_listItem_bluetooth);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_listItem_bluetooth_name);
			holder.tv_address = (TextView) convertView.findViewById(R.id.tv_listItem_bluetooth_address);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		BluetoothBean bluetoothBean =(BluetoothBean) getItem(position);
		if (bluetoothBean!=null) {
			holder.tv_name.setText("设备名："+bluetoothBean.getName());
			holder.tv_address.setText("设备地址："+bluetoothBean.getAddress());
		}
		return convertView;
	}
	
	static class ViewHolder {
		ImageView img;
		TextView tv_name;
		TextView tv_address;
	}

}
