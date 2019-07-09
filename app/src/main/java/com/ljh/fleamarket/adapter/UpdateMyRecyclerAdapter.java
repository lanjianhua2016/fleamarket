package com.ljh.fleamarket.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.bo.UserBO;
import com.ljh.fleamarket.bo.UserVO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by ASUS on 2019/4/16.
 */

public class UpdateMyRecyclerAdapter extends RecyclerView.Adapter <UpdateMyRecyclerAdapter.ViewHolder>{
    private Context context;
    private UserBO userBO;

    public UpdateMyRecyclerAdapter(Context context, UserBO userBO) {
        this.context = context;
        this.userBO = userBO;
    }

    @Override
    public UpdateMyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("userInfo","加载适配器的Item");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_userinfo_recyview_item,parent,false);
        final UpdateMyRecyclerAdapter.ViewHolder holder = new UpdateMyRecyclerAdapter.ViewHolder(view);
        /**
         * 点击事件处理
         */
        return holder;
    }

    //将数据与组件进行绑定
    @Override
    public void onBindViewHolder(UpdateMyRecyclerAdapter.ViewHolder holder, int position) {
        Log.i("userInfo","将数据与组件进行绑定");
        //UserVO user = userVO;
        holder.UpUserID.setText(userBO.getUid());
        holder.UpUserName.setText(userBO.getUname());
        String sex = String.valueOf(userBO.getSex());
        holder.UpUserSex.setText(sex);
        holder.UpUserQQ.setText(userBO.getQq());
        holder.UpUserWeixin.setText(userBO.getWeixin());
        holder.UpUserPhone.setText(userBO.getUphone());

        //图片的操作
        byte [] headportrait = userBO.getUimage();
        Bitmap headportrait_bitmap = BitmapFactory.decodeByteArray(headportrait,0,headportrait.length,null);
        holder.UpUserHeadportrait.setImageBitmap(headportrait_bitmap);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    //实例化组件
    public class ViewHolder extends RecyclerView.ViewHolder {

        View userInfoView;
        ImageView UpUserHeadportrait;
        EditText UpUserID;
        EditText UpUserName;
        EditText UpUserSex;
        EditText UpUserQQ;
        EditText UpUserWeixin;
        EditText UpUserPhone;

        public ViewHolder(View itemView) {
            super(itemView);
            userInfoView = itemView;
            UpUserHeadportrait = itemView.findViewById(R.id.up_user_headportrait);
            UpUserID = itemView.findViewById(R.id.up_user_id);
            UpUserName = itemView.findViewById(R.id.up_user_name);
            UpUserSex = itemView.findViewById(R.id.up_user_sex);
            UpUserQQ = itemView.findViewById(R.id.up_user_qq);
            UpUserWeixin = itemView.findViewById(R.id.up_user_weixin);
            UpUserPhone = itemView.findViewById(R.id.up_user_phone);
        }
    }
}
