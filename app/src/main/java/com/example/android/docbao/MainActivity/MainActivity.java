package com.example.android.docbao.MainActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.docbao.ReadArticleActivity.ReadArticleActivity;
import com.example.android.docbao.DatabaseHandler;
import com.example.android.docbao.DetailsArticleActivity.DetailsArticleActivity;
import com.example.android.docbao.R;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvArticle;
    CustomLvArticleAdapter adapter;
    ArrayList<ArticleObject> arrArticle=new ArrayList<ArticleObject>();;
    ArrayList<ArticleObject> arrArticle2= new ArrayList<ArticleObject>();


    static String linkWeb = "https://thanhnien.vn/rss/home.rss";
    static String titleWeb = "ThanhNien.vn";
    static int idWebSite = 0;
    MenuItem itemMenu_type_article;
    public static DatabaseHandler databaseArticleWasRead;
    public static DatabaseHandler databaseSavedArticle;


    WebSiteObject website;
    ArrayList<WebSiteObject> arrMenuWeb = new ArrayList<WebSiteObject>();
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    String tenWeb[] = {"ThanhNien.vn", "VnExpress.net", "TienPhong.vn", "24h.com.vn", "DanViet.vn", "Tin Đã Lưu"};
    int[] iconWeb = {R.drawable.thanhnien, R.drawable.ic_exx, R.drawable.ic_tienphong, R.drawable.ic_24h, R.drawable.ic_danviet, R.mipmap.star};
    ListView lvWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvArticle = (ListView) findViewById(R.id.lv);
        drawerLayout = (DrawerLayout) findViewById(R.id.menuWeb);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        itemMenu_type_article = (MenuItem) findViewById(R.id.mn_);

        adapter = new CustomLvArticleAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrArticle);
        lvArticle.setAdapter(adapter);

        for (int i = 0; i < tenWeb.length; i++) {
            website = new WebSiteObject(iconWeb[i], tenWeb[i]);
            arrMenuWeb.add(website);
        }
        lvWebsite = (ListView) findViewById(R.id.lv_menuWeb);
        final CustomLvWebsiteAdapter customAdapter_menuWeb = new CustomLvWebsiteAdapter(MainActivity.this, R.layout.custom__lv_website, arrMenuWeb);
        lvWebsite.setAdapter(customAdapter_menuWeb);

        //tạo database
        databaseArticleWasRead = new DatabaseHandler(this, "TinDaDoc.sqlite", null, 1);
        databaseArticleWasRead.QueryData("CREATE TABLE IF NOT EXISTS contacts(id INTEGER PRIMARY KEY AUTOINCREMENT, title NVARCHAR(100),link VARCHAR(100))");

        databaseSavedArticle = new DatabaseHandler(this, "TinDaLuu.sqlite", null, 1);
        databaseSavedArticle.QueryData("CREATE TABLE IF NOT EXISTS contacts(id INTEGER PRIMARY KEY AUTOINCREMENT, img NVARCHAR(100),title NVARCHAR(100),link VARCHAR(100),date NVARCHAR(20))");

        // kiểm tra mạng
        if (isNetworkAvailable() == true) {
            new ReadDataFromURL().execute(linkWeb);
        } else {
            showDialogWhenNoNetwork();
        }

        // setting cái button show menu web
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //hien cai button
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.e("xx", "click ben trai");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        MainActivity.this.setTitle(titleWeb);


        // event click listview
        listviewArticleClick();

        listviewWebsiteClick();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    // click trang báo nào thì menu(loại báo)  trang báo đó hiện
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loaibao, menu);
        getMenuInflater().inflate(R.menu.menu_loaibao_vnexpress, menu);
        getMenuInflater().inflate(R.menu.menu_loaibao_tienphong, menu);
        getMenuInflater().inflate(R.menu.menu_loaibao_24h, menu);
        getMenuInflater().inflate(R.menu.menu_loaibao_danviet, menu);

        showMenuOfWebsite(idWebSite, menu);

        return super.onCreateOptionsMenu(menu);

    }


    // event click  menu  bên phải || click drawerlayout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //click item menu_TypeBao
        String link = selectItemRightMenu(item);

        // nếu có click vào cái listview website
        if(link.equals("abc")==false) {
            new ReadDataFromURL().execute(link);
            Log.e("xx", "click menu ben phải ==" + link);
        }
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }


    class ReadDataFromURL extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            arrArticle.clear();
            adapter.notifyDataSetChanged();

            arrArticle2.clear();

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("      Đang tải, đợi tí có ngay...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //  ArrayList<NewsModel> arr=new ArrayList<NewsModel>();
            String url = strings[0];
            //   arrArticle.clear();
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("item");
                for (org.jsoup.nodes.Element item : elements) {
                    String title = item.select("title").text();
                    String link = item.select("link").text();
                    String date = item.select("pubDate").text();
                    String des = item.select("description").text();
                    //     Log.d("des",des);
                    org.jsoup.nodes.Document docImage = Jsoup.parse(des);
                    String image = docImage.select("img").get(0).attr("src");
                    //  Log.d("img",image);
                    Log.d("link", link);
                    String title2 = title.replace("'", "*");
                    String title3 = title2.replace("&#34;", " ");
                    title3 = title3.replace("&#40;", " ");
                    title3 = title3.replace("&#41;", " ");
                    title3 = title3.replace("&#39;", " ");

                    date = date.replace("(GMT+7)", "");
                    date = date.replace("GMT+7", "");

                    date = date.replace("+0700", "");
                    if (date.indexOf(",") != -1)
                        date = date.substring(date.indexOf(",") + 1);
                    date = date.replace("Jan", "/01/");
                    date = date.replace("Feb", "/02/");
                    date = date.replace("Mar", "/03/");
                    date = date.replace("Apr", "/04/");
                    date = date.replace("May", "/05/");
                    date = date.replace("June", "/06/");
                    date = date.replace("July", "/07/");
                    date = date.replace("Aug", "/08/");
                    date = date.replace("Sept", "/09/");
                    date = date.replace("Oct", "/10/");
                    date = date.replace("Nov", "/11/");
                    date = date.replace("Dec", "/12/");
                    if (date.length() > 11)
                        date = date.substring(0, date.length() - 10);


                    arrArticle2.add(new ArticleObject(title3, link, image, date));
                    Log.d("arr", "" + arrArticle.size());

                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("error ", "" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

//            adapter = new CustomLvArticleAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrArticle);
//            lvArticle.setAdapter(adapter);
            arrArticle.addAll(arrArticle2);
            adapter.notifyDataSetChanged();

            dialog.dismiss();
            super.onPostExecute(s);

            Log.d("arr", "...." + arrArticle.size());
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    private String selectItemRightMenu(MenuItem item) {

        String link = "abc";
        switch (item.getItemId()) {
            case R.id.mn_trangChu:
                link = "https://thanhnien.vn/rss/home.rss";
                break;
            case R.id.mn_phapLuat:
                link = "https://thanhnien.vn/rss/thoi-su/phap-luat-5.rss";
                break;
            case R.id.mn_congNghe:
                link = "https://thanhnien.vn/rss/cong-nghe-game-315.rss";
                break;
            case R.id.mn_kinhDoanh:
                link = "https://thanhnien.vn/rss/tai-chinh-kinh-doanh-49.rss";
                break;
            case R.id.mn_giaoDuc:
                link = "https://thanhnien.vn/rss/giao-duc-587.rss";
                break;
            case R.id.mn_thoiSu:
                link = "https://thanhnien.vn/rss/thoi-su-4.rss";
                break;
            case R.id.mn_giaiTri:
                link = "https://thanhnien.vn/rss/giai-tri-285.rss";
                break;
            case R.id.mn_sucKhoe:
                link = "https://thanhnien.vn/rss/suc-khoe-65.rss";
                break;
            case R.id.mn_theThao:
                link = "https://thanhnien.vn/rss/the-thao-318.rss";
                break;
            case R.id.mn_doiSong:
                link = "https://thanhnien.vn/rss/doi-song-17.rss";
                break;
            case R.id.mn_theGioi:
                link = "https://thanhnien.vn/rss/the-gioi-66.rss";
                break;
            case R.id.mn_batDongSan:
                link = "https://thanhnien.vn/rss/du-lich/bat-dong-san-du-lich-585.rss";
                break;
            case R.id.mn_banDoc:
                link = "https://thanhnien.vn/rss/ban-doc-190.rss";
                break;
            case R.id.mn_tinMoiNong:
                link = "https://thanhnien.vn/rss/chao-ngay-moi-2.rss";
                break;
            case R.id.mn_tinNoiBat:
                link = "https://thanhnien.vn/rss/toi-viet-89.rss";
                break;
            case R.id.mn_tuanVietNam:
                link = "https://thanhnien.vn/rss/van-hoa/mon-ngon-ha-noi-303.rss";
                break;
            case R.id.mn_gocNhinThang:
                link = "https://thanhnien.vn/rss/the-gioi/goc-nhin-129.rss";
                break;

            //vnexpress
            case R.id.mn_ex_trangChu:
                link = "https://vnexpress.net/rss/tin-moi-nhat.rss";
                break;
            case R.id.mn_ex_thoiSu:
                link = "https://vnexpress.net/rss/thoi-su.rss";
                break;
            case R.id.mn_ex_theGioi:
                link = "https://vnexpress.net/rss/the-gioi.rss";
                break;
            case R.id.mn_ex_kinhDoanh:
                link = "https://vnexpress.net/rss/kinh-doanh.rss";
                break;
            case R.id.mn_ex_startup:
                link = "https://vnexpress.net/rss/startup.rss";
                break;
            case R.id.mn_ex_giaiTri:
                link = "https://vnexpress.net/rss/giai-tri.rss";
                break;
            case R.id.mn_ex_theThao:
                link = "https://vnexpress.net/rss/the-thao.rss";
                break;
            case R.id.mn_ex_phapLuat:
                link = "https://vnexpress.net/rss/phap-luat.rss";
                break;
            case R.id.mn_ex_giaoDuc:
                link = "https://vnexpress.net/rss/giao-duc.rss";
                break;
            case R.id.mn_ex_sucKhoe:
                link = "https://vnexpress.net/rss/suc-khoe.rss";
                break;
            case R.id.mn_ex_giaDinh:
                link = "https://vnexpress.net/rss/gia-dinh.rss";
                break;
            case R.id.mn_ex_duLich:
                link = "https://vnexpress.net/rss/du-lich.rss";
                break;
            case R.id.mn_ex_khoaHoc:
                link = "https://vnexpress.net/rss/khoa-hoc.rss";
                break;
            case R.id.mn_ex_soHoa:
                link = "https://vnexpress.net/rss/so-hoa.rss";
                break;
            case R.id.mn_ex_xe:
                link = "https://vnexpress.net/rss/oto-xe-may.rss";
                break;
            case R.id.mn_ex_congDong:
                link = "https://vnexpress.net/rss/cong-dong.rss";
                break;
            case R.id.mn_ex_tamsu:
                link = "https://vnexpress.net/rss/tam-su.rss";
                break;
            case R.id.mn_ex_cuoi:
                link = "https://vnexpress.net/rss/cuoi.rss";
                break;

            //dantri
            case R.id.mn_tp_tc:
                link = "https://tienphong.vn/rss/home.rss";
                break;
            case R.id.mn_tp_sk:
                link = "https://tienphong.vn/rss/suc-khoe-210.rss";
                break;
            case R.id.mn_tp_xh:
                link = "https://tienphong.vn/rss/xa-hoi-2.rss";
                break;
            case R.id.mn_tp_gt:
                link = "https://tienphong.vn/rss/giai-tri-36.rss";
                break;
            case R.id.mn_tp_gdkh:
                link = "https://tienphong.vn/rss/giao-duc-71.rss";
                break;
            case R.id.mn_tp_tt:
                link = "https://tienphong.vn/rss/the-thao-11.rss";
                break;
            case R.id.mn_tp_tg:
                link = "https://tienphong.vn/rss/the-gioi-5.rss";
                break;
            case R.id.mn_tp_kd:
                link = "https://tienphong.vn/rss/kinh-te-3.rss";
                break;
            case R.id.mn_tp_otoxm:
                link = "https://tienphong.vn/rss/xe-113.rss";
                break;
            case R.id.mn_tp_sms:
                link = "https://tienphong.vn/rss/cong-nghe-khoa-hoc-46.rss";
                break;
            case R.id.mn_tp_bacsi:
                link = "https://tienphong.vn/rss/bac-si-online-304.rss";
                break;
            case R.id.mn_tp_cl:
                link = "https://tienphong.vn/rss/chuyen-la-32.rss";
                break;
            case R.id.mn_tp_dulich:
                link = "https://tienphong.vn/rss/du-lich-220.rss";
                break;
            case R.id.mn_tp_nst:
                link = "https://tienphong.vn/rss/gioi-tre-4.rss";
                break;
            case R.id.mn_tp_pl:
                link = "http://dantri.com.vn/phap-luat.rss";
                break;
            case R.id.mn_tp_bd:
                link = "https://tienphong.vn/rss/ban-doc-15.rss";
                break;
            case R.id.mn_tp_vh:
                link = "https://tienphong.vn/rss/van-hoa-7.rss";
                break;
            case R.id.mn_tp_dh:
                link = "https://tienphong.vn/rss/giao-duc-du-hoc-40.rss";
                break;
            case R.id.mn_tp_dl:
                link = "https://tienphong.vn/rss/du-lich-220.rss";
                break;
            case R.id.mn_tp_ds:
                link = "https://tienphong.vn/rss/suc-khoe-thi-tham-ben-goi-144.rss";
                break;
            case R.id.mn_tp_khcn:
                link = "hhttps://tienphong.vn/rss/cong-nghe-45.rss";
                break;

            //24h
            case R.id.mn_24h_tc:
                link = "https://cdn.24h.com.vn/upload/rss/trangchu24h.rss";
                break;
            case R.id.mn_24h_bd:
                link = "https://cdn.24h.com.vn/upload/rss/bongda.rss";
                break;
            case R.id.mn_24h_anhs:
                link = "https://cdn.24h.com.vn/upload/rss/anninhhinhsu.rss";
                break;
            case R.id.mn_24h_tt:
                link = "hhttps://cdn.24h.com.vn/upload/rss/thoitrang.rss";
                break;
            case R.id.mn_24h_tcbds:
                link = "https://cdn.24h.com.vn/upload/rss/taichinhbatdongsan.rss";
                break;
            case R.id.mn_24h_at:
                link = "https://cdn.24h.com.vn/upload/rss/amthuc.rss";
                break;
            case R.id.mn_24h_ld:
                link = "https://cdn.24h.com.vn/upload/rss/lamdep.rss";
                break;
            case R.id.mn_24h_phim:
                link = "https://cdn.24h.com.vn/upload/rss/phim.rss";
                break;
            case R.id.mn_24h_gddh:
                link = "https://cdn.24h.com.vn/upload/rss/giaoducduhoc.rss";
                break;
            case R.id.mn_24h_btcs:
                link = "https://cdn.24h.com.vn/upload/rss/bantrecuocsong.rss";
                break;
            case R.id.mn_24h_tt2:
                link = "https://cdn.24h.com.vn/upload/rss/thethao.rss";
                break;
            case R.id.mn_24h_cntt:
                link = "https://cdn.24h.com.vn/upload/rss/congnghethongtin.rss";
                break;
            case R.id.mn_24h_otoxm:
                link = "https://cdn.24h.com.vn/upload/rss/otoxemay.rss";
                break;
            case R.id.mn_24h_dl:
                link = "https://cdn.24h.com.vn/upload/rss/dulich.rss";
                break;
            case R.id.mn_24h_skds:
                link = "https://cdn.24h.com.vn/upload/rss/suckhoedoisong.rss";
                break;
            case R.id.mn_24h_cuoi24h:
                link = "https://cdn.24h.com.vn/upload/rss/cuoi24h.rss";
                break;
            case R.id.mn_24h_tg:
                link = "https://cdn.24h.com.vn/upload/rss/tintucquocte.rss";
                break;
            case R.id.mn_24h_dss:
                link = "https://cdn.24h.com.vn/upload/rss/doisongshowbiz.rss";
                break;
            case R.id.mn_24h_gt:
                link = "https://cdn.24h.com.vn/upload/rss/giaitri.rss";
                break;


            // danviet
            case R.id.mn_dv_trangChu:
                link = "http://danviet.vn/rss/tin-tuc-1001.rss";
                break;
            case R.id.mn_dv_theGioi:
                link = "http://danviet.vn/rss/the-gioi-1007.rss";
                break;
            case R.id.mn_dv_theThao:
                link = "http://danviet.vn/rss/the-thao-1035.rss";
                break;
            case R.id.mn_dv_phapLuat:
                link = "http://danviet.vn/rss/phap-luat-1008.rss";
                break;
            case R.id.mn_dv_kinhTe:
                link = "http://danviet.vn/rss/kinh-te-1004.rss";
                break;
            case R.id.mn_dv_nhaNong:
                link = "http://danviet.vn/rss/nha-nong-1009.rss";
                break;

            case R.id.mn_dv_giaDinh:
                link = "http://danviet.vn/rss/gia-dinh-1023.rss";
                break;
            case R.id.mn_dv_congNghe:
                link = "http://danviet.vn/rss/cong-nghe-1030.rss";
                break;
            case R.id.mn_dv_otoxm:
                link = "http://danviet.vn/rss/o-to-xe-may-1034.rss";
                break;
            case R.id.mn_dv_banDoc:
                link = "http://danviet.vn/rss/ban-doc-1043.rss";
                break;
            case R.id.mn_dv_duLich:
                link = "http://danviet.vn/rss/du-lich-1097.rss";
                break;
            default:
                break;

        }

        return link;
    }


    private void showMenuOfWebsite(int idWebSite, Menu menu) {


        if (idWebSite == 0) {
//            for(int i = 0; i<4; i++) {
//                menu.getItem(4).setVisible(false);
//            }
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);


        } else if (idWebSite == 1) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);


        } else if (idWebSite == 2) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);

        } else if (idWebSite == 3) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
            menu.getItem(4).setVisible(false);

        } else if (idWebSite == 4) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(true);

        } else if (idWebSite == 5) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);

        }
    }


    private  void listviewArticleClick(){

        final boolean[] isLongClick = {false};

        lvArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override        //load qua activity 2 khi kick vao item listview
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //insert data       chu y chu insert
                if (isLongClick[0] == false) {
                    databaseArticleWasRead.QueryData("INSERT INTO contacts VALUES(null,'" + arrArticle.get(i).title + "','" + arrArticle.get(i).link + "')");
                    Intent intent = new Intent(MainActivity.this, DetailsArticleActivity.class);
                    intent.putExtra("link", arrArticle.get(i).link);
                    intent.putExtra("image", arrArticle.get(i).image);
                    intent.putExtra("title", arrArticle.get(i).title);
                    intent.putExtra("date", arrArticle.get(i).date);
                    startActivity(intent);
                }
            }

        });
        //long click lv

        lvArticle.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (idWebSite == 5) {
                    isLongClick[0] = true;
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("     Xác Nhận...");
                    alertDialog.setMessage("Bạn có thực sự muốn xóa tin này!");
                    alertDialog.setIcon(R.drawable.war);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i2) {
                            databaseSavedArticle.QueryData("DELETE FROM contacts WHERE id='" + arrArticle.get(i).id + "'");
                            arrArticle.remove(i);
                            adapter.notifyDataSetChanged();
                            isLongClick[0] = false;
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.setNeutralButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isLongClick[0] = false;
                            dialogInterface.dismiss();
                        }
                    });

                    alertDialog.show();

                }
                return false;

            }
        });
    }

    private void getSavedArticleFromDatabase(){
        arrArticle.clear();
        Cursor dataContacts = databaseSavedArticle.GetData("SELECT * FROM contacts");
        while (dataContacts.moveToNext()) {    //khi con` du lieu
            int id = dataContacts.getInt(0);

            String img = dataContacts.getString(1);
            String title = dataContacts.getString(2); //cot 1
            String link = dataContacts.getString(3);
            String date = dataContacts.getString(4);
            arrArticle.add(0, new ArticleObject(id, title, link, img, date));
            adapter.notifyDataSetChanged();
        }
    }


    private  void showDialogWhenNoNetwork(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Không có kết nối internet");
        alertDialog.setMessage("Bạn có muốn xem lại những tin đã đọc gần đây !");
        alertDialog.setIcon(R.drawable.errorw);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, ReadArticleActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.setNeutralButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                System.exit(0);
            }
        });

        alertDialog.show();
    }

    private  void listviewWebsiteClick(){
        lvWebsite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                idWebSite = i;

                Log.e("xx", "clickmenuwweb");
                if (i == 1) {
                    linkWeb = "https://vnexpress.net/rss/tin-moi-nhat.rss";
                    titleWeb = "VnExpress.net";
                } else if (i == 0) {
                    linkWeb = "https://thanhnien.vn/rss/home.rss";
                    titleWeb = "ThanhNien.vn";

                } else if (i == 2) {
                    linkWeb = "https://tienphong.vn/rss/home.rss";
                    titleWeb = "TienPhong.vn";
                } else if (i == 3) {
                    linkWeb = "https://cdn.24h.com.vn/upload/rss/tintuctrongngay.rss";
                    titleWeb = "24h.com.vn";

                } else if (i == 4) {
                    linkWeb = "http://danviet.vn/rss/tin-tuc-1001.rss";
                    titleWeb = "DânViệt.vn";

                } else if (i == 5) {
                    getSavedArticleFromDatabase();
                    Log.e("xx", "size=" + arrArticle.size());
                    titleWeb = "Tin Đã Lưu";

                }
                //cap nhap lai menu chon loai bao cua trang web
                if (idWebSite != 5) {
//                    arrArticle.clear();
                    new ReadDataFromURL().execute(linkWeb);
                    Log.e("xx", "sss" + i);
                }
                Log.e("xx", "" + i);
                invalidateOptionsMenu();
                MainActivity.this.setTitle(titleWeb);
                drawerLayout.closeDrawers();  //dong cai tab chon web bao'
            }
        });
    }

}
