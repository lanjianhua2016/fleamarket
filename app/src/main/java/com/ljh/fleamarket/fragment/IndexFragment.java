package com.ljh.fleamarket.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.index.SearchActivity;
import com.ljh.fleamarket.adapter.MyGoodsRecyclerAdapter;
import com.ljh.fleamarket.bo.Goods;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.bo.ResponseBuy;
import com.ljh.fleamarket.bo.SearchBO;
import com.ljh.fleamarket.utils.RequestUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;
import static android.content.Context.MODE_PRIVATE;

public class IndexFragment extends  Fragment {
    private View mView;
    private ViewPager mViewPaper;
    private List<ImageView> images;
    private List<View> dots;
    private int currentItem;
    //记录上一次点的位置
    private int oldPosition = 0;

    private int[] imageIds = new int[]{
            R.drawable.lunbo01,
            R.drawable.lunbo02,
            R.drawable.lunbo03,
            R.drawable.lunbo01,
            R.drawable.lunbo02,
    };

    private String[] titles = new String[]{
            "壹",
            "贰",
            "叁",
            "肆",
            "伍"
    };

    private TextView title;
    private ViewPagerAdapter adapter;
    private ScheduledExecutorService scheduledExecutorService;

    private RefreshLayout mRefreshLayout;
    private MyGoodsRecyclerAdapter recyclerAdapter;
    private List<Goods> resultGoodsList;

    private String userToken;
    private SharedPreferences sharedPreferences;

    //private PullToRefreshAndPushToLoadView pullToRefreshAndPushToLoadView;
    private RecyclerView recyclerView;

    private int pageNumber=1;
    private int pageSize = 7;
    private boolean refreshFlag;

    private View netFailedView;
    private ProgressDialog pd;

