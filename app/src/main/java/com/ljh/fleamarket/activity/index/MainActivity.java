package com.ljh.fleamarket.activity.index;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.adapter.BottomNavigationViewHelper;
import com.ljh.fleamarket.fragment.FindFragment;
import com.ljh.fleamarket.fragment.IndexFragment;
import com.ljh.fleamarket.fragment.MeFragment;
import com.ljh.fleamarket.fragment.SortFragment;
import com.ljh.fleamarket.utils.DataUtils;


public class MainActivity extends AppCompatActivity {

    private IndexFragment indexFragment;
    private SortFragment sortFragment;
    private FindFragment findFragment;
    private MeFragment meFragment;
    private Fragment [] fragments;
    private int lastfragment;//用于记录上个选择的Fragment
    private BottomNavigationView bottomNavigationView;
    private DataUtils appData;
    private long exitTime = 0;//用于判断退出应用程序的时间标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appData = (DataUtils) getApplication();
        appData.setIntentPermission(false);
        initFragment();
    }

    //初始化fragment和fragment数组
    private void initFragment(){

        Intent LoginInent = getIntent();
        String loginInfo = LoginInent.getStringExtra("loginInfo");
        Bundle bundle = new Bundle();
        bundle.putString("loginInfo",loginInfo);

        indexFragment = new IndexFragment();
        sortFragment = new SortFragment();
        findFragment = new FindFragment();
        meFragment = new MeFragment();
        fragments = new Fragment[]{indexFragment, sortFragment, findFragment, meFragment};
        lastfragment=0;

        getSupportFragmentManager().beginTransaction().replace(R.id.mainview, indexFragment).show(indexFragment).commit();
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        meFragment.setArguments(bundle);
    }

    //判断选择的菜单
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Log.i("test","item.getItemId() is :"+item.getItemId());

            if (appData.isIntentPermission()) {
                switch (item.getItemId())
                {
                    case R.id.navigation_home:
                    {
                        if (lastfragment != 0) {
                            switchFragment(lastfragment, 0);
                            lastfragment = 0;

                        }

                        return true;
                    }
                    case R.id.navigation_sort:
                    {
                        if (lastfragment != 1) {
                            switchFragment(lastfragment, 1);
                            lastfragment = 1;

                        }

                        return true;
                    }
                    case R.id.navigation_find:
                    {
                        if (lastfragment != 2) {
                            switchFragment(lastfragment, 2);
                            lastfragment = 2;

                        }

                        return true;
                    }
                    case R.id.navigation_me:
                    {
                        if (lastfragment != 3) {
                            switchFragment(lastfragment, 3);
                            lastfragment = 3;

                        }

                        return true;
                    }

                }
            } else {
                Toast.makeText(MainActivity.this, "请稍等...", Toast.LENGTH_SHORT).show();
            }


            return false;
        }

        private void switchFragment(int lastfragment, int index) {
            FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
            transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
            if(fragments[index].isAdded()==false)
            {
                transaction.add(R.id.mainview,fragments[index]);
            }
            transaction.show(fragments[index]).commitAllowingStateLoss();
        }
    };

    //按两次系统返回键退出应用程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


}


