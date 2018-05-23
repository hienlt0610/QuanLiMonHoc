package com.example.thienml.quanlimonhoc.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.thienml.quanlimonhoc.model.DanhGia;
import com.example.thienml.quanlimonhoc.model.LopHoc;
import com.example.thienml.quanlimonhoc.model.Sinhvien;

import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DataBaseHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "qlsv";

    // Contacts table name
    public static final String TABLE_SV = "sinhvien";
    public static final String TABLE_LOP_HOC = "LopHoc";
    public static final String TABLE_LOP_HOC_SV = "LopHocSV";
    public static final String TABLE_DG = "DanhGia";

    // Contacts Table Columns names
    public static final String KEY_SV_ID = "masv";
    public static final String KEY_SV_NAME = "tensv";
    public static final String KEY_SV_PH_NO = "gioitinh";
    public static final String KEY_SV_TK = "diemtk";
    public static final String KEY_SV_GK = "diemgk";
    public static final String KEY_SV_CK = "diemck";

    public static final String KEY_LOP_HOC_ID = "LopId";
    public static final String KEY_LOP_HOC_MA_LOP = "MaLop";
    public static final String KEY_LOP_HOC_TEN_LOP = "TenLop";
    public static final String KEY_LOP_HOC_KN_DG = "KinhNghiemDanhGia";

    public static final String KEY_DG_ID = "DgId";
    public static final String KEY_DG_BAI_THI = "BaiThi";
    public static final String KEY_DG_CHUAN_DAU_RA = "ChuanDauRa";
    public static final String KEY_DG_CAU_HOI = "CauHoi";
    public static final String KEY_DG_SV_DAT = "SVDat";
    public static final String KEY_DG_SV_KDAT = "SVKhongDat";
    public static final String KEY_DG_NHAN_XET = "NhanXet";

    private Context context;
    SQLiteDatabase database;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
