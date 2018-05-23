package com.example.thienml.quanlimonhoc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.thienml.quanlimonhoc.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    Button btnLop, btnSV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLop = findViewById(R.id.btn_lop);
        btnSV = findViewById(R.id.btn_sv);
        btnSV.setOnClickListener(this);
        btnLop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_lop:
                startActivity(new Intent(MainActivity.this, QuanLyLopHocActivity.class));
                break;
            case R.id.btn_sv:
                startActivity(new Intent(MainActivity.this, QuanLySinhVienActivity.class));
                break;
        }
    }
}
