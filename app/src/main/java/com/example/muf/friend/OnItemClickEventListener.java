package com.example.muf.friend;

import android.view.View;

public interface OnItemClickEventListener {
    void onItemClick(FriendRecyclerAdapter.ViewHolder holder, View view, int pos);
}
