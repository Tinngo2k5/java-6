package edu.poly.assignment_java5_pd11236.model;

import java.util.Date;

public class KhachHangVipDTO {
    private String hoten;
    private Double tongTien;
    private Date ngayMuaDauTien;
    private Date ngayMuaGanNhat;

    public KhachHangVipDTO(String hoten, Double tongTien, Date ngayMuaDauTien, Date ngayMuaGanNhat) {
        this.hoten = hoten;
        this.tongTien = tongTien;
        this.ngayMuaDauTien = ngayMuaDauTien;
        this.ngayMuaGanNhat = ngayMuaGanNhat;
    }

    // Getters và Setters
    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public Date getNgayMuaDauTien() {
        return ngayMuaDauTien;
    }

    public void setNgayMuaDauTien(Date ngayMuaDauTien) {
        this.ngayMuaDauTien = ngayMuaDauTien;
    }

    public Date getNgayMuaGanNhat() {
        return ngayMuaGanNhat;
    }

    public void setNgayMuaGanNhat(Date ngayMuaGanNhat) {
        this.ngayMuaGanNhat = ngayMuaGanNhat;
    }
}
