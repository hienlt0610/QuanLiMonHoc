package com.example.thienml.quanlimonhoc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuanLyLopHocActivity extends BaseActivity {
    DataBaseHelper dbHelper;

    ListView lvLopHoc;
    ArrayList<LopHoc> arrayLopHoc;
    LopHocAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ql_lop);
        setTitle(R.string.ql_lop_hoc_title);
        setHomeAsupEnable(true);
        lvLopHoc = (ListView) findViewById(R.id.listviewLopHoc);
        arrayLopHoc = new ArrayList<>();
        adapter = new LopHocAdapter(this, R.layout.dong_lop_hoc, arrayLopHoc);
        lvLopHoc.setAdapter(adapter);
        //sự kiện nhấn vào listview lớp --> main 2
        lvLopHoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //i trả về vị trí click trên listview
                LopHoc lopHoc = arrayLopHoc.get(i);
                Intent in = ChiTietLopHocActivity.newIntent(QuanLyLopHocActivity.this, lopHoc.getMaLop(), lopHoc.getTenLop());
                startActivity(in);
            }
        });

        //khoi tao database
        dbHelper = new DataBaseHelper(this);
        //tao bang LopHoc
        GetDataLopHoc();
    }


    private void GetDataLopHoc() {
        arrayLopHoc.clear();
        arrayLopHoc.addAll(dbHelper.layDSLopHoc());
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAdd) {
            DialogThem();
        } else if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogThem() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_them_lop);
        dialog.setCancelable(false);
        //Ánh xạ
        final EditText edtTenLop = (EditText) dialog.findViewById(R.id.edittextTenLop);
        final EditText edtMaLop = (EditText) dialog.findViewById(R.id.edittextMaLop);
        Button btnThem = (Button) dialog.findViewById(R.id.buttonThem);
        Button btnHuy = (Button) dialog.findViewById(R.id.buttonHuy);


        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String malop = edtMaLop.getText().toString();
                String tenlop = edtTenLop.getText().toString();
                if (tenlop.equals("") || malop.equals("")) {
                    Toast.makeText(QuanLyLopHocActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    LopHoc lopHoc = new LopHoc();
                    lopHoc.setMaLop(malop);
                    lopHoc.setTenLop(tenlop);
                    boolean isSuccess = dbHelper.taoLop(lopHoc);
                    if (isSuccess) {
                        Toast.makeText(QuanLyLopHocActivity.this, "Thêm lớp thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QuanLyLopHocActivity.this, "Thêm lớp thất bại!!!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                    GetDataLopHoc();
                }
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void DialogDelete(final String tenlop, final String id) {
        AlertDialog.Builder dialogdelete = new AlertDialog.Builder(this);
        dialogdelete.setMessage("Bạn có muốn xóa lớp " + tenlop + " không?");
        dialogdelete.setCancelable(false);
        dialogdelete.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.QueryData("DELETE FROM LopHoc WHERE Id ='" + id + "'");
                Toast.makeText(QuanLyLopHocActivity.this, "Đã xóa lớp " + tenlop, Toast.LENGTH_SHORT).show();
                GetDataLopHoc();
            }
        });
        dialogdelete.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialogdelete.show();
    }

    public void DialogEdit(String ma, String ten, final String id) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit);
        dialog.setCancelable(false);
        final EditText edtMaEdit = (EditText) dialog.findViewById(R.id.edittextMaEdit);
        final EditText edtTenEdit = (EditText) dialog.findViewById(R.id.edittextTenEdit);
        Button btnEdit = (Button) dialog.findViewById(R.id.btnEdit);
        Button btnHuy = (Button) dialog.findViewById(R.id.btnHuy);

        edtMaEdit.setText(ma);
        edtTenEdit.setText(ten);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newma = edtMaEdit.getText().toString().trim();
                String newten = edtTenEdit.getText().toString().trim();
                dbHelper.QueryData("UPDATE " + DataBaseHelper.TABLE_LOP_HOC + " SET " + DataBaseHelper.KEY_LOP_HOC_MA_LOP + " = '" + newma + "'," + DataBaseHelper.KEY_LOP_HOC_TEN_LOP + " ='" + newten + "' WHERE " + DataBaseHelper.KEY_LOP_HOC_ID + " = '" + id + "'");
                Toast.makeText(QuanLyLopHocActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                GetDataLopHoc();
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

