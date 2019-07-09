package com.ljh.fleamarket.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.ljh.fleamarket.activity.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by ASUS on 2019/4/16.
 */

public class ImageUtil {

    //保存Bitmap
    public static String save(Bitmap bitmap, Bitmap.CompressFormat format, int quality, File desFile) {
        Log.i("saveImage", "666");
        try {
            FileOutputStream out = new FileOutputStream(desFile);
            Log.i("saveImage", "777");
            //if (bitmap.compress(format, quality, out)) {
            bitmap.compress(format, quality, out);
                out.flush();
                out.close();
                return desFile.getAbsolutePath();
           // }
            //Log.i("saveImage", "888");
//            if (bitmap != null && !bitmap.isRecycled()) {
//                bitmap.recycle();
//            }
            //Log.i("saveImage", "999");
//            return desFile.getAbsolutePath();
        }
// catch (FileNotFoundException e){
//            e.printStackTrace();
//            Log.i("saveImage","101010");
//    }
    catch (IOException e) {
            e.printStackTrace();
            Log.i("saveImage","1111111111");
        }
        return null;
    }

    //保存到SD卡
    public static String saveImageInSd(Bitmap  bitmap, Bitmap.CompressFormat format, int quality,Context context) {

        Log.i("saveImage","111");
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        Log.i("saveImage","222");
        File dir = new File(Environment.getExternalStorageDirectory()+
                "/"+context.getPackageName());
        Log.i("saveImage","333");
        if(!dir.exists()){
            dir.mkdir();
        }
        Log.i("saveImage","444");
        File desFile = new File(dir, UUID.randomUUID().toString());
        Log.i("saveImage","555");
        return save(bitmap,format,quality,desFile);
    }

/* 保存图片到本地*/
    public static String saveImage(String name, Bitmap bmp) {
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
     * 4.尺寸压缩（通过缩放图片像素来减少图片占用内存大小）
     *
     * @param bmp
     * @param file
     */

    public static void sizeCompress(Bitmap bmp, File file) {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        int ratio = 8;
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap compressRGB565(Bitmap mSrcBitmap,byte [] mSrcByte) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        mSrcBitmap = BitmapFactory.decodeByteArray(mSrcByte,0,mSrcByte.length,options);
        return  mSrcBitmap;
    }

    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

}
