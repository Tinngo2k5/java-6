package edu.poly.assignment_java5_pd11236.model;

import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "SANPHAM")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sanpham", unique = true, nullable = false)
    private Integer idSanpham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai")
    private Loai loai;

    @Column(name = "ten_sanpham", nullable = false)
    private String tenSanpham;

    @Column(name = "soluong", nullable = false)
    private int soluong;

    @Column(name = "gia", nullable = false)
    private int gia;

    @Column(name = "giamgia", nullable = false)
    private int giamgia;

    @Column(name = "hinh")
    private String hinh;

    @Column(name = "motangan", nullable = false)
    private String motangan;

    @Column(name = "mota", nullable = false)
    private String mota;

    @Temporal(TemporalType.DATE)
    @Column(name = "ngaytao", nullable = false)
    private Date ngaytao;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sanPham")
    @ToString.Exclude
    private List<GioHangChiTiet> gioHangChiTiets;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sanPham")
    @ToString.Exclude
    private List<HoaDonChiTiet> hoaDonChiTiets;
}
