package com.example.muf.Streaming;

import android.view.View;

import com.example.muf.friend.FriendRecyclerAdapter;

public interface OnItemClickEventListener {
    void onItemClick(StreamingRecyclerAdapter.ViewHolder holder, View view, int pos);
}
