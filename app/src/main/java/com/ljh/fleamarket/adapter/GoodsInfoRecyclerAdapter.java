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
import com.ljh.fleamarket.bo.Goods;


public class GoodsInfoRecyclerAdapter extends RecyclerView.Adapter <GoodsInfoRecyclerAdapter.ViewHolder>{

    private Context context;
    private Goods goods;

    public GoodsInfoRecyclerAdapter(Context context, Goods goods) {
        this.context = context;
        this.goods = goods;
    }

    @Override
    public GoodsInfoRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("userInfo","加载适配器的Item");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goodsinfo_recyclerview_item,parent,false);
        final GoodsInfoRecyclerAdapter.ViewHolder holder = new GoodsInfoRecyclerAdapter.ViewHolder(view);
        /**
         * 点击事件处理
         */
        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_goodsImage;
        TextView tv_goodsName;
        TextView tv_goodsPrice;
        TextView tv_goodsUnit;
        TextView tv_goodsQuantity;
        TextView tv_goodsType;
        TextView tv_userId;
        TextView tv_contact;
        TextView tv_description;
        public ViewHolder(View itemView) {
            super(itemView);
            //获取控件实例
            iv_goodsImage = itemView.findViewById(R.id.iv_goods);
            tv_goodsName = itemView.findViewById(R.id.goods_name);
            tv_goodsPrice = itemView.findViewById(R.id.goods_price);
            tv_goodsUnit = itemView.findViewById(R.id.goods_unit);
            tv_goodsQuantity = itemView.findViewById(R.id.goods_quantity);
            tv_goodsType = itemView.findViewById(R.id.goods_type);
            tv_userId = itemView.findViewById(R.id.user_id);
            tv_contact = itemView.findViewById(R.id.contact);
            tv_description = itemView.findViewById(R.id.description);
        }
    }

    @Override
    public void onBindViewHolder(GoodsInfoRecyclerAdapter.ViewHolder holder, int position) {
        //将控件与数据绑定
        Bitmap bitmap = BitmapFactory.decodeByteArray(goods.getGoodsImg(), 0, goods.getGoodsImg().length, null);
        holder.iv_goodsImage.setImageBitmap(bitmap);
        holder.tv_goodsName.setText(goods.getGoodsName());
        holder.tv_goodsPrice.setText(String.valueOf(goods.getPrice()));
        holder.tv_goodsUnit.setText(goods.getUnit());
        holder.tv_goodsQuantity.setText(String.valueOf(goods.getQuality()));
        holder.tv_goodsType.setText(goods.getGoodsType());
        holder.tv_userId.setText(goods.getUserid());
        holder.tv_contact.setText(goods.getContact());
        holder.tv_description.setText(goods.getDescription());

    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
