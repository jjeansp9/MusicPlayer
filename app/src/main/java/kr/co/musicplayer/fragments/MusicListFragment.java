package kr.co.musicplayer.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

import kr.co.musicplayer.adapters.RecyclerMusicListAdapter;
import kr.co.musicplayer.databinding.FragmentMusicListBinding;
import kr.co.musicplayer.model.MediaFile;

public class MusicListFragment extends Fragment {

    private FragmentMusicListBinding binding;
    private RecyclerMusicListAdapter adapter;

    private ArrayList<MediaFile> items= new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentMusicListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter= new RecyclerMusicListAdapter(getActivity(), items);
        binding.recycler.setAdapter(adapter);
        binding.recycler.addItemDecoration(new DividerItemDecoration(requireActivity(), LinearLayout.VERTICAL));

        getMusic();
        clickedItems();
    }

    // 오디오 파일 데이터 가져오기
    @SuppressLint("Range")
    private void getMusic() {

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
        };

        Log.d("ContentUri", MediaStore.Audio.Media.EXTERNAL_CONTENT_URI+"");

        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);

        if (cursor != null){
            while (cursor.moveToNext()) {

                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                items.add(new MediaFile(artist, title, duration));
                Log.d("DATAURI", data);
            }
            cursor.close();
        }
    }

    // 클릭한 아이템의 데이터 액티비티로 전달하기
    private void clickedItems(){
        adapter.setItemClickListener(new RecyclerMusicListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getActivity(), items.get(position).getArtist(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}






























