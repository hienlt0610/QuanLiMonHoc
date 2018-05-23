package com.example.thienml.quanlimonhoc;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ChiTietSinhVienActivity extends BaseActivity {
    private static final String KEY_MA_SV = "MA_SV";
    private static final String KEY_MA_LOP = "MA_LOP";
    private static final String KEY_FLAG_UPDATE_INFO_ONLY = "FLAG_UPDATE";
    SQLiteDatabase database = null;
    DataBaseHelper dbHelper;
    String maSv = "";
    String maLop = "";
    boolean isUpdateInfo;
    Button btncapnhat, btnback;
    private RadioGroup rgGT;
    private RadioButton rbNam;
    private RadioButton rbNu;
    private LinearLayout llDiem;
    private EditText txtTK1, txtGK1, txtCK1;
    private Sinhvien svChiTiet;
    private EditText edtTongKEt;

    /**
     * Intent mở màn hình update thông tin của sinh vien
     *
     * @param context Context
     * @param maSV    Mã sinh viên
     * @return
     */
    public static Intent intentUpdateThongTinSV(Context context, String maSV) {
        Intent intent = new Intent(context, ChiTietSinhVienActivity.class);
        intent.putExtra(KEY_MA_SV, maSV);
        intent.putExtra(KEY_FLAG_UPDATE_INFO_ONLY, true);
        return intent;
    }

    /**
     * Intent mở màn hình update điểm của sinh viên
     *
     * @param context Context
     * @param maSv    Mã Sinh viên
     * @param maLop   Mã lớp
     * @return
     */
    public static Intent intentUpdateDiem(Context context, String maSv, String maLop) {
        Intent intent = new Intent(context, ChiTietSinhVienActivity.class);
        intent.putExtra(KEY_MA_SV, maSv);
        intent.putExtra(KEY_MA_LOP, maLop);
        intent.putExtra(KEY_FLAG_UPDATE_INFO_ONLY, false);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongtinsinhvien);
        setHomeAsupEnable(true);
        initArgument();
        dbHelper = new DataBaseHelper(this);
        database = dbHelper.getWritableDatabase();
        rgGT = findViewById(R.id.rdgGT);
        rbNam = findViewById(R.id.rdbGTnam);
        rbNu = findViewById(R.id.rdbGTnu);
        llDiem = findViewById(R.id.ll_sv_diem);
        txtTK1 = (EditText) findViewById(R.id.edtTK);
        txtGK1 = (EditText) findViewById(R.id.edtGK);
        txtCK1 = (EditText) findViewById(R.id.edtCK);
        TextView tvMaSv1 = (TextView) findViewById(R.id.txtMaSV1);
        EditText txtTenSV1 = (EditText) findViewById(R.id.txtTenSV);
        edtTongKEt = findViewById(R.id.tvTongKetGiaTri);

        //Không cho phép sửa mã sinh viên
        tvMaSv1.setEnabled(false);

        if (isUpdateInfo) {
            //Ẩn bảng điểm khi chỉ cho phép chỉnh sửa thông tin của sinh viên
            llDiem.setVisibility(View.GONE);
            //Lấy thông tin sinh viên
            svChiTiet = layThongTinSinhVien(maSv);
        } else {
            //Khi cập cập điểm của sinh viên thuộc lớp, cho phép sửa điểm và disable cập nhật thông tin sinh viên
            tvMaSv1.setEnabled(false);
            txtTenSV1.setEnabled(false);
            rbNam.setEnabled(false);
            rbNu.setEnabled(false);
            //Lấy thông tin sinh viên và bảng điểm
            svChiTiet = layThongTinSinhVien(maSv, maLop);
        }

        InputFilterMinMax filterMinMax = new InputFilterMinMax(0, 10);
        txtTK1.setFilters(new InputFilter[]{filterMinMax});
        txtGK1.setFilters(new InputFilter[]{filterMinMax});
        txtCK1.setFilters(new InputFilter[]{filterMinMax});
        //Xử lý hiển thị thông tin sinh viên lên giao diện
        if (svChiTiet != null) {
            //Set title cho trang
            setTitle(getString(R.string.thong_tin_sv_title) + svChiTiet.getTensv());
            //Hiển thị mã sinh viên
            tvMaSv1.setText(svChiTiet.getMasv());
            txtTenSV1.setText(svChiTiet.getTensv());

            //Hiển thị bảng điểm nếu có
            if (svChiTiet.getDiemCK() != null) {
                txtCK1.setText(String.valueOf(svChiTiet.getDiemCK()));
            }
            if (svChiTiet.getDiemGK() != null) {
                txtGK1.setText(String.valueOf(svChiTiet.getDiemGK()));
            }
            if (svChiTiet.getDiemTK() != null) {
                txtTK1.setText(String.valueOf(svChiTiet.getDiemTK()));
            }
            //Cập nhập checkbox giới tính
            if (svChiTiet.isNam()) {
                rbNam.setChecked(true);
            } else {
                rbNu.setChecked(true);
            }

            //Hiển thị điểm tổng kết
            tinhDiemCuoiKy();
        }
        btncapnhat = findViewById(R.id.btnCapNhat);
        btncapnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capnhatsinhvien();
                tinhDiemCuoiKy();
            }
        });
        btnback = findViewById(R.id.btnBack);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChiTietSinhVienActivity.this.finish();
            }
        });
    }

    /**
     * Lấy các thông tin nhận được khi chuyển sang màn hình thông tin
     */
    private void initArgument() {
        Intent intent = getIntent();
        if (intent != null) {
            //Thông tin mã sinh viên
            maSv = intent.getStringExtra(KEY_MA_SV);
            //Thông tin mã lớp
            maLop = intent.getStringExtra(KEY_MA_LOP);
            //Cờ nhận biết đang ở chế độ sửa thông tin hay sửa điểm
            //true nếu chỉ cho phép sửa thông tin của sinh viên
            //false nếu cho phép sửa điểm của sinh viên
            isUpdateInfo = intent.getBooleanExtra(KEY_FLAG_UPDATE_INFO_ONLY, true);
        }
    }

    public void capnhatsinhvien() {
        TextView tvMaSv1 = (TextView) findViewById(R.id.txtMaSV1);
        EditText txtTenSV1 = (EditText) findViewById(R.id.txtTenSV);

        ContentValues sv = new ContentValues();
        sv.put("tensv", txtTenSV1.getText().toString().trim());
        String gioiTinh = "nam";
        switch (rgGT.getCheckedRadioButtonId()) {
            case R.id.rdbGTnam:
                gioiTinh = "nam";
                break;
            case R.id.rdbGTnu:
                gioiTinh = "nu";
                break;
        }
        sv.put(DataBaseHelper.KEY_SV_PH_NO, gioiTinh);
        String whereClause = DataBaseHelper.KEY_SV_ID + " = ?";
        String[] params = new String[]{tvMaSv1.getText().toString()};
        //Nếu có thể update điểm của sinh viên thì update điểm vào CSDL (Sửa điểm của sinh viên trong lớp học)
        boolean isUpdateDiem = true;
        if (!isUpdateInfo) {
            isUpdateDiem = updateDiem();
        }
        database = dbHelper.getWritableDatabase();
        int update = database.update(DataBaseHelper.TABLE_SV, sv, whereClause, params);
        if (update > 0 && isUpdateDiem) {
            Toast.makeText(this, "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Có lỗi", Toast.LENGTH_SHORT).show();
        }
        database.close();
    }

    /**
     * Thực thi cập nhật điểm sinh viên trong lớp
     *
     * @return
     */
    private boolean updateDiem() {
        //Lấy điểm được nhập
        Integer diemTK = Utils.strToInt(txtTK1.getText().toString().trim(), null);
        Integer diemGK = Utils.strToInt(txtGK1.getText().toString().trim(), null);
        Integer diemCK = Utils.strToInt(txtCK1.getText().toString().trim(), null);
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.KEY_SV_TK, diemTK);
        values.put(DataBaseHelper.KEY_SV_GK, diemGK);
        values.put(DataBaseHelper.KEY_SV_CK, diemCK);
        database = dbHelper.getWritableDatabase();
        String whereClause = DataBaseHelper.KEY_SV_ID + " = ? AND " + DataBaseHelper.KEY_LOP_HOC_MA_LOP + " = ?";
        String[] whereArg = new String[]{maSv, maLop};
        int update = database.update(DataBaseHelper.TABLE_LOP_HOC_SV, values, whereClause, whereArg);
        database.close();
        return update > 0;
    }

    /**
     * Lấy thông tin sinh vien thuộc lớp
     *
     * @param maSv  Mã sinh viên
     * @param maLop Mã lớp
     * @return
     */
    private Sinhvien layThongTinSinhVien(String maSv, String maLop) {
        String query = "SELECT *";
        query += " FROM " + DataBaseHelper.TABLE_LOP_HOC_SV + " lhsv, " + DataBaseHelper.TABLE_SV + " sv";
        query += " ON";
        query += " lhsv." + DataBaseHelper.KEY_SV_ID + " = sv." + DataBaseHelper.KEY_SV_ID;
        query += " WHERE lhsv." + DataBaseHelper.KEY_SV_ID + " = ?";
        query += " AND lhsv." + DataBaseHelper.KEY_LOP_HOC_MA_LOP + " = ?";
        String[] params = new String[]{maSv, maLop};
        Cursor cursor = database.rawQuery(query, params);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Sinhvien sinhvien = new Sinhvien();
                    sinhvien.setMasv(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SV_ID)));
                    sinhvien.setTensv(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SV_NAME)));
                    String gioiTinh = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SV_PH_NO));
                    sinhvien.setNam("nam".equalsIgnoreCase(gioiTinh));
                    //Lấy điểm CK
                    if (!cursor.isNull(cursor.getColumnIndex(DataBaseHelper.KEY_SV_CK))) {
                        String diem = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SV_CK));
                        sinhvien.setDiemCK(Utils.strToInt(diem, null));
                    }
                    //Lấy điểm TK
                    if (!cursor.isNull(cursor.getColumnIndex(DataBaseHelper.KEY_SV_TK))) {
                        String diem = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SV_TK));
                        sinhvien.setDiemTK(Utils.strToInt(diem, null));
                    }
                    //Lấy điểm GK
                    if (!cursor.isNull(cursor.getColumnIndex(DataBaseHelper.KEY_SV_GK))) {
                        String diem = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SV_GK));
                        sinhvien.setDiemGK(Utils.strToInt(diem, null));
                    }
                    return sinhvien;
                }
                while (cursor.moveToNext());
            }
            database.close();
        }
        return null;
    }

    /**
     * Lấy thông tin sinh vien
     *
     * @param maSv
     * @return
     */
    private Sinhvien layThongTinSinhVien(String maSv) {
        String whereClause = DataBaseHelper.KEY_SV_ID + " = ?";
        String[] params = new String[]{maSv};
        Cursor cursor = database.query(DataBaseHelper.TABLE_SV, null, whereClause, params, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Sinhvien sinhvien = new Sinhvien();
                    sinhvien.setMasv(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SV_ID)));
                    sinhvien.setTensv(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SV_NAME)));
                    String gioiTinh = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_SV_PH_NO));
                    sinhvien.setNam("nam".equalsIgnoreCase(gioiTinh));
                    return sinhvien;
                }
                while (cursor.moveToNext());
            }
            database.close();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Khi bấm nút back thì trở về màn hình trước đó
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void tinhDiemCuoiKy() {
        float diemTongKet = 0;
        //Lấy điểm trong các field đc nhập
        Integer diemCK = Utils.strToInt(txtCK1.getText().toString().trim(), null);
        Integer diemGK = Utils.strToInt(txtGK1.getText().toString().trim(), null);
        Integer diemTK = Utils.strToInt(txtTK1.getText().toString().trim(), null);
        //Tính toán điểm tổng kết
        if (diemCK != null) {
            diemTongKet += 0.5f * diemCK;
        }

        if (diemGK != null) {
            diemTongKet += 0.3f * diemGK;
        }

        if (diemTK != null) {
            diemTongKet += 0.2f * diemTK;
        }
        edtTongKEt.setText(String.valueOf(diemTongKet));
    }
}

