package com.example.thienml.quanlimonhoc;


public class LopHoc {
    private String Id;
    private String MaLop;
    private String TenLop;
    private String knDanhGia;

    public LopHoc() {
    }

    public LopHoc(String id, String maLop, String tenLop) {
        this.Id = id;
        MaLop = maLop;
        TenLop = tenLop;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMaLop() {
        return MaLop;
    }

    public void setMaLop(String maLop) {
        MaLop = maLop;
    }

    public String getTenLop() {
        return TenLop;
    }

    public void setTenLop(String tenLop) {
        TenLop = tenLop;
    }

    public String getKnDanhGia() {
        return knDanhGia;
    }

    public void setKnDanhGia(String knDanhGia) {
        this.knDanhGia = knDanhGia;
    }
}


