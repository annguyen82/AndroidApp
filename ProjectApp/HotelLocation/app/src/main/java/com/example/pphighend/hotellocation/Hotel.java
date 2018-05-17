package com.example.pphighend.hotellocation;

import java.io.Serializable;

public class Hotel implements Serializable {
    private String TenKhachSan;
    private String HinhAnh;
    private String DiaChi;
    private Point ToaDo;

    public Hotel() {
    }

    public Hotel(String tenKhachSan, String hinhAnh, String diaChi, Point toaDo) {
        TenKhachSan = tenKhachSan;
        HinhAnh = hinhAnh;
        DiaChi = diaChi;
        ToaDo = toaDo;
    }

    public String getTenKhachSan() {
        return TenKhachSan;
    }

    public void setTenKhachSan(String tenKhachSan) {
        TenKhachSan = tenKhachSan;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }

    public Point getToaDo() {
        return ToaDo;
    }

    public void setToaDo(Point toaDo) {
        ToaDo = toaDo;
    }
}
