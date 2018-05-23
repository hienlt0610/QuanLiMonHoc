package com.example.thienml.quanlimonhoc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thienml.quanlimonhoc.model.DanhGia;
import com.example.thienml.quanlimonhoc.adapter.DanhGiaAdapter;
import com.example.thienml.quanlimonhoc.util.DataBaseHelper;
import com.example.thienml.quanlimonhoc.util.ItemClickSupport;
import com.example.thienml.quanlimonhoc.util.KeyboardUtils;
import com.example.thienml.quanlimonhoc.R;

import java.util.List;

public class DanhGiaLopActivity extends BaseActivity implements View.OnClickListener, KeyboardUtils.SoftKeyboardToggleListener, ItemClickSupport.OnItemLongClickListener {
    private static final String TAG = DanhGiaLopActivity.class.getSimpleName();
    private static final String KEY_MA_LOP = "MA_LOP";
    private RecyclerView rvDanhGia;
    private Button btnThem;
    private DanhGiaAdapter adapter;
    private String maLop;
    private DataBaseHelper dataBaseHelper;

    public static Intent newIntent(Context context, String maLop) {
        Intent intent = new Intent(context, DanhGiaLopActivity.class);
        intent.putExtra(KEY_MA_LOP, maLop);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_gia);
        setHomeAsupEnable(true);
        setTitle("Đánh giá lớp");
        initArg();
        dataBaseHelper = new DataBaseHelper(this);
        rvDanhGia = findViewById(R.id.rv_danh_gia);
        btnThem = findViewById(R.id.btn_them);
        btnThem.setOnClickListener(this);
        //Khởi tạo recyclerview list
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //bắt sự kiện ẩn hiện keyboard
        KeyboardUtils.addKeyboardToggleListener(this, this);
    }

    private void initArg() {
        Intent intent = getIntent();
        maLop = intent.getStringExtra(KEY_MA_LOP);
    }

    private void initRecyclerView() {
        rvDanhGia.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DanhGiaAdapter(this);
        List<DanhGia> danhGiaList = dataBaseHelper.layDanhGiaLop(maLop);
        adapter.setList(danhGiaList);
        rvDanhGia.setAdapter(adapter);
        ItemClickSupport.addTo(rvDanhGia).setOnItemLongClickListener(this);
    }

    //Xử lý click vào button thêm
    @Override
    public void onClick(View v) {
        adapter.add();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_danh_gia, menu);
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
                luuDanhGia();
                break;
            case R.id.action_next:
                //Chuyển sang màn hình Kinh nghiệm đánh giá
                startActivity(KinhNghiemDeXuatActivity.newIntent(this, maLop));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void luuDanhGia() {
        List<DanhGia> list = adapter.getList();
        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "Hiện tại không có đánh nào để lưu", Toast.LENGTH_SHORT).show();
            return;
        }
        //Kiểm tra xem đã nhập đầy đủ thông tin hay chưa :D
        if (kiemTraListDanhGia(list)) {
            //Luu xuong db
            dataBaseHelper.luuDsDanhGia(maLop, list);
            Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Kiểm tra tính hợp lệ của data
     *
     * @param list
     * @return
     */
    private boolean kiemTraListDanhGia(List<DanhGia> list) {
        boolean isValid = true;
        for (DanhGia danhGia : list) {
            if (TextUtils.isEmpty(danhGia.getBaiThi())) {
                isValid = false;
            }
            if (TextUtils.isEmpty(danhGia.getCauHoiDeThi())) {
                isValid = false;
            }
            if (TextUtils.isEmpty(danhGia.getChuanDauRa())) {
                isValid = false;
            }
            if (TextUtils.isEmpty(danhGia.getNhanXet())) {
                isValid = false;
            }
            if (danhGia.getSoSVDat() == null) {
                isValid = false;
            }
            if (danhGia.getSoSVKhongDat() == null) {
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Được gọi khi sự kiện ẩn hiện keyboard đc gọi
     *
     * @param isVisible
     */
    @Override
    public void onToggleSoftKeyboard(boolean isVisible) {
        //Ẩn nút thêm khi keyboard đc hiện lên và cho phép hiển thị lại khi keyboard bị ẩn xuống....
        if (isVisible) {
            btnThem.setVisibility(View.GONE);
        } else {
            btnThem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        KeyboardUtils.removeKeyboardToggleListener(this);
    }

    /**
     * Được gọi khi giữ đè vào đánh giá
     *
     * @param parent   {@code RecyclerView} where the click happened
     * @param view     view within the {@code RecyclerView} that was clicked
     * @param position position of the view in the adapter
     * @param id       row ID of the item that was clicked
     * @return
     */
    @Override
    public boolean onItemLongClick(RecyclerView parent, View view, int position, long id) {
        //Xóa đánh giá khỏi list hiển thị
        adapter.remove(position);
        DanhGia danhGia = adapter.get(position);
        if (danhGia != null && danhGia.getId() != null) {
            boolean isSuccess = dataBaseHelper.xoaDanhGia(danhGia.getId());
            if (isSuccess) {
                Log.d(TAG, "Xóa thành công đánh giá id = " + danhGia.getId());
            }
        }
        return true;
    }
}
