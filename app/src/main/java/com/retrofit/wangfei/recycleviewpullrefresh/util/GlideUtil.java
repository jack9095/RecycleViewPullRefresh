package com.retrofit.wangfei.recycleviewpullrefresh.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.retrofit.wangfei.recycleviewpullrefresh.R;

/**
 *     glide图片加载工具类
 */
public class GlideUtil {

    public static void loadImage(Context context, String url, ImageView imageView){
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imageView);
    }
}
