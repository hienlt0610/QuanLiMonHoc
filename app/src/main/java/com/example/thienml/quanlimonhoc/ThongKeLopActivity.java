package com.example.thienml.quanlimonhoc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.EnumsAlign;
import com.anychart.anychart.LegendLayout;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;

import java.util.ArrayList;
import java.util.List;

public class ThongKeLopActivity extends BaseActivity {
    private static final String KEY_MA_LOP = "MA_LOP";
    private String maLop;
    private AnyChartView chartView;
    private LopHoc lopHoc;
    private DataBaseHelper dbHelper;

    public static Intent newIntent(Context context, String maLop) {
        Intent intent = new Intent(context, ThongKeLopActivity.class);
        intent.putExtra(KEY_MA_LOP, maLop);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke_lop);
        setHomeAsupEnable(true);
        setTitle("Thống kê lớp");
        initArgument();
        dbHelper = new DataBaseHelper(this);
        chartView = findViewById(R.id.any_chart_view);
        //Xử lý lấy thông tin lớp học và hiển thị lên chart
        showChartData();
    }

    /**
     * Xử lý lấy thông tin lớp học và hiển thị lên biểu đồ
     */
    private void showChartData() {
        Pie pie = AnyChart.pie();
        lopHoc = dbHelper.layThongTinLopHoc(maLop);
        List<Sinhvien> dsSinhVienCuaLop = dbHelper.layDsSinhVienCuaLop(maLop);
        //Định nghĩa các biến đếm số sinh viên đậu và rớt
        int tongSvDau = 0;
        int tongSvRot = 0;
        int tongSVCamThi = 0;
        int tongSVbothick = 0;
        for (Sinhvien sinhvien : dsSinhVienCuaLop) {
            /*if (sinhvien.getDiemTK() != null && sinhvien.getDiemGK() != null && sinhvien.getDiemCK() != null) {
                float diemTongKet = 0.5f * sinhvien.getDiemCK() + 0.3f * sinhvien.getDiemGK() + 0.2f * sinhvien.getDiemTK();*/
            if (sinhvien.getDiemTK() == 0 || sinhvien.getDiemGK() == 0) {
                tongSVCamThi += 1;
            } else {
                if (sinhvien.getDiemCK() == null) {
                    tongSVbothick += 1;
                } else {
                    if (sinhvien.getDiemTK() != null && sinhvien.getDiemGK() != null && sinhvien.getDiemCK() != null) {
                        float diemTongKet = 0.5f * sinhvien.getDiemCK() + 0.3f * sinhvien.getDiemGK() + 0.2f * sinhvien.getDiemTK();
                        if (sinhvien.getDiemCK() < 3 || diemTongKet < 4) {
                            tongSvRot += 1;
                        } else {
                            tongSvDau += 1;
                        }
                    }
                }

                /*if (sinhvien.getDiemCK() < 3 || diemTongKet < 4) {
                    tongSvRot += 1;
                } else {
                    tongSvDau += 1;
                }
            }*/
            }
        }
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Sinh viên đậu: " + tongSvDau, tongSvDau));
        data.add(new ValueDataEntry("Sinh viên rớt: " + tongSvRot, tongSvRot));
        data.add(new ValueDataEntry("Bị cấm thi: " + tongSVCamThi, tongSVCamThi));
        data.add(new ValueDataEntry("Sinh viên bỏ thi Cuối Kỳ: " + tongSVbothick, tongSVbothick));

        pie.setData(data);
        int siso = tongSVbothick + tongSVCamThi + tongSvDau + tongSvRot;
        pie.setTitle("Thống kê lớp: " + lopHoc.getTenLop() + " Sỉ số: " + siso);
        pie.getLegend()
                .setPosition("center-bottom")
                .setItemsLayout(LegendLayout.VERTICAL_EXPANDABLE)
                .setAlign(EnumsAlign.CENTER);

        chartView.setChart(pie);

    }

    private void initArgument() {
        Intent intent = getIntent();
        if (intent != null) {
            maLop = intent.getStringExtra(KEY_MA_LOP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_thong_ke, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Khi bấm nút back thì trở về màn hình trước đó
                this.finish();
                break;
            case R.id.action_next:
                //Mở màn hình đánh giá
                startActivity(DanhGiaLopActivity.newIntent(ThongKeLopActivity.this, maLop));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
