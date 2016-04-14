package com.retrofit.wangfei.recycleviewpullrefresh.server;

import android.support.annotation.NonNull;

import com.retrofit.wangfei.recycleviewpullrefresh.MyApplication;
import com.retrofit.wangfei.recycleviewpullrefresh.server.api.ShowApi;
import com.retrofit.wangfei.recycleviewpullrefresh.util.NetUtil;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Android Studio
 * User: wangfei
 * Date: 2016-04-14
 * Time: 9:57
 * Email: 929728742@qq.com
 * Description: 网络请求引擎类
 */
public class RetrofitService {

    //设缓存有效期为两天
    protected static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    protected static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    protected static final String CACHE_CONTROL_NETWORK = "max-age=0";

    private static OkHttpClient mOkHttpClient;

    private RetrofitService() {
    }

    private volatile static RetrofitService instance = null;

    public static RetrofitService getInstance() {
        if (instance == null) {
            synchronized (RetrofitService.class) {
                if (instance == null) {
                    instance = new RetrofitService();
                }
            }
        }
        return instance;
    }

    private volatile static ShowApi showApi = null;

    public static ShowApi createShowApi() {
        if (showApi == null) {
            synchronized (RetrofitService.class) {
                if (showApi == null) {
                    initOkHttpClient();
                    showApi = new Retrofit.Builder()
                            .client(mOkHttpClient)
                            .baseUrl(NewInterface.API)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build().create(ShowApi.class);
                }
            }
        }
        return showApi;
    }

    // 配置OkHttpClient
    private static void initOkHttpClient() {
        if (mOkHttpClient == null) {
            // 因为BaseUrl不同所以这里Retrofit不为静态，但是OkHttpClient配置是一样的,静态创建一次即可
            File cacheFile = new File(MyApplication.getContext().getCacheDir(),
                    "HttpCache"); // 指定缓存路径
            Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); // 指定缓存大小100Mb
            // 云端响应头拦截器，用来配置缓存策略
            Interceptor rewriteCacheControlInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    if (!NetUtil.isConnected(MyApplication.getContext())) {
                        request = request.newBuilder()
                                .cacheControl(CacheControl.FORCE_CACHE).build();
//                        Logger.e("no network");
                    }
                    Response originalResponse = chain.proceed(request);
                    if (NetUtil.isConnected(MyApplication.getContext())) {
                        //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                        String cacheControl = request.cacheControl().toString();
                        return originalResponse.newBuilder()
                                .header("Cache-Control", cacheControl)
                                .removeHeader("Pragma").build();
                    } else {
                        return originalResponse.newBuilder().header("Cache-Control",
                                "public, only-if-cached," + CACHE_STALE_SEC)
                                .removeHeader("Pragma").build();
                    }
                }
            };

            mOkHttpClient = new OkHttpClient.Builder().cache(cache)
                    .addNetworkInterceptor(rewriteCacheControlInterceptor)
                    .addInterceptor(rewriteCacheControlInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS).build();
        }
    }
    /**
     * 根据网络状况获取缓存的策略
     *
     * @return
     */
    @NonNull
    public static String getCacheControl() {
        return NetUtil.isConnected(MyApplication.getContext()) ? CACHE_CONTROL_NETWORK : CACHE_CONTROL_CACHE;
    }
}
