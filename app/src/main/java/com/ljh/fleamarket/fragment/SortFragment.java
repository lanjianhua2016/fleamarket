package com.ljh.fleamarket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljh.fleamarket.activity.R;
import com.ljh.fleamarket.activity.sort.SortApplianceActivity;
import com.ljh.fleamarket.activity.sort.SortBooksActivity;
import com.ljh.fleamarket.activity.sort.SortClothesActivity;
import com.ljh.fleamarket.activity.sort.SortDigitalActivity;
import com.ljh.fleamarket.activity.sort.SortEverydayitemsActivity;
import com.ljh.fleamarket.activity.sort.SortOtherActivity;


public class SortFragment extends Fragment {
    private View digital;
    private View appliance;
    private View everydayitems;
    private View books;
    private View clothes;
    private View other;

    // 只用来指定此fragment的布局
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //  return super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_sort,container,false);
        return view;

    }


    //视图控件请在这里实例化
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        digital = getActivity().findViewById(R.id.digital);
        digital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SortDigitalActivity.class);
                startActivity(intent);
            }
        });

        appliance = getActivity().findViewById(R.id.appliance);
        appliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SortApplianceActivity.class);
                startActivity(intent);
            }
        });

        everydayitems = getActivity().findViewById(R.id.everydayitems);
        everydayitems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SortEverydayitemsActivity.class);
                startActivity(intent);
            }
        });

        books = getActivity().findViewById(R.id.books);
        books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SortBooksActivity.class);
                startActivity(intent);
            }
        });

        clothes = getActivity().findViewById(R.id.clothes);
        clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SortClothesActivity.class);
                startActivity(intent);
            }
        });

        other = getActivity().findViewById(R.id.other);
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SortOtherActivity.class);
                startActivity(intent);
            }
        });




    }
}

