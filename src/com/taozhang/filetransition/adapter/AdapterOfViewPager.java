package com.taozhang.filetransition.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Description: Adapter of ViewPager
 * Created by taozhang on 2016/5/4.
 * Company:Geowind,University of South China.
 * ContactQQ:962076337
 *
 * @updateAuthor taozhang
 * @updateDate 2016/5/4
 */
public class AdapterOfViewPager extends PagerAdapter {

    private List<View> mViews;
    private String[] mTab_titles;
    private Context mContext;

    public AdapterOfViewPager() {
        super();
    }

    public AdapterOfViewPager(List<View> views, String[] tab_titles, Context context) {
        mViews = views;
        mTab_titles = tab_titles;
        mContext = context;
    }

    public List<View> getViews() {
        return mViews;
    }

    public void setViews(List<View> views) {
        mViews = views;
    }

    public String[] getTab_titles() {
        return mTab_titles;
    }

    public void setTab_titles(String[] tab_titles) {
        mTab_titles = tab_titles;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mViews == null ? 0 : mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    /**
     * 滑动切换的时候销毁当前的组件
     */
    public void destroyItem(View container, int position, Object object) {
        if (container !=null){
            ((ViewPager) container).removeView(mViews.get(position));
        }
    };

    /**
     * 每次滑动的时候生成的组件
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(mViews.get(position));
        return mViews.get(position);
    }

    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTab_titles == null? null :mTab_titles[position];
    }

}
