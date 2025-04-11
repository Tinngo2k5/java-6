package edu.poly.assignment_java5_pd11236.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "USERS")
public class Users {

    @Id
    @Column(name = "id_user", unique = true, nullable = false, length = 50)
    private String idUser;

    @Column(name = "sdt", nullable = false, length = 10)
    private String sdt;

    @Column(name = "hinh")
    private String hinh;

    @Column(name = "hoten", nullable = false)
    private String hoten;

    @Column(name = "matkhau", nullable = false, length = 50)
    private String matkhau;

    @Column(name = "vaitro", nullable = false)
    private boolean vaitro;

	

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
    private List<GioHang> gioHangs;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
    private List<HoaDon> hoaDons;

}
