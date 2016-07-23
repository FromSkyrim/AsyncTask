package com.fatty.asyncnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 17255 on 2016/7/19.
 */
public class NewsAdapter extends BaseAdapter {

    private List<NewsBean> mList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    //存储所有URL的String数组
    public static String[] URLS;


    //用于ListView的Adapter
    public NewsAdapter(Context context, List<NewsBean> data) {
        mList = data;
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader();
        //把URL都获取到
        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            URLS[i] = data.get(i).newsIconUrl;
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //BaseAdapter中最重要的getView方法
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_layout, null);
            viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        NewsBean newsBean = mList.get(i);
        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);


        viewHolder.ivIcon.setTag(newsBean.newsIconUrl);
        //采用多线程的方法加载图片
        //new ImageLoader().showImageByThread(viewHolder.ivIcon, newsBean.newsIconUrl);

        //采用AsyncTask的方法加载图片
        mImageLoader.showImageByAsyncTask(viewHolder.ivIcon, newsBean.newsIconUrl);

        viewHolder.tvTitle.setText(newsBean.newsTitle);
        viewHolder.tvContent.setText(newsBean.newsContent);

        return view;
    }

    //使用ViewHolder缓存view，使其不用重复的findviewbyid
    class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle, tvContent;
    }
}
