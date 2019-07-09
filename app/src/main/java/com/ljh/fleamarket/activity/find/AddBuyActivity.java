package com.ljh.fleamarket.activity.find;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.bo.Goods;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.utils.EncoderAndDecoderUtils;
import com.ljh.fleamarket.utils.RequestUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;


public class AddBuyActivity extends AppCompatActivity {
    private Button backto_find2;
    private Button bt_submitBuyGoods;
    private EditText et_getGoodsName;
    private Spinner  getGoodsType;
    private EditText et_getGoodsQuality;
    private EditText et_getGoodsContact;
    private EditText et_getGoodsDescription;
    private ProgressDialog pd;

    private String goodsName;
    private String goodsType;
    private float goodsQuality;
    private String goodsContact;
    private String goodsDescription;

    private String TAG = "addgoods";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_buy);
        backto_find2 = (Button)findViewById(R.id.backto_find2);
        bt_submitBuyGoods = (Button) findViewById(R.id.submit_buy);
        et_getGoodsName = (EditText) findViewById(R.id.goods_name);
        et_getGoodsQuality = (EditText) findViewById(R.id.goods_quality);
        et_getGoodsContact = (EditText) findViewById(R.id.goods_contact);
        et_getGoodsDescription = (EditText) findViewById(R.id.goods_description);
        getGoodsType = (Spinner) findViewById(R.id.goods_type);

        //获取商品类型
        getGoodsType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                goodsType = (String)getGoodsType.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backto_find2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        bt_submitBuyGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitBuyGoodsInfo();
            }
        });
    }

    /**
     * 提交求购信息
     */
    void submitBuyGoodsInfo(){
        if (TextUtils.isEmpty(et_getGoodsQuality.getText().toString())||TextUtils.isEmpty(et_getGoodsName.getText().toString())
        ||TextUtils.isEmpty(et_getGoodsContact.getText().toString())||TextUtils.isEmpty(et_getGoodsDescription.getText().toString())){
            Toast.makeText(this, "请输入完整的商品信息", Toast.LENGTH_SHORT).show();
        }else{
            goodsName = et_getGoodsName.getText().toString();
            goodsQuality = Float.parseFloat(et_getGoodsQuality.getText().toString());
            goodsContact = et_getGoodsContact.getText().toString();
            goodsDescription = et_getGoodsDescription.getText().toString();
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
            String userToken = sharedPreferences.getString("UserToken","");//获取用户token
            String userID = sharedPreferences.getString("UserId","");
            Goods goods = new Goods();
            goods.setOpType(90021);//90011为添加我的摊位，发布新的商品信息，90021为添加我的求购，发布新的求购信息
            goods.setGoodsName(goodsName);
            goods.setGoodsType(goodsType);
            goods.setQuality(goodsQuality);
            goods.setContact(goodsContact);
            goods.setDescription(goodsDescription);
            goods.setToken(userToken);
            goods.setUserid(userID);

            Gson gson = new Gson();
            String reqJson = gson.toJson(goods, Goods.class);
            Log.i(TAG, "reqJson" + reqJson);
            //将reqJson进行编码，后台服务器再进行解码，防止中文乱码
            reqJson = EncoderAndDecoderUtils.enCoder(reqJson);
            String url = "http://47.105.174.254:8080/FleaMarketProj/add_goodsinfo";
            Log.i(TAG, "开始发送提交请求。。。");
            //等待动画
            pd = new ProgressDialog(this);
            pd.setMessage("正在提交...");
            pd.show();//显示等待
            RequestUtils.PostRequestWithOkHttp(url,reqJson, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG, "数据获取失败！！" + e.toString());
                    ConnetctFailed();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "数据获取成功！！");
                        Log.i(TAG, "response.code==" + response.code());

                        String responseStr = response.body().string();
                        responseStr = EncoderAndDecoderUtils.deCoder(responseStr);
                        Log.i(TAG, "response.body().String()==" + responseStr);
                        Gson gson = new Gson();
                        final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);
                        if(!TextUtils.isEmpty(responseBO.toString())){
                            Log.i(TAG, responseBO.toString());
                        }
                        //Log.i(TAG, "登录后返回的token为："+responseBO.token);
                        if (responseBO.flag == 90022) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddBuyActivity.this,responseBO.message, Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddBuyActivity.this,responseBO.message, Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            });
                        }
                    }
                }
            });
        }

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
        if (isNetConnected(AddBuyActivity.this)==false){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddBuyActivity.this, "网络不给力哦!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddBuyActivity.this, "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }
}
