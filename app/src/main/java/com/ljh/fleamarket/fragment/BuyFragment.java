package com.ljh.fleamarket.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.me.MySaleActivity;
import com.ljh.fleamarket.adapter.GoodsBuyAdapter;
import com.ljh.fleamarket.adapter.MyGoodsRecyclerAdapter;
import com.ljh.fleamarket.bo.Goods;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.bo.ResponseBuy;
import com.ljh.fleamarket.bo.SearchBO;
import com.ljh.fleamarket.utils.DataUtils;
import com.ljh.fleamarket.utils.RequestUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;


public class BuyFragment extends Fragment {

    private RefreshLayout mRefreshLayout;
    //private MyGoodsRecyclerAdapter recyclerAdapter;
    private GoodsBuyAdapter goodsBuyAdapter;
    private List<Goods> resultGoodsList;

    private String userToken;
    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private ProgressDialog pd;

    private int pageNumber=1;
    private int pageSize = 5;
    private boolean refreshFlag;
    private DataUtils dataApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buy, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataApp = (DataUtils) getActivity().getApplication();
        dataApp.setIntentPermission(false);
        pd = new ProgressDialog(getActivity());
        DisplayGoods();
        mRefreshLayout = getActivity().findViewById(R.id.smartrefresh_layout_buy);
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

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycleview_buygoods);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
    }

    /**
     * 查询并展示商品信息
     */
    private void DisplayGoods() {
        pd.setMessage("正在刷新...");
        pd.show();

        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS | MODE_ENABLE_WRITE_AHEAD_LOGGING | MODE_APPEND);
        userToken = sharedPreferences.getString("UserToken", "");

        Log.i("goods", "查询得到的userToken:" + userToken);


        String url = "http://47.105.174.254:8080/FleaMarketProj/buy_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(80001);//80001表示刷新求购商品信息
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
                dataApp.setIntentPermission(true);
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
                            pd.dismiss();
                            if (responseBO.flag == 200) {
                                //获取解析后生成goodslist
                                resultGoodsList = responseBuy.getGoodsList();
                                if (resultGoodsList.isEmpty()){
                                    Toast.makeText(getActivity(), "暂无数据!", Toast.LENGTH_SHORT).show();
                                }else {
                                    //创建适配器
                                    goodsBuyAdapter = new GoodsBuyAdapter(getActivity(), resultGoodsList);
                                    //视图加载适配器
                                    recyclerView.setAdapter(goodsBuyAdapter);//设置Adapter(适配器)
                                    Toast.makeText(getActivity(), "刷新成功!!", Toast.LENGTH_SHORT).show();
                                }
                            }else if(responseBO.token==null){
                                Toast.makeText(getActivity(), "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "刷新失败!!", Toast.LENGTH_SHORT).show();
                            }
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
        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS | MODE_ENABLE_WRITE_AHEAD_LOGGING | MODE_APPEND);
        userToken = sharedPreferences.getString("UserToken", "");

        Log.i("goods", "查询得到的userToken:" + userToken);


        String url = "http://47.105.174.254:8080/FleaMarketProj/buy_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(80001);
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
                dataApp.setIntentPermission(true);
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
                                resultGoodsList.clear();//刷新前先清空之前的数据
                                List <Goods> refreshGoodsList = responseBuy.getGoodsList();
                                if (refreshGoodsList.isEmpty()){
                                    Toast.makeText(getActivity(), "暂无数据!", Toast.LENGTH_SHORT).show();
                                }else{
                                    resultGoodsList.addAll(refreshGoodsList);
                                    goodsBuyAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(), "刷新成功!", Toast.LENGTH_SHORT).show();
                                }

                            }else if(responseBO.token==null){
                                Toast.makeText(getActivity(), "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "刷新失败!!", Toast.LENGTH_SHORT).show();
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
        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS | MODE_ENABLE_WRITE_AHEAD_LOGGING | MODE_APPEND);
        userToken = sharedPreferences.getString("UserToken", "");
        Log.i("goods", "查询得到的userToken:" + userToken);

        String url = "http://47.105.174.254:8080/FleaMarketProj/buy_goods";
        SearchBO searchBO = new SearchBO();
        searchBO.setOpType(80002);//80002表示加载更多求购信息

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
                dataApp.setIntentPermission(true);
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
                                List <Goods> loadMoreGoodsList = responseBuy.getGoodsList();
                                if (loadMoreGoodsList.isEmpty()){
                                    Toast.makeText(getActivity(), "没有更多了!!", Toast.LENGTH_SHORT).show();
                                }else {
                                    resultGoodsList.addAll(loadMoreGoodsList);
                                    goodsBuyAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(), "加载成功!!", Toast.LENGTH_SHORT).show();
                                }

                            }else if(responseBO.token==null){
                                Toast.makeText(getActivity(), "身份验证过期,请重新登录!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "加载失败!!", Toast.LENGTH_SHORT).show();
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
        if (isNetConnected(getActivity())==false){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
