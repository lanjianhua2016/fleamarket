package com.ljh.fleamarket.activity.me;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.adapter.Dialogchoosephoto;
import com.ljh.fleamarket.bo.Response;
import com.ljh.fleamarket.bo.UserBO;
import com.ljh.fleamarket.bo.UserVO;
import com.ljh.fleamarket.utils.EncoderAndDecoderUtils;
import com.ljh.fleamarket.utils.RequestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;

import static com.ljh.fleamarket.activity.R.id.up_user_id;
import static com.ljh.fleamarket.activity.me.UserInfoActivty.userInfoActivty;

public class UpDateUserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_backToUserInfo;
    private TextView tv_updateUserInfoFinish;
    //private RecyclerView rv_updateUserInfoView;
    private ImageView iv_portrait;
    private TextView et_Uid;
    private EditText et_Uname;
    private EditText et_Usex;
    private EditText et_Uqq;
    private EditText et_Uweixin;
    private EditText et_Uphone;

    private UserBO userBO = null;
    private byte[] uImage = null;
    private ProgressDialog pd;

    //相册请求码
    private static final int ALBUM_REQUEST_CODE = 1;
    //相机请求码
    private static final int CAMERA_REQUEST_CODE = 2;
    //剪裁请求码pd
    private static final int CROP_REQUEST_CODE = 3;

    //调用照相机返回图片文件
    private File tempFile;

    //图片Uri
    private Uri contentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_date_user_info);
        initActivity();
    }

    private void initActivity() {
        btn_backToUserInfo = (Button) findViewById(R.id.backto_userinfo);
        tv_updateUserInfoFinish = (TextView) findViewById(R.id.update_finish_userinfo);
        //rv_updateUserInfoView = (RecyclerView) findViewById(R.id.userinfo_layout_update);
        iv_portrait = (ImageView) findViewById(R.id.up_user_headportrait);
        et_Uid = (TextView) findViewById(up_user_id);
        et_Uname = (EditText) findViewById(R.id.up_user_name);
        et_Usex = (EditText) findViewById(R.id.up_user_sex);
        et_Uqq = (EditText) findViewById(R.id.up_user_qq);
        et_Uweixin = (EditText) findViewById(R.id.up_user_weixin);
        et_Uphone = (EditText) findViewById(R.id.up_user_phone);
        pd = new ProgressDialog(this);
        btn_backToUserInfo.setOnClickListener(this);
        tv_updateUserInfoFinish.setOnClickListener(this);
        et_Uid.setOnClickListener(this);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
//        rv_updateUserInfoView.setLayoutManager(linearLayoutManager);//设置布局管理器

        DisplayUserInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backto_userinfo:
                Intent backToUserinfoIntent = new Intent();
                setResult(RESULT_OK,backToUserinfoIntent);
                finish();
                break;
            case R.id.update_finish_userinfo:
                //等待动画
                //设置对话框显示内容
                pd.setMessage("正在修改...");
                //显示进度条对话框
                pd.show();
                UpdateUserInfo();
                break;
            case R.id.up_user_headportrait:
                setHeadportrait();
                break;
            case up_user_id:
                Toast.makeText(this, "账号无法修改", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void DisplayUserInfo() {
        Intent intent = getIntent();
        if (intent!=null){
            Bundle bundle = intent.getExtras();
            if (bundle!=null){
                userBO= (UserBO) bundle.getSerializable("user");
                if(userBO!=null){
                    //图片的操作
                    if (userBO.getUimage() != null) {
                        byte[] headportrait = userBO.getUimage();
                        Bitmap headportrait_bitmap = BitmapFactory.decodeByteArray(headportrait, 0, headportrait.length, null);
                        iv_portrait.setImageBitmap(headportrait_bitmap);
                    } else {
                        iv_portrait.setImageResource(R.drawable.heizi);
                    }
                    et_Uname.setText(userBO.getUname());
                    et_Uid.setText(userBO.getUid());
                    String sex;
                    if (userBO.getSex()==1){
                        sex = "男";
                    }else{
                        sex = "女";
                    }
                    et_Usex.setText(sex);
                    et_Uqq.setText(userBO.getQq());
                    et_Uweixin.setText(userBO.getWeixin());
                    et_Uphone.setText(userBO.getUphone());
//                    //创建适配器
//                    Log.i("userInfo",userBO.toString());
//                    UpdateMyRecyclerAdapter myUpdateMyRecyclerAdapter = new UpdateMyRecyclerAdapter(UpDateUserInfoActivity.this,userBO);
//                    Log.i("userInfo","创建适配器完成");
//                    //加载适配器
//                    rv_updateUserInfoView.setAdapter(myUpdateMyRecyclerAdapter);
//                    Log.i("userInfo","加载适配器完成");
                }else{
                    Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 发送修改用户信息请求的方法
     */
    private void UpdateUserInfo() {
        String url = "http://47.105.174.254:8080/FleaMarketProj/user_info";
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        String userToken = sharedPreferences.getString("UserToken", "");

        String uid = userBO.getUid();
        String uname = et_Uname.getText().toString();
        String uphone = et_Uphone.getText().toString();
        int sex;
        if (et_Usex.getText().toString().equals("男")){
            sex = 1;
        }else{
            sex = 0;
        }
        String qq = et_Uqq.getText().toString();
        String weixin = et_Uweixin.getText().toString();

        UserBO user = new UserBO();
        if (uImage==null){
            user.setUimage(userBO.getUimage());
        }else{
            user.setUimage(uImage);
        }
        user.setOpType(60002);//60002为修改用户信息码
        user.setToken(userToken);
        user.setUid(uid);
        user.setUname(uname);
        user.setUphone(uphone);
        user.setSex(sex);
        user.setQq(qq);
        user.setWeixin(weixin);
        Gson gson = new Gson();
        String reqJson = gson.toJson(user, UserBO.class);//对象转字符串，用于发送请求
        reqJson = EncoderAndDecoderUtils.enCoder(reqJson);


        RequestUtils.PostRequestWithOkHttp(url, reqJson, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("userInfo", "数据获取失败！！" + e.toString());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("userInfo", "数据获取成功！！");
                    Log.i("userInfo", "response.code==" + response.code());

                    final String responseStr = response.body().string();
                    Log.i("userInfo", "response.body().String()==" + responseStr);
                    Gson gson = new Gson();
                    final Response responseBO = gson.fromJson(responseStr, Response.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBO.flag == 200) {
                                Log.i("userInfo","解析数据完成");
                                Toast.makeText(UpDateUserInfoActivity.this, responseBO.message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UpDateUserInfoActivity.this, responseBO.message, Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();//等待条消失
                            userInfoActivty.finish();
                            Intent baockToUserinfo = new Intent(UpDateUserInfoActivity.this, UserInfoActivty.class);
                            startActivity(baockToUserinfo);
                            finish();
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取相机相册图片
     */
    private void setHeadportrait() {
        Log.i("uerinfo", "in register:点击弹出对话框");
        new Dialogchoosephoto(UpDateUserInfoActivity.this) {
            @Override
            public void btnPickByTake() {
                //用于保存调用相机拍照后所生成的文件

                Log.i("uerinfo", "in register:点击选择调用相机");
                tempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
                //判断版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //如果在Android7.0以上,使用FileProvider获取Uri
                    contentUri = FileProvider.getUriForFile(UpDateUserInfoActivity.this, "com.ghl.bottomview.fileprovider", tempFile);
                } else {    //否则使用Uri.fromFile(file)方法获取Uri
                    contentUri = Uri.fromFile(tempFile);
                }

                //跳转到调用系统相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                Log.i("uerinfo", "in register:准备进入相机回调方法");
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }

            @Override
            public void btnPickBySelect() {
                Log.i("uerinfo", "in register:点击选择调用相册");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("image/*");
                Log.i("uerinfo", "in register:准备进入相册回调方法");
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
                Log.i("uerinfo", "in register:进入调用相机回调方法");
                if (resultCode == RESULT_OK) {
                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        contentUri = FileProvider.getUriForFile(UpDateUserInfoActivity.this, "com.ghl.bottomview.fileprovider", tempFile);
                        Log.i("uerinfo", "in register:调用相机后准备裁剪图片");
                        cropPhoto(contentUri);//裁剪图片
                    } else {
                        contentUri = Uri.fromFile(tempFile);
                        Log.i("uerinfo", "in register:调用相机后准备裁剪图片");
                        cropPhoto(contentUri);//裁剪图片
                    }
                }
                break;
            //调用相册后返回
            case ALBUM_REQUEST_CODE:
                Log.i("uerinfo", "in register:进入调用相册回调方法");
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    Log.i("uerinfo", "in register:调用相册后准备裁剪图片");
                    cropPhoto(uri);//裁剪图片
                }
                break;
            //调用剪裁后返回
            case CROP_REQUEST_CODE:
                Log.i("uerinfo", "in register:进入裁剪图片回调方法");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    //在这里获得了剪裁后的Bitmap对象，可以用于上传
                    Bitmap image = bundle.getParcelable("data");
                    // Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream());
                    //设置到ImageView上
                    Log.i("uerinfo", "准备显示图片");
                    //UpUserHeadportrait.setImageBitmap(image);

                    Log.i("saveImage","开始保存图片");

//                    //将图片保存到SD卡
//                    ImageUtil.saveImageInSd(image,Bitmap.CompressFormat.JPEG, 100,this);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    uImage = baos.toByteArray();

                    //也可以进行一些保存、压缩等操作后上传
                    //ImageUtil.saveImage("userHeader", image);
//                    File file = new File(path);
                    /*
                    *上传文件的操作
                    */

                }
                break;
        }
    }

    /* 裁剪图片 */
    private void cropPhoto(Uri uri) {
        Log.i("uerinfo", "in register:开始裁剪图片");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        Log.i("uerinfo", "in register:准备进入裁剪图片回调方法");
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }
}
