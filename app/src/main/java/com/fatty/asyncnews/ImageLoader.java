package com.fatty.asyncnews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 17255 on 2016/7/20.
 */
public class ImageLoader {

    private String mUrl;
    private ImageView mImageView;
    private LruCache<String, Bitmap> mCache;

    //构造方法，用于实例化LruCache
    public ImageLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            //在每次存入缓存时调用，告诉系统我们传入的图片有多大
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    //把bitmap对象加到cache中
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mCache.put(url, bitmap);
        }
    }

    //从cache中获取bitmap对象
    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

    public Bitmap getBitmapFromUrl(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //使用异步任务AsyncTask读取图片，在postExecute方法中在UI线程更新ImageView
    public void showImageByAsyncTask(ImageView imageView, String url) {
        //从缓存中取出图片
        Bitmap bitmap = getBitmapFromCache(url);
        //如果缓存中没有图片，就异步下载图片
        if (bitmap == null) {
            new NewsAsyncTask(imageView, url).execute(url);
        } else {
            if (imageView.getTag().equals(url)) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;

        public NewsAsyncTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            //从网络上下载图片
            Bitmap bitmap = getBitmapFromUrl(strings[0]);
            //如果图片下载到了
            if (bitmap != null) {
                //把图片添加到缓存中
                addBitmapToCache(strings[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //标记imageView，使其展示的图片与url对应
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }



//    //使用多线程handler在UI线程中更新ImageView
//    private android.os.Handler mHandler = new android.os.Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            //标记imageView，使其展示的图片与url对应
//            if (mImageView.getTag().equals(mUrl)) {
//                mImageView.setImageBitmap((Bitmap) msg.obj);
//            }
//        }
//    };

//    //在非UI线程中读取图片，并使用message传递到UI线程中
//    public void showImageByThread(ImageView imageView, final String urlString) {
//        mImageView = imageView;
//        mUrl = urlString;
//
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                Bitmap bitmap = getBitmapFromUrl(urlString);
//                Message mMessage = Message.obtain();
//                mMessage.obj = bitmap;
//                mHandler.sendMessage(mMessage);
//            }
//        }.start();
//    }


}
