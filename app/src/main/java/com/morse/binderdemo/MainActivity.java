package com.morse.binderdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.morse.ipc.MServiceManager;
import com.morse.ipc.bean.User;
import com.morse.ipc.services.imp.UserManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MServiceManager.getDefault().addService("UserManager", UserManager.class);
        UserManager.getInstance().setUser(new User("morse","123456"));

    }

    public void jump(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }
}
