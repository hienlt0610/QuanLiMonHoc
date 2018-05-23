package com.example.thienml.quanlimonhoc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thienml.quanlimonhoc.util.DataBaseHelper;
import com.example.thienml.quanlimonhoc.model.LopHoc;
import com.example.thienml.quanlimonhoc.R;

public class KinhNghiemDeXuatActivity extends BaseActivity {
    private static final String KEY_MA_LOP = "MA_LOP";
    private String maLop;
    private EditText edtText;
    private DataBaseHelper dbHelper;

    public static Intent newIntent(Context context, String maLop) {
        Intent intent = new Intent(context, KinhNghiemDeXuatActivity.class);
        intent.putExtra(KEY_MA_LOP, maLop);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kinh_nghiem);
        setHomeAsupEnable(true);
        setTitle("Kinh nghiệm đề xuất");
        initArg();
        dbHelper = new DataBaseHelper(this);
        edtText = findViewById(R.id.edtText);
        getInfo();
    }

    private void getInfo() {
        LopHoc lopHoc = dbHelper.layThongTinLopHoc(maLop);
        if (lopHoc != null) {
            edtText.setText(lopHoc.getKnDanhGia());
        }
    }

    private void initArg() {
        Intent intent = getIntent();
        maLop = intent.getStringExtra(KEY_MA_LOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_kinh_nghiem_de_xuat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Khi bấm nút back thì trở về màn hình trước đó
                this.finish();
                break;
            case R.id.action_luu:
                //Save thong tin
                luuThongTin();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void luuThongTin() {
        String kinhNghiemText = edtText.getText().toString().trim();
        boolean isSuccess = dbHelper.luuKinhNghiemDanhGia(maLop, kinhNghiemText);
        if (isSuccess) {
            Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không thể lưu!!!", Toast.LENGTH_SHORT).show();
        }
    }
}
