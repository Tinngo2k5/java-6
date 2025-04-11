package edu.poly.assignment_java5_pd11236.model;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"gioHangChiTiets"}) // Tránh lỗi vòng lặp khi in chuỗi
@Table(name = "GIOHANG")
public class GioHang implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giohang", unique = true, nullable = false)
    private Integer idGiohang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private Users users;

    @OneToMany(mappedBy = "gioHang", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GioHangChiTiet> gioHangChiTiets;

}
