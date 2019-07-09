package com.ljh.fleamarket.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.bo.Goods;

import java.util.List;

public class GoodsBuyAdapter extends RecyclerView.Adapter<GoodsBuyAdapter.ViewHolder> {
    private Context context;
    private List<Goods> goodsList;

    public GoodsBuyAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View goodsView;
        TextView tv_goodsName;
        TextView tv_goodsQuantity;
        TextView tv_goodsType;
        TextView tv_goodsContact;

        public ViewHolder(View itemView) {
            super(itemView);
            goodsView = itemView;
            tv_goodsName = itemView.findViewById(R.id.goods_name);
            tv_goodsQuantity = itemView.findViewById(R.id.goods_quality);
            tv_goodsType = itemView.findViewById(R.id.goods_type);
            tv_goodsContact = itemView.findViewById(R.id.goods_contact);
        }
    }

    @Override
    public GoodsBuyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_buy_item, parent, false);
        final GoodsBuyAdapter.ViewHolder holder = new GoodsBuyAdapter.ViewHolder(view);

        /**
         * 设置点击事件
         */
        holder.goodsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Goods goods = goodsList.get(position);
//                Intent intent = new Intent(v.getContext(), GoodsInfoActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("goods", goods);
//                intent.putExtras(bundle);
//                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(GoodsBuyAdapter.ViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        holder.tv_goodsName.setText(goods.getGoodsName());
        holder.tv_goodsQuantity.setText("" + goods.getQuality());
        holder.tv_goodsType.setText(goods.getGoodsType());
        holder.tv_goodsContact.setText(goods.getContact());

    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    //下面两个方法提供给页面刷新和加载时调用
    public void refresh(List<Goods> addList) {
        //增加数据
        int position = goodsList.size();
        goodsList.addAll(position, addList);
        notifyDataSetChanged();
    }
}