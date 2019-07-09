package com.ljh.fleamarket.activity.index;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.adapter.GoodsBuyAdapter;
import com.ljh.fleamarket.adapter.MyGoodsRecyclerAdapter;
import com.ljh.fleamarket.bo.Goods;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.bo.ResponseBuy;
import com.ljh.fleamarket.bo.SearchBO;
import com.ljh.fleamarket.utils.EncoderAndDecoderUtils;
import com.ljh.fleamarket.utils.RequestUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class SearchActivity extends AppCompatActivity {
    private Button backtohome;
    private Button btSearch;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private ProgressDialog pd;
    private MyGoodsRecyclerAdapter myGoodsRecyclerAdapter;
    private List<Goods> resultGoodsList;
    private RefreshLayout mRefreshLayout;

    private String userToken;
    private SharedPreferences sharedPreferences;
    private int pageNumber=1;
    private int pageSize = 5;
    private boolean refreshFlag;
    private String goodsKeyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        btSearch = (Button) findViewById(R.id.bt_search);
        etSearch = (EditText) findViewById(R.id.et_search);
        backtohome = (Button)findViewById(R.id.backto_home);
        pd = new ProgressDialog(this);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.sr_layout);
        recyclerView = (RecyclerView) findViewById(R.id.rv_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        backtohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etSearch.getText().toString())){
                    Toast.makeText(SearchActivity.this, "请输入查询内容", Toast.LENGTH_SHORT).show();
                }else{
                    goodsKeyWord = etSearch.getText().toString();
                    DisplayGoods();
                }
            }
        });


        //刷新
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                RefreshGoods();
                mRefreshLayout.finishRefresh();
            }
        });

        //加载更多
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                LoadingMoreGoods();
                mRefreshLayout.finishLoadMore();
            }
        });

    }

    /**
     * 查询并展示商品信息
     */
    private void DisplayGoods() {
        pd.setMessage("正在刷新...");
        pd.show();

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS | MODE_ENABLE_WRITE_AHEAD_LOGGING | MODE_APPEND);
        userToken = sharedPreferences.getString("UserToken", "");

        Log.i("goods", "查询得到的userToken:" + userToken);

        String url = "http://47.105.174.254:8080/FleaMarketProj/search_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(70001);//70001表示刷新求购商品信息
        //searchBO.setToken(userToken);

        searchBO.setToken(userToken);
        searchBO.setGoodsKeyWord(goodsKeyWord);//关键字查询
        searchBO.setPageNumber(1);
        searchBO.setPageSize(pageSize);

        Gson gson = new Gson();
        String reqJson = gson.toJson(searchBO, SearchBO.class);
        Log.i("goods", "reqJson" + reqJson);
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);
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
                                    Toast.makeText(SearchActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                                }else {
                                    //创建适配器
                                    myGoodsRecyclerAdapter = new MyGoodsRecyclerAdapter(SearchActivity.this, resultGoodsList);
                                    //视图加载适配器
                                    recyclerView.setAdapter(myGoodsRecyclerAdapter);//设置Adapter(适配器)
                                    Toast.makeText(SearchActivity.this, "加载成功!", Toast.LENGTH_SHORT).show();
                                }

                            }else if(responseBO.token==null){
                                Toast.makeText(SearchActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SearchActivity.this, "加载失败!", Toast.LENGTH_SHORT).show();
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


        String url = "http://47.105.174.254:8080/FleaMarketProj/search_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(70001);
        //searchBO.setToken(userToken);

        searchBO.setToken(userToken);
        searchBO.setGoodsKeyWord(goodsKeyWord);//关键字查询
        searchBO.setPageNumber(1);
        searchBO.setPageSize(pageSize);

        Gson gson = new Gson();
        String reqJson = gson.toJson(searchBO, SearchBO.class);
        Log.i("goods", "reqJson" + reqJson);
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);
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
                                resultGoodsList.clear();//清空之前的数据
                                List <Goods> GoodsList = responseBuy.getGoodsList();
                                resultGoodsList.addAll(GoodsList);
                                myGoodsRecyclerAdapter.notifyDataSetChanged();//刷新适配器
//                                //创建适配器
//                                myGoodsRecyclerAdapter = new MyGoodsRecyclerAdapter(SearchActivity.this, resultGoodsList);
//                                //视图加载适配器
//                                recyclerView.setAdapter(goodsBuyAdapter);//设置Adapter(适配器)
                                Toast.makeText(SearchActivity.this, "刷新成功!!", Toast.LENGTH_SHORT).show();

                            }else if(responseBO.token==null){
                                Toast.makeText(SearchActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SearchActivity.this, "刷新失败!!", Toast.LENGTH_SHORT).show();
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
            pageNumber=1;//刷新数据后又从第二页开始加载
            refreshFlag=false;
        }
        pageNumber++;
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        userToken = sharedPreferences.getString("UserToken", "");
        Log.i("goods", "查询得到的userToken:" + userToken);

        String url = "http://47.105.174.254:8080/FleaMarketProj/search_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(70002);//80002表示加载更多求购信息

        searchBO.setToken(userToken);
        searchBO.setGoodsKeyWord(goodsKeyWord);
        searchBO.setPageNumber(pageNumber);
        searchBO.setPageSize(pageSize);

        Gson gson = new Gson();
        String reqJson = gson.toJson(searchBO, SearchBO.class);
        Log.i("goods", "reqJson" + reqJson);
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);
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
                                if (loadMoreGoodsList.isEmpty()){
                                    Toast.makeText(SearchActivity.this, "没有更多了!!", Toast.LENGTH_SHORT).show();
                                }else {
                                    resultGoodsList.addAll(loadMoreGoodsList);
                                    myGoodsRecyclerAdapter.notifyDataSetChanged();//刷新适配器
                                    Toast.makeText(SearchActivity.this, "加载成功!!", Toast.LENGTH_SHORT).show();
                                }

                            }else if(responseBO.token==null){
                                Toast.makeText(SearchActivity.this, "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SearchActivity.this, "加载失败!!", Toast.LENGTH_SHORT).show();
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
        if (isNetConnected(SearchActivity.this)==false){
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SearchActivity.this, "网络不给力哦!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SearchActivity.this, "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }
}
