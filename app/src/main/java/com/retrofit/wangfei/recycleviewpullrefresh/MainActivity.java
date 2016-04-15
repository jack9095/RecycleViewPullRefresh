package com.retrofit.wangfei.recycleviewpullrefresh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.retrofit.wangfei.recycleviewpullrefresh.adapter.NewsListAdapter;
import com.retrofit.wangfei.recycleviewpullrefresh.util.Constance;
import com.retrofit.wangfei.recycleviewpullrefresh.view.ProgressLayout;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Android Studio
 * User: fei.wang
 * Date: 2016-04-14
 * Time: 9:57
 * QQ: 929728742
 * Description: RecycleView的上拉加载和下拉刷新
 */
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;  //下拉刷新控件
    @Bind(R.id.progress_layout)
    ProgressLayout progressLayout;          //进度条布局（通用，可现实错误按钮，点击重试）
    @Bind(R.id.progress_loading_main)
    ProgressView progress_loading_main;    // 加载数据是显示的进度圆圈
    private LinearLayoutManager mRecycleViewLayoutManager;

    private int mPageNum = 1;
    private NewsListAdapter mAdapter;
    private List<String> mNews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initRecyclerView();
        swipeRefreshLayout.setColorSchemeResources(Constance.colors);//设置下拉刷新控件变换的四个颜色
        mAdapter = new NewsListAdapter(this, mNews);
        recyclerview.setAdapter(mAdapter);
        recyclerViewOnItemClickListener();
        refresh();
        loadMore(mAdapter);
        progress_loading_main.setVisibility(View.VISIBLE);
        initData();

    }

    /**进入页面的初始化数据*/
    private void initData(){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                netNewsList(true);
                progress_loading_main.setVisibility(View.GONE);
            }
        }, 2000);
    }

    /**RecyclerView每个item的点击事件*/
    private void recyclerViewOnItemClickListener() {
        mAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Snackbar.make(view, "fly", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        //设置分割线
//        recyclerview.addItemDecoration(new ListItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);
        mRecycleViewLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mRecycleViewLayoutManager);  // 设置RecycleView，显示是ListView还是gridView还是瀑布流
    }

    /**下拉刷新*/
    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        netNewsList(true);
                        swipeRefreshLayout.setRefreshing(false); // 停止刷新
                    }
                }, 2000);
            }
        });
    }

    /**
     * 设置上拉加载更多
     *
     * @param adapter RecyclerView适配器
     */
    public void loadMore(final NewsListAdapter adapter) {
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int lastVisibleItem;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mRecycleViewLayoutManager.findLastVisibleItemPosition();  // 滑动到最后一个
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //  效果在暂停时显示, 否则会导致重绘异常
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == adapter.getItemCount()
                        && adapter.isShowFooter()) {

                    if (mNews != null && mNews.size() >= 10) {  // 真实开发中要设置mNews.size()大于加载分页显示的个数
                        adapter.loadLayout.setVisibility(View.VISIBLE);
                        //加载更多
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                netNewsList(false);
                            }
                        }, 2000);
                    }
                }
            }
        });
    }

    /**
     * 从网络加载数据列表
     *
     * @param isRefresh 是否刷新  true 为刷新，false为不刷新
     */
    private void netNewsList(boolean isRefresh) {
//        viewDelegate.showLoading();
        if (isRefresh) {
            mPageNum = 1;
        } else {
            mPageNum++;
        }

        if (isRefresh) {
            if (!mNews.isEmpty()) {
                mNews.clear();
            }
        }

//        progressLayout.showLoading();  // 网络请求数据的时候，开始加载数据
//
//        if (!progressLayout.isContent()) {  // 网络请求数据成功的时候
//            progressLayout.showContent();
//        }
//
//        if (!progressLayout.isError()) {    // 网络请求数据失败的时候
//            progressLayout.showError(R.string.load_error, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Snackbar.make(v, "网络请求数据失败", Snackbar.LENGTH_SHORT).show();
//                }
//            });
//        }

        // TODO 这里把页数mPageNum上传到服务端
        lists.clear();
        mNews.addAll(getData());
        mAdapter.notifyDataSetChanged();
    }

    private List<String> lists = new ArrayList<>();
    private List<String> getData() {
        for (int i = 0; i < 10; i++) {
            lists.add(i + "");
        }
        return lists;
    }
}
