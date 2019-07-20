package com.ljh.fleamarket.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.bo.UserBO;
import com.ljh.fleamarket.bo.UserVO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MyUserRecyclerAdapter extends RecyclerView.Adapter <MyUserRecyclerAdapter.ViewHolder> {
    private Context context;
    private UserBO userBO;

    public MyUserRecyclerAdapter(Context context, UserBO userBO) {
        this.context = context;
        this.userBO = userBO;
    }

    @Override
    public MyUserRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userinfo_recyclerview_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //将数据与组件进行绑定
    @Override
    public void onBindViewHolder(MyUserRecyclerAdapter.ViewHolder holder, int position) {
        Log.i("userInfo","将数据与组件进行绑定");
        Log.i("userInfo", "onBindViewHolder: "+userBO.toString());
        UserBO user = userBO;

        holder.userID.setText(user.getUid());
        holder.userName.setText(user.getUname());
        String sex;
        if (user.getSex()==1){
            sex = "男";
        }else{
            sex = "女";
        }
        holder.userSex.setText(sex);
        holder.userQQ.setText(user.getQq());
        holder.userWeixin.setText(user.getWeixin());
        holder.userPhone.setText(user.getUphone());

        if (userBO.getUimage() != null) {
            //图片的操作
            byte[] headportrait = userBO.getUimage();
            Bitmap headportrait_bitmap = BitmapFactory.decodeByteArray(headportrait, 0, headportrait.length, null);
            holder.userHeadportrait.setImageBitmap(headportrait_bitmap);
        } else {
            holder.userHeadportrait.setImageResource(R.drawable.heizi);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    //实例化组件
    public class ViewHolder extends RecyclerView.ViewHolder {

        View userInfoView;
        ImageView userHeadportrait;
        TextView userID;
        TextView userName;
        TextView userSex;
        TextView userQQ;
        TextView userWeixin;
        TextView userPhone;

        public ViewHolder(View itemView) {
            super(itemView);
            userInfoView = itemView;
            userHeadportrait = itemView.findViewById(R.id.user_headportrait);
            userID = itemView.findViewById(R.id.user_id);
            userName = itemView.findViewById(R.id.user_name);
            userSex = itemView.findViewById(R.id.user_sex);
            userQQ = itemView.findViewById(R.id.user_qq);
            userWeixin = itemView.findViewById(R.id.user_weixin);
            userPhone = itemView.findViewById(R.id.user_phone);
        }
    }
}
