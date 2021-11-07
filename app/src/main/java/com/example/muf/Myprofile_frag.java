package com.example.muf;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Myprofile_frag extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.myprofile_layout, container, false);

        view.findViewById(R.id.profile_edit_but).setOnClickListener(onClickListener);
        return view;
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.profile_edit_but:
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
                break;
        }
    };

}