//        Toast.makeText(context, "T?o b?ng", Toast.LENGTH_LONG).show();
        String queryTableSinhvien = "CREATE TABLE " + TABLE_SV + "("
                + KEY_SV_ID + " VARCHAR PRIMARY KEY," + KEY_SV_NAME + " TEXT,"
                + KEY_SV_PH_NO + " TEXT)";

        String queryCreateLopHoc = "CREATE TABLE IF NOT EXISTS " + TABLE_LOP_HOC + "("
                + KEY_LOP_HOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_LOP_HOC_MA_LOP + " VARCHAR(200),"
                + KEY_LOP_HOC_KN_DG + " VARCHAR(1000),"
                + KEY_LOP_HOC_TEN_LOP + " VARCHAR(200))";

        String queryLopHocSV = "CREATE TABLE " + TABLE_LOP_HOC_SV + "("
                + KEY_SV_ID + " VARCHAR,"
                + KEY_LOP_HOC_MA_LOP + " VARCHAR,"
                + KEY_SV_TK + " INTEGER DEFAULT (null)," + KEY_SV_GK
                + " INTEGER DEFAULT (null),"
                + KEY_SV_CK + " INTEGER DEFAULT (null)" + ")";

        String queryTableDanhGia = "CREATE TABLE " + TABLE_DG + "("
                + KEY_LOP_HOC_MA_LOP + " VARCHAR,"
                + KEY_DG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DG_BAI_THI + " TEXT,"
                + KEY_DG_CHUAN_DAU_RA + " TEXT,"
                + KEY_DG_CAU_HOI + " TEXT,"
                + KEY_DG_SV_DAT + " TEXT,"
                + KEY_DG_SV_KDAT + " TEXT,"
                + KEY_DG_NHAN_XET + " TEXT)";

        db.execSQL(queryTableSinhvien);
        db.execSQL(queryLopHocSV);
        db.execSQL(queryCreateLopHoc);
        db.execSQL(queryTableDanhGia);

        Log.d(TAG, queryTableSinhvien);
        Log.d(TAG, queryLopHocSV);
        Log.d(TAG, queryCreateLopHoc);
        Log.d(TAG, queryTableDanhGia);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOP_HOC_SV);

        // Create tables again
        onCreate(db);
    }

    public void QueryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public Cursor GetData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    private Integer getIntCol(Cursor c, String colName, Integer defaultValue) {
        Integer value = defaultValue;
        if (!c.isNull(c.getColumnIndex(colName))) {
            value = c.getInt(c.getColumnIndex(colName));
        }
        return value;
    }

    private Long getLongCol(Cursor c, String colName, Long defaultValue) {
        Long value = defaultValue;
        if (!c.isNull(c.getColumnIndex(colName))) {
            value = c.getLong(c.getColumnIndex(colName));
        }
        return value;
    }

    public SQLiteDatabase openDb() {
        return getWritableDatabase();
    }

    public boolean ktSvTonTaiTrongLop(String maSv, String maLop) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = KEY_SV_ID + " = ? AND " + KEY_LOP_HOC_MA_LOP + " = ?";
        String[] params = new String[]{maSv, maLop};
        Cursor cursor = db.query(TABLE_LOP_HOC_SV, null, selection, params, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return true;
        }
        db.close();
        return false;
    }

    public LopHoc layThongTinLopHoc(String maLop) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = KEY_LOP_HOC_MA_LOP + " = ?";
        String[] params = new String[]{maLop};
        Cursor cursor = db.query(TABLE_LOP_HOC, null, selection, params, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int cId = cursor.getInt(cursor.getColumnIndex(KEY_LOP_HOC_ID));
            String cMaLop = cursor.getString(cursor.getColumnIndex(KEY_LOP_HOC_MA_LOP));
            String cTenLop = cursor.getString(cursor.getColumnIndex(KEY_LOP_HOC_TEN_LOP));
            String cKNDanhGia = cursor.getString(cursor.getColumnIndex(KEY_LOP_HOC_KN_DG));
            LopHoc lopHoc = new LopHoc(String.valueOf(cId), cMaLop, cTenLop);
            lopHoc.setKnDanhGia(cKNDanhGia);
            return lopHoc;
        }
        db.close();
        return null;
    }

    public boolean taoLop(LopHoc lopHoc) {
        database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOP_HOC_MA_LOP, lopHoc.getMaLop());
        values.put(KEY_LOP_HOC_TEN_LOP, lopHoc.getTenLop());
        values.put(KEY_LOP_HOC_KN_DG, lopHoc.getKnDanhGia());
        long insert = database.insert(TABLE_LOP_HOC, null, values);
        database.close();
        return insert > 0;
    }

    public List<Sinhvien> layDsSinhVienCuaLop(String maLop) {
        List<Sinhvien> list = new ArrayList<>();
        database = getReadableDatabase();
        //Run lệnh sql lấy danh sách sinh viên của lớp
        String query = "SELECT * FROM " + DataBaseHelper.TABLE_LOP_HOC_SV + " LHSV, " + DataBaseHelper.TABLE_SV
                + " SV ON LHSV.masv = SV.masv WHERE LHSV.MaLop = '" + maLop + "'";
        Cursor c = database.rawQuery(query, null);
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
        database.close();
        return list;
    }

    public List<LopHoc> layDSLopHoc() {
        List<LopHoc> list = new ArrayList<>();
        database = getReadableDatabase();
        Cursor c = database.query(TABLE_LOP_HOC, null, null, null, null, null, null);
        try {
            while (c.moveToNext()) {
                LopHoc lopHoc = new LopHoc();
                lopHoc.setId(c.getString(c.getColumnIndex(KEY_LOP_HOC_ID)));
                lopHoc.setMaLop(c.getString(c.getColumnIndex(KEY_LOP_HOC_MA_LOP)));
                lopHoc.setTenLop(c.getString(c.getColumnIndex(KEY_LOP_HOC_TEN_LOP)));
                lopHoc.setKnDanhGia(c.getString(c.getColumnIndex(KEY_LOP_HOC_KN_DG)));
                list.add(lopHoc);
            }
        } finally {
            c.close();
        }
        database.close();
        return list;
    }

    public List<DanhGia> layDanhGiaLop(String maLop) {
        database = getReadableDatabase();
        List<DanhGia> list = new ArrayList<>();
        String selection = KEY_LOP_HOC_MA_LOP + " = ?";
        String[] params = new String[]{maLop};
        Cursor c = database.query(TABLE_DG, null, selection, params, null, null, null);
        try {
            while (c.moveToNext()) {
                DanhGia danhGia = new DanhGia();
                danhGia.setId(getIntCol(c, KEY_DG_ID, null));
                danhGia.setBaiThi(c.getString(c.getColumnIndex(KEY_DG_BAI_THI)));
                danhGia.setCauHoiDeThi(c.getString(c.getColumnIndex(KEY_DG_CAU_HOI)));
                danhGia.setChuanDauRa(c.getString(c.getColumnIndex(KEY_DG_CHUAN_DAU_RA)));
                danhGia.setNhanXet(c.getString(c.getColumnIndex(KEY_DG_NHAN_XET)));
                danhGia.setSoSVDat(getIntCol(c, KEY_DG_SV_DAT, null));
                danhGia.setSoSVKhongDat(getIntCol(c, KEY_DG_SV_KDAT, null));
                list.add(danhGia);
            }
        } finally {
            c.close();
        }
        database.close();
        return list;
    }

    /**
     * Lưu lại danh sách đánh giá vào csdl
     *
     * @return
     */
    public boolean luuDsDanhGia(String maLop, List<DanhGia> data) {
        database = getWritableDatabase();
        database.beginTransaction();
        for (DanhGia danhGia : data) {
            ContentValues values = new ContentValues();
            values.put(DataBaseHelper.KEY_DG_ID, danhGia.getId());
            values.put(DataBaseHelper.KEY_DG_BAI_THI, danhGia.getBaiThi());
            values.put(DataBaseHelper.KEY_DG_CHUAN_DAU_RA, danhGia.getChuanDauRa());
            values.put(DataBaseHelper.KEY_DG_CAU_HOI, danhGia.getCauHoiDeThi());
            values.put(DataBaseHelper.KEY_DG_SV_DAT, danhGia.getSoSVDat());
            values.put(DataBaseHelper.KEY_DG_SV_KDAT, danhGia.getSoSVKhongDat());
            values.put(DataBaseHelper.KEY_DG_NHAN_XET, danhGia.getNhanXet());
            values.put(DataBaseHelper.KEY_LOP_HOC_MA_LOP, maLop);
            database.insertWithOnConflict(TABLE_DG, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
        return true;
    }

    public boolean luuKinhNghiemDanhGia(String maLop, String kinhNghiem) {
        database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOP_HOC_KN_DG, kinhNghiem);
        String selection = KEY_LOP_HOC_MA_LOP + " = ?";
        String[] params = new String[]{maLop};
        int update = database.update(TABLE_LOP_HOC, values, selection, params);
        database.close();
        return update > 0;
    }

    public boolean xoaDanhGia(int id) {
        database = getWritableDatabase();
        String selection = KEY_DG_ID + " = ?";
        String[] params = new String[]{String.valueOf(id)};
        int delete = database.delete(TABLE_DG, selection, params);
        database.close();
        return delete > 0;
    }
}

