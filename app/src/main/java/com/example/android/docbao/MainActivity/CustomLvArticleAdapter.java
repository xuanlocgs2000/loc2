package com.example.android.docbao.MainActivity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.docbao.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomLvArticleAdapter extends ArrayAdapter<ArticleObject> {


    public CustomLvArticleAdapter(Context context, int resourse, List<ArticleObject> items) {
        super(context, resourse, items);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi=LayoutInflater.from(getContext());
            v= vi.inflate(R.layout.custom_lv_article,null);
        }

        try {
            // Get item
            ArticleObject articleObject = getItem(position);
            if (articleObject != null) {
                TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
                tvTitle.setText(articleObject.title);

                TextView tvDate = (TextView) v.findViewById(R.id.tv_date);
                tvDate.setText(articleObject.date);

                ImageView imgImage = (ImageView) v.findViewById(R.id.img_image);
                if (articleObject.image.isEmpty()) {
                    imgImage.setImageResource(R.drawable.trang);
                } else {
                    Picasso.with(getContext()).load(articleObject.image).into(imgImage);
                }


            }
        }catch (Exception e){
            Log.e("exception","Loi : "+e);
        }

        return v;
    }
}

