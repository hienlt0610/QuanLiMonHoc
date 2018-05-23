
package com.example.thienml.quanlimonhoc.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.thienml.quanlimonhoc.util.DataBaseHelper;
import com.example.thienml.quanlimonhoc.adapter.MyAdapter;
import com.example.thienml.quanlimonhoc.R;
import com.example.thienml.quanlimonhoc.model.Sinhvien;

import java.util.ArrayList;
import java.util.List;

public class TimSinhVienActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String KEY_SINH_VIEN = "SINH_VIEN";
    ListView lvSinhVien = null;
    ArrayList<Sinhvien> arrayList = null;
    MyAdapter adapter = null;
    DataBaseHelper dbHelper;
    SQLiteDatabase db;
    private Button btnSearch;
    private EditText edtMaSv, edtTenSV;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, TimSinhVienActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_sinh_vien);
        //Set tiêu đề
        setTitle(R.string.tim_sinh_vien);
        setHomeAsupEnable(true);
        //Init database
        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();

        lvSinhVien = (ListView) findViewById(R.id.listSV);
        btnSearch = findViewById(R.id.btnTim);
        edtMaSv = findViewById(R.id.edtMaSV);
        edtTenSV = findViewById(R.id.edtTenSV);
        btnSearch.setOnClickListener(this);
        lvSinhVien.setOnItemClickListener(this);


        arrayList = (ArrayList<Sinhvien>) layDanhSachSV(null, null);
        adapter = new MyAdapter(this, R.layout.custom_listview, arrayList); //khởi tạo adapter
        //Không hiển bảng điểm do đang ở màn hình quản lý sinh viên
        adapter.setShowInfoOnly(true);
        lvSinhVien.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Khi bấm nút back thì trở về màn hình trước đó
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public List<Sinhvien> layDanhSachSV(String maSV, String tenSV) {
        List<Sinhvien> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        //Chạy câu lệnh sql lấy sinh viên theo mã và tên
        builder.append("SELECT * FROM ");
        builder.append(DataBaseHelper.TABLE_SV);
        if (!TextUtils.isEmpty(maSV) || !TextUtils.isEmpty(tenSV)) {
            builder.append(" WHERE ");
            if (!TextUtils.isEmpty(maSV)) {
                builder.append(DataBaseHelper.KEY_SV_ID).append(" LIKE ").append("'%").append(maSV).append("%'");
                if (!TextUtils.isEmpty(tenSV)) {
                    builder.append(" AND ");
                }
            }
            if (!TextUtils.isEmpty(tenSV)) {
                builder.append(DataBaseHelper.KEY_SV_NAME).append(" LIKE ").append("'%").append(tenSV).append("%'");
            }
        }

        Cursor c = db.rawQuery(builder.toString(), null);
//        Cursor c = db.query(DataBaseHelper.TABLE_SV, null, null, null, null, null, null);
        try {
            while (c.moveToNext()) {
                Sinhvien sv = new Sinhvien();
                sv.setMasv(c.getString(0));
                sv.setTensv(c.getString(1));
//                sv.setDiemTK(c.isNull(3) ? null : c.getInt(3));
//                sv.setDiemGK(c.isNull(4) ? null : c.getInt(4));
//                sv.setDiemCK(c.isNull(5) ? null : c.getInt(5));
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

    /**
     * Xử lý sự kiện khi click nút tìm sinh viên
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        //Lấy mã và tên của sinh viên cần tìm
        String maSv = edtMaSv.getText().toString().trim();
        String tenSv = edtTenSV.getText().toString().trim();

        //Lấy danh sách sinh viên tương ứng với những thông tin cần tìm và hiển thị ra màn hình
        List<Sinhvien> dsSinhVien = layDanhSachSV(maSv, tenSv);
        adapter.setListSv(dsSinhVien);
    }

    /**
     * Xử lý sự kiện khi click vào item của danh sách sinh viên
     *
     * @param adapterView
     * @param view
     * @param pos         Vị trí item đc click
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        //Khi click vào item của list thì chọn sinh viên đó và thêm nó vào màn hình thông tin lớp học
        Sinhvien item = adapter.getItem(pos);
        Intent intent = new Intent();
        intent.putExtra(KEY_SINH_VIEN, item);
        setResult(RESULT_OK, intent);
        //Tắt màn hình
        this.finish();
    }
}
