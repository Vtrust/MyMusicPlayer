package com.example.vtrust.mymusicplayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vtrust on 18-5-8.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private List<MusicItem> musicItemList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;//歌曲名
        TextView singer;//歌手
        TextView duration;//时长
        TextView position;//序号
        ImageView cover;//图片

        public ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.music_name);
            singer = view.findViewById(R.id.music_singer);
            cover = view.findViewById(R.id.music_cover);
            duration = view.findViewById(R.id.music_duration);
            //position = view.findViewById(R.id.music_position);
        }
    }

    public MusicAdapter(List<MusicItem> musicItems) {
        super();
        musicItemList = musicItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MusicItem musicItem = musicItemList.get(position);
        holder.name.setText(musicItem.name);
        if(musicItem.thumb!=null){
            holder.cover.setImageBitmap(musicItem.thumb);
        }
        holder.singer.setText(musicItem.singer);
        holder.duration.setText(Utils.convertMSecendToTime(musicItem.duration));
        //holder.position.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return musicItemList.size();
    }
}
