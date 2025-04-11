package edu.poly.assignment_java5_pd11236.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GioHangChiTietId implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Column(name = "id_giohang", nullable = false)
    private int idGiohang;

    @Column(name = "id_sanpham", nullable = false)
    private int idSanpham;

    public GioHangChiTietId() {}

    public GioHangChiTietId(int idGiohang, int idSanpham) {
        this.idGiohang = idGiohang;
        this.idSanpham = idSanpham;
    }

    public int getIdGiohang() {
        return idGiohang;
    }

    public void setIdGiohang(int idGiohang) {
        this.idGiohang = idGiohang;
    }

    public int getIdSanpham() {
        return idSanpham;
    }

    public void setIdSanpham(int idSanpham) {
        this.idSanpham = idSanpham;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GioHangChiTietId that = (GioHangChiTietId) o;
        return idGiohang == that.idGiohang && idSanpham == that.idSanpham;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGiohang, idSanpham);
    }
}