    // 只用来指定此fragment的布局
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_index, container, false);
        setView();
        return mView;

    }


    //视图控件请在这里实例化
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button searchbutton = (Button) getActivity().findViewById(R.id.search);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        DisplayGoods();
        mRefreshLayout = getActivity().findViewById(R.id.smartrefresh_layout_index);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycleview_index);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                RefreshGoods();
                mRefreshLayout.finishRefresh(true);
            }
        });

        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                LoadingMoreGoods();
                mRefreshLayout.finishLoadMore(true);
            }
        });

        netFailedView = getActivity().findViewById(R.id.net_failed_layout);
    }

    private void setView() {
        mViewPaper = (ViewPager) mView.findViewById(R.id.vp);

        //显示的图片
        images = new ArrayList<ImageView>();
        for (int i = 0; i < imageIds.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(imageIds[i]);
            images.add(imageView);
        }

        //显示的小点
        dots = new ArrayList<View>();
        dots.add(mView.findViewById(R.id.dot_0));
        dots.add(mView.findViewById(R.id.dot_1));
        dots.add(mView.findViewById(R.id.dot_2));
        dots.add(mView.findViewById(R.id.dot_3));
        dots.add(mView.findViewById(R.id.dot_4));

        title = (TextView) mView.findViewById(R.id.title);
        title.setText(titles[0]);

        adapter = new ViewPagerAdapter();
        mViewPaper.setAdapter(adapter);

        mViewPaper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                title.setText(titles[position]);
                dots.get(position).setBackgroundResource(R.drawable.dot_yes);
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_no);

                oldPosition = position;
                currentItem = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /*定义的适配器*/
    public class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(images.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(images.get(position));
            return images.get(position);
        }


    }

    /**
     * 利用线程池定时执行动画轮播
     */
    @Override
    public void onStart() {
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPageTask(),
                2,
                2,
                TimeUnit.SECONDS);
    }

    /**
     * 图片轮播任务
     */
    private class ViewPageTask implements Runnable{
        @Override
        public void run() {
            currentItem = (currentItem + 1) % imageIds.length;
            mHandler.sendEmptyMessage(0);
        }

    }
    /**
     * 接收子线程传递过来的数据
     */
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mViewPaper.setCurrentItem(currentItem);
        };

    };

    @Override
    public void onStop() {
        super.onStop();
        if(scheduledExecutorService != null){
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }

    /**
     * 查询并展示商品信息
     */
    private void DisplayGoods() {
        pd = new ProgressDialog(getActivity());
        pd.setMessage("正在刷新...");
        pd.show();

        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS | MODE_ENABLE_WRITE_AHEAD_LOGGING | MODE_APPEND);
        userToken = sharedPreferences.getString("UserToken", "");

        Log.i("goods", "查询得到的userToken:" + userToken);

        //String url = "http://118.89.217.225:8080/Proj20/buy";
        //192.168.43.167:8080/FleaMarketProj/buy_refresh_goods?&reqJson={token: 6ef232aef0b547b7b527c9bcfbb6cbfc,pageNumber:4,pageSize:1,opType:90004}

        String url = "http://47.105.174.254:8080/FleaMarketProj/buy_refresh_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(90004);
        //searchBO.setToken(userToken);

        searchBO.setToken(userToken);
        searchBO.setPageNumber(1);
        searchBO.setPageSize(pageSize);

        Gson gson = new Gson();
        String reqJson = gson.toJson(searchBO, SearchBO.class);
        Log.i("goods", "reqJson" + reqJson);

        Log.i("goods", "开始发送查询请求。。。");

        RequestUtils.PostRequestWithOkHttp(url, reqJson, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("goods", "数据获取失败！！" + e.toString());
                ConnetctFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("goods", "数据获取成功！！");
                    Log.i("goods", "response.code==" + response.code());

                    final String responseStr = response.body().string();
                    Log.i("goods", "response.body().String()==" + responseStr);
                    Gson gson = new Gson();
                    final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);//仅仅解析出flag和message，用于判断是否查询成功
                    Log.i("goods", "responseBO:" + responseBO.toString());

                    final ResponseBuy responseBuy = gson.fromJson(responseStr, ResponseBuy.class);//将所有信息都解析出来

                    if (responseBO.flag == 200) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //获取解析后生成goodslist
                                resultGoodsList = responseBuy.getGoodsList();
                                if (resultGoodsList.isEmpty()){
                                    Toast.makeText(getActivity(), "暂无数据!", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }else{
                                    //创建适配器
                                    recyclerAdapter = new MyGoodsRecyclerAdapter(getActivity(), resultGoodsList);
                                    //视图加载适配器
                                    recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
                                    Toast.makeText(getActivity(), "刷新成功!", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            }
                        });
                    }else if(responseBO.token==null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });

                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "刷新失败!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });

                    }
                }
            }

        });
    }

    /**
     * 刷新商品信息
     */
    private void RefreshGoods() {
        refreshFlag = true;
        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS | MODE_ENABLE_WRITE_AHEAD_LOGGING | MODE_APPEND);
        userToken = sharedPreferences.getString("UserToken", "");

        Log.i("goods", "查询得到的userToken:" + userToken);

        //String url = "http://118.89.217.225:8080/Proj20/buy";
        //192.168.43.167:8080/FleaMarketProj/buy_refresh_goods?&reqJson={token: 6ef232aef0b547b7b527c9bcfbb6cbfc,pageNumber:4,pageSize:1,opType:90004}

        String url = "http://47.105.174.254:8080/FleaMarketProj/buy_refresh_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(90004);
        //searchBO.setToken(userToken);

        searchBO.setToken(userToken);
        searchBO.setPageNumber(1);
        searchBO.setPageSize(pageSize);

        Gson gson = new Gson();
        String reqJson = gson.toJson(searchBO, SearchBO.class);
        Log.i("goods", "reqJson" + reqJson);

        Log.i("goods", "开始发送查询请求。。。");

        RequestUtils.PostRequestWithOkHttp(url, reqJson, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("goods", "数据获取失败！！" + e.toString());
                ConnetctFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("goods", "数据获取成功！！");
                    Log.i("goods", "response.code==" + response.code());

                    final String responseStr = response.body().string();
                    Log.i("goods", "response.body().String()==" + responseStr);
                    Gson gson = new Gson();
                    final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);//仅仅解析出flag和message，用于判断是否查询成功
                    Log.i("goods", "responseBO:" + responseBO.toString());

                    final ResponseBuy responseBuy = gson.fromJson(responseStr, ResponseBuy.class);//将所有信息都解析出来

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBO.flag == 200) {
                                netFailedView.setVisibility(View.GONE);
                                //获取解析后生成goodslist
                                resultGoodsList.clear();//刷新前先清空之前的数据
                                List <Goods> refreshGoodsList = responseBuy.getGoodsList();
                                if (refreshGoodsList.isEmpty()){
                                    Toast.makeText(getActivity(), "暂无数据!", Toast.LENGTH_SHORT).show();
                                }else{
                                    resultGoodsList.addAll(refreshGoodsList);
                                    recyclerAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(), "刷新成功!", Toast.LENGTH_SHORT).show();
                                }
                            }else if(responseBO.token==null){
                                Toast.makeText(getActivity(), "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), "刷新失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });
    }

    /**
     * 加载更多商品信息
     */
    private void LoadingMoreGoods() {
        if(refreshFlag==true){
            pageNumber=1;
            refreshFlag=false;
        }
        pageNumber++;
        sharedPreferences = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userToken = sharedPreferences.getString("UserToken", "");
        Log.i("goods", "查询得到的userToken:" + userToken);
        //String url = "http://118.89.217.225:8080/Proj20/buy";
        //192.168.43.167:8080/FleaMarketProj/buy_refresh_goods?&reqJson={token: 6ef232aef0b547b7b527c9bcfbb6cbfc,pageNumber:4,pageSize:1,opType:90004}

        String url = "http://47.105.174.254:8080/FleaMarketProj/buy_loadmore_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(90004);
        //searchBO.setToken(userToken);

        searchBO.setToken(userToken);
        searchBO.setPageNumber(pageNumber);
        searchBO.setPageSize(pageSize);

        Gson gson = new Gson();
        String reqJson = gson.toJson(searchBO, SearchBO.class);
        Log.i("goods", "reqJson" + reqJson);

        Log.i("goods", "开始发送查询请求。。。");

        RequestUtils.PostRequestWithOkHttp(url, reqJson, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("goods", "数据获取失败！！" + e.toString());
                ConnetctFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("goods", "数据获取成功！！");
                    Log.i("goods", "response.code==" + response.code());

                    final String responseStr = response.body().string();
                    Log.i("goods", "response.body().String()==" + responseStr);
                    Gson gson = new Gson();
                    final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);//仅仅解析出flag和message，用于判断是否查询成功
                    Log.i("goods", "responseBO:" + responseBO.toString());

                    final ResponseBuy responseBuy = gson.fromJson(responseStr, ResponseBuy.class);//将所有信息都解析出来

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBO.flag == 200) {
                                //获取解析后生成goodslist
                                List<Goods> loadMoreGoodsList = responseBuy.getGoodsList();
                                if (loadMoreGoodsList.isEmpty()){
                                    Toast.makeText(getActivity(), "没有更多了!!", Toast.LENGTH_SHORT).show();
                                }else {
                                    resultGoodsList.addAll(loadMoreGoodsList);
                                    recyclerAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(), "加载成功!!", Toast.LENGTH_SHORT).show();
                                }
                            }else if(responseBO.token==null){
                                Toast.makeText(getActivity(), "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), "加载失败!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });
    }

    /**
     * 检查网络连接状态
     */
    public  boolean isNetConnected(Context paramContext) {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
            return true;
        return false;
    }

    /**
     * 判断是网络出错还是服务器出错
     */
    public void ConnetctFailed(){
        if (isNetConnected(getActivity())==false){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    netFailedView.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "网络不给力哦!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }
}


