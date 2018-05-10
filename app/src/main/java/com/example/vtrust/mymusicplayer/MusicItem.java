package com.example.vtrust.mymusicplayer;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by vtrust on 18-5-8.
 */

public class MusicItem {
    public String name;//歌名
    public String singer;//歌手
    public long duration;//时长
    public long size;//大小
    public int albumId;//专辑ID

    public Uri songUri;//歌文件
    public Uri albumUri;//歌封面
    public Bitmap thumb;//封面文件

    public long playedTime;

    public MusicItem() {
    }

    public MusicItem(String name, String singer, long duration, long size, int albumId, Uri songUri, Uri albumUri) {
        this.name = name;
        this.singer = singer;
        this.duration = duration;
        this.size = size;
        this.albumId = albumId;
        this.songUri = songUri;
        this.albumUri = albumUri;
    }

    public boolean equals(Object o) {
        MusicItem another = (MusicItem) o;
        //音乐的Uri相同，则说明两者相同
        return another.songUri.equals(this.songUri);
    }
}
