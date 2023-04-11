package kr.co.musicplayer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
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

    public interface OnItemClickListener{
        void onClick(View view, int position);
    }

    private OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener onItemClickListener){
        this.itemClickListener= onItemClickListener;
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
        holder.binding.data.setText(item.getData());

        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + item.getUri());
        Bitmap albumArt = null;

        try {
            albumArt = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.binding.img.setImageBitmap(albumArt);

        holder.binding.getRoot().setOnClickListener( v -> itemClickListener.onClick(holder.binding.getRoot(), position));
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
