package com.retrofit.wangfei.recycleviewpullrefresh.server;

/**
 * Created by Android Studio
 * User: wangfei
 * Date: 2016-04-14
 * Time: 9:57
 * Email: 929728742@qq.com
 * Description: 新闻易源API通用响应数据
 */
public class ShowApiResponse<T> {
    public String showapi_res_code;
    public String showapi_res_error;
    public T showapi_res_body;
}
