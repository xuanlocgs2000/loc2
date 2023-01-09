package com.example.android.docbao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //Database handler là một class mà Android cho phép bạn xử lý các thao tác đối với database của SQLite,
    //vì vậy bạn có thể tạo một class khác thừa kế nó và tùy chỉnh việc điều khiển database theo ý mình
    //Truy vấn không trả kết quả, thêm, cập nhập, xóa.
    public void QueryData(String sql){
       SQLiteDatabase database=getWritableDatabase();
        database.execSQL(sql);
    }
    //Truy vấn có kết quả, select only
    public Cursor GetData(String sql){ //kieu con tro
        SQLiteDatabase database=getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
