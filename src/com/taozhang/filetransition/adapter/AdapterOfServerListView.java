package com.taozhang.filetransition.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.bean.ServiceBean;

/**
 * Description:
 * Created by taozhang on 2016/5/9.
 * Company:Geowind,University of South China.
 * ContactQQ:962076337
 *
 * @updateAuthor taozhang
 * @updateDate 2016/5/9
 */
public class AdapterOfServerListView extends BaseAdapter {

    private ArrayList<ServiceBean> hosts;
    private Context context;

    public AdapterOfServerListView(ArrayList<ServiceBean> hosts, Context context) {
        this.hosts = hosts;
        this.context = context;
    }

    public void addData(ArrayList<ServiceBean> hosts) {
        this.hosts.addAll(hosts);
    }

    public void cleanData(){
        hosts.clear();
    }

    public ArrayList<ServiceBean> getHosts() {
        return hosts;
    }

    @Override
    public int getCount() {
        return hosts == null ? 0 : hosts.size();
    }

    @Override
    public Object getItem(int position) {
        return hosts == null ? null : hosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_hosts, null, false);
            holder.textView = (TextView) convertView.findViewById(R.id.hostName);
            holder.ip = (TextView) convertView.findViewById(R.id.address);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        ServiceBean serviceBean = hosts.get(position);
        holder.textView.setText(serviceBean.name);
        holder.ip.setText(serviceBean.address.getAddress().toString());
        return convertView;
    }

    class Holder {
        TextView textView;
        TextView ip;
    }
}
