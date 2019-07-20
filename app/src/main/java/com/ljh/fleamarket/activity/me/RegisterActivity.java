package com.ljh.fleamarket.activity.me;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.find.AddSaleActivity;
import com.ljh.fleamarket.adapter.Dialogchoosephoto;
import com.ljh.fleamarket.bo.ResponseBO;
import com.ljh.fleamarket.bo.UserBO;
import com.ljh.fleamarket.utils.ImageUtil;
import com.ljh.fleamarket.utils.RequestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backto_login1;
    private Button register;
    private View backgroundview1;
    private EditText getUsername;
    private EditText getPassword;
    private EditText getPasswordAgain;
    private ImageView imageView;
    private String icon = "上传地址";
    private String userName;
    private String passWord;
    private String passWordAgain;
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
    //图片文件名
    private String fileName;

    private SharedPreferences sharedPreferences;


    private String TAG = "register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setInit();//初始化
    }

    /**
     * 初始化组件
     */
    private void setInit() {
        getUsername = (EditText) findViewById(R.id.et_username);
        getPassword = (EditText) findViewById(R.id.et_password);
        getPasswordAgain = (EditText) findViewById(R.id.et_password_again);
        backgroundview1 = findViewById(R.id.registerlayout);
        register = (Button) findViewById(R.id.btn_register);
        backto_login1 = (Button) findViewById(R.id.backto_login1);
        imageView = (ImageView) findViewById(R.id.headportrait);
        register.setOnClickListener(this);
        backto_login1.setOnClickListener(this);
        imageView.setOnClickListener(this);

        //设置背景透明度
        backgroundview1.getBackground().setAlpha(50);
    }

    /**
     * 重写点击事件方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //点击注册按钮
            case R.id.btn_register:
                toRegiste();
                break;

            //点击返回键
            case R.id.backto_login1:
                Intent int_backtolobgin = new Intent();
                setResult(RESULT_OK, int_backtolobgin);
                finish();
                break;

            //点击上传头像
            case R.id.headportrait:
                //动态获取运行时权限，调用相机相册设置头像
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    } else if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    } else if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                    } else {
                        setHeadportrait();
                    }
                }
                break;
        }

    }


    /**
     * 注册方法
     */
    private void toRegiste() {
        //获取用户名密码
        userName = getUsername.getText().toString().trim();//去除空格，获取用户名
        passWord = getPassword.getText().toString().trim();//去除空格，获取密码
        passWordAgain = getPasswordAgain.getText().toString().trim();
        sharedPreferences = getSharedPreferences("Userinfo",MODE_PRIVATE);

        //获取用户拍照生成的头像
        SharedPreferences getUserImageSp=getSharedPreferences("userHeader",MODE_PRIVATE);
        String getUserHeadStr = getUserImageSp.getString("userHeader","");
        byte [] userHeadByte=null;
        if (getUserHeadStr.length()!=0||getUserHeadStr!=null){
            //将字符串转为字节数组
            userHeadByte = Base64.decode(getUserHeadStr.getBytes(), Base64.DEFAULT);
        }else{
            Toast.makeText(this, "用户头像为空！", Toast.LENGTH_SHORT).show();
        }


        //判断用户名、密码不能为空
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(RegisterActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            getUsername.requestFocus();//输入框失去焦点
            return;
        } else if (TextUtils.isEmpty(passWord)) {
            Toast.makeText(RegisterActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            getPassword.requestFocus();//输入框失去焦点
            return;
        } else if (TextUtils.isEmpty(passWordAgain)) {
            Toast.makeText(RegisterActivity.this, "确认密码不能为空！", Toast.LENGTH_SHORT).show();
            getPasswordAgain.requestFocus();//输入框失去焦点
            return;
        } else if (passWord.equals(passWordAgain)) {
            //保存用户信息
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("UserName", userName);
            editor.putString("PassWord", passWord);
            editor.apply();
            //Toast.makeText(RegisterActivity.this, "两次密码一致", Toast.LENGTH_SHORT).show();

            String url = "http://47.105.174.254:8080/FleaMarketProj/register";

            UserBO userBO = new UserBO();//用户类，自己创建
            userBO.setOpType(90001);//设置操作类型，90001为类型码，其他类型码查看接口文档
            userBO.setUname(userName);//将获取的用户名传给对象
            userBO.setUpassword(passWord);//将获取的密码传给对象

            //设置用户头像
            if(userHeadByte.length==0||userHeadByte==null){
                Log.i("register","存储用户头像的字节数组为空："+userHeadByte);
            }else{
                userBO.setUimage(userHeadByte);
            }

            Gson gson = new Gson();//创建Gson对象
            String jsonStr = gson.toJson(userBO, UserBO.class);//将对象转为字符串，发送请求时用到

            Log.i(TAG,"jsonStr:"+jsonStr);

            //等待动画
            pd = new ProgressDialog(this);
            pd.setMessage("正在注册...");
            pd.show();//显示等待
            //注册请求发送
            //registerRequestWithOkHttp();

            //封装后的请求方法
            RequestUtils.PostRequestWithOkHttp(url,jsonStr,new okhttp3.Callback(){

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG, "获取数据失败!!" + e.toString());
                    ConnetctFailed();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {//回调的方法执行在子线程。
                        Log.i(TAG, "获取数据成功!!");
                        Log.i(TAG, "response.code()==" + response.code());

                        final String responseStr = response.body().string();
                        Log.i(TAG, "response.body().string()==" + responseStr);

                        Gson gson = new Gson();
                        final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);
                        //ResponseBO为自己定义的类，目的是将发送请求后返回的response字符串重新转为对象
                        Log.i(TAG, responseBO.toString());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                if (responseBO.flag == 200) {
                                    Toast.makeText(RegisterActivity.this,responseBO.message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this,responseBO.message, Toast.LENGTH_SHORT).show();
                                }
                                returnLogin();//跳转到登录界面
                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 返回登录界面
     */
    private void returnLogin() {
        userName = getUsername.getText().toString().trim();//去除空格，获取用户名
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
        finish();
    }

    /**
     * 设置头像
     */
    private void setHeadportrait() {
        Log.i(TAG, "in register:点击弹出对话框");
        new Dialogchoosephoto(RegisterActivity.this) {
            @Override
            public void btnPickByTake() {
                //用于保存调用相机拍照后所生成的文件
                Log.i(TAG, "in register:点击选择调用相机");
                fileName = System.currentTimeMillis() + ".jpg";
                tempFile = new File(RegisterActivity.this.getExternalCacheDir(), fileName);//存储在项目缓存目录下
                try {
                    tempFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //存储在手机内存根目录下
//                tempFile = new File(Environment.getExternalStorageDirectory(), "/"+System.currentTimeMillis() + ".jpg");
//                if (!tempFile.getParentFile().exists()){
//                    tempFile.getParentFile().mkdirs();
//                }
                //判断版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //如果在Android7.0以上,使用FileProvider获取Uri
                    contentUri = FileProvider.getUriForFile(RegisterActivity.this, "com.ljh.activity.fileprovider ", tempFile);
                } else {                                                  //否则使用Uri.fromFile(file)方法获取Uri
                    contentUri = Uri.fromFile(tempFile);
                }
                //跳转到调用系统相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);  //保存图片
//                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                Log.i(TAG, "in register:准备进入相机回调方法");
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }

            @Override
            public void btnPickBySelect() {
                Log.i(TAG, "in register:点击选择调用相册");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                photoPickerIntent.setType("image/*");
                Log.i(TAG, "in register:准备进入相册回调方法");
                startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
            }
        }.show();
    }


//        //点击上传头像
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.i(TAG,"in register:点击弹出对话框");
//                new Dialogchoosephoto(RegisterActivity.this) {
//                    @Override
//                    public void btnPickByTake() {
//                        //用于保存调用相机拍照后所生成的文件
//
//                        Log.i(TAG,"in register:点击选择调用相机");
//                        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
//                        //tempFile = new File(getExternalCacheDir(),"head_image.jpg");
//
//                        //  /storage/emulated/0/1544353017346.jpg
//                        //  /storage/emulated/0/1544353017346.jpg
//                       //Log.i("8888","tempFile is :"+tempFile);
//
//
//                        //判断版本
//                        if (Build.VERSION.SDK_INT >= 24) {   //如果在Android7.0以上,使用FileProvider获取Uri
//                            //intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                            contentUri = FileProvider.getUriForFile(RegisterActivity.this, "com.ghl.bottomview.fileprovider", tempFile);
//
//                            //Log.e("dasd", contentUri.toString());
//                            //intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
//                        } else {    //否则使用Uri.fromFile(file)方法获取Uri
//                            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
//                            contentUri = Uri.fromFile(tempFile);
//                        }
//
//                        //跳转到调用系统相机
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT,contentUri);
//                        Log.i(TAG,"in register:准备进入相机回调方法");
//                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
//                    }
//
//                    @Override
//                    public void btnPickBySelect() {
//                        Log.i(TAG,"in register:点击选择调用相册");
//                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//                        //Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
//                        photoPickerIntent.setType("image/*");
//                        Log.i(TAG,"in register:准备进入相册回调方法");
//                        startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
//                    }
//                }.show();
//            }
//        });
//    }


    /**
     * 动态申请权限后的回调方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//成功申请相机权限，接下来申请读内存权限
                    if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    } else if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                    } else {
                        setHeadportrait();
                    }
                }else {
                    Toast.makeText(this, "You denied the CAMERA PERMISSION", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//成功申请读内存权限，接下来申请写内存权限
                    if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                    } else {
                        setHeadportrait();
                    }
                } else {
                    Toast.makeText(this, "You denied the READ_EXTERNAL_STORAGE PERMISSION", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//成功申请写内存权限，可以开始访问相机和相册
                    setHeadportrait();
                } else {
                    Toast.makeText(this, "You denied the WRITE_EXTERNAL_STORAGE PERMISSION", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /*回调接口*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            // 调用相机后返回
            case CAMERA_REQUEST_CODE:
                Log.i(TAG, "in register:进入调用相机回调方法");
                if (resultCode == RESULT_OK) {
                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        contentUri = FileProvider.getUriForFile(RegisterActivity.this, "com.ljh.activity.fileprovider", tempFile);
                        Log.i(TAG, "in register:调用相机后准备裁剪图片");
                        cropPhoto(tempFile, contentUri);//裁剪图片
                    } else {
                        contentUri = Uri.fromFile(tempFile);
                        Log.i(TAG, "in register:调用相机后准备裁剪图片");
                        cropPhoto(tempFile, contentUri);//裁剪图片
                    }
                }
                break;
            //调用相册后返回
            case ALBUM_REQUEST_CODE:
                Log.i(TAG, "in register:进入调用相册回调方法");
                if (resultCode == RESULT_OK) {
                    contentUri = intent.getData();
                    fileName = System.currentTimeMillis() + ".jpg";//为图片生成一个新的文件名，并在剪切后将其保存在项目缓存目录下
                    tempFile = new File(RegisterActivity.this.getExternalCacheDir(), fileName);
                    Log.i(TAG, "in register:调用相册后准备裁剪图片");
                    cropPhoto(tempFile, contentUri);//裁剪图片
                }
                break;
            //调用剪裁后返回
            case CROP_REQUEST_CODE:
                Log.i(TAG, "in register:进入裁剪图片回调方法");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    //在这里获得了剪裁后的Bitmap对象，可以用于上传
                    Bitmap image = bundle.getParcelable("data");
                    // Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream());
                    //设置到ImageView上
                    Log.i(TAG, "准备显示图片");
                    imageView.setImageBitmap(image);

//                    //image为拍照后生成的bitmap对象下面开始将其转为byte数组
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte [] userImage = baos.toByteArray();
//
//                    Log.i("register","即将存储在sp中的字节数组为："+userImage);
//                    //将图片字节数组转为字符串并保存在sp中
//                    String userHeaderImage = new String(Base64.encode(userImage,Base64.DEFAULT));
//                    Log.i("register","即将存储在sp中的由字节数组转换而来的字符串为："+userHeaderImage);
//
//                    sharedPreferences=getSharedPreferences("userHeader",MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("userHeader",userHeaderImage);
//                    editor.apply();

                }
                break;
        }
    }

    /* 裁剪图片 */
    private void cropPhoto(File tempFile, Uri uri) {
        Log.i(TAG, "in register:开始裁剪图片");
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));//将剪切后的图片保存在项目缓存目录下
        Log.i(TAG, "in register:准备进入裁剪图片回调方法");
        startActivityForResult(intent, CROP_REQUEST_CODE);
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
        if (isNetConnected(RegisterActivity.this)==false){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "网络不给力哦!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "服务器出错啦，请稍后再试!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }



}