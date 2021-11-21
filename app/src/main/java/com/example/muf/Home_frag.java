package com.example.muf;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muf.Streaming.OnItemClickEventListener;
import com.example.muf.Streaming.Stream;
import com.example.muf.Streaming.StreamingRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

public class Home_frag extends Fragment {
    private final static String TAG = "HOME";
    private View view;
    private TextView No_textview, Set_textview;
    private int flag = -1;
    private String kname;
    private String ename;
    private RecyclerView recyclerView;
    private StreamingRecyclerAdapter adapter;
    private ArrayList<String> uriList;
    private ArrayList<Stream> streamList;
    private FirebaseFirestore db;
    private SpotifyApi spotifyApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeFrag onCreate", "kimgijeong");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_layout, container, false);
        recyclerView = view.findViewById(R.id.recycler_streaming);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        No_textview = view.findViewById(R.id.No_zone_inhome);
        Set_textview = view.findViewById(R.id.view_myzone_inhome);
        db = FirebaseFirestore.getInstance();
        uriList = new ArrayList<>();
        streamList = new ArrayList<>();
        streamList = new ArrayList<>();
        spotifyApi = new SpotifyApi();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("HomeFrag", " onActivityCreated kimgijeong");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) { //HomeActivity에서 bundle받기
            Bundle bundle = getArguments();
            flag = bundle.getInt("flag");
            kname = bundle.getString("name");
            ename = bundle.getString("englishname");
            Log.d("HomeFrag onCreateView", "flagvalue = " + flag + " kimgijeong");
            if (flag == 1) { //Zone이 설정 된 경우
                No_textview.setVisibility(View.INVISIBLE);
                Set_textview.setText(kname);
                Set_textview.setVisibility(View.VISIBLE);
            } else if (flag == 0) {
                No_textview.setVisibility(View.VISIBLE);
                Set_textview.setVisibility(View.INVISIBLE);
            }
        }

        if (flag == 1) {
            spotifyApi.setAccessToken(homeActivity.AUTH_TOKEN);
            adapter = new StreamingRecyclerAdapter(streamList,getActivity().getApplicationContext());
            db.collection(ename).document("UserLists").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            for (Object value : doc.getData().values()) {
                                String parse[] = value.toString().split(":");
                                Track track = spotifyApi.getService().getTrack(parse[2]);
                                Stream stream = new Stream(track.name, track.artists.get(0).name, track.album.images.get(0).url,value.toString());
                                streamList.add(stream);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnItemClickEventListener() {
                @Override
                public void onItemClick(StreamingRecyclerAdapter.ViewHolder holder, View view, int pos) {
                    Stream item = adapter.getItem(pos);
                    homeActivity.mSpotifyAppRemote.getPlayerApi().play(item.getTrackUri());
                }
            });
        }
    }
}
