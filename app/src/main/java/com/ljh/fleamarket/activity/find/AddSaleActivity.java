package com.ljh.fleamarket.activity.find;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.me.MySaleActivity;
import com.ljh.fleamarket.adapter.Dialogchoosephoto;
import com.ljh.fleamarket.bo.Goods;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.utils.EncoderAndDecoderUtils;
import com.ljh.fleamarket.utils.ImageUtil;
import com.ljh.fleamarket.utils.RequestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AddSaleActivity extends AppCompatActivity implements View.OnClickListener{
    private Button backto_find1;
    private EditText getGoodsName;
    private EditText getGoodsPrice;
    private EditText getGoodsUnit;
    private EditText getGoodsQuality;
    //private EditText getGoodsUserID;
    private EditText getGoodsContact;
    private EditText getGoodsDescription;
    private Spinner getGoodsType;
    private Button goodsSubmit;
    private ImageView getGoodsImage;
    private  ProgressDialog pd;

    private SharedPreferences sharedPreferences;
    private String goodsName;
    private float goodsPrice;
    private String goodsType;
    private String userToken;
    private String goodsUnit;
    private float goodsQuality;
    private String goodsUserID;
    private String goodsContact;
    private String goodsDescription;

    //相册请求码
    private static final int ALBUM_REQUEST_CODE = 1;
    //相机请求码
    private static final int CAMERA_REQUEST_CODE = 2;
    //剪裁请求码
    private static final int CROP_REQUEST_CODE = 3;

    //调用照相机返回图片文件
    private File tempFile;
    private String goodsPath;
    private String goodsImageName;
    //图片Uri
    private Uri contentUri;
    //存储照片的字节数组
    private byte[] imageByte;

    private String TAG = "addgoods";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_sell);

        //初始化组件
        try {
            setInit();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化组件方法
     */
    private void setInit() throws PackageManager.NameNotFoundException {
        getGoodsName = (EditText) findViewById(R.id.goods_Name);
        getGoodsPrice = (EditText) findViewById(R.id.goods_Price);
        getGoodsUnit = (EditText) findViewById(R.id.goods_Unit);
        getGoodsQuality = (EditText) findViewById(R.id.goods_Quality);
        //getGoodsUserID = (EditText) findViewById(R.id.goods_UserID);
        getGoodsContact = (EditText) findViewById(R.id.goods_contact);
        getGoodsDescription = (EditText) findViewById(R.id.goods_description);
        getGoodsType = (Spinner) findViewById(R.id.goods_Type);
        backto_find1 = (Button)findViewById(R.id.backto_find1);
        goodsSubmit = (Button) findViewById(R.id.submit_sell);
        getGoodsImage = (ImageView) findViewById(R.id.goods_Image);
        backto_find1.setOnClickListener(this);
        goodsSubmit.setOnClickListener(this);
        getGoodsImage.setOnClickListener(this);

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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backto_find1:
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.submit_sell:
                //提交商品信息
                submitGoods();
                break;
            case R.id.goods_Image:
                setGoodsImage();
                break;

        }
    }

    /**
     * 提交商品信息
     */
    private void submitGoods() {
        if (TextUtils.isEmpty(getGoodsName.getText().toString())){
            Toast.makeText(this, "请输入商品名称", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(getGoodsPrice.getText().toString())){
            Toast.makeText(this, "请输入商品价格", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(getGoodsPrice.getText().toString())){
            Toast.makeText(this, "请输入商品数量", Toast.LENGTH_SHORT).show();
        }

        //设置商品照片
        else if (imageByte==null){
            Toast.makeText(this, "请上传商品图片", Toast.LENGTH_SHORT).show();
        }else {
            goodsName = getGoodsName.getText().toString();//获取商品名称
            goodsUnit = getGoodsUnit.getText().toString();//获取商品单位
            //goodsUserID = getGoodsUserID.getText().toString();//获取商品发布人ID
            goodsPrice = Float.valueOf(getGoodsPrice.getText().toString());//获取商品价格
            goodsQuality = Float.valueOf(getGoodsQuality.getText().toString());//获取商品数量
            goodsContact = getGoodsContact.getText().toString();//获取联系方式
            goodsDescription = getGoodsDescription.getText().toString();//获取商品描述信息
            sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
            userToken = sharedPreferences.getString("UserToken","");//获取用户token
            goodsUserID = sharedPreferences.getString("UserId","");//获取用户id

            Log.i(TAG,"goodsName:"+goodsName);
            Log.i(TAG,"goodsPrice:"+String.valueOf(goodsPrice));
            Log.i(TAG,"goodsUnit:"+goodsUnit);
            Log.i(TAG,"goodsQuality:"+String.valueOf(goodsQuality));
            Log.i(TAG,"goodsType:"+goodsType);
            Log.i(TAG,"userToken:"+userToken);

            String url = "http://47.105.174.254:8080/FleaMarketProj/add_goodsinfo";

            Goods goods = new Goods();
            goods.setOpType(90011);//90011为添加我的摊位，发布新的商品信息，90021为添加我的求购，发布新的求购信息
            goods.setGoodsName(goodsName);
            goods.setPrice(goodsPrice);
            goods.setUnit(goodsUnit);
            goods.setQuality(goodsQuality);
            goods.setGoodsType(goodsType);//商品类型
            goods.setToken(userToken);
            goods.setGoodsImg(imageByte);
            goods.setContact(goodsContact);
            goods.setDescription(goodsDescription);
            goods.setUserid(goodsUserID);

            Gson gson = new Gson();
            String reqJson = gson.toJson(goods, Goods.class);
            Log.i(TAG, "reqJson" + reqJson);
            //将reqJson进行编码，后台服务器再进行解码，防止中文乱码
            reqJson = EncoderAndDecoderUtils.enCoder(reqJson);
            Log.i(TAG, "开始发送提交请求。。。");
            //等待动画
            pd = new ProgressDialog(this);
            pd.setMessage("正在提交...");
            pd.show();//显示等待
            RequestUtils.PostRequestWithOkHttp(url,reqJson,new okhttp3.Callback(){
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
                        Log.i(TAG, responseBO.toString());
                        //Log.i(TAG, "登录后返回的token为："+responseBO.token);
                        if (responseBO.flag == 90012) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddSaleActivity.this,responseBO.message, Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddSaleActivity.this,responseBO.message, Toast.LENGTH_SHORT).show();
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
     * 设置商品图片
     */
    private void setGoodsImage() {
        Log.i(TAG, "in sellActivity:点击弹出对话框");
        new Dialogchoosephoto(AddSaleActivity.this) {
            @Override
            public void btnPickByTake() {
                //用于保存调用相机拍照后所生成的文件

                Log.i(TAG, "in sellActivity:点击选择调用相机");
                goodsPath = Environment.getExternalStorageDirectory().getPath();
                goodsImageName = System.currentTimeMillis() + ".jpg";
                tempFile = new File(goodsPath,goodsImageName);
                //tempFile = new File(getExternalCacheDir(),"head_image.jpg");

                //判断版本
                if (Build.VERSION.SDK_INT >= 24) {   //如果在Android7.0以上,使用FileProvider获取Uri
                    contentUri = FileProvider.getUriForFile(AddSaleActivity.this, "com.ljh.activity.fileprovider", tempFile);

                } else {    //否则使用Uri.fromFile(file)方法获取Uri
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    contentUri = Uri.fromFile(tempFile);
                }

                //跳转到调用系统相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                Log.i(TAG, "in sellActivity:准备进入相机回调方法");
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }

            @Override
            public void btnPickBySelect() {
                Log.i(TAG, "in sellActivity:点击选择调用相册");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                photoPickerIntent.setType("image/*");
                Log.i(TAG, "in sellActivity:准备进入相册回调方法");
                startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
            }
        }.show();
    }


    /*回调接口*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            // 调用相机后返回
            case CAMERA_REQUEST_CODE:
                Log.i(TAG, "in sellActivity:进入调用相机回调方法");
                if (resultCode == RESULT_OK) {
                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        contentUri = FileProvider.getUriForFile(AddSaleActivity.this, "com.ljh.activity.fileprovider", tempFile);
                        Log.i(TAG, "in sellActivity:调用相机后准备裁剪图片");
                        cropPhoto(contentUri);//裁剪图片
                    } else {
                        contentUri = Uri.fromFile(tempFile);
                        Log.i(TAG, "in sellActivity:调用相机后准备裁剪图片");
                        cropPhoto(contentUri);//裁剪图片
                    }
                }
                break;
            //调用相册后返回
            case ALBUM_REQUEST_CODE:
                Log.i(TAG, "in sellActivity:进入调用相册回调方法");
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    Log.i(TAG, "in sellActivity:调用相册后准备裁剪图片");
                    cropPhoto(uri);//裁剪图片
                }
                break;
            //调用剪裁后返回
            case CROP_REQUEST_CODE:
                Log.i(TAG, "in sellActivity:进入裁剪图片回调方法");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    //在这里获得了剪裁后的Bitmap对象，可以用于上传
                    Bitmap image = bundle.getParcelable("data");
                    // Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream());
                    //设置到ImageView上
                    Log.i(TAG, "准备显示图片");
                    getGoodsImage.setImageBitmap(image);
                    Log.i("goodsimage", "压缩前图片的大小"+ (ImageUtil.getBitmapSize(image))
                            + "M宽度为"+ image.getWidth() + "高度为"+ image.getHeight());

                    //将bitmap转为字节数组
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageByte = baos.toByteArray();
                    //压缩图片
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    image = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length,options);
                    Log.i("goodsimage", "压缩后图片的大小"+ (ImageUtil.getBitmapSize(image))
                            + "M宽度为"+ image.getWidth() + "高度为"+ image.getHeight());
                    //将压缩后的bitmap转为字节数组
                    baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageByte = baos.toByteArray();

                    //也可以进行一些保存、压缩等操作后上传
                    String path = saveImage("GoodsImage", image);
                    File file = new File(path);
                    /*
                    *上传文件的额操作
                    */
                }
                break;
        }
    }

    /* 裁剪图片 */
    private void cropPhoto(Uri uri) {
        Log.i(TAG, "in sellActivity:开始裁剪图片");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        Log.i(TAG, "in sellActivity:准备进入裁剪图片回调方法");
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    /* 保存图片到本地*/
    public String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        if (isNetConnected(AddSaleActivity.this)==false){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddSaleActivity.this, "网络不给力哦!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddSaleActivity.this, "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }
}
