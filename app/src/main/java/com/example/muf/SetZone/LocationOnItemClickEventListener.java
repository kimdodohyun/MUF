package com.example.muf.SetZone;

import android.view.View;

import com.example.muf.friend.FriendRecyclerAdapter;

public interface LocationOnItemClickEventListener {
    void onItemClick(LocationListAdapter.LocationListViewHolder holder, View view, int pos);
}
