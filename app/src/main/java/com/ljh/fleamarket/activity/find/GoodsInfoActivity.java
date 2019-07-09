package com.ljh.fleamarket.activity.find;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.adapter.GoodsInfoRecyclerAdapter;
import com.ljh.fleamarket.bo.Goods;


public class GoodsInfoActivity extends AppCompatActivity {
    private Button btBack;
    private RecyclerView recyclerView;
    private Goods goods;
    private GoodsInfoRecyclerAdapter goodsInfoRecyclerAdapter;
    private static final String TAG = "GoodsInfoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);

        btBack = (Button) findViewById(R.id.back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent();
                setResult(RESULT_OK,backIntent);
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.goodsinfo_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
        recyclerView.setLayoutManager(linearLayoutManager);
        getData();
        if (goods!=null){
            goodsInfoRecyclerAdapter = new GoodsInfoRecyclerAdapter(this,goods);
            recyclerView.setAdapter(goodsInfoRecyclerAdapter);
        }else{
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
        }

    }
    public void getData(){
        Intent intent = getIntent();
        if (intent!=null){
            Bundle bundle = intent.getExtras();
            if (bundle!=null){
                goods = (Goods) bundle.getSerializable("goods");
//                goods.setGoodsImg(bundle.getByteArray("goodsImage"));
//                goods.setGoodsName(bundle.getString("goodsName"));
//                Log.e(TAG, "getData: "+bundle.getByteArray("goodsImage"));
//                goods.setPrice(bundle.getFloat("goodsPrice"));
//                goods.setQuality(bundle.getFloat("goodsQuantity"));
//                goods.setUnit(bundle.getString("goodsUnit"));
//                goods.setGoodsType(bundle.getString("goodsType"));
//                goods.setUserid(bundle.getString("userId"));
//                goods.setContact(bundle.getString("Contact"));
//                goods.setDescription(bundle.getString("Description"));
            }
        }
    }

}
