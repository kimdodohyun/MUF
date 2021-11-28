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
import com.example.muf.friend.RecommenFriendAdapter;
import com.example.muf.model.OtherSongList;
import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private FirebaseAuth firebaseAuth;
    private String myUid;
    private String otherUid;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewRecommendFriend;
    private StreamingRecyclerAdapter adapter;
    private RecommenFriendAdapter adapterRecommendFriend;
    private ArrayList<String> uriList;
    private ArrayList<Stream> streamList;
    private FirebaseFirestore db;
    private SpotifyApi spotifyApi;
    private OtherSongList otherSongList;
    private OtherSongList mySongList;
    private ArrayList<OtherSongList> arrayListOtherSongList;
    private ArrayList<String> otherUserList;
    private ArrayList<String> recommendList;
    private UserModel userInfo;
    private ArrayList<UserModel> arrayListUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       db = FirebaseFirestore.getInstance();
       userInfo = new UserModel();
       firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
        streamList = new ArrayList<>();
        spotifyApi = new SpotifyApi();
       arrayListUserModel = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_layout, container, false);
        recyclerView = view.findViewById(R.id.recycler_streaming);
        recyclerViewRecommendFriend = view.findViewById(R.id.recyclerViewRecommendFriend);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerViewRecommendFriend.setHasFixedSize(true);
        recyclerViewRecommendFriend.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        No_textview = view.findViewById(R.id.No_zone_inhome);
        Set_textview = view.findViewById(R.id.view_myzone_inhome);

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
            adapterRecommendFriend = new RecommenFriendAdapter(arrayListUserModel, getActivity().getApplicationContext());
            db.collection(ename).document("UserLists").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            for (Object value : doc.getData().values()) {
                                String parse1[] = value.toString().split("_");
                                //parse1[0] == userUid
                                if(parse1[0].equals(myUid))
                                    continue;
                                otherUid = parse1[0];
//                                adapterRecommendFriend.notifyDataSetChanged();
                                Log.d("starttest", "onComplete: "+homeActivity.recommendList.size());
                                for(int i =0; i<homeActivity.recommendList.size(); i++){
                                    if(homeActivity.recommendList.get(i).equals(otherUid)){
                                        db.collection("Users").document(otherUid).collection("Myinfo").document("info")
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                UserModel user = new UserModel();
                                                user = task.getResult().toObject(UserModel.class);
                                                arrayListUserModel.add(user);
                                                recyclerViewRecommendFriend.setAdapter(adapterRecommendFriend);
                                                Log.d("starttest", "onComplete: " + user.getUid());
                                            }
                                        });
                                    }
                                }
                                adapterRecommendFriend.notifyDataSetChanged();
                                String parse2[] = parse1[1].split(":");
                                Track track = spotifyApi.getService().getTrack(parse2[2]);
                                Stream stream = new Stream(track.name, track.artists.get(0).name, track.album.images.get(0).url, parse1[1]);
                                streamList.add(stream);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
            recyclerView.setAdapter(adapter);
            Log.d("bindTest", "onStart: after adapter" );
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
