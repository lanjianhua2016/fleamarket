package com.ljh.fleamarket.activity.me;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.adapter.MyGoodsRecyclerAdapter;
import com.ljh.fleamarket.bo.CollectBO;
import com.ljh.fleamarket.bo.Goods;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.bo.ResponseBuy;
import com.ljh.fleamarket.bo.SearchBO;
import com.ljh.fleamarket.utils.RequestUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class CollectionActivity extends AppCompatActivity {

    private Button backToMe;
    private RefreshLayout mRefreshLayout;
    private MyGoodsRecyclerAdapter recyclerAdapter;
    private List<Goods> resultGoodsList;

    private String userToken;
    private SharedPreferences sharedPreferences;

    //private PullToRefreshAndPushToLoadView pullToRefreshAndPushToLoadView;
    private RecyclerView recyclerView;
    private ProgressDialog pd;
    private int pageNumber = 1;
    private int pageSize = 5;
    private boolean refreshFlag;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        pd = new ProgressDialog(this);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.smartrefresh_layout_collection);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview_collection);
        backToMe = (Button) findViewById(R.id.backto_me);
        backToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent();
                setResult(RESULT_OK,backIntent);
                finish();
            }
        });

        DisplayGoods();
        mRefreshLayout = (RefreshLayout) findViewById(R.id.smartrefresh_layout_collection);
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

        recyclerView.setHasFixedSize(true);
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);//瀑布布局
//        recyclerView.setLayoutManager(layoutManager);//设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
        recyclerView.setLayoutManager(linearLayoutManager);
//        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(), Color.DKGRAY,2,2);
//        dividerItemDecoration.setDrawBorderTopAndBottom(true);
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    /**
     * 查询并展示商品信息，根据用户uid查询用户所发布的求购信息，参数：uid，
     */
    private void DisplayGoods() {

        pd.setMessage("正在刷新...");
        pd.show();

        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        userToken = sharedPreferences.getString("UserToken", "");
        userId = sharedPreferences.getString("UserId", "");

        Log.i("goods", "查询得到的userToken:" + userToken);

        //String url = "http://118.89.217.225:8080/Proj20/buy";
        //192.168.43.167:8080/FleaMarketProj/buy_refresh_goods?&reqJson={token: 6ef232aef0b547b7b527c9bcfbb6cbfc,pageNumber:4,pageSize:1,opType:90004}

        String url = "http://47.105.174.254:8080/FleaMarketProj/collect_goods";
        CollectBO collectBO = new CollectBO();
        collectBO.setOpType(50004);//50004表示查询用户收藏商品
        collectBO.setToken(userToken);
        collectBO.setPageNumber(1);
        collectBO.setPageSize(5);
        collectBO.setUserId(userId);

        Gson gson = new Gson();
        String reqJson = gson.toJson(collectBO, CollectBO.class);
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            if (responseBO.flag == 200) {
                                //获取解析后生成goodslist
                                resultGoodsList = responseBuy.getGoodsList();
                                if (resultGoodsList.isEmpty()) {
                                    Toast.makeText(CollectionActivity.this, "暂无数据!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //创建适配器
                                    recyclerAdapter = new MyGoodsRecyclerAdapter(CollectionActivity.this, resultGoodsList);
                                    //视图加载适配器
                                    recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
                                    Toast.makeText(CollectionActivity.this, "刷新成功!", Toast.LENGTH_SHORT).show();
                                }
                            } else if (responseBO.token == null) {
                                Toast.makeText(CollectionActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CollectionActivity.this, "刷新失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });
    }

    /**
     * 刷新商品信息
     */
    private void RefreshGoods() {
        refreshFlag = true;
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        userToken = sharedPreferences.getString("UserToken", "");

        Log.i("goods", "查询得到的userToken:" + userToken);

        String url = "http://47.105.174.254:8080/FleaMarketProj/collect_goods";
        CollectBO collectBO = new CollectBO();
        collectBO.setOpType(50004);//50004表示刷新查询用户收藏商品
        //searchBO.setToken(userToken);

        collectBO.setToken(userToken);
        collectBO.setPageNumber(1);
        collectBO.setPageSize(5);
        collectBO.setUserId(userId);

        Gson gson = new Gson();
        String reqJson = gson.toJson(collectBO, CollectBO.class);
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBO.flag == 200) {
                                resultGoodsList = responseBuy.getGoodsList();
                                if (resultGoodsList.isEmpty()) {
                                    Toast.makeText(CollectionActivity.this, "暂无数据!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //创建适配器
                                    recyclerAdapter = new MyGoodsRecyclerAdapter(CollectionActivity.this, resultGoodsList);
                                    //视图加载适配器
                                    recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
                                    Toast.makeText(CollectionActivity.this, "刷新成功!", Toast.LENGTH_SHORT).show();
                                    //pullToRefreshAndPushToLoadView.finishLoading();
                                }
                            } else if (responseBO.token == null) {
                                Toast.makeText(CollectionActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CollectionActivity.this, "刷新失败!", Toast.LENGTH_SHORT).show();
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
        if (refreshFlag == true) {
            pageNumber = 1;
            refreshFlag = false;
        }
        pageNumber++;
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        userToken = sharedPreferences.getString("UserToken", "");
        Log.i("goods", "查询得到的userToken:" + userToken);
        //String url = "http://118.89.217.225:8080/Proj20/buy";
        //192.168.43.167:8080/FleaMarketProj/buy_refresh_goods?&reqJson={token: 6ef232aef0b547b7b527c9bcfbb6cbfc,pageNumber:4,pageSize:1,opType:90004}

        String url = "http://47.105.174.254:8080/FleaMarketProj/collect_goods";
        CollectBO collectBO = new CollectBO();
        collectBO.setOpType(50005);//50004表示查询用户收藏商品
        //searchBO.setToken(userToken);

        collectBO.setToken(userToken);
        collectBO.setPageNumber(pageNumber);
        collectBO.setPageSize(pageSize);
        collectBO.setUserId(userId);

        Gson gson = new Gson();
        String reqJson = gson.toJson(collectBO, CollectBO.class);
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBO.flag == 200) {
                                //获取解析后生成goodslist
                                List<Goods> loadMoreGoodsList = responseBuy.getGoodsList();
                                if (loadMoreGoodsList.isEmpty()) {
                                    Toast.makeText(CollectionActivity.this, "没有更多了!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (int i = 0; i < loadMoreGoodsList.size(); i++) {
                                        resultGoodsList.add(loadMoreGoodsList.get(i));
                                    }
                                    recyclerAdapter.notifyDataSetChanged();
                                    Toast.makeText(CollectionActivity.this, "加载成功!", Toast.LENGTH_SHORT).show();
                                }

                            } else if (responseBO.token == null) {
                                Toast.makeText(CollectionActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CollectionActivity.this, "加载失败!", Toast.LENGTH_SHORT).show();
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
    public boolean isNetConnected(Context paramContext) {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
            return true;
        return false;
    }

    /**
     * 判断是网络出错还是服务器出错
     */
    public void ConnetctFailed() {
        if (isNetConnected(CollectionActivity.this) == false) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CollectionActivity.this, "网络不给力哦!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CollectionActivity.this, "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }
}
