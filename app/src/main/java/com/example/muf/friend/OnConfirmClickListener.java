package com.example.muf.friend;

import android.view.View;

public interface OnConfirmClickListener {
    void onItemClick(FriendRequestRecyclerAdapter.ViewHolder holder, View view, int pos);
}
