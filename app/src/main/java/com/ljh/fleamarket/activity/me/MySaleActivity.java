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
import com.ljh.fleamarket.activity.find.AddSaleActivity;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MySaleActivity extends AppCompatActivity implements View.OnClickListener{
    private Button addSale;
    private Button backto_me;

    private RefreshLayout mRefreshLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sale);

        setInit();
    }

    /**
     * 初始化组件
     */
    private void setInit() {
        pd = new ProgressDialog(this);
        //返回我的界面
        backto_me = (Button) findViewById(R.id.backto_me);
        backto_me.setOnClickListener(this);

        //添加我的摊位
        addSale = (Button) findViewById(R.id.add_sale);
        addSale.setOnClickListener(this);

        DisplayGoods();
        mRefreshLayout = (RefreshLayout) findViewById(R.id.smartrefresh_layout_mysale);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                RefreshGoods();
                mRefreshLayout.finishRefresh(true);
//                //List<String>  data = initDatas();
//                resultGoodsList = RefreshGoods();
////                Log.i("goods","goodslist.size():"+String.valueOf(resultGoodsList.size()));
//                Message message = new Message();
//                message.what = 1;
//                message.obj = resultGoodsList;
//                mHandler.sendMessageDelayed(message, 2000);
            }
        });


        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                LoadingMoreGoods();
                mRefreshLayout.finishLoadMore(true);
//                //List<String>  data = initDatas();
//                resultGoodsList = LoadingMoreGoods();
////                Log.i("goods","goodslist.size():"+String.valueOf(resultGoodsList.size()));
//                Message message = new Message();
//                message.what = 2;
//                message.obj = resultGoodsList;
//                mHandler.sendMessageDelayed(message, 2000);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycleview_mysale);
        recyclerView.setHasFixedSize(true);
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);//瀑布布局
//        recyclerView.setLayoutManager(layoutManager);//设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


    }

    /**
     *点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backto_me:
                Intent backToMe = new Intent();
                setResult(RESULT_OK,backToMe);
                finish();
                break;

            case R.id.add_sale:
                Intent addMySaleIntent = new Intent(this,AddSaleActivity.class);
                startActivity(addMySaleIntent);
                break;
        }

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

        //String url = "http://118.89.217.225:8080/Proj20/buy";
        //192.168.43.167:8080/FleaMarketProj/buy_refresh_goods?&reqJson={token: 6ef232aef0b547b7b527c9bcfbb6cbfc,pageNumber:4,pageSize:1,opType:90004}

        String url = "http://47.105.174.254:8080/FleaMarketProj/buy_refresh_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(90004);
        //searchBO.setToken(userToken);

        searchBO.setToken(userToken);
        searchBO.setPageNumber(1);
        searchBO.setPageSize(5);

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            if (responseBO.flag == 200) {
                                //获取解析后生成goodslist
                                resultGoodsList = responseBuy.getGoodsList();
                                if (resultGoodsList.isEmpty()){
                                    Toast.makeText(MySaleActivity.this, "暂无数据!", Toast.LENGTH_SHORT).show();
                                }else {
                                    //创建适配器
                                    recyclerAdapter = new MyGoodsRecyclerAdapter(MySaleActivity.this, resultGoodsList);
                                    //视图加载适配器
                                    recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
                                    Toast.makeText(MySaleActivity.this, "刷新成功!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(responseBO.token==null){
                                Toast.makeText(MySaleActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MySaleActivity.this, "刷新失败!", Toast.LENGTH_SHORT).show();
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
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS | MODE_ENABLE_WRITE_AHEAD_LOGGING | MODE_APPEND);
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
        searchBO.setPageSize(5);

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBO.flag == 200) {
                                //获取解析后生成goodslist
//                                List <Goods> refreshGoodsList = responseBuy.getGoodsList();
//                                for(int i=0;i<refreshGoodsList.size();i++){
//                                    resultGoodsList.add(refreshGoodsList.get(i));
//                                }
//                                recyclerAdapter.notifyDataSetChanged();

                                resultGoodsList = responseBuy.getGoodsList();
                                //创建适配器
                                recyclerAdapter = new MyGoodsRecyclerAdapter(MySaleActivity.this, resultGoodsList);
                                //视图加载适配器
                                recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
                                Toast.makeText(MySaleActivity.this, "刷新成功!", Toast.LENGTH_SHORT).show();
                                //pullToRefreshAndPushToLoadView.finishLoading();
                            }else if(responseBO.token==null){
                                Toast.makeText(MySaleActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MySaleActivity.this, "刷新失败!", Toast.LENGTH_SHORT).show();
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
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS | MODE_ENABLE_WRITE_AHEAD_LOGGING | MODE_APPEND);
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBO.flag == 200) {
                                //获取解析后生成goodslist
                                List <Goods> loadMoreGoodsList = responseBuy.getGoodsList();
                                if (loadMoreGoodsList.isEmpty()){
                                    Toast.makeText(MySaleActivity.this, "没有更多了!!", Toast.LENGTH_SHORT).show();
                                }else {
                                    for(int i=0;i<loadMoreGoodsList.size();i++){
                                        resultGoodsList.add(loadMoreGoodsList.get(i));
                                    }
                                    recyclerAdapter.notifyDataSetChanged();
                                    Toast.makeText(MySaleActivity.this, "加载成功!", Toast.LENGTH_SHORT).show();
                                    //pullToRefreshAndPushToLoadView.finishLoading();
                                }

                            }else if(responseBO.token==null){
                                Toast.makeText(MySaleActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MySaleActivity.this, "加载失败!", Toast.LENGTH_SHORT).show();
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
        if (isNetConnected(MySaleActivity.this)==false){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MySaleActivity.this, "网络不给力哦!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MySaleActivity.this, "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }

}
