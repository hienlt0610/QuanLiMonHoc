package com.example.thienml.quanlimonhoc;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class QuanLySinhVienActivity extends BaseActivity {

    Button btnthem;
    RadioGroup rdg;
    EditText txttennv, txtmanv;
    RadioButton rdb1, rdb2;
    ListView lvSinhVien = null;
    ArrayList<Sinhvien> arrayList = null;
    MyAdapter adapter = null;
    DataBaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ql_sv);
        setTitle(R.string.ql_sv_title);
        setHomeAsupEnable(true);
        //Init database
        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();
        btnthem = findViewById(R.id.btnThem);
        rdg = findViewById(R.id.rdgGT);
        rdb1 = findViewById(R.id.rdbGTnam);
        rdb2 = findViewById(R.id.rdbGTnu);
        txtmanv = findViewById(R.id.txtMaNV);
        txttennv = findViewById(R.id.txtTenNV);
        lvSinhVien = findViewById(R.id.listSV);

        //Set default checked for male
        rdb1.setChecked(true);

        arrayList = (ArrayList<Sinhvien>) xemdssinhvien();
        adapter = new MyAdapter(this, R.layout.custom_listview, arrayList); //khởi tạo adapter
        //Không hiển bảng điểm do đang ở màn hình quản lý sinh viên
        adapter.setShowInfoOnly(true);
        lvSinhVien.setAdapter(adapter);                                 //gán adapter vào listview
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemSV();
            }
        });

        //sự kiện ấn vào ListView sinh viên để thay đổi thuộc tính
        lvSinhVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = arrayList.get(position).getMasv();
                Intent in = ChiTietSinhVienActivity.intentUpdateThongTinSV(QuanLySinhVienActivity.this, item);
                startActivityForResult(in, 0);
            }
        });
        lvSinhVien.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = arrayList.get(position).getMasv() + "";
                Xoasinhvien(item);
                arrayList.remove(position);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void ThemSV() {
        boolean isValidAdd = !TextUtils.isEmpty(txtmanv.getText().toString().trim())
                && !TextUtils.isEmpty(txttennv.getText().toString().trim());
        if (isValidAdd) {
            ContentValues sv1 = new ContentValues();
            int check = rdg.getCheckedRadioButtonId();
            sv1.put(DataBaseHelper.KEY_SV_ID, txtmanv.getText().toString());
            sv1.put(DataBaseHelper.KEY_SV_NAME, txttennv.getText().toString());
            if (check == R.id.rdbGTnam)
                sv1.put(DataBaseHelper.KEY_SV_PH_NO, "nam");
            else
                sv1.put(DataBaseHelper.KEY_SV_PH_NO, "nữ");
            if (db.insert("sinhvien", null, sv1) == -1) {
                Toast.makeText(this, "Thêm sinh viên thất bại", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Thêm sinh viên thành công", Toast.LENGTH_LONG).show();
            }
            Sinhvien sv = new Sinhvien();
            sv.setMasv(txtmanv.getText().toString().trim());
            sv.setTensv(txttennv.getText().toString().trim());
            if (check == R.id.rdbGTnam)
                sv.setNam(true);
            else
                sv.setNam(false);

            arrayList.add(sv);
            adapter.notifyDataSetChanged();
            txtmanv.setText("");
            txttennv.setText("");
        } else {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public List<Sinhvien> xemdssinhvien() {
        List<Sinhvien> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor c = db.query("sinhvien", null, null, null, null, null, null);
        try {
            while (c.moveToNext()) {
                Sinhvien sv = new Sinhvien();
                sv.setMasv(c.getString(0));
                sv.setTensv(c.getString(1));
                sv.setDiemTK(c.isNull(3) ? null : c.getInt(3));
                sv.setDiemGK(c.isNull(4) ? null : c.getInt(4));
                sv.setDiemCK(c.isNull(5) ? null : c.getInt(5));
                String gioiTinh = c.getString(2);
                if ("nam".equalsIgnoreCase(gioiTinh))
                    sv.setNam(true);
                else
                    sv.setNam(false);
                list.add(sv);
            }
        } finally {
            c.close();
        }
        return list;
    }

    private void xuLyThongKe() {
        Toast.makeText(this, "Xử lý thống kê", Toast.LENGTH_SHORT).show();
    }

    public void Xoasinhvien(String masv) {
        if (db.delete("sinhvien", "masv=?", new String[]{masv}) != 0) {
            Toast.makeText(this, "Xoá sinh viên thành công", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Xoá thất bại", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        arrayList.clear();
        arrayList.addAll(xemdssinhvien());
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.action_thong_ke:
                xuLyThongKe();
                break;
        }
        return true;
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_sinh_vien, menu);
//        return true;
//    }

}


