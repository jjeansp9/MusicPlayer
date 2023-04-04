package kr.co.musicplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

        items.add(new MediaFile("1", "1", "1"));
        items.add(new MediaFile("", "", "2"));
        items.add(new MediaFile("", "", "3"));
    }
}






























