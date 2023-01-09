package com.example.android.docbao.ReadArticleActivity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.docbao.DetailsArticleActivity.DetailsArticleOfflineActivity;
import com.example.android.docbao.MainActivity.MainActivity;
import com.example.android.docbao.R;

import java.util.ArrayList;

public class ReadArticleActivity extends AppCompatActivity {
    ListView lv_tindadoc;
    CustomLvReadArticleAdapter adapter;
   static ArrayList<ReadArticleObject> arrTinDaDoc= new ArrayList<ReadArticleObject>();
    static ArrayList<ReadArticleObject> arrTinDaDoc2= new ArrayList<ReadArticleObject>();


    static int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);


        // Lấy tin đã đọc gần đầy từ database
        arrTinDaDoc.clear();
        arrTinDaDoc2.clear();

        Cursor dataContacts = MainActivity.databaseArticleWasRead.GetData("SELECT * FROM contacts");
        while (dataContacts.moveToNext()) {    //Khi còn dữ liệu
            int id = dataContacts.getInt(0);
            String title = dataContacts.getString(1); //Cột 1
            String link = dataContacts.getString(2);
            ReadArticleObject tinDaDoc = new ReadArticleObject(id, title, link);
            arrTinDaDoc.add(tinDaDoc);
        }
        Log.e("abc", "size lúc chưa lọc"+ arrTinDaDoc.size());

        // lấy "10" cái tin gần nhất, còn lại xóa, có thể chỉnh thêm nhưng bỏ đi, toang lắm
        for(int i=arrTinDaDoc.size()-1; i>=0;i--){

            if(arrTinDaDoc2.size()<10){
                arrTinDaDoc2.add(arrTinDaDoc.get(i));
            }else{
                MainActivity.databaseArticleWasRead.QueryData("DELETE FROM contacts WHERE id='" + arrTinDaDoc.get(i).id + "'");
            }
        }







        lv_tindadoc=(ListView) findViewById(R.id.lv_tinDaDoc);

        adapter = new CustomLvReadArticleAdapter(ReadArticleActivity.this,
                R.layout.custom__lv_read_article,
                arrTinDaDoc2);
        lv_tindadoc.setAdapter(adapter);
        lv_tindadoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(ReadArticleActivity.this,DetailsArticleOfflineActivity.class);
                intent.putExtra("link",arrTinDaDoc2.get(i).link);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
