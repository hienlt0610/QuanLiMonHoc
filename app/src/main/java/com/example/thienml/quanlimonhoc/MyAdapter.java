package com.example.thienml.quanlimonhoc;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<Sinhvien> {
    private Context context;
    private int layoutID;
    ArrayList<Sinhvien> arrayList;
    private boolean showInfoOnly;

    public MyAdapter(Context context, int layoutID, ArrayList<Sinhvien> arrayList) {
        super(context, layoutID, arrayList);
        this.context = context;
        this.layoutID = layoutID;
        this.arrayList = arrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View rooview = inflater.inflate(R.layout.custom_listview, parent, false);
        Sinhvien sv = arrayList.get(position);
        //Ánh xạ
        ImageView imageView = (ImageView) rooview.findViewById(R.id.imageSex);
        TextView tvname = (TextView) rooview.findViewById(R.id.tvName);
        TextView tvmasv = (TextView) rooview.findViewById(R.id.tvMaSV);
        TextView txttk = (TextView) rooview.findViewById(R.id.txtTK);
        TextView txtgk = (TextView) rooview.findViewById(R.id.txtGK);
        TextView txtck = (TextView) rooview.findViewById(R.id.txtCK);
        LinearLayout llDiem = rooview.findViewById(R.id.ll_diem);

        //Chỉ hiển thị thông tin sinh viên, không hiển thị bản điểm
        if (showInfoOnly) {
            llDiem.setVisibility(View.GONE);
        } else {
            llDiem.setVisibility(View.VISIBLE);
        }

        //Gán giá trị
        if (arrayList.get(position).isNam())
            imageView.setImageResource(R.drawable.boy);
        else
            imageView.setImageResource(R.drawable.girl);
        tvmasv.setText("Mã SV: " + sv.getMasv());
        tvname.setText("Tên SV: " + sv.getTensv());
        txttk.setText((sv.getDiemTK() == null) ? "*" : String.valueOf(sv.getDiemTK()));
        txtgk.setText((sv.getDiemGK() == null) ? "*" : String.valueOf(sv.getDiemGK()));
        txtck.setText((sv.getDiemCK() == null) ? "*" : String.valueOf(sv.getDiemCK()));
        return rooview;
    }

    /**
     * Cho phép hiển thị bảng điểm
     *
     * @param showInfoOnly true nếu chỉ muốn hiển thị thông tin, không hiển thị bảng điểm
     */
    public void setShowInfoOnly(boolean showInfoOnly) {
        this.showInfoOnly = showInfoOnly;
    }

    public void setListSv(List<Sinhvien> list){
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        arrayList.clear();
        arrayList.addAll(list);
        notifyDataSetChanged();
    }

    public Sinhvien getItem(int pos){
        try {
            return arrayList.get(pos);
        } catch (Exception e) {
        }
        return null;
    }
}
