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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Android Studio
 * User: wangfei
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
    private LinearLayoutManager mRecycleViewLayoutManager;

    private int mPageNum = 1;
    private NewsListAdapter mAdapter;
    //新闻数据列表
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
        mAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Snackbar.make(view, "fly", Snackbar.LENGTH_SHORT).show();
            }
        });

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
        loadMore(mAdapter);
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
        recyclerview.setLayoutManager(mRecycleViewLayoutManager);
    }

    /**
     * 设置加载更多接口
     *
     * @param adapter 加载更多的回调
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

                    if (mNews != null && mNews.size() > 0) {
                        adapter.loadLayout.setVisibility(View.VISIBLE);
                    }
                    //加载更多
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            netNewsList(false);
                        }
                    }, 2000);
                }
            }
        });
    }

    /**
     * 从网络加载数据列表
     *
     * @param isRefresh 是否刷新
     */
    private void netNewsList(final boolean isRefresh) {
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
        mNews.addAll(initData());
        mAdapter.notifyDataSetChanged();
    }

    private List<String> initData() {
        for (int i = 0; i < 5; i++) {
            mNews.add(i + "");
        }
        return mNews;
    }
}
