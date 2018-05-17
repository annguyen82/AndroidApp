package com.example.pphighend.hotellocation;

import java.io.Serializable;

/**
 * Created by Phong Nguyen on 5/5/2018.
 */

public class TaiKhoan implements Serializable{
    private String HoTen;
    private String TenDangNhap;
    private String MatKhau;
    private String Email;
    private String HinhAnh;
    public TaiKhoan(){}
    public TaiKhoan(String hoTen, String tenDangNhap, String matKhau, String email, String hinhAnh) {
        HoTen = hoTen;
        TenDangNhap = tenDangNhap;
        MatKhau = matKhau;
        Email = email;
        HinhAnh = hinhAnh;
    }
    public TaiKhoan(String tenDangNhap) {
        TenDangNhap = tenDangNhap;
    }



    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public String getTenDangNhap() {
        return TenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        TenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String matKhau) {
        MatKhau = matKhau;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }
}
