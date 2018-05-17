package com.example.pphighend.hotellocation;

import java.io.Serializable;

/**
 * Created by Phong Nguyen on 5/8/2018.
 */

public class favorite implements Serializable {
    private String TenDangNhap;
    private Point ToaDo;
    public favorite() {
    }
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

    public favorite(Point toaDo, String tenDangNhap) {
        ToaDo = toaDo;
        TenDangNhap = tenDangNhap;
    }


}
