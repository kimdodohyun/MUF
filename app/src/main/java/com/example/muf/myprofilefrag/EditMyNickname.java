package com.example.muf.myprofilefrag;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.muf.R;

public class EditMyNickname extends AppCompatActivity {
    private EditText editText;
    private TextView textView;
    private String inputtext = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_my_nickname_layout);

        editText = findViewById(R.id.editTextInEditNickName);
        textView = findViewById(R.id.completeSetNickname);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString() != null){
                    inputtext = editText.getText().toString();
                }
                Intent intent = new Intent();
                intent.putExtra("nickname", inputtext);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}