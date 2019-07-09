package com.ljh.fleamarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.find.GoodsInfoActivity;
import com.ljh.fleamarket.bo.Goods;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ASUS on 2019/4/4.
 */

public class MyGoodsRecyclerAdapter extends RecyclerView.Adapter <MyGoodsRecyclerAdapter.ViewHolder>{
    private Context context;
    private List <Goods> goodsList;

    public MyGoodsRecyclerAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View goodsView;
        ImageView img_goodsImg;
        TextView tv_goodsName;
        TextView tv_goodsPrice;
        TextView tv_goodsUnit;
        TextView tv_goodsQuantity;
        TextView tv_goodsType;
        public ViewHolder(View itemView) {
            super(itemView);
            goodsView = itemView;
            img_goodsImg = itemView.findViewById(R.id.goods_image);
            tv_goodsName = itemView.findViewById(R.id.goods_name);
            tv_goodsPrice = itemView.findViewById(R.id.goods_price);
            tv_goodsUnit = itemView.findViewById(R.id.goods_unit);
            tv_goodsQuantity = itemView.findViewById(R.id.goods_quality);
            tv_goodsType = itemView.findViewById(R.id.goods_type);
        }
    }

    @Override
    public MyGoodsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_recyclerview_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        /**
         * 设置点击事件
         */
        holder.goodsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Goods goods = goodsList.get(position);
//                Toast.makeText(v.getContext(), "you clicked :" + goods.getGoodsName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(),GoodsInfoActivity.class);
                Bundle bundle = new Bundle();
//                bundle.putByteArray("goodsImage",goods.getGoodsImg());
//                bundle.putString("goodsName",goods.getGoodsName());
//                bundle.putFloat("goodsPrice",goods.getPrice());
//                bundle.putFloat("goodsQuantity",goods.getQuality());
//                bundle.putString("goodsUnit",goods.getUnit());
//                bundle.putString("goodsType",goods.getGoodsType());
//                bundle.putString("userId",goods.getUserid());
//                bundle.putString("Contact",goods.getContact());
//                bundle.putString("Description",goods.getDescription());
                bundle.putSerializable("goods",goods);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        holder.tv_goodsName.setText(goods.getGoodsName());
        holder.tv_goodsUnit.setText(goods.getUnit());
        holder.tv_goodsPrice.setText(""+goods.getPrice());
        holder.tv_goodsQuantity.setText(""+goods.getQuality());
        holder.tv_goodsType.setText(goods.getGoodsType());

        // 图片的操作
        if (goods.getGoodsImg()!=null){
            byte[] bytes = goods.getGoodsImg();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            holder.img_goodsImg.setImageBitmap(bitmap);
        }

    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }



}
