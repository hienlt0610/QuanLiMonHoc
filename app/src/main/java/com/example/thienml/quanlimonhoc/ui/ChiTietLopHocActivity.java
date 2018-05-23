package com.example.thienml.quanlimonhoc.ui;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thienml.quanlimonhoc.util.DataBaseHelper;
import com.example.thienml.quanlimonhoc.adapter.MyAdapter;
import com.example.thienml.quanlimonhoc.R;
import com.example.thienml.quanlimonhoc.model.Sinhvien;
import com.example.thienml.quanlimonhoc.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class ChiTietLopHocActivity extends BaseActivity {
    private static final String KEY_MA_LOP = "MA_LOP";
    private static final String KEY_TENLOP = "TENLOP";
    public static final int REQUEST_SINH_VIEN = 100;
    ListView lvSinhVien = null;
    ArrayList<Sinhvien> arrayList = null;
    MyAdapter adapter = null;
    DataBaseHelper dbHelper;
    SQLiteDatabase db;
    private String maLop;
    private String tenLop;

    public static Intent newIntent(Context context, String maLop, String tenLop) {
        Intent intent = new Intent(context, ChiTietLopHocActivity.class);
        intent.putExtra(KEY_MA_LOP, maLop);
        intent.putExtra(KEY_TENLOP, tenLop);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thongtin_lop);
        initArgument();
        setTitle(tenLop);
        setHomeAsupEnable(true);
        //Init database
        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();
        lvSinhVien = (ListView) findViewById(R.id.listSV);

        arrayList = (ArrayList<Sinhvien>) xemdssinhvien();
        adapter = new MyAdapter(this, R.layout.custom_listview, arrayList); //khởi tạo adapter
        //Cho phép hiển thị bảng điểm
        adapter.setShowInfoOnly(false);
        lvSinhVien.setAdapter(adapter);                                 //gán adapter vào listview

        //sự kiện ấn vào ListView sinh viên để thay đổi thuộc tính
        lvSinhVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String maSV = arrayList.get(position).getMasv();
//                Intent in = new Intent(getApplicationContext(), ChiTietSinhVienActivity.class);
//                in.putExtra("tramasv", item);
                Intent intent = ChiTietSinhVienActivity.intentUpdateDiem(ChiTietLopHocActivity.this, maSV, maLop);
                startActivityForResult(intent, 0);
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

    private void initArgument() {
        Intent intent = getIntent();
        if (intent != null) {
            maLop = intent.getStringExtra(KEY_MA_LOP);
            tenLop = intent.getStringExtra(KEY_TENLOP);
        }
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


    public List<Sinhvien> xemdssinhvien() {
        List<Sinhvien> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        //Run lệnh sql lấy danh sách sinh viên của lớp
        String query = "SELECT * FROM " + DataBaseHelper.TABLE_LOP_HOC_SV + " LHSV, " + DataBaseHelper.TABLE_SV
                + " SV ON LHSV.masv = SV.masv WHERE LHSV.MaLop = '" + maLop + "'";
        Cursor c = db.rawQuery(query, null);
        try {
            while (c.moveToNext()) {
                Sinhvien sv = new Sinhvien();
                sv.setMasv(c.getString(c.getColumnIndex(DataBaseHelper.KEY_SV_ID)));
                sv.setTensv(c.getString(c.getColumnIndex(DataBaseHelper.KEY_SV_NAME)));
                String gioiTinh = c.getString(c.getColumnIndex(DataBaseHelper.KEY_SV_PH_NO));
                if ("nam".equalsIgnoreCase(gioiTinh))
                    sv.setNam(true);
                else
                    sv.setNam(false);
                //Lấy điểm CK
                if (!c.isNull(c.getColumnIndex(DataBaseHelper.KEY_SV_CK))) {
                    String diem = c.getString(c.getColumnIndex(DataBaseHelper.KEY_SV_CK));
                    sv.setDiemCK(Utils.strToInt(diem, null));
                }
                //Lấy điểm TK
                if (!c.isNull(c.getColumnIndex(DataBaseHelper.KEY_SV_TK))) {
                    String diem = c.getString(c.getColumnIndex(DataBaseHelper.KEY_SV_TK));
                    sv.setDiemTK(Utils.strToInt(diem, null));
                }
                //Lấy điểm GK
                if (!c.isNull(c.getColumnIndex(DataBaseHelper.KEY_SV_GK))) {
                    String diem = c.getString(c.getColumnIndex(DataBaseHelper.KEY_SV_GK));
                    sv.setDiemGK(Utils.strToInt(diem, null));
                }
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
        if (db.delete(DataBaseHelper.TABLE_LOP_HOC_SV, DataBaseHelper.KEY_SV_ID + "=? AND " + DataBaseHelper.KEY_LOP_HOC_MA_LOP + "=?", new String[]{masv, maLop}) != 0) {
            Toast.makeText(this, "Xoá sinh viên thành công", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Xoá thất bại", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_SINH_VIEN && data != null) {
            //Nhận thông tin sinh viên từ màn hình tìm sinh viên được chọn
            Sinhvien itemSinhVien = (Sinhvien) data.getSerializableExtra(TimSinhVienActivity.KEY_SINH_VIEN);
            themSinhVienVaoLopHoc(itemSinhVien);
        }
        arrayList.clear();
        arrayList.addAll(xemdssinhvien());
        adapter.notifyDataSetChanged();
    }

    /**
     * Thêm sinh viên vào lớp học
     *
     * @param sv Thông tin sinh viên
     */
    private void themSinhVienVaoLopHoc(Sinhvien sv) {
        if (sv == null) return;
        //Chỉ thêm sinh viên vào lớp khi lớp ko có sinh viên này
        if (!dbHelper.ktSvTonTaiTrongLop(sv.getMasv(), maLop)) {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DataBaseHelper.KEY_SV_ID, sv.getMasv());
            values.put(DataBaseHelper.KEY_LOP_HOC_MA_LOP, maLop);
            values.putNull(DataBaseHelper.KEY_SV_TK);
            values.putNull(DataBaseHelper.KEY_SV_GK);
            values.putNull(DataBaseHelper.KEY_SV_CK);
            long insert = db.insert(DataBaseHelper.TABLE_LOP_HOC_SV, null, values);
            db.close();
        } else {
            Toast.makeText(this, "Sinh viên này đã tồn tại trong lớp, vui lòng thêm sinh viên khác", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chi_tiet_lop_hoc, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Khi bấm nút thêm sinh viên thì mở màn hình danh sách sinh viên và chọn sinh viên từ đó
        if (item.getItemId() == R.id.menuAdd) {
            Intent intent = TimSinhVienActivity.newIntent(this);
            startActivityForResult(intent, REQUEST_SINH_VIEN);
        } else if (item.getItemId() == android.R.id.home) {
            //Khi bấm nút back thì trở về màn hình trước đó
            this.finish();
        } else if (item.getItemId() == R.id.action_thong_ke) {
            //Khi click vào nút thống kê thì chuyển hướng sang màn hình thống kê lớp
            startActivity(ThongKeLopActivity.newIntent(this, maLop));
        }
        return super.onOptionsItemSelected(item);
    }
}

