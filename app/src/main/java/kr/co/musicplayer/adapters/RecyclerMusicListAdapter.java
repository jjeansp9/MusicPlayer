package kr.co.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.musicplayer.R;
import kr.co.musicplayer.databinding.RecyclerMusicListItemBinding;
import kr.co.musicplayer.model.MediaFile;

public class RecyclerMusicListAdapter extends RecyclerView.Adapter<RecyclerMusicListAdapter.VH> {

    Context context;
    ArrayList<MediaFile> items;

    public RecyclerMusicListAdapter(Context context, ArrayList<MediaFile> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.recycler_music_list_item, parent, false);
        return new RecyclerMusicListAdapter.VH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MediaFile item= items.get(position);

        holder.binding.composer.setText(item.getArtist());
        holder.binding.title.setText(item.getTitle());
        holder.binding.duration.setText(item.getDuration());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder{

        RecyclerMusicListItemBinding binding;

        public VH(@NonNull View itemView) {
            super(itemView);

            binding= RecyclerMusicListItemBinding.bind(itemView);
        }





    }
}
