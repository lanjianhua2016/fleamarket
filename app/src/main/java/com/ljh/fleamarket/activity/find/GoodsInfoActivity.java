package com.ljh.fleamarket.activity.find;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.me.MyBuyActivity;
import com.ljh.fleamarket.adapter.GoodsInfoRecyclerAdapter;
import com.ljh.fleamarket.adapter.MyGoodsRecyclerAdapter;
import com.ljh.fleamarket.bo.CollectBO;
import com.ljh.fleamarket.bo.Goods;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.bo.ResponseBuy;
import com.ljh.fleamarket.utils.EncoderAndDecoderUtils;
import com.ljh.fleamarket.utils.RequestUtils;
import com.ljh.fleamarket.utils.SharedPreferencesUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class GoodsInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btBack;
    private RecyclerView recyclerView;
    private Goods goods;
    private GoodsInfoRecyclerAdapter goodsInfoRecyclerAdapter;
    private static final String TAG = "GoodsInfoActivity";
    private View layout_collection;
    private TextView tv_addShop;
    private TextView tv_buyNow;
    private ImageView iv_collection;
    private int goodsId;
    private String userId;
    public static String colletFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        btBack = (Button) findViewById(R.id.back);
        layout_collection = findViewById(R.id.l_layout_collect);
        tv_addShop = (TextView) findViewById(R.id.tv_add_to_shop);
        tv_buyNow = (TextView) findViewById(R.id.buy_now);
        iv_collection = (ImageView) findViewById(R.id.iv_collect);
        layout_collection.setOnClickListener(this);
        tv_addShop.setOnClickListener(this);
        tv_buyNow.setOnClickListener(this);
        btBack.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.goodsinfo_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
        recyclerView.setLayoutManager(linearLayoutManager);
        getData();
        if (goods != null) {
            goodsInfoRecyclerAdapter = new GoodsInfoRecyclerAdapter(this, goods);
            recyclerView.setAdapter(goodsInfoRecyclerAdapter);

            userId = goods.getUserid();
            goodsId = goods.getGoodsID();
            Log.i(TAG, "onCreate: userId:" + userId);
            Log.i(TAG, "onCreate: goodsId:" + goodsId);
            //判断商品是否已经收藏过，并设置收藏按钮状态
            CheckIsCollect(userId, goodsId);
        } else {
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent backIntent = new Intent();
                setResult(RESULT_OK, backIntent);
                finish();
                break;
            case R.id.l_layout_collect:
                if (userId != null) {
                    CheckIsCollect(userId, goodsId);
                    Log.i("colletFlag", "onClick: " + colletFlag);
                    if (colletFlag != null) {//已收藏，点击调用取消收藏方法，同时改变按钮状态
                        DeletCollection(userId, goodsId);
                    } else {//未收藏，点击调用收藏方法,同时改变按钮状态
                        CollectGoods(userId, goodsId);
                    }
                } else {
                    Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_add_to_shop:
                Toast.makeText(this, "加入购物车成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buy_now:
                Toast.makeText(this, "立即购买", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                goods = (Goods) bundle.getSerializable("goods");
            }
        }
    }

    /**
     * 收藏商品
     *
     * @param userId
     * @param goodsId
     */
    public void CollectGoods(String userId, int goodsId) {
        String url = "http://47.105.174.254:8080/FleaMarketProj/collect_goods";
        //Goods good = new Goods();
        CollectBO collectBO = new CollectBO();
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);

        collectBO.setUserId(sharedPreferences.getString("UserId", ""));
        collectBO.setGoodsId(goodsId);
        collectBO.setOpType(50001);//50001表示收藏操作
        String reqJson = gson.toJson(collectBO, CollectBO.class);
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);

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
                    final CollectBO collectBO = gson.fromJson(responseStr, CollectBO.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (collectBO.getFlag() == 200) {
                                iv_collection.setImageResource(R.mipmap.icon_collection_b);
                                Toast.makeText(GoodsInfoActivity.this, "收藏成功!", Toast.LENGTH_SHORT).show();
                            } else {
                                iv_collection.setImageResource(R.mipmap.icon_collection_a);
                                Toast.makeText(GoodsInfoActivity.this, "收藏失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });
    }

    /**
     * 取消收藏
     *
     * @param userId
     * @param goodsId
     */
    public void DeletCollection(String userId, int goodsId) {
        String url = "http://47.105.174.254:8080/FleaMarketProj/collect_goods";
        CollectBO collectBO = new CollectBO();
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        collectBO.setUserId(sharedPreferences.getString("UserId", ""));
        collectBO.setGoodsId(goodsId);
        collectBO.setOpType(50002);//50002表示取消收藏操作
        String reqJson = gson.toJson(collectBO, CollectBO.class);
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);

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
                    final CollectBO collectBO = gson.fromJson(responseStr, CollectBO.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (collectBO.getFlag() == 200) {
                                iv_collection.setImageResource(R.mipmap.icon_collection_a);
                                Toast.makeText(GoodsInfoActivity.this, "取消收藏成功!", Toast.LENGTH_SHORT).show();
                            } else {
                                iv_collection.setImageResource(R.mipmap.icon_collection_b);
                                Toast.makeText(GoodsInfoActivity.this, "取消收藏失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });
    }

    /**
     * 查询是否已收藏
     *
     * @param userId
     * @param goodsId
     * @return
     */
    public void CheckIsCollect(String userId, int goodsId) {
        String url = "http://47.105.174.254:8080/FleaMarketProj/collect_goods";
//        Goods good = new Goods();
        CollectBO collectBO = new CollectBO();
        Gson gson = new Gson();
        collectBO.setUserId(userId);
        collectBO.setGoodsId(goodsId);
        collectBO.setOpType(50003);//50003表示查询是否已收藏操作
        String reqJson = gson.toJson(collectBO, CollectBO.class);
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);

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
                    final CollectBO collectBO = gson.fromJson(responseStr, CollectBO.class);


                    if (collectBO.getFlag() == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(GoodsInfoActivity.this, collectBO.getMessage(), Toast.LENGTH_SHORT).show();
                                iv_collection.setImageResource(R.mipmap.icon_collection_b);//已收藏状态
                            }
                        });
                        colletFlag = "已收藏";
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(GoodsInfoActivity.this, "未收藏!", Toast.LENGTH_SHORT).show();
                                iv_collection.setImageResource(R.mipmap.icon_collection_a);//未收藏状态
                            }
                        });
                        colletFlag = null;
                    }

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
        if (isNetConnected(GoodsInfoActivity.this) == false) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GoodsInfoActivity.this, "网络不给力哦!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GoodsInfoActivity.this, "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
