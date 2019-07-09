package com.ljh.fleamarket.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.find.AddBuyActivity;
import com.ljh.fleamarket.activity.find.AddSaleActivity;


public class FindFragment extends Fragment implements View.OnClickListener {

    private Button sell_button;
    private Button buy_button;
    private Button add_sell_buy;
    private int flag;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    // 只用来指定此fragment的布局
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_find,container,false);
        return view;

    }


    //视图控件请在这里实例化
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        /**
         *  摊位按钮
         */
        sell_button = (Button)getActivity().findViewById(R.id.sell);
        sell_button.setTextColor(Color.parseColor("#13fb03"));
        sell_button.setEnabled(false);
        sell_button.setOnClickListener(this);

        //求购按钮
        buy_button = (Button)getActivity().findViewById(R.id.buy);
        //设置按钮的默认选择状态
        buy_button.setOnClickListener(this);

        //添加摊位或求购
        add_sell_buy = getActivity().findViewById(R.id.add_sell_buy);
        add_sell_buy.setOnClickListener(this);

        //设置默认的页面
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.find_main_layout,new SaleFragment());
        fragmentTransaction.commit();

        //pullToRefreshAndPushToLoadView = (PullToRefreshAndPushToLoadView)getActivity().findViewById(R.id.prpt_goods);
        //Log.e("pullToRefresh", String.valueOf(pullToRefreshAndPushToLoadView));
//        recycler();

//        recyclerView = (RecyclerView)getActivity().findViewById(R.id.recycle_view_goods);
//        Log.e("recyclerView", String.valueOf(recyclerView));
//        recyclerView.setHasFixedSize(true);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);//设置布局，默认也是垂直布局
//
//        recyclerView.setLayoutManager(linearLayoutManager);
//
////        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(), Color.DKGRAY,2,2);
////        dividerItemDecoration.setDrawBorderTopAndBottom(true);
////        recyclerView.addItemDecoration(dividerItemDecoration);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//
//        DisplayGoods();

//        pullToRefreshAndPushToLoadView.setOnRefreshAndLoadMoreListener(new PullToRefreshAndPushToLoadView.PullToRefreshAndPushToLoadMoreListener() {
//            @Override
//            public void onRefresh() {
//                refresh();
//            }
//
//            @Override
//            public void onLoadMore() {
//                load();
//            }
//        }, 0);
    }

    /**
     *点击事件
     */
    @Override
    public void onClick(View v) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.sell:
                flag = 0;
                sell_button.setEnabled(false);
                sell_button.setTextColor(Color.parseColor("#13fb03"));
                buy_button.setEnabled(true);
                buy_button.setTextColor(Color.BLACK);
                fragmentTransaction.replace(R.id.find_main_layout,new SaleFragment());
                break;
            case R.id.buy:
                flag = 1;
                buy_button.setEnabled(false);
                buy_button.setTextColor(Color.parseColor("#13fb03"));
                sell_button.setEnabled(true);
                sell_button.setTextColor(Color.BLACK);
                fragmentTransaction.replace(R.id.find_main_layout,new BuyFragment());
                break;
            case R.id.add_sell_buy:
                if (flag == 0) {
                    Intent intent_sale = new Intent(getActivity(), AddSaleActivity.class);
                    startActivity(intent_sale);
                } else if (flag == 1) {
                    Intent intent_buy = new Intent(getActivity(), AddBuyActivity.class);
                    startActivity(intent_buy);
                } else
                    break;
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }

//    private void list(){
////        items=new String[]{};
//        ListView listView = new ListView(getActivity());
//        ViewGroup.MarginLayoutParams mlp=new ViewGroup.MarginLayoutParams(-1,-1);
////        mlp.leftMargin=100;
//        listView.setLayoutParams(mlp);
//        pullToRefreshAndPushToLoadView.addView(listView);
////        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.item, items));
//        listView.setAdapter(new MyLVAdapter(items,getActivity()));
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(),"===setOnItemClickListener======"+position,Toast.LENGTH_SHORT).show();
//            }
//        });
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(),"===setOnItemLongClickListener======"+position,Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
//    }
//
//    private void grid(){
//        GridView gridView = new GridView(getActivity());
//        gridView.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
//        gridView.setNumColumns(3);
//        pullToRefreshAndPushToLoadView.addView(gridView);
//        gridView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item, items));
//    }

