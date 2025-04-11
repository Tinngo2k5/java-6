package edu.poly.assignment_java5_pd11236.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "HOADONCHITIET")
public class HoaDonChiTiet {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "idHoadon", column = @Column(name = "id_hoadon", nullable = false)),
        @AttributeOverride(name = "idSanpham", column = @Column(name = "id_sanpham", nullable = false))
    })
    private HoaDonChiTietId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoadon", nullable = false, insertable = false, updatable = false)
    @ToString.Exclude
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sanpham", nullable = false, insertable = false, updatable = false)
    @ToString.Exclude
    private SanPham sanPham;

    @Column(name = "soluong", nullable = false)
    private int soluong;

    @Column(name = "giamgia")
    private int giamgia;

    @Column(name = "dongia", nullable = false)
    private double dongia;

}
