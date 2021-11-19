package com.example.muf.myprofilefrag;

import android.view.View;

import com.example.muf.friend.FriendRecyclerAdapter;

public interface OnItemClickEventListener {
    void onItemClick(FriendRecyclerAdapter.ViewHolder holder, View view, int pos);
}