//    private View recycler(){
//        RecyclerView recyclerView = (RecyclerView)getActivity().findViewById(R.id.recycle_view_goods);
//        items=new String[]{};
////        MyAdapter myAdapter = new MyAdapter(items,getContext());
//
//        Log.e("fragment3","创建适配器完成");
////        recyclerView.setAdapter(myAdapter);
//        Log.e("fragment3","加载适配器完成");
//        recyclerView.setHasFixedSize(true);
////        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
//        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(), Color.DKGRAY,2,2);
//        dividerItemDecoration.setDrawBorderTopAndBottom(true);
//        recyclerView.addItemDecoration(dividerItemDecoration);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//
//
//        return recyclerView;
//    }


    /**
     * refresh
     */
//    private void refresh() {
////        new Thread() {
////            @Override
////            public void run() {
////                super.run();
////                try {
////                    Thread.sleep(30 * 1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////                pullToRefreshAndPushToLoadView.finishRefreshing();
////            }
////        }.start();
//
//        RefreshGoods();
//    }


    /**
     * load
     */
//    private void load() {
////        new Thread() {
////            @Override
////            public void run() {
////                super.run();
////                try {
////                    Thread.sleep(10 * 1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////                pullToRefreshAndPushToLoadView.finishLoading();
////            }
////        }.start();
//
//        LoadingMoreGoods();
//    }



//    class MyLVAdapter extends BaseAdapter {
//
//        private String[] datas;
//        private Context mContext;
//        public MyLVAdapter(String[] datas,Context mContext){
//            this.datas=datas;
//            this.mContext=mContext;
//        }
//
//
//        @Override
//        public int getCount() {
//            return datas.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return datas[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder=null;
//            if(convertView==null){
//                convertView=LayoutInflater.from(mContext).inflate(R.layout.refresh_item,null);
//                viewHolder=new ViewHolder();
//                viewHolder.tv= (TextView) convertView.findViewById(R.id.tv);
//                convertView.setTag(viewHolder);
//            }else{
//                viewHolder= (ViewHolder) convertView.getTag();
//            }
//            viewHolder.tv.setText(datas[position]);
//            viewHolder.tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(getActivity(),"onClick====="+position,Toast.LENGTH_SHORT).show();
//                }
//            });
//            viewHolder.tv.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    Toast.makeText(getActivity(),"onLongClick====="+position,Toast.LENGTH_SHORT).show();
//                    new AlertDialog.Builder(getActivity()).setTitle("hello").setMessage(position+":"+items[position]).setNegativeButton("cancel",null).show();
//                    return true;
//                }
//            });
//            return convertView;
//        }
//
//    }

//    static class ViewHolder{
//        TextView tv;
//    }

//    class MyAdapter extends RecyclerView.Adapter< MyAdapter.MyViewHolder> {
//
//        private String[] datas;
//        private Context mContext;
//        public MyAdapter(String[] datas,Context mContext){
//            this.datas=datas;
//            this.mContext=mContext;
//        }
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.refresh_item, parent,
//                    false));
//        }
//
//        @Override
//        public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
//            holder.tv.setText(datas[position]);
//            holder.tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(getActivity(),"onClick====="+holder.getAdapterPosition(),Toast.LENGTH_SHORT).show();
//                }
//            });
//            holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    Toast.makeText(getActivity(),"onLongClick====="+holder.getAdapterPosition(),Toast.LENGTH_SHORT).show();
//                    new AlertDialog.Builder(getActivity()).setTitle("hello").setMessage(holder.getAdapterPosition()+":"+items[holder.getAdapterPosition()]).setNegativeButton("cancel",null).show();
//                    return true;
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return datas.length;
//        }
//
//        class MyViewHolder extends RecyclerView.ViewHolder {
//            TextView tv;
//
//            public MyViewHolder(View view) {
//                super(view);
//                tv = (TextView) view.findViewById(R.id.tv);
//            }
//        }
//    }


    /**
     * 查询并展示商品信息
     */
