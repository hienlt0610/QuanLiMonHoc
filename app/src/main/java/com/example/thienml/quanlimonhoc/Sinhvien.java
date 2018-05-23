package com.example.thienml.quanlimonhoc;


import java.io.Serializable;

public class Sinhvien implements Serializable {
    private String masv;
    private String tensv;
    private boolean gioitinh;
    private Integer diemTK, diemGK, diemCK;

    public String getMasv() {
        return masv;
    }

    public void setMasv(String masv) {
        this.masv = masv;
    }

    public String getTensv() {
        return tensv;
    }

    public void setTensv(String tensv) {
        this.tensv = tensv;
    }

    public boolean isNam() {
        return gioitinh;
    }
    public void setNam(boolean gioitinh) {
        this.gioitinh = gioitinh;
    }
    @Override
    public String toString()
    {
        return masv+"-"+tensv+"-"+gioitinh;
    }


    public Integer getDiemTK() {
        return diemTK;
    }

    public void setDiemTK(Integer diemTK) {
        this.diemTK = diemTK;
    }

    public Integer getDiemGK() {
        return diemGK;
    }

    public void setDiemGK(Integer diemGK) {
        this.diemGK = diemGK;
    }

    public Integer getDiemCK() {
        return diemCK;
    }

    public void setDiemCK(Integer diemCK) {
        this.diemCK = diemCK;
    }
}
