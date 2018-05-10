package com.example.vtrust.mymusicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DemoActivity";

    // view
    RecyclerView recyclerViewAll;

    private SlidingUpPanelLayout mLayout;
    private List<MusicItem> musicItemArrayList = new ArrayList<MusicItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // 权限获取
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        } else {
            initViewPage();
            initSlidingUpPanelLayout();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViewPage();
                    initSlidingUpPanelLayout();
                } else {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    void initViewPage(){
        ViewPager viewPager = findViewById(R.id.viewPager);
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.layout_all_music,null,false);
        View view2 = inflater.inflate(R.layout.layout_love_music,null,false);
        View view3 = inflater.inflate(R.layout.layout_play_music,null,false);

        ArrayList<View> viewLists = new ArrayList<View>();
        viewLists.add(view1);
        viewLists.add(view2);
        viewLists.add(view3);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(viewLists);
        viewPager.setAdapter(pagerAdapter);

        TabLayout mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        // 使用 TabLayout 和 ViewPager 相关联
        mTabLayout.setupWithViewPager(viewPager);
        // TabLayout指示器添加文本
        mTabLayout.getTabAt(0).setText("全部音乐");
        mTabLayout.getTabAt(1).setText("我的收藏");
        mTabLayout.getTabAt(2).setText("播放列表");

        recyclerViewAll = view1.findViewById(R.id.all_music);
        recyclerViewAll.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewAll.setLayoutManager(linearLayoutManager);
        MusicAdapter adapter = new MusicAdapter(musicItemArrayList);
        recyclerViewAll.setAdapter(adapter);

        // 异步加载数据
        MusicUpdateTask musicUpdateTask = new MusicUpdateTask();
        musicUpdateTask.execute();
    }

    void initSlidingUpPanelLayout(){
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }


    public class MusicUpdateTask extends AsyncTask<Object, MusicItem, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... objects) {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String where = MediaStore.Audio.Media.DATA + " like \"%"+"/music"+"%\"";
            String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(uri, null, where, null, sortOrder);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    Uri songUri = Uri.withAppendedPath(uri, id);

                    int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID));
                    Uri albumUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);

                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                    if (size > 1000 * 800) {
                        if (name.contains("-")) {
                            String[] str = name.split("-");
                            singer = str[0];
                            name = str[1];
                        }
                    }
                    MusicItem musicItem = new MusicItem(name,  singer,  duration,  size,  albumId, songUri, albumUri);

                    //获取封面
                    if (albumUri != null) {
                        ContentResolver res = getContentResolver();
                        musicItem.thumb = Utils.createThumbFromUir(res, albumUri);
                    }
                    publishProgress(musicItem);
                }
                // 释放资源
                cursor.close();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(MusicItem... values) {
            super.onProgressUpdate(values);
            MusicItem musicItem = (MusicItem) values[0];

            //主线程
            musicItemArrayList.add(musicItem);
            MusicAdapter adapter = (MusicAdapter)recyclerViewAll.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toobal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        if(id == R.id.play_list_menu){
//            //响应用户对菜单的点击，显示播放列表
//            //showPlayList();
//        }

        return super.onOptionsItemSelected(item);
    }
}
