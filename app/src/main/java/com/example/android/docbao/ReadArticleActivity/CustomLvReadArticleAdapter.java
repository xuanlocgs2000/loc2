package com.example.android.docbao.ReadArticleActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.docbao.R;

import java.util.List;


public class CustomLvReadArticleAdapter extends ArrayAdapter<ReadArticleObject> {
    public CustomLvReadArticleAdapter(Context context, int resourse, List<ReadArticleObject> items) {
        super(context, resourse, items);

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi=LayoutInflater.from(getContext());
            v= vi.inflate(R.layout.custom__lv_read_article,null);
        }

        // Get item
        ReadArticleObject articleWasRead = getItem(position);
        if (articleWasRead!=null){
            TextView title = (TextView) v.findViewById(R.id.text);
            title.setText(articleWasRead.title);
            //TextView tvPosition = (TextView) v.findViewById(R.id.item_employee_tv_position);
            //tvPosition.setText("Staff");
            //********Phần này đang bug*******
            ImageView imgManager = (ImageView) v.findViewById(R.id.img);

        }



        return v;
    }



}
