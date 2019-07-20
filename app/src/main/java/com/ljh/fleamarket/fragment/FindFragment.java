package com.ljh.fleamarket.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;


import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.find.AddBuyActivity;
import com.ljh.fleamarket.activity.find.AddSaleActivity;
import com.ljh.fleamarket.activity.me.LoginActivity;
import com.ljh.fleamarket.utils.DataUtils;

import static android.content.Context.MODE_PRIVATE;


public class FindFragment extends Fragment implements View.OnClickListener {

    private Button sell_button;
    private Button buy_button;
    private Button add_sell_buy;
    private int flag;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DataUtils dataApp;

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
        dataApp = (DataUtils) getActivity().getApplication();
        dataApp.setIntentPermission(false);
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
    }

    /**
     *点击事件
     */
    @Override
    public void onClick(View v) {
        fragmentTransaction = fragmentManager.beginTransaction();
        if (dataApp.isIntentPermission()) {
            switch (v.getId()) {
                case R.id.sell:
                    flag = 0;
                    sell_button.setEnabled(false);
                    sell_button.setTextColor(Color.parseColor("#13fb03"));
                    buy_button.setEnabled(true);
                    buy_button.setTextColor(Color.BLACK);
                    fragmentTransaction.replace(R.id.find_main_layout, new SaleFragment());
                    break;
                case R.id.buy:
                    flag = 1;
                    buy_button.setEnabled(false);
                    buy_button.setTextColor(Color.parseColor("#13fb03"));
                    sell_button.setEnabled(true);
                    sell_button.setTextColor(Color.BLACK);
                    fragmentTransaction.replace(R.id.find_main_layout, new BuyFragment());
                    break;
                case R.id.add_sell_buy:
                    //判断token是否为空，为空则表示未登录状态，提示用户登录
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
                    String userToken = sharedPreferences.getString("UserToken", "");//获取用户token
                    if (userToken == "") {
                        Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                        Intent toLogin = new Intent(getActivity(), LoginActivity.class);
                        startActivity(toLogin);
                        getActivity().onBackPressed();//销毁当前的fragment
                    } else {
                        if (flag == 0) {
                            Intent intent_sale = new Intent(getActivity(), AddSaleActivity.class);
                            startActivity(intent_sale);
                        } else if (flag == 1) {
                            Intent intent_buy = new Intent(getActivity(), AddBuyActivity.class);
                            startActivity(intent_buy);
                        }
                    }
                    break;
                default:
                    break;
            }
            fragmentTransaction.commit();
        } else {
            Toast.makeText(getActivity(), "请稍等...", Toast.LENGTH_SHORT).show();
        }
    }
}

