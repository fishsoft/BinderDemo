package com.morse.binderdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.morse.ipc.MServiceManager;
import com.morse.ipc.services.IUserManager;
import com.morse.ipc.services.imp.UserManager;

public class SecondActivity extends AppCompatActivity {

    private IUserManager userManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        MServiceManager.getDefault().open(this);
    }

    public void getObject(View view) {
        userManager = MServiceManager.getDefault().getInstance(IUserManager.class);
    }

    public void getData(View view) {
        Toast.makeText(this, userManager.getUser().toString(), Toast.LENGTH_SHORT).show();
    }
}
