package com.example.thienml.quanlimonhoc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class DanhGiaAdapter extends RecyclerView.Adapter<DanhGiaAdapter.ViewHolder> {
    private List<DanhGia> list;
    private LayoutInflater inflater;
    private Context context;

    public DanhGiaAdapter(Context context) {
        list = new ArrayList<>();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_danh_gia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DanhGia danhGia = list.get(position);
        holder.edtBaiThi.setText(danhGia.getBaiThi());
        holder.edtCauHoi.setText(danhGia.getCauHoiDeThi());
        holder.edtChuanDauRa.setText(danhGia.getChuanDauRa());
        holder.edtNhanXet.setText(danhGia.getNhanXet());
        if (danhGia.getSoSVDat() != null) {
            holder.edtSvDat.setText(String.valueOf(danhGia.getSoSVDat()));
        } else {
            holder.edtSvDat.setText(null);
        }

        if (danhGia.getSoSVKhongDat() != null) {
            holder.edtSvKhongDat.setText(String.valueOf(danhGia.getSoSVKhongDat()));
        } else {
            holder.edtSvKhongDat.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<DanhGia> data) {
        if (data == null) return;
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void add() {
        list.add(new DanhGia());
        notifyItemInserted(list.size() - 1);
    }

    public void remove(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }

    public DanhGia get(int pos) {
        if (pos >= 0 && pos < list.size()) {
            return list.get(pos);
        }
        return null;
    }

    public List<DanhGia> getList() {
        return list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText edtBaiThi, edtChuanDauRa, edtCauHoi, edtSvDat, edtSvKhongDat, edtNhanXet;

        public ViewHolder(View itemView) {
            super(itemView);
            edtBaiThi = itemView.findViewById(R.id.edt_bai_thi);
            edtChuanDauRa = itemView.findViewById(R.id.edt_chuan_dau_ra);
            edtCauHoi = itemView.findViewById(R.id.edt_ch_de_thi);
            edtSvDat = itemView.findViewById(R.id.edt_sv_dat);
            edtSvKhongDat = itemView.findViewById(R.id.edt_sv_khong_dat);
            edtNhanXet = itemView.findViewById(R.id.edt_nhan_xet);

            //set listener
            edtBaiThi.addTextChangedListener(new TextChange(this, edtBaiThi));
            edtChuanDauRa.addTextChangedListener(new TextChange(this, edtChuanDauRa));
            edtCauHoi.addTextChangedListener(new TextChange(this, edtCauHoi));
            edtSvDat.addTextChangedListener(new TextChange(this, edtSvDat));
            edtSvKhongDat.addTextChangedListener(new TextChange(this, edtSvKhongDat));
            edtNhanXet.addTextChangedListener(new TextChange(this, edtNhanXet));
        }
    }

    class TextChange implements TextWatcher {
        private ViewHolder viewHolder;
        private EditText editText;

        public TextChange(ViewHolder viewHolder, EditText editText) {
            this.viewHolder = viewHolder;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            DanhGia danhGia = list.get(viewHolder.getAdapterPosition());
            String plainText = editText.getText().toString().trim();
            switch (editText.getId()) {
                case R.id.edt_bai_thi:
                    danhGia.setBaiThi(plainText);
                    break;
                case R.id.edt_chuan_dau_ra:
                    danhGia.setChuanDauRa(plainText);
                    break;
                case R.id.edt_ch_de_thi:
                    danhGia.setCauHoiDeThi(plainText);
                    break;
                case R.id.edt_sv_dat:
                    Integer number = Utils.strToInt(plainText, null);
                    danhGia.setSoSVDat(number);
                    break;
                case R.id.edt_sv_khong_dat:
                    number = Utils.strToInt(plainText, null);
                    danhGia.setSoSVKhongDat(number);
                    break;
                case R.id.edt_nhan_xet:
                    danhGia.setNhanXet(plainText);
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
