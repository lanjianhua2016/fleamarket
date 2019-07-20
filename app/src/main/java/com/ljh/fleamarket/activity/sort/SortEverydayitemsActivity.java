package com.ljh.fleamarket.activity.sort;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.ljh.fleamarket.bo.Goods;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.bo.ResponseBuy;
import com.ljh.fleamarket.bo.SearchBO;
import com.ljh.fleamarket.utils.DataUtils;
import com.ljh.fleamarket.utils.EncoderAndDecoderUtils;
import com.ljh.fleamarket.utils.RequestUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class SortEverydayitemsActivity extends AppCompatActivity {
    private Button backtosort3;
    private RefreshLayout RefreshLayout;
    private MyGoodsRecyclerAdapter recyclerAdapter;
    private List<Goods> resultGoodsList;

    private String userToken;
    private SharedPreferences sharedPreferences;

    //private PullToRefreshAndPushToLoadView pullToRefreshAndPushToLoadView;
    private RecyclerView recyclerView;
    private ProgressDialog pd;

    private int pageNumber=1;
    private int pageSize = 5;
    private boolean refreshFlag;
    private DataUtils dataApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort_everyday);
        dataApp = (DataUtils) getApplication();
        dataApp.setIntentPermission(false);
        backtosort3 = (Button)findViewById(R.id.backto_sort3);
        backtosort3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataApp.isIntentPermission()) {
                    finish();
                } else {
                    Toast.makeText(SortEverydayitemsActivity.this, "请稍等...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setInit();
    }

    void setInit(){
        pd = new ProgressDialog(this);
        DisplayGoods();
        RefreshLayout = (RefreshLayout) findViewById(R.id.smartrefresh_layout_sort_everydayitems);
        RefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                RefreshGoods();
                RefreshLayout.finishRefresh(true);
            }
        });

        RefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                LoadingMoreGoods();
                RefreshLayout.finishLoadMore(true);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycleview_mysale);
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
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    /**
     * 查询并展示商品信息
     */
    private void DisplayGoods() {
        pd.setMessage("正在刷新...");
        pd.show();

        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        userToken = sharedPreferences.getString("UserToken", "");

        Log.i("goods", "查询得到的userToken:" + userToken);

        String url = "http://47.105.174.254:8080/FleaMarketProj/sort_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(90004);

        searchBO.setToken(userToken);
        searchBO.setGoodsType("日常用品");

        searchBO.setPageNumber(1);
        searchBO.setPageSize(pageSize);

        Gson gson = new Gson();
        String reqJson = gson.toJson(searchBO, SearchBO.class);
        Log.i("goods", "reqJson" + reqJson);

        Log.i("goods", "开始发送查询请求。。。");
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);
        RequestUtils.PostRequestWithOkHttp(url, reqJson, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("goods", "数据获取失败！！" + e.toString());
                ConnetctFailed();
                dataApp.setIntentPermission(true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("goods", "数据获取成功！！");
                    Log.i("goods", "response.code==" + response.code());

                    String responseStr = response.body().string();

                    responseStr = EncoderAndDecoderUtils.deCoder(responseStr);
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
                                resultGoodsList = responseBuy.getGoodsList();
                                if (resultGoodsList.isEmpty()){
                                    Toast.makeText(SortEverydayitemsActivity.this, "暂无数据!", Toast.LENGTH_SHORT).show();
                                }else {
                                    //创建适配器
                                    recyclerAdapter = new MyGoodsRecyclerAdapter(SortEverydayitemsActivity.this, resultGoodsList);
                                    //视图加载适配器
                                    recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
                                    Toast.makeText(SortEverydayitemsActivity.this, "刷新成功!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(responseBO.token==null){
                                Toast.makeText(SortEverydayitemsActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(SortEverydayitemsActivity.this, "刷新失败!", Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();
                            dataApp.setIntentPermission(true);
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

        String url = "http://47.105.174.254:8080/FleaMarketProj/sort_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(90004);

        searchBO.setToken(userToken);
        searchBO.setGoodsType("日常用品");

        searchBO.setPageNumber(1);
        searchBO.setPageSize(pageSize);

        Gson gson = new Gson();
        String reqJson = gson.toJson(searchBO, SearchBO.class);
        Log.i("goods", "reqJson" + reqJson);

        Log.i("goods", "开始发送查询请求。。。");
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);
        RequestUtils.PostRequestWithOkHttp(url, reqJson, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("goods", "数据获取失败！！" + e.toString());
                ConnetctFailed();
                dataApp.setIntentPermission(true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("goods", "数据获取成功！！");
                    Log.i("goods", "response.code==" + response.code());

                    String responseStr = response.body().string();
                    responseStr = EncoderAndDecoderUtils.deCoder(responseStr);
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
                                //创建适配器
                                recyclerAdapter = new MyGoodsRecyclerAdapter(SortEverydayitemsActivity.this, resultGoodsList);
                                //视图加载适配器
                                recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
                                Toast.makeText(SortEverydayitemsActivity.this, "刷新成功!", Toast.LENGTH_SHORT).show();
                                //pullToRefreshAndPushToLoadView.finishLoading();
                            }else if(responseBO.token==null){
                                Toast.makeText(SortEverydayitemsActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SortEverydayitemsActivity.this, "刷新失败!", Toast.LENGTH_SHORT).show();
                            }
                            dataApp.setIntentPermission(true);
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
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        userToken = sharedPreferences.getString("UserToken", "");
        Log.i("goods", "查询得到的userToken:" + userToken);

        String url = "http://47.105.174.254:8080/FleaMarketProj/sort_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(90004);
        //searchBO.setToken(userToken);
        searchBO.setGoodsType("日常用品");
        searchBO.setToken(userToken);
        searchBO.setPageNumber(pageNumber);
        searchBO.setPageSize(pageSize);
        searchBO.setGetMoreFlag("getMore");
        Gson gson = new Gson();
        String reqJson = gson.toJson(searchBO, SearchBO.class);
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);
        Log.i("goods", "reqJson" + reqJson);

        Log.i("goods", "开始发送查询请求。。。");

        RequestUtils.PostRequestWithOkHttp(url, reqJson, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("goods", "数据获取失败！！" + e.toString());
                ConnetctFailed();
                dataApp.setIntentPermission(true);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("goods", "数据获取成功！！");
                    Log.i("goods", "response.code==" + response.code());

                    String responseStr = response.body().string();
                    responseStr = EncoderAndDecoderUtils.deCoder(responseStr);
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
                                if (loadMoreGoodsList.isEmpty()){
                                    Toast.makeText(SortEverydayitemsActivity.this, "没有更多了!!", Toast.LENGTH_SHORT).show();
                                }else {
                                    for(int i=0;i<loadMoreGoodsList.size();i++){
                                        resultGoodsList.add(loadMoreGoodsList.get(i));
                                    }
                                    recyclerAdapter.notifyDataSetChanged();
                                    Toast.makeText(SortEverydayitemsActivity.this, "加载成功!", Toast.LENGTH_SHORT).show();
                                    //pullToRefreshAndPushToLoadView.finishLoading();
                                }

                            }else if(responseBO.token==null){
                                Toast.makeText(SortEverydayitemsActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SortEverydayitemsActivity.this, "加载失败!", Toast.LENGTH_SHORT).show();
                            }
                            dataApp.setIntentPermission(true);
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
        if (isNetConnected(SortEverydayitemsActivity.this)==false){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SortEverydayitemsActivity.this, "网络不给力哦!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SortEverydayitemsActivity.this, "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }
}
