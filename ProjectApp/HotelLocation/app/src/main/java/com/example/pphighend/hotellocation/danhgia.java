package com.example.pphighend.hotellocation;

import java.io.Serializable;

/**
 * Created by Phong Nguyen on 5/8/2018.
 */

public class danhgia implements Serializable {
    private String NoiDung;
    private String TenDangNhap;
    private Point ToaDo;

    public Point getToaDo() {
        return ToaDo;
    }

    public void setToaDo(Point toaDo) {
        ToaDo = toaDo;
    }

    public String getTenDangNhap() {
        return TenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        TenDangNhap = tenDangNhap;
    }

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        NoiDung = noiDung;
    }

    public danhgia() {

    }

    public danhgia(Point toaDo, String tenDangNhap, String noiDung) {
        ToaDo = toaDo;
        TenDangNhap = tenDangNhap;
        NoiDung = noiDung;
    }
}
