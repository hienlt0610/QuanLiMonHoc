package com.example.thienml.quanlimonhoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thienml.quanlimonhoc.model.LopHoc;
import com.example.thienml.quanlimonhoc.R;
import com.example.thienml.quanlimonhoc.ui.QuanLyLopHocActivity;

import java.util.List;

public class LopHocAdapter extends BaseAdapter {
    private QuanLyLopHocActivity context;
    private int layout;
    private List<LopHoc> lopHocList;

    public LopHocAdapter(QuanLyLopHocActivity context, int layout, List<LopHoc> lopHocList) {
        this.context = context;
        this.layout = layout;
        this.lopHocList = lopHocList;
    }


    @Override
    public int getCount() {
        return lopHocList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        TextView txtMaLop, txtTenLop;
        ImageView imgDelete, imgEdit;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder.txtTenLop    = (TextView) view.findViewById(R.id.textviewTenLop);
            holder.txtMaLop     = (TextView) view.findViewById(R.id.textviewMaLop);
            holder.imgDelete    = (ImageView)view.findViewById(R.id.imageviewDelete);
            holder.imgEdit      = (ImageView) view.findViewById(R.id.imageviewEdit);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final LopHoc lopHoc = lopHocList.get(i);
        holder.txtTenLop.setText(lopHoc.getTenLop());
        holder.txtMaLop.setText(lopHoc.getMaLop());

        //bắt sự kiện xóa
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.DialogEdit(lopHoc.getMaLop(),lopHoc.getTenLop(),lopHoc.getId());
            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.DialogDelete(lopHoc.getTenLop(),lopHoc.getId());
            }
        });
        return view;
    }
}