//    private void DisplayGoods() {
//        final ProgressDialog pd = new ProgressDialog(getActivity());
//        pd.setMessage("正在刷新...");
//        pd.show();
//
//        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS|MODE_ENABLE_WRITE_AHEAD_LOGGING|MODE_APPEND);
//        userToken = sharedPreferences.getString("UserToken","");
//
//        Log.i("goods", "查询得到的userToken:"+userToken);
//
//        //String url = "http://118.89.217.225:8080/Proj20/buy";
//        //192.168.43.167:8080/FleaMarketProj/buy_refresh_goods?&reqJson={token: 6ef232aef0b547b7b527c9bcfbb6cbfc,pageNumber:4,pageSize:1,opType:90004}
//
//        String url = "http://192.168.43.167:8080/FleaMarketProj/buy_refresh_goods";
//        SearchBO searchBO = new SearchBO();
//        searchBO.setOpType(90004);
//        //searchBO.setToken(userToken);
//
//        searchBO.setToken("49637f86af37454494608648cd783b8c");
//        searchBO.setPageNumber(1);
//        searchBO.setPageSize(5);
//
//        Gson gson = new Gson();
//        String reqJson = gson.toJson(searchBO, SearchBO.class);
//        Log.i("goods", "reqJson" + reqJson);
//
//        Log.i("goods", "开始发送查询请求。。。");
//
//        RequestUtils.PostRequestWithOkHttp(url,reqJson,new okhttp3.Callback(){
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.i("goods", "数据获取失败！！" + e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Log.i("goods", "数据获取成功！！");
//                    Log.i("goods", "response.code==" + response.code());
//
//                    final String responseStr = response.body().string();
//                    Log.i("goods", "response.body().String()==" + responseStr);
//                    Gson gson = new Gson();
//                    final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);//仅仅解析出flag和message，用于判断是否查询成功
//                    Log.i("goods", "responseBO:" + responseBO.toString());
//
//                    final ResponseBuy responseBuy = gson.fromJson(responseStr, ResponseBuy.class);//将所有信息都解析出来
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pd.dismiss();
//                            if (responseBO.flag == 200) {
//                                //获取解析后生成goodslist
//                                List <Goods> resultGoodsList =responseBuy.getGoodsList();
//                                //创建适配器
//                                MyGoodsRecyclerAdapter recyclerAdapter = new MyGoodsRecyclerAdapter(getActivity(),resultGoodsList);
//                                //视图加载适配器
//                               recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
//                                Toast.makeText(getActivity(), "刷新成功!!", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(getActivity(), "刷新失败!!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//
//        });
////        //创建OkHttpClient实例对象
////        OkHttpClient client = new OkHttpClient();
////
////        RequestBody requestBody = new FormBody.Builder()
////                .add("reqJson", reqJson)
////                .build();
////
////        Request request = new Request.Builder()
////                .url(url)
////                .post(requestBody)
////                .build();
////
////        client.newCall(request).enqueue(new Callback() {
////            @Override
////            public void onFailure(Call call, IOException e) {
////                Log.i("goods", "数据获取失败！！" + e.toString());
////            }
////
////            @Override
////            public void onResponse(Call call, Response response) throws IOException {
////                if (response.isSuccessful()) {
////                    Log.i("goods", "数据获取成功！！");
////                    Log.i("goods", "response.code==" + response.code());
////
////                    final String responseStr = response.body().string();
////                    Log.i("goods", "response.body().String()==" + responseStr);
////                    Gson gson = new Gson();
////                    final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);//仅仅解析出flag和message，用于判断是否查询成功
////                    Log.i("goods", "responseBO:"+responseBO.toString());
////
////                    final ResponseBuy responseBuy = gson.fromJson(responseStr,ResponseBuy.class);//将所有信息都解析出来
////
////
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            pd.dismiss();
////                            if (responseBO.flag == 200) {
////                                /**
////                                 * 显示商品信息
////                                 */
////                                //获取解析后生成goodslist
////                                List <Goods> resultGoodsList =responseBuy.getGoodsList();
////                                //创建适配器
////                                MyGoodsRecyclerAdapter recyclerAdapter = new MyGoodsRecyclerAdapter(MySaleActivity.this,resultGoodsList);
////                                //视图加载适配器
////                                listMySale.setAdapter(recyclerAdapter);//设置Adapter(适配器)
////                                Toast.makeText(MySaleActivity.this, "刷新成功!!", Toast.LENGTH_SHORT).show();
////                            } else {
////                                Toast.makeText(MySaleActivity.this, "刷新失败!!", Toast.LENGTH_SHORT).show();
////                            }
////                        }
////                    });
////                }
////
////            }
////
////
////        });
////
//    }

    /**
     * 刷新商品信息
     */
//    private List <Goods> RefreshGoods() {
//        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS|MODE_ENABLE_WRITE_AHEAD_LOGGING|MODE_APPEND);
//        userToken = sharedPreferences.getString("UserToken","");
//
//        Log.i("goods", "查询得到的userToken:"+userToken);
//
//        //String url = "http://118.89.217.225:8080/Proj20/buy";
//        //192.168.43.167:8080/FleaMarketProj/buy_refresh_goods?&reqJson={token: 6ef232aef0b547b7b527c9bcfbb6cbfc,pageNumber:4,pageSize:1,opType:90004}
//
//        String url = "http://192.168.43.167:8080/FleaMarketProj/buy_refresh_goods";
//        SearchBO searchBO = new SearchBO();
//        searchBO.setOpType(90004);
//        //searchBO.setToken(userToken);
//
//        searchBO.setToken("49637f86af37454494608648cd783b8c");
//        searchBO.setPageNumber(1);
//        searchBO.setPageSize(5);
//
//        Gson gson = new Gson();
//        String reqJson = gson.toJson(searchBO, SearchBO.class);
//        Log.i("goods", "reqJson" + reqJson);
//
//        Log.i("goods", "开始发送查询请求。。。");
//
//        RequestUtils.PostRequestWithOkHttp(url,reqJson,new okhttp3.Callback(){
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.i("goods", "数据获取失败！！" + e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Log.i("goods", "数据获取成功！！");
//                    Log.i("goods", "response.code==" + response.code());
//
//                    final String responseStr = response.body().string();
//                    Log.i("goods", "response.body().String()==" + responseStr);
//                    Gson gson = new Gson();
//                    final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);//仅仅解析出flag和message，用于判断是否查询成功
//                    Log.i("goods", "responseBO:" + responseBO.toString());
//
//                    final ResponseBuy responseBuy = gson.fromJson(responseStr, ResponseBuy.class);//将所有信息都解析出来
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (responseBO.flag == 200) {
//                                //获取解析后生成goodslist
//                                resultGoodsList =responseBuy.getGoodsList();
//                                Log.i("goods","goodslist.size():"+String.valueOf(resultGoodsList.size()));
//                                //创建适配器
//                //////                recyclerAdapter = new MyGoodsRecyclerAdapter(getActivity(),resultGoodsList);
//                                //视图加载适配器
//                /////                recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
//                                Toast.makeText(getActivity(), "刷新成功!!", Toast.LENGTH_SHORT).show();
//                                //pullToRefreshAndPushToLoadView.finishLoading();
//                            } else {
//                                Toast.makeText(getActivity(), "刷新失败!!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//
//        });
//
//        return resultGoodsList;
//    }

    /**
     * 加载更多商品信息
     */
//    private List <Goods> LoadingMoreGoods() {
//        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS|MODE_ENABLE_WRITE_AHEAD_LOGGING|MODE_APPEND);
//        userToken = sharedPreferences.getString("UserToken","");
//
//        Log.i("goods", "查询得到的userToken:"+userToken);
//
//        //String url = "http://118.89.217.225:8080/Proj20/buy";
//        //192.168.43.167:8080/FleaMarketProj/buy_refresh_goods?&reqJson={token: 6ef232aef0b547b7b527c9bcfbb6cbfc,pageNumber:4,pageSize:1,opType:90004}
//        pageNumber++;
//        String url = "http://192.168.43.167:8080/FleaMarketProj/buy_loadmore_goods";
//        SearchBO searchBO = new SearchBO();
//        searchBO.setOpType(90004);
//        //searchBO.setToken(userToken);
//
//        searchBO.setToken("49637f86af37454494608648cd783b8c");
//        searchBO.setPageNumber(pageNumber);
//        searchBO.setPageSize(pageSize);
//
//        Gson gson = new Gson();
//        String reqJson = gson.toJson(searchBO, SearchBO.class);
//        Log.i("goods", "reqJson" + reqJson);
//
//        Log.i("goods", "开始发送查询请求。。。");
//
//        RequestUtils.PostRequestWithOkHttp(url,reqJson,new okhttp3.Callback(){
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.i("goods", "数据获取失败！！" + e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Log.i("goods", "数据获取成功！！");
//                    Log.i("goods", "response.code==" + response.code());
//
//                    final String responseStr = response.body().string();
//                    Log.i("goods", "response.body().String()==" + responseStr);
//                    Gson gson = new Gson();
//                    final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);//仅仅解析出flag和message，用于判断是否查询成功
//                    Log.i("goods", "responseBO:" + responseBO.toString());
//
//                    final ResponseBuy responseBuy = gson.fromJson(responseStr, ResponseBuy.class);//将所有信息都解析出来
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (responseBO.flag == 200) {
//                                //获取解析后生成goodslist
//                                resultGoodsList =responseBuy.getGoodsList();
//                                Log.i("goods","goodslist.size():"+String.valueOf(resultGoodsList.size()));
//                                //创建适配器
//                                //MyGoodsRecyclerAdapter recyclerAdapter = new MyGoodsRecyclerAdapter(getActivity(),resultGoodsList);
//                                //视图加载适配器
//                                //recyclerView.setAdapter(recyclerAdapter);//设置Adapter(适配器)
//                                Toast.makeText(getActivity(), "加载成功!!", Toast.LENGTH_SHORT).show();
//                                //pullToRefreshAndPushToLoadView.finishLoading();
//                            } else {
//                                Toast.makeText(getActivity(), "加载失败!!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//
//        });
//        Log.i("goods","goodslist.size():"+String.valueOf(resultGoodsList.size()));
//        return resultGoodsList;
//    }

//    private Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what){
//                case 1:         //刷新加载
//                    List<Goods> mList  = (List<Goods>) msg.obj;
//                    mRefreshLayout.finishRefresh(true);
////                    adapter.setDatas(mList);
//                    //Log.i("goods","goodslist.size():"+String.valueOf(mList.size()));
//                    //创建适配器
//                    recyclerAdapter = new MyGoodsRecyclerAdapter(getActivity(),mList);
//                    //视图加载适配器
//                    recyclerView.setAdapter(recyclerAdapter);
//
//                    break;
//                case 2:         //加载更多
//                    List <Goods> mLoadMoreDatas = (List<Goods>) msg.obj;
//                    mRefreshLayout.finishLoadMore(true);
////                    adapter.addMoreValue(mLoadMoreDatas);
//                    //Log.i("goods","goodslist.size():"+String.valueOf(mLoadMoreDatas.size()));
//                    //创建适配器
//                    recyclerAdapter = new MyGoodsRecyclerAdapter(getActivity(),mLoadMoreDatas);
//                    //视图加载适配器
//                    recyclerView.setAdapter(recyclerAdapter);
//                    break;
//            }
//            return false;
//        }
//    });

//
//    // 只用来指定此fragment的布局
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view=inflater.inflate(R.layout.fragment_find,container,false);
//        return view;
//    }
//
//
//    //视图控件请在这里实例化
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        //初始化ListView
//        goodsList = getActivity().findViewById(R.id.goods_Display);
//
//        //摊位按钮
//        sell_button = (Button)getActivity().findViewById(R.id.find_sell);
//        sell_button.setOnClickListener(this);
//
//        //求购按钮
//        buy_button = (Button)getActivity().findViewById(R.id.find_buy);
//        //设置按钮的默认选择状态
//        buy_button.setEnabled(false);
//        buy_button.setTextColor(Color.parseColor("#04bdf0"));
//        buy_button.setOnClickListener(this);
//
//        //添加摊位或求购
//        add_sell_buy = getActivity().findViewById(R.id.add_sell_buy);
//        add_sell_buy.setOnClickListener(this);
//
//
//        //获取登录后返回的token
//        sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_NO_LOCALIZED_COLLATORS|MODE_ENABLE_WRITE_AHEAD_LOGGING|MODE_APPEND);
//        userToken = sharedPreferences.getString("UserToken","");
//
//        Log.i("goods", "查询得到的userToken:"+userToken);
//        DisplayGoods();
//    }
//
//    /**
//     *点击事件
//     */
//    @Override
//    public void onClick(View v) {
//        sell_button.setEnabled( true );
//        sell_button.setTextColor( Color.WHITE );
//        buy_button.setEnabled( true );
//        buy_button.setTextColor( Color.WHITE );
//        switch (v.getId()){
//            case R.id.find_sell:
//                flag = 0;
//                sell_button.setEnabled( false );
//                sell_button.setTextColor( Color.parseColor( "#04bdf0" ) );
//                break;
//            case R.id.find_buy:
//                flag = 1;
//                buy_button.setEnabled( false );
//                buy_button.setTextColor( Color.parseColor( "#04bdf0" ) );
//                break;
//            case R.id.add_sell_buy:
//                if(flag == 0){
//                    sell_button.setEnabled( false );
//                    sell_button.setTextColor( Color.parseColor( "#04bdf0" ) );
//                    Intent intent_sell = new Intent(getActivity(), AddSaleActivity.class );
//                    startActivity( intent_sell );
//                }
//                else if(flag == 1){
//                    buy_button.setEnabled( false );
//                    buy_button.setTextColor( Color.parseColor( "#04bdf0" ) );
//                    Intent intent_buy = new Intent( getActivity(), AddBuyActivity.class );
//                    startActivity( intent_buy );
//                }
//                else
//                    break;
//                break;
//
//            default:
//                break;
//        }
//
//
//
//    }
//
//    /**
//     * 查询并展示商品信息
//     */
//    private void DisplayGoods() {
//        final ProgressDialog pd = new ProgressDialog(getActivity());
//        pd.setMessage("正在刷新...");
//        pd.show();
//        Log.i("goods","userToken:"+userToken);
//
//        String url = "http://118.89.217.225:8080/Proj20/buy";
//
//        SearchBO searchBO = new SearchBO();
//        searchBO.setOpType(90004);
//        searchBO.setToken(userToken);
//
//        Gson gson = new Gson();
//        String reqJson = gson.toJson(searchBO, SearchBO.class);
//        Log.i("goods", "reqJson" + reqJson);
//
//        Log.i("goods", "开始发送查询请求。。。");
//        RequestUtils.PostRequestWithOkHttp(url,reqJson,new okhttp3.Callback(){
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.i("goods", "数据获取失败！！" + e.toString());
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            Log.i("goods", "数据获取成功！！");
//                            Log.i("goods", "response.code==" + response.code());
//
//                            final String responseStr = response.body().string();
//                            Log.i("goods", "response.body().String()==" + responseStr);
//                            Gson gson = new Gson();
//                            final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);//仅仅解析出flag和message，用于判断是否查询成功
//                            Log.i("goods", "responseBO:" + responseBO.toString());
//
//                            final ResponseBuy responseBuy = gson.fromJson(responseStr, ResponseBuy.class);//将所有信息都解析出来
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pd.dismiss();
//                                    if (responseBO.flag == 200) {
//                                        /**
//                                         * 显示商品信息
//                                         */
//                                        //获取解析后生成goodslist
//                                        List<Goods> resultGoodsList = responseBuy.getGoodsList();
//                                        //创建适配器
//                                        ArrayAdapter<Goods> adapter = new GoodsAdapter(getActivity(), R.layout.goods_listview_item, resultGoodsList);
//                                        //视图加载适配器
//                                        goodsList.setAdapter(adapter);
//                                        Toast.makeText(getActivity(), "刷新成功!!", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(getActivity(), "刷新失败!!", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//                    }
//        });
//        //创建OkHttpClient实例对象
////        OkHttpClient client = new OkHttpClient();
////
////        RequestBody requestBody = new FormBody.Builder()
////                .add("reqJson", reqJson)
////                .build();
////
////        Request request = new Request.Builder()
////                .url(url)
////                .post(requestBody)
////                .build();
//
////        client.newCall(request).enqueue(new Callback() {
////            @Override
////            public void onFailure(Call call, IOException e) {
////                Log.i("goods", "数据获取失败！！" + e.toString());
////            }
////
////            @Override
////            public void onResponse(Call call, Response response) throws IOException {
////                if (response.isSuccessful()) {
////                    Log.i("goods", "数据获取成功！！");
////                    Log.i("goods", "response.code==" + response.code());
////
////                    final String responseStr = response.body().string();
////                    Log.i("goods", "response.body().String()==" + responseStr);
////                    Gson gson = new Gson();
////                    final ResponseBO responseBO = gson.fromJson(responseStr, ResponseBO.class);//仅仅解析出flag和message，用于判断是否查询成功
////                    Log.i("goods", "responseBO:"+responseBO.toString());
////
////                    final ResponseBuy responseBuy = gson.fromJson(responseStr,ResponseBuy.class);//将所有信息都解析出来
//////                    Log.i("goods", "responseBuy:"+responseBuy.toString());
//////
//////                    Log.i("goods","responseBuy.getGoodsList().get(0):"+responseBuy.getGoodsList().get(0));
//////                    Log.i("goods","responseBuy.getGoodsList().get(0):"+responseBuy.getGoodsList().get(1));
//////                    Log.i("goods","responseBuy.getGoodsList().get(0):"+responseBuy.getGoodsList().get(2));
////
////                    getActivity().runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            pd.dismiss();
////                            if (responseBO.flag == 200) {
////                                /**
////                                 * 显示商品信息
////                                 */
////                                //获取解析后生成goodslist
////                                List <Goods> resultGoodsList =responseBuy.getGoodsList();
////                                //创建适配器
////                                ArrayAdapter <Goods> adapter = new GoodsAdapter(getActivity(),R.layout.goods_listview_item,resultGoodsList);
////                                //视图加载适配器
////                                goodsList.setAdapter(adapter);
////                                Toast.makeText(getActivity(), "刷新成功!!", Toast.LENGTH_SHORT).show();
////                            } else {
////                                Toast.makeText(getActivity(), "刷新失败!!", Toast.LENGTH_SHORT).show();
////                            }
////                        }
////                    });
////                }
////
////            }
//
//
// //       });
//    }
//
//
//
//    // 适配器 (这里没有加入速度的考虑,如果想加入请参考书，自已加入。）
//    public class  GoodsAdapter extends  ArrayAdapter <Goods> {
//        private int resourceId;
//
//        public GoodsAdapter(Context context,int textViewResourceId,List <Goods> objects) {
//            super(context, textViewResourceId, objects);
//            resourceId = textViewResourceId;
//        }
//        // 这里为简单明了，没有加入对速度优化的考虑
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            Goods goods = getItem(position);
//            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
//
//            //接下来将goods对象和属性和视图控对应起来
//            ImageView img_goodsImg = view.findViewById(R.id.goods_image);
//            TextView tv_goodsId = view.findViewById(R.id.goods_id);
//            TextView tv_goodsName = view.findViewById(R.id.goods_name);
//            TextView tv_goodsPrice = view.findViewById(R.id.goods_price);
//            TextView tv_goodsUnit = view.findViewById(R.id.goods_unit);
//            TextView tv_goodsQuantity = view.findViewById(R.id.goods_quality);
//            TextView tv_goodsType = view.findViewById(R.id.goods_type);
//
//            tv_goodsId.setText(goods.getGoodsID());
//            tv_goodsName.setText(goods.getGoodsName());
//            tv_goodsUnit.setText(goods.getUnit());
//            tv_goodsPrice.setText(""+goods.getPrice());
//            tv_goodsQuantity.setText(""+goods.getQuality());
//            tv_goodsType.setText(goods.getGoodsTypeName());
//
//            // 图片的操作
//            byte[] bytes = goods.getGoodsImg();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
//            img_goodsImg.setImageBitmap(bitmap);
//
//            return view;
//        }
//    }
}

