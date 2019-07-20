package com.ljh.fleamarket.activity.index;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ljh.fleamarket.activity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WelcomeActivity extends Activity {
    @BindView(R.id.pager_guide)
    ViewPager viewPager;
    @BindView(R.id.btn_guide)
    Button btn_guide;

    List<View> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //初始化绑定
        ButterKnife.bind(this);
        initViewPager();
        //设置适配器
        viewPager.setAdapter(new MyPagerAdapter());

        btn_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到主页
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    /**
     * 初始化ViewPager
     */

    private void initViewPager() {
        list = new ArrayList<View>();

        ImageView iv1 = new ImageView(this);
        iv1.setImageResource(R.drawable.welcome1);
        iv1.setScaleType(ImageView.ScaleType.FIT_XY);

        ImageView iv2 = new ImageView(this);
        iv2.setImageResource(R.drawable.welcome2);
        iv2.setScaleType(ImageView.ScaleType.FIT_XY);

        ImageView iv3 = new ImageView(this);
        //为ImageView添加图片资源
        iv3.setImageResource(R.drawable.welcome3);
        //设置图片充满屏幕
        iv3.setScaleType(ImageView.ScaleType.FIT_XY);

        //把ImageView添加到list集合
        list.add(iv1);
        list.add(iv2);
        list.add(iv3);

        //监听ViewPager滑动的效果
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            //选中的页卡
            @Override
            public void onPageSelected(int position) {
                //如果是第三个页面就显示按钮，反之不显示
                if (position == 2) {
                    btn_guide.setVisibility(View.VISIBLE);
                } else {
                    btn_guide.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    /**
     * ViewPager的适配器
     */
    class MyPagerAdapter extends PagerAdapter {

        //计算item个数
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        //item的销毁方法
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }


}
